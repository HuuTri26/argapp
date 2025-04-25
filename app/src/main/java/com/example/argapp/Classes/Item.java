package com.example.argapp.Classes;

import java.io.Serializable;
import java.util.Objects;

public class Item implements Serializable {

    private String m_Name;
    private double m_Price;
    private String m_Image;
    private int m_Quantity;

    public Item()
    {

    }

    public Item(String i_Name, double i_Price, int i_Quantity, String i_Image) {
        this.m_Name = i_Name;
        this.m_Price = i_Price;
        this.m_Quantity = i_Quantity;
        this.m_Image = i_Image;
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

