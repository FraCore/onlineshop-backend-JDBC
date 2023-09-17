package com.onlineshopbackendjdbc.model;


import jakarta.validation.constraints.NotNull;



public class Product {

    @NotNull
    private Integer product_id;

    @NotNull
    private String product_name;

    @NotNull
    private String product_unit;

    @NotNull
    private Double product_price;

    public  Product() {}

    public Product(Integer product_id, String product_name, String product_unit, Double product_price) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.product_unit = product_unit;
        this.product_price = product_price;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_unit() {
        return product_unit;
    }

    public void setProduct_unit(String product_unit) {
        this.product_unit = product_unit;
    }

    public Double getProduct_price() {
        return product_price;
    }

    public void setProduct_price(Double product_price) {
        this.product_price = product_price;
    }

}
