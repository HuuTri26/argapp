package com.example.argapp.Classes;

public class Category {
    private String m_Name;
    private String m_Image;
    private String m_Id;
    private String m_Season;

    public Category(String i_Id, String i_Name, String i_Image, String i_Season) {
        this.m_Id = i_Id;
        this.m_Name = i_Name;
        this.m_Image = i_Image;
        this.m_Season = i_Season;
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

    public String getSeason() {
        return m_Season != null ? m_Season : "";
    }
}