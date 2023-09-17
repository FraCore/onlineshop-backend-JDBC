package com.onlineshopbackendjdbc.controller;


import com.onlineshopbackendjdbc.database.ProductJdbcRepository;
import com.onlineshopbackendjdbc.model.Product;
import com.onlineshopbackendjdbc.model.Storage;
import com.onlineshopbackendjdbc.database.interfaces.ProductRepository;
import com.onlineshopbackendjdbc.database.interfaces.StorageRepository;
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
import java.util.Optional;

@Controller
@RequestMapping(path = "product")
public class ProductController {

    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StorageRepository storageRepository;

    @PostMapping(path="/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> addNewProduct(@Valid @RequestBody Product product) {
        try {
            if(isProductPresent(product.getProduct_id())) {
                return new ResponseEntity<>("Product already exists", HttpStatus.CONFLICT);
            }
            try {
                productRepository.save(product);
                return new ResponseEntity<>("Product added successfully", HttpStatus.CREATED);

            }catch(Exception e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>("An error occurred",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (SQLException sqlException) {
            return new ResponseEntity<>(String.format("SQL Error %s has occured", sqlException.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> updateProduct(@Valid @RequestBody Product product) {
        try {
            if(!isProductPresent(product.getProduct_id())) {
                return new ResponseEntity<>(String.format("Product with id: %d not found", product.getProduct_id()), HttpStatus.NOT_FOUND);
            }
            try {
                product.setProduct_id(product.getProduct_id());
                productRepository.save(product);
                return new ResponseEntity<>("Product successfully updated", HttpStatus.OK);
            } catch (Exception e) {
                logger.error(e.getMessage());
                return new ResponseEntity<>("An error occurred",HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (SQLException sqlException) {
            return new ResponseEntity<>(String.format("SQL Error %s has occured", sqlException.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/addStock/{productId}/{storageId}/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addProductStock(@PathVariable Integer productId,
                                                  @PathVariable Integer storageId,
                                                  @Valid @RequestBody Storage storageInput) {
        try {
            if (!isProductPresent(productId)) {
                return new ResponseEntity<>(String.format("No Product with productId: %d found", productId), HttpStatus.NOT_FOUND);
            }

            Optional<Storage> optionalStorage = storageRepository.findById(storageId);
            return optionalStorage.map(storage -> updateExistingStorage(productId, storageId, storageInput, storage)).orElseGet(() -> createNewStorage(storageInput, storageId, productId));
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>(String.format("SQL Error %s has occured", sqlException.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isProductPresent(Integer productId) throws SQLException {
            return productRepository.existsById(productId);
    }

    private ResponseEntity<String> updateExistingStorage(Integer productId, Integer storageId, Storage storageInput, Storage existingStorage) {
        if (!existingStorage.getProduct_id().equals(productId)) {
            return new ResponseEntity<>(String.format("Storage Id %d has productId: %d in stock and cannot store Product with id: %d",
                    storageId, existingStorage.getProduct_id(), productId), HttpStatus.BAD_REQUEST);
        }

        try {
            existingStorage.setStorage_stock(storageInput.getStorage_stock());
            storageRepository.save(existingStorage);
            return new ResponseEntity<>(String.format("New Amount of %d was deposited to storageId: %d for productId: %d",
                    existingStorage.getStorage_stock(), storageId, productId), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("An error occurred while updating the storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> createNewStorage(Storage storageInput, Integer storageId, Integer productId) {
        try {
            storageRepository.save(storageInput);
            return new ResponseEntity<>(String.format("Stock of %d was added to storageId: %d for productId: %d",
                    storageInput.getStorage_stock(), storageId, productId), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("An error occurred while creating new storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
