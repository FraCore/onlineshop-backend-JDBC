package com.onlineshopbackendjdbc.model;


import jakarta.validation.constraints.NotNull;

import java.util.Date;


public class Order {

    @NotNull
    private Integer order_id;

    @NotNull
    private Date order_date;
    @NotNull
    private OrderState order_state;


    public Order(Integer order_id, Date order_date, OrderState order_state) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.order_state = order_state;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Date getOrder_date() {
        return order_date;
    }

    public void setOrder_date(Date order_date) {
        this.order_date = order_date;
    }

    public OrderState getOrder_state() {
        return order_state;
    }

    public void setOrder_state(OrderState order_state) {
        this.order_state = order_state;
    }
}
