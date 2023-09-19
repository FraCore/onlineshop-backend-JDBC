package com.onlineshopbackend.database.implementation;

import com.onlineshopbackend.database.DatabaseConnection;
import com.onlineshopbackend.database.interfaces.PositionRepository;
import com.onlineshopbackend.model.Position;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PositionJdbRepository implements PositionRepository {

    @Override
    public Integer createPosition(Position position) throws SQLException {
        String sql = "INSERT INTO positions (position_amount, order_id, product_id) VALUES (?,?,?)";
        Integer generatedKey = null;
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, position.getAmount());
            ps.setInt(2, position.getOrderId());
            ps.setInt(3, position.getProductId());

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
    public void updatePosition(Position position, Integer positionId) throws SQLException {
        String sql = "UPDATE positions SET position_amount = ?, order_id = ?, product_id = ? WHERE position_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, position.getAmount());
            ps.setInt(2, position.getOrderId());
            ps.setInt(3, position.getProductId());
            ps.setInt(4, positionId);

            ps.executeUpdate();
        }
    }

    @Override
    public void delete(Integer positionId) throws SQLException {
        String sql = "DELETE FROM positions WHERE position_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, positionId);

            ps.executeUpdate();
        }
    }

    @Override
    public Position findById(Integer positionId) throws SQLException {
            Position position = null;
            String sql = "SELECT * FROM positions WHERE position_id = ?";

            try(Connection connection = DatabaseConnection.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setInt(1, positionId);
                try(ResultSet rs = ps.executeQuery()) {
                    if(rs.next()) {
                        position = new Position(
                                rs.getInt("position_amount"),
                                rs.getInt("order_id"),
                                rs.getInt("product_id")
                                );
                    }
                }
            }
            return position;
    }

    @Override
    public void deletePositionsWithOrderId(Integer orderId) throws SQLException {
        String sql = "DELETE FROM positions WHERE order_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            ps.executeUpdate();
        }
    }

    @Override
    public Position findProductForOrder(Integer productId, Integer orderId) throws SQLException {
        Position position = null;
        String sql = "SELECT * FROM positions WHERE product_id = ? AND order_id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setInt(2, orderId);

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    position = new Position(
                            rs.getInt("position_id"),
                            rs.getInt("position_amount"),
                            rs.getInt("order_id"),
                            rs.getInt("product_id")
                    );
                }
            }
        }
        return position;
    }

    @Override
    public List<Position> getPositionsForOrderId(Integer orderId) throws SQLException {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT * FROM positions WHERE order_id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Position position = new Position(
                            rs.getInt("position_id"),
                            rs.getInt("position_amount"),
                            rs.getInt("order_id"),
                            rs.getInt("product_id")
                    );
                    positions.add(position);
                }
            }
        }
        return positions;
    }

    @Override
    public boolean isProductPresentInAnyPosition(Integer productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM positions WHERE product_id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
}
