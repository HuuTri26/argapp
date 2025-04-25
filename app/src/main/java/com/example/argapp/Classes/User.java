package com.example.argapp.Classes;
public class User {
    private String m_FirstName;
    private String m_LastName;
    private String m_Password;
    private String m_Email;
    private String m_PhoneNumber;
    public User()
    {

    }
    public User(String i_FirstName, String i_LastName, String i_Password,
                String i_Email, String i_PhoneNumber)
    {
        m_FirstName = i_FirstName;
        m_LastName = i_LastName;
        m_Password = i_Password;
        m_Email = i_Email;
        m_PhoneNumber = i_PhoneNumber;
    }

    public String getPhoneNumber() {
        return m_PhoneNumber;
    }

    public String getEmail() {
        return m_Email;
    }

    public String getPassword() {
        return m_Password;
    }

    public String getLastName() {
        return m_LastName;
    }

    public String getFirstName() {
        return m_FirstName;
    }

    public void setFirstName(String i_FirstName) {
        this.m_FirstName = i_FirstName;
    }

    public void setLastName(String i_LastName) {
        this.m_LastName = i_LastName;
    }

    public void setPassword(String i_Password) {
        this.m_Password = i_Password;
    }

    public void setEmail(String i_Email) {
        this.m_Email = i_Email;
    }

    public void setPhoneNumber(String i_PhoneNumber) {
        this.m_PhoneNumber = i_PhoneNumber;
    }
}
