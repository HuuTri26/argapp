package com.example.argapp.Classes;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.argapp.Interfaces.OnSoldItemsFetchedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SoldItemsMap {
    private static final Map<Date, Map<String, Integer>> m_SoldItemsMap = new HashMap<>();
    private static final SimpleDateFormat m_DateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    /**
     * Phương thức để lấy Map các doanh số của từng sản phẩm được bán trong i_days ngày qua từ Firebase Realtime Database
     * @param context  Context để hiển thị thông báo lỗi nếu cần
     * @param i_days     Số ngày gần đây để truy vấn dữ liệu bán hàng
     * @param callback Interface callback để trả về kết quả sau khi truy vấn thành công hoặc thất bại
     */
    public static void getSoldItemMapRecently(Context context, int i_days, OnSoldItemsFetchedListener callback) {
        // Khởi tạo tham chiếu đến Firebase Database
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;
        Date currentDate = new Date();

        // Lấy instance của Firebase Database
        m_Database = FirebaseDatabase.getInstance();

        // Clear the map before starting
        m_SoldItemsMap.clear();

        // Counter to track completed queries
        final int[] completedQueries = {0};
        final int totalQueries = i_days;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        // Duyệt từ ngày hiện tại về trước theo số ngày đã chỉ định
        for (int i = 0; i < i_days; i++) {
            Date dateToProcess = calendar.getTime();
            String dateString = m_DateFormat.format(dateToProcess);
            Log.d("SoldItemMap", "Querying date: " + dateString);

            m_Ref = m_Database.getReference("Data/SoldItems/" + dateString);
            m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("SoldItemMap", "Firebase query for date: " + dateString);
                    Log.d("SoldItemMap", "Snapshot exists: " + snapshot.exists());
                    Log.d("SoldItemMap", "Children count: " + snapshot.getChildrenCount());

                    Map<String, Integer> soldItems = new HashMap<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d("SoldItemMap", "Processing child: " + dataSnapshot.getKey());
                        String itemId = dataSnapshot.child("Id").getValue(String.class);
                        Integer sales = dataSnapshot.child("Sales").getValue(Integer.class);

                        Log.d("SoldItemMap", "Child data - Id: " + itemId + ", Sales: " + sales);

                        if (itemId != null && sales != null) {
                            // Use the Firebase key as the identifier (e.g., "fruits-itemId1")
                            soldItems.put(dataSnapshot.getKey(), sales);
                            Log.d("SoldItemMap", "Added Item: " + dataSnapshot.getKey() + " - Sales: " + sales);
                        }
                    }

                    Log.d("SoldItemMap", "Total items found for " + dateString + ": " + soldItems.size());

                    // Lưu danh sách bán hàng vào bản đồ với ngày tương ứng
                    m_SoldItemsMap.put(dateToProcess, soldItems);

                    // Increment completed queries counter
                    completedQueries[0]++;
                    Log.d("SoldItemMap", "Completed queries: " + completedQueries[0] + "/" + totalQueries);

                    // Call success callback only when all queries are complete
                    if (completedQueries[0] == totalQueries) {
                        Log.d("SoldItemMap", "All queries completed. Total map size: " + m_SoldItemsMap.size());
                        callback.onSuccess();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SoldItemMap", "Firebase error for date " + dateString + ": " + error.getMessage());
                    // Even if one query fails, increment the counter to prevent hanging
                    completedQueries[0]++;
                    if (completedQueries[0] == totalQueries) {
                        Log.d("SoldItemMap", "All queries completed (some may have failed). Total map size: " + m_SoldItemsMap.size());
                        callback.onSuccess();
                    }
                }
            });

            // Chuyển tờ lịch về ngày trước đó để tiếp tục vòng lặp
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
    }

    /**
     * Phương thức để lấy Map các doanh số cho một ngày cụ thể
     *
     * @param context    Context để hiển thị thông báo lỗi nếu cần
     * @param dateString Ngày cần truy vấn theo format yyyy-MM-dd
     * @param callback   Interface callback để trả về kết quả sau khi truy vấn thành công hoặc thất bại
     */
    public static void getSoldItemMapForSpecificDate(Context context, String dateString, OnSoldItemsFetchedListener callback) {
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;

        // Lấy instance của Firebase Database
        m_Database = FirebaseDatabase.getInstance();

        // Clear the map before starting
        m_SoldItemsMap.clear();

        Log.d("SoldItemMap", "Querying for date: " + dateString);

        m_Ref = m_Database.getReference("Data/SoldItems/" + dateString);
        m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("SoldItemMap", "Firebase query for date: " + dateString);
                Log.d("SoldItemMap", "Snapshot exists: " + snapshot.exists());
                Log.d("SoldItemMap", "Children count: " + snapshot.getChildrenCount());

                Map<String, Integer> soldItems = new HashMap<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Log.d("SoldItemMap", "Processing child: " + dataSnapshot.getKey());
                    String itemId = dataSnapshot.child("Id").getValue(String.class);
                    Integer sales = dataSnapshot.child("Sales").getValue(Integer.class);

                    Log.d("SoldItemMap", "Child data - Id: " + itemId + ", Sales: " + sales);

                    if (itemId != null && sales != null) {
                        // Use the key (like "beverages-item1") as the identifier for the map
                        soldItems.put(dataSnapshot.getKey(), sales);
                        Log.d("SoldItemMap", "Added Item: " + dataSnapshot.getKey() + " - Sales: " + sales);
                    }
                }

                Log.d("SoldItemMap", "Total items found for " + dateString + ": " + soldItems.size());

                // Lưu danh sách bán hàng vào bản đồ với ngày tương ứng
                try {
                    Date date = m_DateFormat.parse(dateString);
                    m_SoldItemsMap.put(date, soldItems);
                } catch (Exception e) {
                    Log.e("SoldItemMap", "Error parsing date: " + e.getMessage());
                }

                Log.d("SoldItemMap", "Query completed. Total map size: " + m_SoldItemsMap.size());
                callback.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SoldItemMap", "Firebase error: " + error.getMessage());
                callback.onFailure("Error fetching data: " + error.getMessage());
            }
        });
    }

    /**
     * Original method - keeping for reference but fixing the date issue
     */
    public static void getSoldItemMapRecentlyOriginal(Context context, int i_days, OnSoldItemsFetchedListener callback) {
        // Khởi tạo tham chiếu đến Firebase Database
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;
        Date currentDate = new Date();

        // Lấy instance của Firebase Database
        m_Database = FirebaseDatabase.getInstance();

        // Clear the map before starting
        m_SoldItemsMap.clear();

        // Counter to track completed queries
        final int[] completedQueries = {0};
        final int totalQueries = i_days;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        // Duuyệt từ ngày hiện tại về trước theo số ngày đã chỉ định
        for (int i = 0; i < i_days; i++) {
            Date dateToProcess = calendar.getTime();
            Log.d("SoldItemMap", "Date: " + m_DateFormat.format(dateToProcess));

            m_Ref = m_Database.getReference("Data/SoldItems/" + m_DateFormat.format(dateToProcess));
            m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("SoldItemMap", "Firebase query for date: " + m_DateFormat.format(dateToProcess));
                    Log.d("SoldItemMap", "Snapshot exists: " + snapshot.exists());
                    Log.d("SoldItemMap", "Children count: " + snapshot.getChildrenCount());
                    
                    Map<String, Integer> soldItems = new HashMap<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Log.d("SoldItemMap", "Processing child: " + dataSnapshot.getKey());
                        String itemId = dataSnapshot.child("Id").getValue(String.class);
                        Integer sales = dataSnapshot.child("Sales").getValue(Integer.class);
                        
                        Log.d("SoldItemMap", "Child data - Id: " + itemId + ", Sales: " + sales);

                        if (itemId != null && sales != null) {
                            soldItems.put(itemId, sales);
                            //Log.d("SoldItemMap", "Added Item: " + itemId + " - Sales: " + sales);
                        }
                    }
                    
                    Log.d("SoldItemMap", "Total items found for " + m_DateFormat.format(dateToProcess) + ": " + soldItems.size());
                    
                    // Lưu danh sách bán hàng vào bản đồ với ngày tương ứng
                    m_SoldItemsMap.put(dateToProcess, soldItems);

                    // Increment completed queries counter
                    completedQueries[0]++;
                    Log.d("SoldItemMap", "Completed queries: " + completedQueries[0] + "/" + totalQueries);

                    // Call success callback only when all queries are complete
                    if (completedQueries[0] == totalQueries) {
                        Log.d("SoldItemMap", "All queries completed. Total map size: " + m_SoldItemsMap.size());
                        callback.onSuccess();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SoldItemMap", "Firebase error: " + error.getMessage());
                    callback.onFailure("Error fetching data: " + error.getMessage());
                }
            });

            // Chuyển tơng lịch về ngày trước đó để tiếp tục vòng lặp
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        // Remove this line - callback.onSuccess();
    }

    /**
     * Phương thức để lấy Map các doanh số của từng sản phẩm được bán trong ngày hiện tại từ Firebase Realtime Database
     * @param context  Context để hiển thị thông báo lỗi nếu cần
     * @param callback Interface callback để trả về kết quả sau khi truy vấn thành công hoặc thất bại
     */
    public static void getSoldItemMapCurrently(Context context, OnSoldItemsFetchedListener callback) {
        // Khởi tạo tham chiếu đến Firebase Database
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;

        // Lấy instance của Firebase Database
        m_Database = FirebaseDatabase.getInstance();
        m_Ref = m_Database.getReference("Data/SoldItems/" + m_DateFormat.format(new Date()));

        m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Integer> soldItems = new HashMap<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String itemId = dataSnapshot.child("Id").getValue(String.class);
                    Integer sales = dataSnapshot.child("Sales").getValue(Integer.class);

                    if (itemId != null && sales != null) {
                        soldItems.put(itemId, sales);
                        Log.d("SoldItemMap", "Item: " + itemId + " - Sales: " + sales);
                    }
                }
                // Lưu danh sách bán hàng vào bản đồ với ngày hiện tại
                m_SoldItemsMap.put(new Date(), soldItems);
                callback.onSuccess();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure("Error fetching data: " + error.getMessage());
            }
        });
    }

    /**
     * Phương thức để lấy top n sản phẩm có doanh số cao nhất từ m_SoldItemsMap
     * @param n Số lượng sản phẩm cần lấy
     * @return Map<String, Integer> chứa top 3 sản phẩm với itemId làm key và tổng doanh số làm value
     */
    public static Map<String, Integer> getTop(int n) {
        // Tạo Map để tổng hợp doanh số của từng sản phẩm
        Map<String, Integer> totalSalesMap = new HashMap<>();

        // Tổng hợp doanh số từ tất cả các ngày
        for (Map<String, Integer> dailySales : m_SoldItemsMap.values()) {
            for (Map.Entry<String, Integer> entry : dailySales.entrySet()) {
                String itemId = entry.getKey();
                Integer sales = entry.getValue();

                totalSalesMap.put(itemId, totalSalesMap.getOrDefault(itemId, 0) + sales);
            }
        }

        // Chuyển Map thành List để sắp xếp
        java.util.List<Map.Entry<String, Integer>> sortedList = new java.util.ArrayList<>(totalSalesMap.entrySet());

        // Sắp xếp theo doanh số giảm dần
        sortedList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        // Lấy top n
        Map<String, Integer> top_n_Items = new java.util.LinkedHashMap<>();
        int count = 0;
        for (Map.Entry<String, Integer> entry : sortedList) {
            if (count >= n) break;
            Log.d("SoldItemMap", "Top Item: " + entry.getKey() + " - Sales: " + entry.getValue());
            top_n_Items.put(entry.getKey(), entry.getValue());
            count++;
        }

        return top_n_Items;
    }
}
