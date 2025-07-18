package mx.edu.utez.warehousemanagerfx.Models.Dao;

public class Branch {
    private int branchId;
    private boolean rent;
    private boolean sell;
    private boolean available;
    private boolean expired;
    private int adminId;

    public Branch(int branchId, boolean rent, boolean sell, boolean available, boolean expired, int adminId) {
        this.branchId = branchId;
        this.rent = rent;
        this.sell = sell;
        this.available = available;
        this.expired = expired;
        this.adminId = adminId;
    }

    // Getters y Setters
    public int getBranchId() {
        return branchId;
    }

    public void setBranchId(int branchId) {
        this.branchId = branchId;
    }

    public boolean isRent() {
        return rent;
    }

    public void setRent(boolean rent) {
        this.rent = rent;
    }

    public boolean isSell() {
        return sell;
    }

    public void setSell(boolean sell) {
        this.sell = sell;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
}
