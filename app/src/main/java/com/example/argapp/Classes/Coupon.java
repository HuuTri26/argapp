package com.example.argapp.Classes;

import java.util.Date;

public class Coupon {
    private String m_Id;
    private String m_productId;
    private String m_Type;
    private Double m_DiscountValue;
    private Date m_StartDate;
    private Date m_EndDate;
    private String m_Description;

    public Coupon() {
        // Constructor mặc định
    }

    public Coupon(String i_Id, String i_productId, String i_Type, Double i_DiscountValue, Date i_StartDate, Date i_EndDate, String i_Description) {
        this.m_Id = i_Id;
        this.m_productId = i_productId;
        this.m_Type = i_Type;
        this.m_DiscountValue = i_DiscountValue;
        this.m_StartDate = i_StartDate;
        this.m_EndDate = i_EndDate;
        this.m_Description = i_Description;
    }
    public String getId() {
        return m_Id;
    }
    public String getProductId() {
        return m_productId;
    }
    public String getType() {
        return m_Type;
    }
    public Double getDiscountValue() {
        return m_DiscountValue;
    }
    public Date getStartDate() {
        return m_StartDate;
    }
    public Date getEndDate() {
        return m_EndDate;
    }
    public String getDescription() {
        return m_Description;
    }

}
