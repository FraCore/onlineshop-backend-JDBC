package com.onlineshopbackend.model;

import jakarta.validation.constraints.NotNull;

public class Stock {
    @NotNull
    private Integer storage_id;

    @NotNull
    private Integer amount;

    @NotNull
    private Integer product_id;

    public Stock(Integer storage_id, Integer amount, Integer product_id) {
        this.storage_id = storage_id;
        this.amount = amount;
        this.product_id = product_id;
    }

    public Integer getStorage_id() {
        return storage_id;
    }

    public void setStorage_id(Integer storage_id) {
        this.storage_id = storage_id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }


}
