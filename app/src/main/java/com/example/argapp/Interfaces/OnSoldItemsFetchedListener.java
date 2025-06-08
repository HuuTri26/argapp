package com.example.argapp.Interfaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OnSoldItemsFetchedListener {
    public void onSuccess();
    public void onFailure(String errorMessage);
}
