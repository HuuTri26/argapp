package com.example.argapp.Classes;

public class Category {
    private String m_Name;
    private String m_Image;
    private String m_Id;

    public Category(String i_Id, String i_Name, String i_Image) {
        this.m_Id = i_Id;
        this.m_Name = i_Name;
        this.m_Image = i_Image;
    }

    public String getId() {
        return m_Id;
    }

    public String getName() {
        return m_Name;
    }

    public String getImage() {
        return m_Image;
    }
}
