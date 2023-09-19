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
            String jsonResponse = String.format("{\"orderId\": %d}", generatedKey);
            return new ResponseEntity<>(jsonResponse, HttpStatus.CREATED);
        }catch(SQLException e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/addProduct", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> addProductToOrder(@Valid @RequestBody Position position) {
        try {
            if (!isProductPresent(position.getProductId())) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (!isOrderOpen(position.getOrderId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            Position existingPosition = findProductForOrder(position.getProductId(), position.getOrderId());
            if(existingPosition == null) {
                Integer generatedKey = positionJdbRepository.createPosition(position);
                String jsonResponse = String.format("{\"positionId\": %d, \"totalAmount\": %d}", generatedKey, position.getAmount());
                return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
            }
            position.setAmount(position.getAmount() + existingPosition.getAmount());
            positionJdbRepository.updatePosition(position, existingPosition.getId());
            String jsonResponse = String.format("{\"positionId\": %d, \"totalAmount\": %d}", existingPosition.getId(), position.getAmount());
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);

        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(path="/removeProduct/{positionId}")
    public @ResponseBody ResponseEntity<String> removeProductFromOrder(@PathVariable Integer positionId) {
        try {
            Position position = positionJdbRepository.findById(positionId);

            if (position == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            if (!isOrderOpen(position.getOrderId())) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            positionJdbRepository.delete(positionId);
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path="/deleteOrder/{orderId}")
    public @ResponseBody ResponseEntity<String> deleteOrder(@PathVariable Integer orderId) {
        try {
            if (!isOrderOpen(orderId)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            positionJdbRepository.deletePositionsWithOrderId(orderId);
            orderJdbcRepository.deleteOrder((orderId));
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/submitOrder/{orderId}")
    public @ResponseBody ResponseEntity<String> submitOrder(@PathVariable Integer orderId) {
        try {
            Order order = orderJdbcRepository.findOpenOrderById(orderId);
            if (order == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            //Check for Positions for Order id and get back List with product_id and amount
            List<Position> positions = positionJdbRepository.getPositionsForOrderId(orderId);

            //Check products of Lists with total Stock amount
            for(Position position : positions){
                Integer totalStock = storageJdbcRepository.getTotalStockForProduct(position.getProductId());
                if(totalStock < position.getAmount()){
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            }
            orderJdbcRepository.submitOrder(positions, orderId);
            return new ResponseEntity<>(HttpStatus.OK);


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
