package mx.edu.utez.warehousemanagerfx.Models.Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchDao {
    public List<Branch> findAll() throws SQLException {
        List<Branch> branches = new ArrayList<>();
        String query = "SELECT branch_id, rent, sell, available, expired, admin_id FROM branches";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                branches.add(new Branch(
                        rs.getInt("branch_id"),
                        rs.getBoolean("rent"),
                        rs.getBoolean("sell"),
                        rs.getBoolean("available"),
                        rs.getBoolean("expired"),
                        rs.getInt("admin_id")
                ));
            }
        }
        return branches;
    }

    public boolean create(Branch branch) throws SQLException {
        String query = "INSERT INTO branches (branch_id, rent, sell, available, expired, admin_id) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, branch.getBranchId());
            pstmt.setBoolean(2, branch.isRent());
            pstmt.setBoolean(3, branch.isSell());
            pstmt.setBoolean(4, branch.isAvailable());
            pstmt.setBoolean(5, branch.isExpired());
            pstmt.setInt(6, branch.getAdminId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean update(Branch branch) throws SQLException {
        String query = "UPDATE branches SET rent = ?, sell = ?, available = ?, expired = ?, admin_id = ? WHERE branch_id = ?";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, branch.isRent());
            pstmt.setBoolean(2, branch.isSell());
            pstmt.setBoolean(3, branch.isAvailable());
            pstmt.setBoolean(4, branch.isExpired());
            pstmt.setInt(5, branch.getAdminId());
            pstmt.setInt(6, branch.getBranchId());

            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean delete(int branchId) throws SQLException {
        String query = "DELETE FROM branches WHERE branch_id = ?";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, branchId);
            return pstmt.executeUpdate() > 0;
        }
    }
}
