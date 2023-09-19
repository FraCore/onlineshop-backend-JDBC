package com.onlineshopbackend.database.interfaces;


import com.onlineshopbackend.model.Product;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

public interface ProductRepository {

    Integer insert(Product product) throws SQLException;

    void update(Product product) throws  SQLException;

    void delete(Integer productId) throws SQLException;

    boolean existsById(Integer id) throws SQLException;
}
