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
}
