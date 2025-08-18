package mx.edu.utez.warehousemanagerfx.models;

import java.time.LocalDate;

public class WarehouseTransaction {
    private int idTransaction;
    private String transactionType;
    private LocalDate transactionDate;
    private LocalDate paymentExpirationDate;
    private int idWarehouse;
    private int idClient;
    private int idAdmin;

    public WarehouseTransaction() {}

    public WarehouseTransaction(int idTransaction, String transactionType, LocalDate transactionDate, LocalDate paymentExpirationDate,
                                int idWarehouse, int idClient, int idAdmin) {
        this.idTransaction = idTransaction;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.paymentExpirationDate = paymentExpirationDate;
        this.idWarehouse = idWarehouse;
        this.idClient = idClient;
        this.idAdmin = idAdmin;
    }

    public int getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(int idTransaction) {
        this.idTransaction = idTransaction;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public LocalDate getPaymentExpirationDate() {
        return paymentExpirationDate;
    }

    public void setPaymentExpirationDate(LocalDate paymentExpirationDate) {
        this.paymentExpirationDate = paymentExpirationDate;
    }

    public int getIdWarehouse() {
        return idWarehouse;
    }

    public void setIdWarehouse(int idWarehouse) {
        this.idWarehouse = idWarehouse;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    @Override
    public String toString() {
        return "WarehouseTransaction{" +
                "idTransaction=" + idTransaction +
                ", transactionType='" + transactionType + '\'' +
                ", transactionDate=" + transactionDate +
                ", paymentExpirationDate=" + paymentExpirationDate +
                ", idWarehouse=" + idWarehouse +
                ", idClient=" + idClient +
                ", idAdmin=" + idAdmin +
                '}';
    }
}