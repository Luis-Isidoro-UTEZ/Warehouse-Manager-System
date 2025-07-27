package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDao {

    // Metodos CRUD

    // Read
    public List<Warehouse> readWarehouses(String orderColumn, String clause) {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM WAREHOUSES ORDER BY " + orderColumn + " " + clause;
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
    public List<Warehouse> readWarehousesStatus(String status, String orderColumn, String clause) {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM WAREHOUSES WHERE STATUS LIKE ? ORDER BY " + orderColumn + " " + clause;
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, status);
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
    public List<Warehouse> readWarehousesSearch(String keyword, String orderColumn, String clause) {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM WAREHOUSES WHERE WAREHOUSE LIKE ? OR RENTALPRICE LIKE ? OR SALEPRICE LIKE ? OR SIZEQMETERS LIKE ? OR STATUS LIKE ? ORDER BY " + orderColumn + " " + clause;
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
    public List<Warehouse> readWarehousesSearchAndStatus(String keyword, String status, String orderColumn, String clause) {
        List<Warehouse> warehouses = new ArrayList<>();
        String query = "SELECT * FROM WAREHOUSES WHERE (WAREHOUSE LIKE ? OR RENTALPRICE LIKE ? OR SALEPRICE LIKE ? OR SIZEQMETERS LIKE ?) AND STATUS LIKE ? ORDER BY " + orderColumn + " " + clause;
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+keyword+"%");
            ps.setString(2, "%"+keyword+"%");
            ps.setString(3, "%"+keyword+"%");
            ps.setString(4, "%"+keyword+"%");
            ps.setString(5, status);
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

    // Read by Price and Size Range Sliders
    public List<Warehouse> readWarehousesPriceAndSize(double minPrice, double maxPrice, double minSize, double maxSize, boolean priceType, String orderColumn, String clause) {
        List<Warehouse> warehouses = new ArrayList<>();
        String priceColumn = priceType ? "RENTALPRICE" : "SALEPRICE";
        String query = "SELECT * FROM WAREHOUSES WHERE (" + priceColumn + " BETWEEN ? AND ?) AND (SIZEQMETERS BETWEEN ? AND ?) ORDER BY " + orderColumn + " " + clause;
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ps.setDouble(3, minSize);
            ps.setDouble(4, maxSize);
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

    // Read by Price and Size Range Sliders + Search (With Keyword)
    public List<Warehouse> readWarehousesPriceSizeAndSearch(String keyword, double minPrice, double maxPrice, double minSize, double maxSize, boolean priceType, String orderColumn, String clause) {
        List<Warehouse> warehouses = new ArrayList<>();
        String priceColumn = priceType ? "RENTALPRICE" : "SALEPRICE";
        String query = "SELECT * FROM WAREHOUSES WHERE WAREHOUSE LIKE ? AND (" + priceColumn + " BETWEEN ? AND ?) AND (SIZEQMETERS BETWEEN ? AND ?) ORDER BY " + orderColumn + " " + clause;
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + keyword + "%");
            ps.setDouble(2, minPrice);
            ps.setDouble(3, maxPrice);
            ps.setDouble(4, minSize);
            ps.setDouble(5, maxSize);
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

    // Read by Price and Size Range Sliders + Status
    public List<Warehouse> readWarehousesPriceSizeAndStatus(double minPrice, double maxPrice, double minSize, double maxSize, boolean priceType, String status, String orderColumn, String clause) {
        List<Warehouse> warehouses = new ArrayList<>();
        String priceColumn = priceType ? "RENTALPRICE" : "SALEPRICE";
        String query = "SELECT * FROM WAREHOUSES WHERE (" + priceColumn + " BETWEEN ? AND ?) AND (SIZEQMETERS BETWEEN ? AND ?) AND STATUS LIKE ? ORDER BY " + orderColumn + " " + clause;
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ps.setDouble(3, minSize);
            ps.setDouble(4, maxSize);
            ps.setString(5, status);
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

    // Read with all filters
    public List<Warehouse> readWarehousesFiltered(String keyword, double minPrice, double maxPrice, double minSize, double maxSize, String status, boolean priceType, String orderColumn, String clause) {
        List<Warehouse> warehouses = new ArrayList<>();
        String priceColumn = priceType ? "RENTALPRICE" : "SALEPRICE";
        String query = "SELECT * FROM WAREHOUSES WHERE WAREHOUSE LIKE ? AND " +
                priceColumn + " BETWEEN ? AND ? AND SIZEQMETERS BETWEEN ? AND ? AND STATUS = ? ORDER BY " + orderColumn + " " + clause;
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + keyword + "%");
            ps.setDouble(2, minPrice);
            ps.setDouble(3, maxPrice);
            ps.setDouble(4, minSize);
            ps.setDouble(5, maxSize);
            ps.setString(6, status);
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

    // Method to obtain the minimum and maximum size in m2 of the warehouses table
    public double[] getMinMaxSize() {
        double min = 0, max = 0;
        String sql = "SELECT MIN(SIZEQMETERS) AS min_size, MAX(SIZEQMETERS) AS max_size FROM WAREHOUSES";
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                min = rs.getDouble("min_size");
                max = rs.getDouble("max_size");
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new double[]{min, max};
    }

    // Method to obtain the minimum and maximum price of the warehouses table
    public double[] getMinMaxPrice(boolean priceType) {
        double min = 0, max = 0;
        String priceColumn = priceType ? "RENTALPRICE" : "SALEPRICE";
        String sql = "SELECT MIN(" + priceColumn + ") AS min_price, MAX(" + priceColumn + ") AS max_price FROM WAREHOUSES";
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                min = rs.getDouble("min_price");
                max = rs.getDouble("max_price");
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new double[]{min, max};
    }
}
