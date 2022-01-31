package com.spring.boot.learning.limitsservice.controller;

import com.spring.boot.learning.limitsservice.model.MyConfiguration;
import com.spring.boot.learning.limitsservice.configuration.Configuration;
import com.spring.boot.learning.limitsservice.model.ProductPrice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LimitsController {

    @Autowired
    private Configuration configuration;


    @GetMapping(value = "/configuration")
    @ResponseBody
    public MyConfiguration getConfiguration(){
        return new MyConfiguration(configuration.getMinimum(),configuration.getMaximum());
    }

    @GetMapping("/product/{productId}/price")
    @ResponseBody
    public ProductPrice getProductPrice(@PathVariable("productId") int productId){
        return new ProductPrice(productId,1000);
    }

}
