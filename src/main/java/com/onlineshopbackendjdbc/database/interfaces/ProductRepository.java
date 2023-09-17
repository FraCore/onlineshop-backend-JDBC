package com.onlineshopbackendjdbc.database.interfaces;


import com.onlineshopbackendjdbc.model.Product;

import java.sql.SQLException;

public interface ProductRepository {

    public void save(Product product) throws SQLException;

    public boolean existsById(Integer id) throws SQLException;
}
