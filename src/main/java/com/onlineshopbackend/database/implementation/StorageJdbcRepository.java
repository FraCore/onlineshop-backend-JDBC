package com.onlineshopbackend.database.implementation;

import com.onlineshopbackend.database.DatabaseConnection;
import com.onlineshopbackend.database.interfaces.StorageRepository;
import com.onlineshopbackend.model.Storage;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StorageJdbcRepository implements StorageRepository {

    @Override
    public Storage findStorageById(Integer id) throws SQLException {
        Storage storage = null;
        String sql = "SELECT * FROM storages WHERE storage_id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, id);

            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    storage = new Storage(rs.getInt("storage_stock"),
                            rs.getInt("product_id")
                            );
                }
            }
        }
        return storage;
    }

    @Override
    public Integer createStorage (Storage storage) throws SQLException {
        String sql = "INSERT INTO storages (storage_id, storage_stock, product_id) VALUES (?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, storage.getStorage_id());
            ps.setInt(2, storage.getStorage_stock());
            ps.setInt(3, storage.getProduct_id());

            ps.executeUpdate();

        }
        return storage.getStorage_id();
    }

    @Override
    public void updateStorage(Storage storage, Integer storageId) throws SQLException {
        String sql = "UPDATE storages SET storage_stock = ? , product_id = ? WHERE storage_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, storage.getStorage_stock());
            ps.setInt(2, storage.getProduct_id());
            ps.setInt(3, storageId);

            ps.executeUpdate();
        }
    }

    @Override
    public void deleteStorage(Integer storageId) throws SQLException {
        String sql = "DELETE FROM storages WHERE storage_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, storageId);

            ps.executeUpdate();
        }
    }

    @Override
    public Integer getTotalStockForProduct(Integer product_id) throws SQLException {
        Integer totalStock = 0;
        String sql = "SELECT SUM(storage_stock) as total_stock FROM storages WHERE product_id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, product_id);
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    totalStock = rs.getInt("total_stock");
                }
            }
        }
        return totalStock;
    }

    @Override
    public List<Storage> getAllStoragesForId(Integer product_id) throws SQLException {
        List<Storage> storages = new ArrayList<>();
        String sql = "SELECT * FROM storages WHERE product_id = ?";

        try(Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, product_id);

            try(ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Storage storage = new Storage(
                            rs.getInt("storage_id"),
                            rs.getInt("storage_stock"),
                            rs.getInt("product_id")
                    );
                    storages.add(storage);
                }
            }
        }
        return storages;
    }
}
