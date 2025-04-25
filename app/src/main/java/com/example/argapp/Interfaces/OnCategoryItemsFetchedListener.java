package com.example.argapp.Interfaces;

import com.example.argapp.Classes.Item;

import java.util.List;

public interface OnCategoryItemsFetchedListener {
    void onCategoryItemsFetched(List<Item> categoryItems);
}
