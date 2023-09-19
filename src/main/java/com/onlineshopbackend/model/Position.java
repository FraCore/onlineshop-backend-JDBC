package com.onlineshopbackend.model;

import jakarta.validation.constraints.NotNull;

public class Position {

    private Integer id;

    @NotNull
    private Integer amount;

    @NotNull
    private Integer orderId;

    @NotNull
    private Integer productId;

    public Position(Integer id, Integer amount, Integer orderId, Integer productId) {
        this.id = id;
        this.amount = amount;
        this.orderId = orderId;
        this.productId = productId;
    }

    public Position(Integer amount, Integer orderId, Integer productId) {
        this.amount = amount;
        this.orderId = orderId;
        this.productId = productId;
    }
    public Position() {}

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
