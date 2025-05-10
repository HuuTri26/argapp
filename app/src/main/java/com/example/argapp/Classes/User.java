package com.example.argapp.Classes;

public class User {
    // Field names for Firebase serialization/deserialization 
    private String FirstName; 
    private String LastName;  
    private String Password;  
    private String Email;     
    private String PhoneNumber; 
    private String avatar;
    private String address;
    
    // These fields are for maintaining compatibility with existing code
    private transient String m_FirstName;
    private transient String m_LastName;
    private transient String m_Password;
    private transient String m_Email;
    private transient String m_PhoneNumber;

    public User()
    {
        // Required empty constructor for Firebase
    }

    public User(String i_FirstName, String i_LastName, String i_Password,
                String i_Email, String i_PhoneNumber)
    {
        this.FirstName = i_FirstName;
        this.LastName = i_LastName;
        this.Password = i_Password;
        this.Email = i_Email;
        this.PhoneNumber = i_PhoneNumber;
        
        // Keep compatibility fields in sync
        this.m_FirstName = i_FirstName;
        this.m_LastName = i_LastName;
        this.m_Password = i_Password;
        this.m_Email = i_Email;
        this.m_PhoneNumber = i_PhoneNumber;
    }


    public User(String m_FirstName, String m_LastName, String m_Password, String m_Email, String m_PhoneNumber, String m_avatar, String m_address) {
        this.FirstName = m_FirstName;
        this.LastName = m_LastName;
        this.Password = m_Password;
        this.Email = m_Email;
        this.PhoneNumber = m_PhoneNumber;
        this.avatar = m_avatar;
        this.address = m_address;
        
        // Keep compatibility fields in sync
        this.m_FirstName = m_FirstName;
        this.m_LastName = m_LastName;
        this.m_Password = m_Password;
        this.m_Email = m_Email;
        this.m_PhoneNumber = m_PhoneNumber;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public String getEmail() {
        return Email;
    }

    public String getPassword() {
        return Password;
    }

    public String getLastName() {
        return LastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String i_FirstName) {
        this.FirstName = i_FirstName;
        this.m_FirstName = i_FirstName;
    }

    public void setLastName(String i_LastName) {
        this.LastName = i_LastName;
        this.m_LastName = i_LastName;
    }

    public void setPassword(String i_Password) {
        this.Password = i_Password;
        this.m_Password = i_Password;
    }

    public void setEmail(String i_Email) {
        this.Email = i_Email;
        this.m_Email = i_Email;
    }

    public void setPhoneNumber(String i_PhoneNumber) {
        this.PhoneNumber = i_PhoneNumber;
        this.m_PhoneNumber = i_PhoneNumber;
    }

    public String getM_avatar() {
        return avatar;
    }

    public void setM_avatar(String m_avatar) {
        this.avatar = m_avatar;
    }

    public String getM_address() {
        return address;
    }

    public void setM_address(String m_address) {
        this.address = m_address;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "firstName='" + FirstName + '\'' +
                ", lastName='" + LastName + '\'' +
                ", email='" + Email + '\'' +
                ", phone='" + PhoneNumber + '\'' +
                '}';
    }
}