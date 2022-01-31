package com.spring.boot.microservices.currencyconversionservice.resilience;

import com.spring.boot.microservices.currencyconversionservice.resilience.model.Product;
import com.spring.boot.microservices.currencyconversionservice.resilience.service.ResilienceService;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.function.Function;

@RestController
public class ResilienceController {

    Logger logger = LoggerFactory.getLogger(ResilienceController.class);

    @Autowired
    private ResilienceService service;


    // retry and fallback with configuration
    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable int id) {
        return service.getProduct(() -> {
            logger.info("request processing");
            throw new RuntimeException();
        });
    }

    // ratelimit with configuration
    // test with this command, watch -n 0.1 curl http://localhost:8100/product/1/ratelimit
    @GetMapping("/product/{id}/ratelimit")
    public Product getProductWithRateLimit(@PathVariable int id) {
        return service.getProductWithRateLimit(() -> {
            return new Product(id,"rateLimit");
        });
    }

    //bulk head with configuration
    // test with this command, watch -n 0.1 curl http://localhost:8100/product/1/bulkhead
    @GetMapping("/product/{id}/bulkhead")
    public Product getProductWithBulkHead(@PathVariable int id) {
        return service.getProductWithBulkHead(() -> {
            return new Product(id,"bulkhead");
        });
    }

    // Circuit breaker with configuration
    // test with this command, watch -n 0.1 curl http://localhost:8100/product/1/circuitbreaker
    // you can verify how many calls will go in open state and then fallback will be invoked in closed state. Then after few second it will send few call in half-open state.
    @GetMapping("/product/{id}/circuitbreaker")
    public Product getProductWithCircuitBreaker(@PathVariable int id) {
        return service.getProductWithCircuitBreaker(() -> {
            throw new RuntimeException("error");
            //return new Product(id,"circuit breaker");
        });
    }

    // ratelimit with configuration
    // test with this command, watch -n 0.1 curl http://localhost:8100/product/1/ratelimit/code
    @GetMapping("/product/{id}/ratelimit/code")
    public Product getProductWithRateLimitCode(@PathVariable int id) {
        RateLimiterConfig config = RateLimiterConfig
                .custom()
                .limitRefreshPeriod(Duration.ofSeconds(60))
                .limitForPeriod(10)
                .build();
        RateLimiterRegistry registry = RateLimiterRegistry.of(config);
        RateLimiter rateLimiter = registry.rateLimiter("my");
        Function<Integer, Product> decorated
                = RateLimiter.decorateFunction(rateLimiter, (pid)-> {
                    return new Product(pid,"ratelimit");
                }
        );

        return decorated.apply(id);
    }

    @GetMapping("/product/{id}/bulkhead/Code")
    public Product getProductWithBulkHeadCode(@PathVariable int id) {
        BulkheadConfig config = BulkheadConfig.custom().maxConcurrentCalls(1).build();
        BulkheadRegistry registry = BulkheadRegistry.of(config);
        Bulkhead bulkhead = registry.bulkhead("my");
        Function<Integer, Product> decorated
                = Bulkhead.decorateFunction(bulkhead, (pid)-> {
            return new Product(pid,"ratelimit");
        });

        return decorated.apply(id);
    }

    @GetMapping("/product/{id}/circuitbreaker/code")
    public Product getProductWithCircuitBreakerCode(@PathVariable int id) {
        CircuitBreakerRegistry circuitBreakerRegistry
                = CircuitBreakerRegistry.ofDefaults();
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(20)
                .ringBufferSizeInClosedState(5)
                .build();
        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        CircuitBreaker circuitBreaker = registry.circuitBreaker("my");
        Function<Integer, Product> decorated = CircuitBreaker
                .decorateFunction(circuitBreaker, (pid)-> {
                    return new Product(pid,"circuitBreaker");
                });

        return decorated.apply(id);
    }

    // retry with code
    @GetMapping("/product/{id}/retry")
    public Product getProductWithRetry(@PathVariable Integer id) {
        RetryConfig config = RetryConfig
                .custom()
                .maxAttempts(2)
                .intervalFunction(
                        IntervalFunction.of(
                                Duration
                                        .ofMillis(1000)))
                .build();
        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("my");
        Function<Integer, Product> decorated
                = Retry.decorateFunction(retry, (productId) -> {
                   return service.getProductWithRetry(() -> {
                       logger.info("request processing with retry");
                       //return new Product(productId, "new retry product");
                       throw new RuntimeException();
                   });
        });
        return decorated.apply(id);
    }

    private Product fallback(int id, Exception ex) {
        return new Product(1,"some product");
    }



}
