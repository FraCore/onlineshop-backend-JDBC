package com.onlineshopbackend.database.interfaces;


import com.onlineshopbackend.model.Storage;

import java.sql.SQLException;
import java.util.List;

public interface StorageRepository {

    Storage findStorageById(Integer id) throws SQLException;

    Integer createStorage(Storage storage) throws SQLException;

    void updateStorage(Storage storage, Integer storageId) throws SQLException;

    void deleteStorage(Integer storageId) throws  SQLException;

    Integer getTotalStockForProduct(Integer product_id) throws  SQLException;

    List<Storage> getAllStoragesForId(Integer product_id) throws SQLException;
}
