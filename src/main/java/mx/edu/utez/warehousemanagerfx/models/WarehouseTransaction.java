package mx.edu.utez.warehousemanagerfx.models;

import java.time.LocalDate;

public class WarehouseTransaction {
    private int idTransaction;
    private String transactionType;
    private LocalDate transactionDate;
    private LocalDate paymentExpirationDate;
    private Warehouse warehouse;
    private Client client;
    private Administrator admin;

    public WarehouseTransaction() {}

    public WarehouseTransaction(int idTransaction, String transactionType, LocalDate transactionDate, LocalDate paymentExpirationDate,
                                Warehouse warehouse, Client client, Administrator admin) {
        this.idTransaction = idTransaction;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.paymentExpirationDate = paymentExpirationDate;
        this.warehouse = warehouse;
        this.client = client;
        this.admin = admin;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Administrator getAdmin() {
        return admin;
    }

    public void setAdmin(Administrator admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "WarehouseTransaction{" +
                "idTransaction=" + idTransaction +
                ", transactionType='" + transactionType + '\'' +
                ", transactionDate=" + transactionDate +
                ", paymentExpirationDate=" + paymentExpirationDate +
                ", warehouse=" + (warehouse != null ? warehouse.getWarehouseCode() : "null") +
                ", client=" + (client != null ? client.getFullName() : "null") +
                ", admin=" + (admin != null ? admin.getFullName() : "null") +
                '}';
    }
}