package com.spring.boot.microservices.currencyconversionservice.resilience.model;

public class Product {

    private Integer id;

    private String name;

    public Product(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
