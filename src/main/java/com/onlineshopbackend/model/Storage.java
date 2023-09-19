package com.onlineshopbackend.model;


import jakarta.validation.constraints.NotNull;


public class Storage {

    private Integer storage_id;

    @NotNull
    private Integer storage_stock;

    @NotNull
    private Integer product_id;

    public Storage(Integer storage_id, Integer storage_stock, Integer product_id) {
        this.storage_id = storage_id;
        this.storage_stock = storage_stock;
        this.product_id = product_id;
    }

    public Storage(Integer storage_stock, Integer product_id) {
        this.storage_stock = storage_stock;
        this.product_id = product_id;
    }

    public Storage() {
    }

    public Integer getStorage_stock() {
        return storage_stock;
    }

    public void setStorage_stock(Integer storage_stock) {
        this.storage_stock = storage_stock;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getStorage_id() {
        return storage_id;
    }

    public void setStorage_id(Integer storage_id) {
        this.storage_id = storage_id;
    }

}

