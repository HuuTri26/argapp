package com.example.argapp.Classes;

import java.util.Map;

public class OrderBill {
    private String orderBillId;
    private String userUid; // chính là key như "26bHh7N..."
    private long orderDate;
    private String status;
    private double totalPrice;
    private Map<String, OrderBillItem> items;
    private String address;
    private String phoneNumber;

    public OrderBill() {
    }

    public OrderBill(String userUid, long orderDate, String status, double totalPrice) {
        this.userUid = userUid;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
    }



    public OrderBill(String userUid, long orderDate, String status, double totalPrice, Map<String, OrderBillItem> items) {
        this.userUid = userUid;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public String getOrderBillId() {
        return orderBillId;
    }

    public void setOrderBillId(String orderBillId) {
        this.orderBillId = orderBillId;
    }
    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public long getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(long orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Map<String, OrderBillItem> getItems() {
        return items;
    }

    public void setItems(Map<String, OrderBillItem> items) {
        this.items = items;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
