package com.example.argapp.Interfaces;

import com.example.argapp.Classes.Item;

public interface OnShoppingCartItemListener {
    public void onRemoveItem(Item item);
    public void onQuantitySelected(Item item);
    public void onItemClicked(Item item);
}
