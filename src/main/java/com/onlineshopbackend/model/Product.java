package com.onlineshopbackend.model;


import jakarta.validation.constraints.NotNull;



public class Product {

    @NotNull
    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String unit;

    @NotNull
    private Double price;

    public  Product() {}

    public Product(Integer id, String name, String unit, Double price) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.price = price;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
