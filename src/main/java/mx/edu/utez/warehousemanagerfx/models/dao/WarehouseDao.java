package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDao {

    // Metodos CRUD

    // Read
    public List<Warehouse> readWarehouses() {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM WAREHOUSES ORDER BY ID ASC";
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Warehouse w = new Warehouse();
                w.setId(rs.getInt("ID"));
                w.setName(rs.getString("WAREHOUSE"));
                w.setImgSrc(rs.getString("IMAGE"));
                w.setRentalPrice(rs.getDouble("RENTALPRICE"));
                w.setSalePrice(rs.getDouble("SALEPRICE"));
                w.setSize(rs.getDouble("SIZEQMETERS"));
                w.setStatus(rs.getString("STATUS"));
                warehouses.add(w);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warehouses;
    }

    // Read by Status
    public List<Warehouse> readWarehousesStatus(String status) {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM WAREHOUSES WHERE STATUS LIKE ? ORDER BY ID ASC";
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+status+"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Warehouse w = new Warehouse();
                w.setId(rs.getInt("ID"));
                w.setName(rs.getString("WAREHOUSE"));
                w.setImgSrc(rs.getString("IMAGE"));
                w.setRentalPrice(rs.getDouble("RENTALPRICE"));
                w.setSalePrice(rs.getDouble("SALEPRICE"));
                w.setSize(rs.getDouble("SIZEQMETERS"));
                w.setStatus(rs.getString("STATUS"));
                warehouses.add(w);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warehouses;
    }

    // Read by Search (Keyword Only)
    public List<Warehouse> readWarehousesSearch(String keyword) {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM WAREHOUSES WHERE WAREHOUSE LIKE ? OR RENTALPRICE LIKE ? OR SALEPRICE LIKE ? OR SIZEQMETERS LIKE ? OR STATUS LIKE ? ORDER BY ID ASC";
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+keyword+"%");
            ps.setString(2, "%"+keyword+"%");
            ps.setString(3, "%"+keyword+"%");
            ps.setString(4, "%"+keyword+"%");
            ps.setString(5, "%"+keyword+"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Warehouse w = new Warehouse();
                w.setId(rs.getInt("ID"));
                w.setName(rs.getString("WAREHOUSE"));
                w.setImgSrc(rs.getString("IMAGE"));
                w.setRentalPrice(rs.getDouble("RENTALPRICE"));
                w.setSalePrice(rs.getDouble("SALEPRICE"));
                w.setSize(rs.getDouble("SIZEQMETERS"));
                w.setStatus(rs.getString("STATUS"));
                warehouses.add(w);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warehouses;
    }

    // Read by Search (With Keyword) and Status
    public List<Warehouse> readWarehousesSearchAndStatus(String keyword, String status) {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM WAREHOUSES WHERE (WAREHOUSE LIKE ? OR RENTALPRICE LIKE ? OR SALEPRICE LIKE ? OR SIZEQMETERS LIKE ?) AND STATUS LIKE ? ORDER BY ID ASC";
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+keyword+"%");
            ps.setString(2, "%"+keyword+"%");
            ps.setString(3, "%"+keyword+"%");
            ps.setString(4, "%"+keyword+"%");
            ps.setString(5, "%"+status+"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Warehouse w = new Warehouse();
                w.setId(rs.getInt("ID"));
                w.setName(rs.getString("WAREHOUSE"));
                w.setImgSrc(rs.getString("IMAGE"));
                w.setRentalPrice(rs.getDouble("RENTALPRICE"));
                w.setSalePrice(rs.getDouble("SALEPRICE"));
                w.setSize(rs.getDouble("SIZEQMETERS"));
                w.setStatus(rs.getString("STATUS"));
                warehouses.add(w);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warehouses;
    }
}
