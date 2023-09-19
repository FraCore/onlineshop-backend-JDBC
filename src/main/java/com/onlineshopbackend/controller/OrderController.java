package com.onlineshopbackend.controller;

import com.onlineshopbackend.database.implementation.OrderJdbcRepository;
import com.onlineshopbackend.database.implementation.PositionJdbRepository;
import com.onlineshopbackend.database.implementation.ProductJdbcRepository;
import com.onlineshopbackend.database.implementation.StorageJdbcRepository;
import com.onlineshopbackend.model.Order;
import com.onlineshopbackend.model.OrderState;
import com.onlineshopbackend.model.Position;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(path = "order")
public class OrderController {

    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @Autowired
    private ProductJdbcRepository productJdbcRepository;

    @Autowired
    private StorageJdbcRepository storageJdbcRepository;

    @Autowired
    private OrderJdbcRepository orderJdbcRepository;

    @Autowired
    PositionJdbRepository positionJdbRepository;

    @PostMapping(path="/create")
    public @ResponseBody ResponseEntity<String> createNewOrder () {
        try {
            Date date = new Date();
            Order order = new Order(date, OrderState.OPEN);
            Integer generatedKey = orderJdbcRepository.createOrder(order);
            String jsonResponse = String.format("{\"order_id\": %d}", generatedKey);
            return new ResponseEntity<>(jsonResponse, HttpStatus.CREATED);
        }catch(SQLException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("An error occurred",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/addProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> addProductToOrder(@Valid @RequestBody Position position) {
        try {
            if (!isProductPresent(position.getProduct_id())) {
                return new ResponseEntity<>(String.format("Product with id: %d not found", position.getProduct_id()), HttpStatus.NOT_FOUND);
            }

            if (!isOrderOpen(position.getOrder_id())) {
                return new ResponseEntity<>(String.format("Order with id: %d is closed", position.getOrder_id()), HttpStatus.BAD_REQUEST);
            }
            Position existingPosition = findProductForOrder(position.getProduct_id(), position.getOrder_id());
            if(existingPosition == null) {
                Integer generatedKey = positionJdbRepository.createPosition(position);
                String jsonResponse = String.format("{\"position_id\": %d}", generatedKey);
                return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
            }
            position.setPosition_amount(position.getPosition_amount() + existingPosition.getPosition_amount());
            positionJdbRepository.updatePosition(position, existingPosition.getPosition_id());
            String jsonResponse = String.format("{\"position_id\": %d}", existingPosition.getPosition_id());
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(path="/removeProduct/{positionId}")
    public @ResponseBody ResponseEntity<String> removeProductFromOrder(@PathVariable Integer positionId) {
        try {
            Position position = positionJdbRepository.findById(positionId);

            if (position == null) {
                return new ResponseEntity<>(String.format("Position with id: %d not found", positionId), HttpStatus.NOT_FOUND);
            }

            if (!isOrderOpen(position.getOrder_id())) {
                return new ResponseEntity<>(String.format("Order with id: %d is closed", position.getOrder_id()), HttpStatus.BAD_REQUEST);
            }
            positionJdbRepository.delete(positionId);
            return new ResponseEntity<>(String.format("Position with id: %d deleted", positionId), HttpStatus.OK);

        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/deleteOrder/{orderId}")
    public @ResponseBody ResponseEntity<String> deleteOrder(@PathVariable Integer orderId) {
        try {
            if (!isOrderOpen(orderId)) {
                return new ResponseEntity<>(String.format("Order with id: %d not found or closed", orderId), HttpStatus.BAD_REQUEST);
            }
            positionJdbRepository.deletePositionsWithOrderId(orderId);
            orderJdbcRepository.deleteOrder((orderId));
            return new ResponseEntity<>(String.format("Order with id: %d deleted", orderId), HttpStatus.OK);
        }catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/submitOrder/{orderId}")
    public @ResponseBody ResponseEntity<String> submitOrder(@PathVariable Integer orderId) {
        try {
            Order order =orderJdbcRepository.findOpenOrderById(orderId);
            if (order == null) {
                return new ResponseEntity<>(String.format("Order with id: %d not found or closed", orderId), HttpStatus.BAD_REQUEST);
            }
            //Check for Positions for Order id and get back List with product_id and amount
            List<Position> positions = positionJdbRepository.getPositionsForOrderId(orderId);

            //Check products of Lists with total Stock amount
            for(Position position : positions){
                Integer totalStock = storageJdbcRepository.getTotalStockForProduct(position.getProduct_id());
                if(totalStock < position.getPosition_amount()){
                    return new ResponseEntity<>(String.format("Not enough stock for productId: %d", position.getProduct_id()), HttpStatus.BAD_REQUEST);
                }
            }
            orderJdbcRepository.submitOrder(positions, orderId);
            return new ResponseEntity<>(String.format("Order with id: %d submitted", orderId), HttpStatus.OK);


        }catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isProductPresent(Integer productId) throws SQLException {
        return productJdbcRepository.existsById(productId);
    }

    private Boolean isOrderOpen(Integer orderId) throws SQLException {
        return orderJdbcRepository.findOpenOrderById(orderId) != null;
    }

    private Position findProductForOrder(Integer productId, Integer orderId) throws  SQLException {
        return positionJdbRepository.findProductForOrder(productId, orderId);
    }

}
