package com.example.argapp.Classes;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.argapp.Interfaces.OnCategoryItemsFetchedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryItemsList
{
    private static List<Item> m_CategoryItems = new ArrayList<>();
    private static List<Item> m_AllItems = new ArrayList<>();
    public static void GetItemsListByCategoryId(String i_CategoryId, Context context, OnCategoryItemsFetchedListener callback)
    {
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;

        m_CategoryItems.clear();
        m_Database = FirebaseDatabase.getInstance();
        m_Ref = m_Database.getReference("Data/CategoriesItems/" + i_CategoryId);

        m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String itemName = itemSnapshot.child("Name").getValue(String.class);
                    Double itemPrice = itemSnapshot.child("Price").getValue(Double.class);
                    String itemImage = itemSnapshot.child("Image").getValue(String.class);
                    String itemDescription = itemSnapshot.child("Description").getValue(String.class);
                    String itemUnit = itemSnapshot.child("Unit").getValue(String.class);
                    int itemInventory = itemSnapshot.child("Inventory").getValue(Integer.class);



                    int itemQuantity = 0;

                    Item item = new Item(itemName, itemPrice, itemQuantity, itemImage, itemInventory);

                    // Thiết lập các trường mới nếu có dữ liệu
                    if (itemDescription != null) {
                        item.setDescription(itemDescription);
                    }
                    if (itemUnit != null) {
                        item.setUnit(itemUnit);
                    }
                    m_CategoryItems.add(item);
                }

                callback.onCategoryItemsFetched(m_CategoryItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                callback.onCategoryItemsFetched(new ArrayList<>());
            }
        });
    }

    public static void GetAllItems(Context context, OnCategoryItemsFetchedListener callback)
    {
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;

        m_AllItems.clear();
        m_Database = FirebaseDatabase.getInstance();
        m_Ref = m_Database.getReference("Data/CategoriesItems");

        m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for(DataSnapshot itemSnapshot : categorySnapshot.getChildren())
                    {
                        String itemName = itemSnapshot.child("Name").getValue(String.class);
                        Double itemPrice = itemSnapshot.child("Price").getValue(Double.class);
                        String itemImage = itemSnapshot.child("Image").getValue(String.class);
                        String itemDescription = itemSnapshot.child("Description").getValue(String.class);
                        String itemUnit = itemSnapshot.child("Unit").getValue(String.class);
                        int itemInventory = itemSnapshot.child("Inventory").getValue(Integer.class);

                        int itemQuantity = 0;

                        Item item = new Item(itemName, itemPrice, itemQuantity, itemImage, itemInventory);
                        // Thiết lập các trường mới nếu có dữ liệu
                        if (itemDescription != null) {
                            item.setDescription(itemDescription);
                        }

                        if (itemUnit != null) {
                            item.setUnit(itemUnit);
                        }
                        m_AllItems.add(item);
                    }
                }

                callback.onCategoryItemsFetched(m_AllItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                callback.onCategoryItemsFetched(new ArrayList<>());
            }
        });
    }
}

