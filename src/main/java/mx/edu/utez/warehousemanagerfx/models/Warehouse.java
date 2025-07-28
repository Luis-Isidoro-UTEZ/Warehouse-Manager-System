package mx.edu.utez.warehousemanagerfx.models;

public class Warehouse {

    // Attributes

    private int id;
    private String imgSrc;
    private String name;
    private double rentalPrice;
    private double salePrice;
    private double size;
    private String status;

    // Constructor

    public Warehouse() {
    }

    public Warehouse(int id, String imgSrc, String name, double rentalPrice, double salePrice, double size, String status) {
        this.id = id;
        this.imgSrc = imgSrc;
        this.name = name;
        this.rentalPrice = rentalPrice;
        this.salePrice = salePrice;
        this.size = size;
        this.status = status;
    }

    // Getters & Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
