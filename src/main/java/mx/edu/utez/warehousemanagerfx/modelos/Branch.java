package mx.edu.utez.warehousemanagerfx.modelos;

import java.time.LocalDate;

public class Branch extends Property {
    // Attributes
    private int idBranch;
    private String branchCode;
    private LocalDate registrationDate;
    private int availableCount;
    private int rentedCount;
    private int soldCount;

    // Constructor Methods
    public Branch() {
    }

    public Branch(int idProperty, String propertyType, String country, String state, String municipality, String postalCode, String neighborhood, String addressDetail,
                  int idBranch, String branchCode, LocalDate registrationDate) {
        super(idProperty, propertyType, country, state, municipality, postalCode, neighborhood, addressDetail);
        this.idBranch = idBranch;
        this.branchCode = branchCode;
        this.registrationDate = registrationDate;
        this.availableCount = 0;
        this.rentedCount = 0;
        this.soldCount = 0;
    }

    // Class Methods
    @Override
    public String toString() {
        return "Branch{" +
                "idBranch=" + idBranch +
                ", branchCode='" + branchCode + '\'' +
                ", registrationDate=" + registrationDate + '\'' +
                ", availableCount=" + availableCount +
                ", rentedCount=" + rentedCount +
                ", soldCount=" + soldCount +
                '}';
    }

    // Getters y setters
    public int getIdBranch() {
        return idBranch;
    }

    public void setIdBranch(int idBranch) {
        this.idBranch = idBranch;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public int getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(int availableCount) {
        this.availableCount = availableCount;
    }

    public int getRentedCount() {
        return rentedCount;
    }

    public void setRentedCount(int rentedCount) {
        this.rentedCount = rentedCount;
    }

    public int getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(int soldCount) {
        this.soldCount = soldCount;
    }
}
