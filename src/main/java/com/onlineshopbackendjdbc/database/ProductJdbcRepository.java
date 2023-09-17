package com.onlineshopbackendjdbc.database;

import com.onlineshopbackendjdbc.database.interfaces.ProductRepository;
import com.onlineshopbackendjdbc.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductJdbcRepository implements ProductRepository {
    private static final Logger logger = LogManager.getLogger(ProductJdbcRepository.class);


    @Override
    public void save(Product product){
        String sql = "INSERT INTO products (product_name, ...) VALUES (?, ...)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, product.getProduct_name());

            ps.executeUpdate();
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
        }
    }

    @Override
    public boolean existsById(Integer id) throws SQLException{
        String sql = "SELECT COUNT(*) FROM products WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }


}
