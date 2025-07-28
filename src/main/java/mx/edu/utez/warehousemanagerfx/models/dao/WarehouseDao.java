package mx.edu.utez.warehousemanagerfx.models.dao;

import mx.edu.utez.warehousemanagerfx.models.Warehouse;
import mx.edu.utez.warehousemanagerfx.utils.database.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WarehouseDao {

    // Methods CRUD

    // Read
    /**
     * Flexible method to obtain warehouses according to a combination of filters:
     * @param keyword     keyword for multi-field search
     * @param status      status filter, "Show All/Status" to skip
     * @param minPrice    minimum price
     * @param maxPrice    maximum price
     * @param minSize     minimum size
     * @param maxSize     maximum size
     * @param priceRental true=filter RENTALPRICE; false=filter SALEPRICE
     * @param orderColumn column for ORDER BY
     * @param orderDir    "ASC" o "DESC"
     * @param useKeyword      if it includes a keyword filter
     * @param useStatus   if it includes a status filter
     * @param useSlider   if it includes price/size filters
     */
    public List<Warehouse> readWarehouses(
            String keyword,
            String status,
            double minPrice,
            double maxPrice,
            double minSize,
            double maxSize,
            boolean priceRental,
            String orderColumn,
            String orderDir,
            boolean useKeyword,
            boolean useStatus,
            boolean useSlider
    ) {
        List<Warehouse> warehouses = new ArrayList<>();
        String priceCol = priceRental ? "RENTALPRICE" : "SALEPRICE";

        StringBuilder query = new StringBuilder("SELECT * FROM WAREHOUSES WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (useKeyword) {
            query.append(" AND (WAREHOUSE LIKE ? OR RENTALPRICE LIKE ? OR SALEPRICE LIKE ? OR SIZEQMETERS LIKE ? OR STATUS LIKE ?)");
            String like = "%" + keyword + "%";
            for (int i = 0; i < 5; i++) params.add(like);
        }
        if (useStatus) {
            query.append(" AND STATUS = ?");
            params.add(status);
        }
        if (useSlider) {
            query.append(" AND ").append(priceCol).append(" BETWEEN ? AND ?");
            query.append(" AND SIZEQMETERS BETWEEN ? AND ?");
            params.add(minPrice);
            params.add(maxPrice);
            params.add(minSize);
            params.add(maxSize);
        }
        query.append(" ORDER BY ").append(orderColumn).append(" ").append(orderDir);

        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return warehouses;
    }

    // Read to get MIN/MAX
    /**
     * Method to obtain the minimum and maximum size/price of the warehouses table
     * @return array with [minimum, maximum]
     * @throws SQLException if the query fails
     */
    public double[] getMinMax(String columnName) {
        List<String> allowedColumns = List.of("SIZEQMETERS", "RENTALPRICE", "SALEPRICE");
        if (!allowedColumns.contains(columnName.toUpperCase())) {
            throw new IllegalArgumentException("Invalid column name: " + columnName);
        }
        double min = 0, max = 0;
        String query = "SELECT MIN(" + columnName + ") AS min_value, MAX(" + columnName + ") AS max_value FROM WAREHOUSES";
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnectionLocal();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                min = rs.getDouble("min_value");
                max = rs.getDouble("max_value");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new double[]{min, max};
    }
}
