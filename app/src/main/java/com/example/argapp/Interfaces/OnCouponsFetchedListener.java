package com.example.argapp.Interfaces;

import com.example.argapp.Classes.Coupon;

import java.util.List;

public interface OnCouponsFetchedListener {
    void onCouponsFetched(List<Coupon> coupons);
}
