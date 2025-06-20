package com.example.argapp.Classes;

public class User {
    private String m_FirstName;
    private String m_LastName;
    private String m_Password;
    private String m_Email;
    private String m_PhoneNumber;
    private String m_avatar;
    private String m_address;
    private String m_role; // Thêm trường role

    public User() {
        m_role = "user"; // Mặc định là user
    }

    public User(String i_FirstName, String i_LastName, String i_Password,
                String i_Email, String i_PhoneNumber) {
        m_FirstName = i_FirstName;
        m_LastName = i_LastName;
        m_Password = i_Password;
        m_Email = i_Email;
        m_PhoneNumber = i_PhoneNumber;
        m_role = "user"; // Mặc định là user
    }

    public User(String m_FirstName, String m_LastName, String m_Password, String m_Email,
                String m_PhoneNumber, String m_avatar, String m_address) {
        this.m_FirstName = m_FirstName;
        this.m_LastName = m_LastName;
        this.m_Password = m_Password;
        this.m_Email = m_Email;
        this.m_PhoneNumber = m_PhoneNumber;
        this.m_avatar = m_avatar;
        this.m_address = m_address;
        this.m_role = "user"; // Mặc định là user
    }

    public User(String m_FirstName, String m_LastName, String m_Password, String m_Email,
                String m_PhoneNumber, String m_avatar, String m_address, String m_role) {
        this.m_FirstName = m_FirstName;
        this.m_LastName = m_LastName;
        this.m_Password = m_Password;
        this.m_Email = m_Email;
        this.m_PhoneNumber = m_PhoneNumber;
        this.m_avatar = m_avatar;
        this.m_address = m_address;
        this.m_role = m_role;
    }

    // Getters
    public String getPhoneNumber() { return m_PhoneNumber; }
    public String getEmail() { return m_Email; }
    public String getPassword() { return m_Password; }
    public String getLastName() { return m_LastName; }
    public String getFirstName() { return m_FirstName; }
    public String getAvatar() { return m_avatar; }
    public String getAddress() { return m_address; }
    public String getRole() { return m_role; }

    // Setters
    public void setFirstName(String i_FirstName) { this.m_FirstName = i_FirstName; }
    public void setLastName(String i_LastName) { this.m_LastName = i_LastName; }
    public void setPassword(String i_Password) { this.m_Password = i_Password; }
    public void setEmail(String i_Email) { this.m_Email = i_Email; }
    public void setPhoneNumber(String i_PhoneNumber) { this.m_PhoneNumber = i_PhoneNumber; }
    public void setAvatar(String m_avatar) { this.m_avatar = m_avatar; }
    public void setAddress(String m_address) { this.m_address = m_address; }
    public void setRole(String m_role) { this.m_role = m_role; }

    // Helper methods
    public boolean isAdmin() {
        return "admin".equals(m_role);
    }

    public boolean isUser() {
        return "user".equals(m_role);
    }
}