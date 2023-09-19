package com.onlineshopbackend.model;

import jakarta.validation.constraints.NotNull;

public class Position {

    private Integer position_id;

    @NotNull
    private Integer position_amount;

    @NotNull
    private Integer order_id;

    @NotNull
    private Integer product_id;

    public Position(Integer position_id, Integer position_amount, Integer order_id, Integer product_id) {
        this.position_id = position_id;
        this.position_amount = position_amount;
        this.order_id = order_id;
        this.product_id = product_id;
    }

    public Position(Integer position_amount, Integer order_id, Integer product_id) {
        this.position_amount = position_amount;
        this.order_id = order_id;
        this.product_id = product_id;
    }
    public Position() {}

    public Integer getPosition_amount() {
        return position_amount;
    }

    public void setPosition_amount(Integer position_amount) {
        this.position_amount = position_amount;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Integer product_id) {
        this.product_id = product_id;
    }

    public Integer getPosition_id() {
        return position_id;
    }

    public void setPosition_id(Integer position_id) {
        this.position_id = position_id;
    }

}
