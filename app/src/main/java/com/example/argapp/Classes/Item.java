package com.example.argapp.Classes;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable {
    private String m_Id;
    private String m_Type;
    private String m_Name;
    private double m_Price;
    private String m_Image;
    private int m_Quantity;
    private String description;  // Trường mô tả sản phẩm
    private String unit;         // Đơn vị tính (kg, g, cái, chai, v.v.)

    /**
     * Constructor mặc định cho Item
     * Sử dụng khi cần tạo một đối tượng Item mà không cần khởi tạo các trường
     */

    public Item() {

    }

    public Item(String i_Name, double i_Price, int i_Quantity, String i_Image) {
        this.m_Name = i_Name;
        this.m_Price = i_Price;
        this.m_Quantity = i_Quantity;
        this.m_Image = i_Image;
        this.description = "";
        this.unit = "";
    }

    // Thêm constructor mới có đủ các trường
    public Item(String i_Id, String i_Type, String i_Name, double i_Price, int i_Quantity, String i_Image, String description, String unit) {
        this.m_Id = i_Id;
        this.m_Type = i_Type;
        this.m_Name = i_Name;
        this.m_Price = i_Price;
        this.m_Quantity = i_Quantity;
        this.m_Image = i_Image;
        this.description = description;
        this.unit = unit;
    }

    public String getId() {
        return m_Id;
    }

    public String getType() {
        return m_Type;
    }

    public String getName() {
        return m_Name;
    }

    public void setName(String i_Name) {
        this.m_Name = i_Name;
    }

    public double getPrice() {
        return m_Price;
    }

    public void setPrice(double i_Price) {
        this.m_Price = i_Price;
    }

    public int getQuantity() {
        return m_Quantity;
    }

    public void setQuantity(int i_Quantity) {
        this.m_Quantity = i_Quantity;
    }

    public String getImage() {
        return m_Image;
    }

    public void setImage(String i_Image) {
        this.m_Image = i_Image;
    }

    // Thêm các getter và setter cho các trường mới
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return m_Name.equals(item.getName()); // Compare relevant fields
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_Name); // Use relevant fields for hashCode
    }
}

