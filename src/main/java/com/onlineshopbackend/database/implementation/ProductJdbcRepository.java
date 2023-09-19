package com.onlineshopbackend.database.implementation;

import com.onlineshopbackend.database.DatabaseConnection;
import com.onlineshopbackend.database.interfaces.ProductRepository;
import com.onlineshopbackend.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Repository
public class ProductJdbcRepository implements ProductRepository {

    private static final Logger logger = LogManager.getLogger(ProductJdbcRepository.class);


    @Override
    public Integer insert(Product product) throws SQLException{
        String sql = "INSERT INTO products (product_id, product_name, product_unit, product_price) VALUES (?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, product.getId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getUnit());
            ps.setDouble(4, product.getPrice());

            ps.executeUpdate();
        }
        return product.getId();
    }

    @Override
    public void update(Product product) throws SQLException {
        String sql = "UPDATE products SET product_name = ?, product_unit = ?, product_price = ? WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getUnit());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4, product.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Integer productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, productId);

            ps.executeUpdate();
        }
    }

    @Override
    public boolean existsById(Integer id) throws SQLException {
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
