package mx.edu.utez.warehousemanagerfx.models;

public class Warehouse {
    private String imgSrc;
    private String name;
    private int price;
    private int size;
    private String status;

    public Warehouse() {
    }

    public Warehouse(String imgSrc, String name, int price, int size, String status) {
        this.imgSrc = imgSrc;
        this.name = name;
        this.price = price;
        this.size = size;
        this.status = status;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
