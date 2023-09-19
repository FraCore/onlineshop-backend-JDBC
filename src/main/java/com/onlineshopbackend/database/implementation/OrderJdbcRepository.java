package com.onlineshopbackend.database.implementation;

import com.onlineshopbackend.database.DatabaseConnection;
import com.onlineshopbackend.database.interfaces.OrderRepository;
import com.onlineshopbackend.model.Order;
import com.onlineshopbackend.model.OrderState;
import com.onlineshopbackend.model.Position;
import com.onlineshopbackend.model.Storage;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderJdbcRepository implements OrderRepository {

    @Override
    public Integer createOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (order_date, order_state) VALUES (?,?)";
        Integer generatedKey = null;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDate(1, new java.sql.Date(order.getOrder_date().getTime()));
            ps.setString(2, OrderState.OPEN.name());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedKey = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating position failed, no ID obtained.");
                }
            }
        }
        return generatedKey;
    }

    @Override
    public Order findOpenOrderById(Integer orderId) throws SQLException {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE order_id = ? AND order_state = 'OPEN'";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    order = new Order(rs.getDate("order_date"), OrderState.valueOf(rs.getString("order_state")));
                }
            }
        }
        return order;
    }

    @Override
    public void deleteOrder(Integer orderId) throws SQLException {
        String sql = "DELETE FROM orders WHERE order_id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1, orderId);

            ps.executeUpdate();
        }
    }


    public void submitOrder(List<Position> positions, Integer orderId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            StorageJdbcRepository storageRepo = new StorageJdbcRepository();
            List<PreparedStatement> preparedStatements = new ArrayList<>();

            // Reduce amount on Storage for all positions
            for (Position position : positions) {
                List<Storage> storages = storageRepo.getAllStoragesForId(position.getProduct_id());
                int requiredAmount = position.getPosition_amount();

                for (Storage storage : storages) {
                    if (requiredAmount == 0) {
                        break;
                    }

                    if (storage.getStorage_stock() <= requiredAmount) {
                        requiredAmount -= storage.getStorage_stock();

                        PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM storages WHERE storage_id = ?");
                        deleteStmt.setInt(1, storage.getStorage_id());
                        preparedStatements.add(deleteStmt);
                    } else {
                        // If the storage has more stock than required, deduct the required amount and prepare statement for update
                        int newStock = storage.getStorage_stock() - requiredAmount;
                        PreparedStatement updateStmt = connection.prepareStatement("UPDATE storages SET storage_stock = ? WHERE storage_id = ?");
                        updateStmt.setInt(1, newStock);
                        updateStmt.setInt(2, storage.getStorage_id());
                        preparedStatements.add(updateStmt);

                        requiredAmount = 0;
                    }
                }

                if (requiredAmount > 0) {
                    for (PreparedStatement stmt : preparedStatements) {
                        stmt.close();
                    }
                    connection.rollback();
                    throw new SQLException("Not enough stock to fulfill the order.");
                }
            }

            // If storage reduced, close the order
            PreparedStatement updateOrderStmt = connection.prepareStatement("UPDATE orders SET order_state = ? WHERE order_id = ?");
            updateOrderStmt.setString(1, OrderState.CLOSED.name());
            updateOrderStmt.setInt(2, orderId);
            preparedStatements.add(updateOrderStmt);

            for (PreparedStatement stmt : preparedStatements) {
                stmt.executeUpdate();
                stmt.close();
            }

            connection.commit();
        } catch (SQLException e) {
            throw new SQLException("Error processing order: " + e.getMessage());
        }
    }

}
