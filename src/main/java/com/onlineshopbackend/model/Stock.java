package com.onlineshopbackend.model;

import jakarta.validation.constraints.NotNull;

public class Stock {
    @NotNull
    private Integer id;

    @NotNull
    private Integer amount;

    @NotNull
    private Integer productId;

    public Stock(Integer id, Integer amount, Integer productId) {
        this.id = id;
        this.amount = amount;
        this.productId = productId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }


}
