package com.example.argapp.Classes;

public class OrderBillItem {
    private String productKey;     // key như "item1", "item2"
    private String productName;
    private double salePrice; // giá bán của sản phẩm
    private int quantity;
    private String unit;
    private String image;

    // Constructor mặc định cần thiết cho Firebase
    public OrderBillItem() {
    }

    // Constructor đầy đủ
    public OrderBillItem(String productKey, String productName, double salePrice, int quantity, String unit, String image) {
        this.productKey = productKey;
        this.productName = productName;
        this.salePrice = salePrice;
        this.quantity = quantity;
        this.unit = unit;
        this.image = image;
    }

    // Getters và Setters
    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
