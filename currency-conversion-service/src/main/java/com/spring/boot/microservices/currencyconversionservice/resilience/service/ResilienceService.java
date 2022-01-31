package com.spring.boot.microservices.currencyconversionservice.resilience.service;

import com.spring.boot.microservices.currencyconversionservice.resilience.model.Product;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;

@Service
public class ResilienceService {


    // retry with configuration
    @io.github.resilience4j.retry.annotation.Retry(name = "default", fallbackMethod = "fallbackProduct")
    public Product getProduct(Callable<Product> callable){
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RateLimiter(name = "default")
    public Product getProductWithRateLimit(Callable<Product> callable){
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Bulkhead(name = "default")
    public Product getProductWithBulkHead(Callable<Product> callable){
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @CircuitBreaker(name = "default", fallbackMethod = "fallbackProduct")
    public Product getProductWithCircuitBreaker(Callable<Product> callable){
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Product getProductWithRetry(Callable<Product> callable){
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Product fallbackProduct(Callable<Product> callable, Exception ex){
        return new Product(1,"some product");
    }



}


