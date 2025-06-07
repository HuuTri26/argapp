package com.example.argapp.Classes;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.argapp.Interfaces.OnCategoryItemsFetchedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
                    String itemId = itemSnapshot.child("Id").getValue(String.class);
                    String itemType = itemSnapshot.child("Type").getValue(String.class);
                    String itemName = itemSnapshot.child("Name").getValue(String.class);
                    Double itemPrice = itemSnapshot.child("Price").getValue(Double.class);
                    String itemImage = itemSnapshot.child("Image").getValue(String.class);
                    String itemDescription = itemSnapshot.child("Description").getValue(String.class);
                    String itemUnit = itemSnapshot.child("Unit").getValue(String.class);


                    int itemQuantity = 0;

                    Item item = new Item(itemId, itemType, itemName, itemPrice, itemQuantity, itemImage, itemDescription, itemUnit);

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
                        int itemQuantity = 0;

                        Item item = new Item(itemName, itemPrice, itemQuantity, itemImage);
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

//    public static void timSanPhamTheoGia(double minPrice, double maxPrice, Context context, OnCategoryItemsFetchedListener callback){
//        FirebaseDatabase m_Database;
//        DatabaseReference m_Ref;
//
//        m_CategoryItems.clear();
//        m_Database = FirebaseDatabase.getInstance();
//        m_Ref = m_Database.getReference("Data/CategoriesItems");
//
//        m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
//                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
//                        String itemName = itemSnapshot.child("Name").getValue(String.class);
//                        Double itemPrice = itemSnapshot.child("Price").getValue(Double.class);
//                        String itemImage = itemSnapshot.child("Image").getValue(String.class);
//                        String itemDescription = itemSnapshot.child("Description").getValue(String.class);
//                        String itemUnit = itemSnapshot.child("Unit").getValue(String.class);
//
//                        int itemQuantity = 0;
//
//                        // Kiểm tra giá trị giá
//                        if (itemPrice != null && itemPrice >= minPrice && itemPrice <= maxPrice) {
//                            Item item = new Item(itemName, itemPrice, itemQuantity, itemImage);
//                            if (itemDescription != null) {
//                                item.setDescription(itemDescription);
//                            }
//                            if (itemUnit != null) {
//                                item.setUnit(itemUnit);
//                            }
//                            m_CategoryItems.add(item);
//                        }
//                    }
//                }
//
//                callback.onCategoryItemsFetched(m_CategoryItems);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
//                callback.onCategoryItemsFetched(new ArrayList<>());
//            }
//        });
//    }

    public static void timSanPhamTheoGia(Double minPrice, Double maxPrice, Context context, OnCategoryItemsFetchedListener callback) {
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;

        m_CategoryItems.clear();
        m_Database = FirebaseDatabase.getInstance();
        m_Ref = m_Database.getReference("Data/CategoriesItems");

        m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                        String itemName = itemSnapshot.child("Name").getValue(String.class);
                        Double itemPrice = itemSnapshot.child("Price").getValue(Double.class);
                        String itemImage = itemSnapshot.child("Image").getValue(String.class);
                        String itemDescription = itemSnapshot.child("Description").getValue(String.class);
                        String itemUnit = itemSnapshot.child("Unit").getValue(String.class);

                        int itemQuantity = 0;

                        // Kiểm tra giá trị giá
                        boolean matchesPriceRange = true;

                        if (itemPrice != null) {
                            // Nếu có minPrice, kiểm tra giá lớn hơn hoặc bằng minPrice
                            if (minPrice != null && itemPrice < minPrice) {
                                matchesPriceRange = false;
                            }
                            // Nếu có maxPrice, kiểm tra giá nhỏ hơn hoặc bằng maxPrice
                            if (maxPrice != null && itemPrice > maxPrice) {
                                matchesPriceRange = false;
                            }
                        } else {
                            matchesPriceRange = false; // Nếu giá không tồn tại, bỏ qua sản phẩm
                        }

                        // Nếu thỏa mãn điều kiện giá, thêm vào danh sách
                        if (matchesPriceRange) {
                            Item item = new Item(itemName, itemPrice, itemQuantity, itemImage);
                            if (itemDescription != null) {
                                item.setDescription(itemDescription);
                            }
                            if (itemUnit != null) {
                                item.setUnit(itemUnit);
                            }
                            m_CategoryItems.add(item);
                        }
                    }
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
}

