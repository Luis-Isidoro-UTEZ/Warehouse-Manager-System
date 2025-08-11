package mx.edu.utez.warehousemanagerfx.models;

import java.time.LocalDate;

public class Branch extends Property {
    // Attributes
    private int idBranch;
    private String branchCode;
    private LocalDate registrationDate;

    // Constructor Methods
    public Branch() {
    }

    public Branch(int idProperty, String propertyType, String state, String municipality, String neighborhood, String addressDetail,
                  int idBranch, String branchCode, LocalDate registrationDate) {
        super(idProperty, propertyType, state, municipality, neighborhood, addressDetail);
        this.idBranch = idBranch;
        this.branchCode = branchCode;
        this.registrationDate = registrationDate;
    }

    // Class Methods
    @Override
    public String toString() {
        return "Branch{" +
                "idBranch=" + idBranch +
                ", branchCode='" + branchCode + '\'' +
                ", registrationDate=" + registrationDate + '\'' +
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
}
