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
            String jsonResponse = String.format("{\"id\": %d}", product.getId());
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (SQLException sqlException) {
            logger.error(sqlException.getMessage());
            return new ResponseEntity<>(String.format(sqlException.getMessage()),HttpStatus.INTERNAL_SERVER_ERROR);
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

    @PostMapping(path="/removeStock", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> removeProductStock(@Valid @RequestBody Stock stock) {
        try {
            if (!isProductPresent(stock.getProductId())) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            Storage storage = storageJdbcRepository.findStorageById(stock.getId());
            if(storage != null) {
                return decreaseExistingStorage(stock.getProductId(), stock.getId(), stock, storage);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (SQLException sqlException) {
            logger.error("Error fetching/updating storage: " + sqlException.getMessage());
            return new ResponseEntity<>(String.format("SQL Error %s occurred", sqlException.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path="/getTotalStockForProduct/{productId}")
    public ResponseEntity<String> getTotalStockForProduct(@PathVariable Integer productId) {
        try {
            if (!isProductPresent(productId)) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            String jsonResponse = String.format("{\"totalStock\": %d}", storageJdbcRepository.getTotalStockForProduct(productId));
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
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
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            existingStorage.setStorage_stock(stock.getAmount() + existingStorage.getStorage_stock());
            storageJdbcRepository.updateStorage(existingStorage, storageId);
            String jsonResponse = String.format("{\"storage_id\": %d, \"storage_amount\": %d}", storageId, existingStorage.getStorage_stock());
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<String> decreaseExistingStorage(Integer productId, Integer storageId, Stock stock, Storage existingStorage) {
        if (!existingStorage.getProduct_id().equals(productId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(existingStorage.getStorage_stock() < stock.getAmount()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            existingStorage.setStorage_stock(existingStorage.getStorage_stock() - stock.getAmount());
            if(existingStorage.getStorage_stock() == 0) {
                storageJdbcRepository.deleteStorage(storageId);
                String jsonResponse = String.format("{\"storage_id\": %d, \"storage_amount\": 0}", storageId);
                return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
            }
            storageJdbcRepository.updateStorage(existingStorage, storageId);
            String jsonResponse = String.format("{\"storage_id\": %d, \"storage_amount\": %d}", storageId, existingStorage.getStorage_stock());
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Unexpected error: " + e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


        private ResponseEntity<String> createNewStorage(Stock stock) {
        try {
            Storage storage = new Storage(stock.getId(), stock.getAmount(), stock.getProductId());
            Integer generatedKey = storageJdbcRepository.createStorage(storage);
            String jsonResponse = String.format("{\"storage_id\": %d, \"storage_amount\": %d}", generatedKey, storage.getStorage_stock());
            return new ResponseEntity<>(jsonResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
