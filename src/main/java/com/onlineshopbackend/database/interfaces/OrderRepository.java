package com.onlineshopbackend.database.interfaces;

import com.onlineshopbackend.model.Order;
import com.onlineshopbackend.model.Position;

import java.sql.SQLException;

public interface OrderRepository {

    Integer createOrder(Order order) throws SQLException;

    Order findOpenOrderById(Integer orderId) throws SQLException;

    void deleteOrder(Integer orderId) throws  SQLException;

}
