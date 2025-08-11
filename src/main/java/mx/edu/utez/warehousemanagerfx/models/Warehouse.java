package mx.edu.utez.warehousemanagerfx.models;

import mx.edu.utez.warehousemanagerfx.Branch;

import java.time.LocalDate;

public class Warehouse {
    // Attributes
    private int idWarehouse;
    private String warehouseCode;
    private LocalDate registrationDate;
    private String warehouseName;
    private String image;
    private double rentalPrice;
    private double salePrice;
    private double sizeSqMeters;
    private String status;
    private Branch branch; // reference to Branch

    // Constructor Methods
    public Warehouse() {
    }

    public Warehouse(int idWarehouse, String warehouseCode, LocalDate registrationDate,
                     String warehouseName, String image, double rentalPrice, double salePrice,
                     double sizeSqMeters, String status, Branch branch) {
        this.idWarehouse = idWarehouse;
        this.warehouseCode = warehouseCode;
        this.registrationDate = registrationDate;
        this.warehouseName = warehouseName;
        this.image = image;
        this.rentalPrice = rentalPrice;
        this.salePrice = salePrice;
        this.sizeSqMeters = sizeSqMeters;
        this.status = status;
        this.branch = branch;
    }

    // Class Methods
    @Override
    public String toString() {
        return "Warehouse{" +
                "idWarehouse=" + idWarehouse +
                ", warehouseCode='" + warehouseCode + '\'' +
                ", registrationDate=" + registrationDate +
                ", warehouseName='" + warehouseName + '\'' +
                ", image='" + image + '\'' +
                ", rentalPrice=" + rentalPrice +
                ", salePrice=" + salePrice +
                ", sizeSqMeters=" + sizeSqMeters +
                ", status='" + status + '\'' +
                ", branch=" + branch +
                '}';
    }

    // Getters & Setters
    public int getIdWarehouse() {
        return idWarehouse;
    }

    public void setIdWarehouse(int idWarehouse) {
        this.idWarehouse = idWarehouse;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getRentalPrice() {
        return rentalPrice;
    }

    public void setRentalPrice(double rentalPrice) {
        this.rentalPrice = rentalPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public double getSizeSqMeters() {
        return sizeSqMeters;
    }

    public void setSizeSqMeters(double sizeSqMeters) {
        this.sizeSqMeters = sizeSqMeters;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
