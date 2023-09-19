package com.onlineshopbackend.database.interfaces;

import com.onlineshopbackend.model.Position;

import java.sql.SQLException;
import java.util.List;

public interface PositionRepository {

    Integer createPosition(Position position) throws SQLException;

    void updatePosition(Position position, Integer positionId) throws SQLException;

    void delete (Integer positionId) throws  SQLException;

    void deletePositionsWithOrderId(Integer orderId) throws  SQLException;

    Position findById (Integer positionId) throws  SQLException;

    Position findProductForOrder(Integer productId, Integer orderId) throws  SQLException;

    List<Position> getPositionsForOrderId(Integer orderId) throws  SQLException;

    boolean isProductPresentInAnyPosition(Integer productId) throws  SQLException;
}
