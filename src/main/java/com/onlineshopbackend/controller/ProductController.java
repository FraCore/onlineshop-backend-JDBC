package com.onlineshopbackend.controller;


import com.onlineshopbackend.database.implementation.PositionJdbRepository;
import com.onlineshopbackend.database.implementation.StorageJdbcRepository;
import com.onlineshopbackend.database.implementation.ProductJdbcRepository;
import com.onlineshopbackend.model.Product;
import com.onlineshopbackend.model.Stock;
import com.onlineshopbackend.model.Storage;
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

@Controller
@RequestMapping(path = "product")
public class ProductController {

    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @Autowired
    private ProductJdbcRepository productJdbcRepository;

    @Autowired
    private StorageJdbcRepository storageJdbcRepository;

    @Autowired
    PositionJdbRepository positionJdbRepository;

    @PostMapping(path="/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> addNewProduct(@Valid @RequestBody Product product) {
        try {
            if(isProductPresent(product.getId())) {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
            Integer productId = productJdbcRepository.insert(product);
            String jsonResponse = String.format("{\"id\": %d}", productId);
            return new ResponseEntity<>(jsonResponse, HttpStatus.CREATED);
        }catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody ResponseEntity<String> updateProduct(@Valid @RequestBody Product product) {
        try {
            if(!isProductPresent(product.getId())) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            product.setId(product.getId());
            productJdbcRepository.update(product);
            return new ResponseEntity<>("Product successfully updated", HttpStatus.OK);
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>(String.format("SQL Error %s has occured", sqlException.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path="/delete/{productId}")
    public @ResponseBody ResponseEntity<String> updateProduct(@PathVariable Integer productId) {
        try {
            if(!isProductPresent(productId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            if(positionJdbRepository.isProductPresentInAnyPosition(productId)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if(storageJdbcRepository.getTotalStockForProduct(productId) > 0) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            productJdbcRepository.delete(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/addStock", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addProductStock(@Valid @RequestBody Stock stock) {
        try {
            if (!isProductPresent(stock.getProductId())) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Storage storage = storageJdbcRepository.findStorageById(stock.getId());
            if(storage != null) {
                return increaseExistingStorage(stock.getProductId(), stock.getId(), stock, storage);
            } else {
                return createNewStorage(stock);
            }
        } catch (SQLException sqlException) {
            logger.error("Error fetching/updating storage: " + sqlException.getMessage());
            return new ResponseEntity<>(String.format("SQL Error %s occurred", sqlException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(path="/removeStock", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeProductStock(@Valid @RequestBody Stock stock) {
        try {
            if (!isProductPresent(stock.getProductId())) {
                return new ResponseEntity<>(String.format("No Product with productId: %d found", stock.getProductId()), HttpStatus.NOT_FOUND);
            }
            Storage storage = storageJdbcRepository.findStorageById(stock.getId());
            if(storage != null) {
                return decreaseExistingStorage(stock.getProductId(), stock.getId(), stock, storage);
            } else {
                return new ResponseEntity<>(String.format("No Storage with id: %d",
                        stock.getId()), HttpStatus.BAD_REQUEST);
            }
        } catch (SQLException sqlException) {
            logger.error("Error fetching/updating storage: " + sqlException.getMessage());
            return new ResponseEntity<>(String.format("SQL Error %s occurred", sqlException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(path="/getTotalStockForProduct/{productId}")
    public ResponseEntity<String> getTotalStockForProduct(@PathVariable Integer productId) {
        try {
            if (!isProductPresent(productId)) {
                return new ResponseEntity<>(String.format("No Product with productId: %d found", productId), HttpStatus.NOT_FOUND);
            }
             return new ResponseEntity<>(storageJdbcRepository.getTotalStockForProduct(productId).toString(), HttpStatus.OK);
        }catch (SQLException sqlException) {
            logger.error("Error fetching/updating storage: " + sqlException.getMessage());
            return new ResponseEntity<>(String.format("SQL Error %s occurred", sqlException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isProductPresent(Integer productId) throws SQLException {
            return productJdbcRepository.existsById(productId);
    }

    private ResponseEntity<String> increaseExistingStorage(Integer productId, Integer storageId, Stock stock, Storage existingStorage) {
        if (!existingStorage.getProduct_id().equals(productId)) {
            return new ResponseEntity<>(String.format("Storage Id %d has productId: %d in stock and cannot store Product with id: %d",
                    storageId, existingStorage.getProduct_id(), productId), HttpStatus.BAD_REQUEST);
        }
        try {
            existingStorage.setStorage_stock(stock.getAmount() + existingStorage.getStorage_stock());
            storageJdbcRepository.updateStorage(existingStorage, storageId);
            return new ResponseEntity<>(String.format("New Amount of %d was deposited to storageId: %d for productId: %d",
                    existingStorage.getStorage_stock(), storageId, productId), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("An error occurred while updating the storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<String> decreaseExistingStorage(Integer productId, Integer storageId, Stock stock, Storage existingStorage) {
        if (!existingStorage.getProduct_id().equals(productId)) {
            return new ResponseEntity<>(String.format("Storage Id %d has productId: %d in stock and cannot store Product with id: %d",
                    storageId, existingStorage.getProduct_id(), productId), HttpStatus.BAD_REQUEST);
        }
        if(existingStorage.getStorage_stock() < stock.getAmount()) {
            return new ResponseEntity<>(String.format("Amount of requested stock %d is higher then the available amount: %d in storageId: %d for product: %d",
                    stock.getAmount(), existingStorage.getStorage_stock(), storageId, productId), HttpStatus.BAD_REQUEST);
        }
        try {
            existingStorage.setStorage_stock(existingStorage.getStorage_stock() - stock.getAmount());
            if(existingStorage.getStorage_stock() == 0) {
                storageJdbcRepository.deleteStorage(storageId);
                return new ResponseEntity<>(String.format("Amount decreased - all goods where sold for storageId: %d, storage gets deleted and is available for other products",
                     storageId), HttpStatus.OK);
            }
            storageJdbcRepository.updateStorage(existingStorage, storageId);
            return new ResponseEntity<>(String.format("New Amount of %d was deposited to storageId: %d for productId: %d",
                    existingStorage.getStorage_stock(), storageId, productId), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("An error occurred while updating the storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


        private ResponseEntity<String> createNewStorage(Stock stock) {
        try {
            Storage storage = new Storage(stock.getId(), stock.getAmount(), stock.getProductId());
            Integer generatedKey = storageJdbcRepository.createStorage(storage);
            String jsonResponse = String.format("{\"storage_id\": %d}", generatedKey);
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>("An error occurred while creating new storage", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
