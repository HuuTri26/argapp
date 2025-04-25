package com.example.argapp.Interfaces;

import com.example.argapp.Classes.Item;

public interface OnItemListener
{
    public void onItemClick(Item item);
    public void onLikeClick(Item item);
    public void onAddToCartClick(Item item);

}
