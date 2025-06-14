package com.example.argapp.Classes;

import android.content.Context;
import android.util.Log;
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
import java.util.Map;

public class CategoryItemsList {
    private static List<Item> m_CategoryItems = new ArrayList<>();
    private static List<Item> m_AllItems = new ArrayList<>();

    public static void GetItemsListByCategoryId(String i_CategoryId, Context context, OnCategoryItemsFetchedListener callback) {
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

    public static void GetAllItems(Context context, OnCategoryItemsFetchedListener callback) {
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;

        m_AllItems.clear();
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

    // hàm tìm sản phẩm theo tên và giá
    public static void searchItemsByNameAndPrice(String query, Double minPrice, Double maxPrice, Context context, OnCategoryItemsFetchedListener callback) {
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

                        // Kiểm tra tên (chứa từ khóa không phân biệt hoa thường)
                        boolean matchesName = (query == null || query.isEmpty()) ||
                                (itemName != null && itemName.toLowerCase().contains(query.toLowerCase()));

                        // Kiểm tra giá trị giá
                        boolean matchesPriceRange = true;

                        if (itemPrice != null) {
                            if (minPrice != null && itemPrice < minPrice) {
                                matchesPriceRange = false;
                            }
                            if (maxPrice != null && itemPrice > maxPrice) {
                                matchesPriceRange = false;
                            }
                        } else {
                            matchesPriceRange = false; // Nếu giá không tồn tại, bỏ qua sản phẩm
                        }

                        // Nếu thỏa mãn cả tên và giá, thêm vào danh sách
                        if (matchesName && matchesPriceRange) {
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

    public static void searchCategoryItemsByTheMostPopular(Context context, Map<String, Integer> i_Top_n_Items, OnCategoryItemsFetchedListener callback) {
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
                        String itemId = itemSnapshot.child("Id").getValue(String.class);
                        String itemType = itemSnapshot.child("Type").getValue(String.class);
                        String itemName = itemSnapshot.child("Name").getValue(String.class);
                        Double itemPrice = itemSnapshot.child("Price").getValue(Double.class);
                        String itemImage = itemSnapshot.child("Image").getValue(String.class);
                        String itemDescription = itemSnapshot.child("Description").getValue(String.class);
                        String itemUnit = itemSnapshot.child("Unit").getValue(String.class);

                        int itemQuantity = 0;

                        String itemIdentifier = itemType + "-" + itemId;
                        Log.d("CategoryItemsList", "itemIdentifier:" + itemIdentifier);
                        // Kiểm tra nếu sản phẩm nằm trong danh sách top n
                        if (i_Top_n_Items.containsKey(itemIdentifier)) {
                            Item item = new Item(itemId, itemType, itemName, itemPrice, itemQuantity, itemImage, itemDescription, itemUnit);
                            m_CategoryItems.add(item);
                        }
                    }
                }

                // Sort items by sales in decreasing order before callback
                m_CategoryItems.sort((item1, item2) -> {
                    // Get sales for each item from the top items map
                    Integer sales1 = i_Top_n_Items.get(item1.getId() + "-" + item1.getType());
                    Integer sales2 = i_Top_n_Items.get(item2.getId() + "-" + item2.getType());

                    // Handle null values
                    if (sales1 == null) sales1 = 0;
                    if (sales2 == null) sales2 = 0;

                    // Sort in decreasing order (highest sales first)
                    return sales2.compareTo(sales1);
                });

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
