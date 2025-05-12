package com.example.argapp.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.argapp.Classes.Item;
import com.example.argapp.Classes.OrderBill;
import com.example.argapp.Classes.ShoppingCart;
import com.example.argapp.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UserModel {
    private FirebaseAuth m_Auth;
    private FirebaseDatabase m_Database;
    private DatabaseReference m_Ref;
    private FirebaseUser m_FirebaseUser;

    public UserModel()
    {
        m_Auth = FirebaseAuth.getInstance();
        m_Database = FirebaseDatabase.getInstance();
        m_Ref = m_Database.getReference("Users");
        m_FirebaseUser = m_Auth.getCurrentUser();
    }

    public interface AuthCallback
    {
        void onSuccess();
        void onFailure(Exception i_Exception);
    }

    public interface UserCallback
    {
        void onSuccess(User user);
        void onFailure(DatabaseError error);
    }

    public interface UpdateProfileCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface ShoppingCartCallback
    {
        void onSuccess(ShoppingCart userShoppingCart);
        void onFailure(DatabaseError error);
    }

    public interface LikedItemsCallback
    {
        void onSuccess(HashMap<String, Item> userLikedItemsList);
        void onFailure(DatabaseError error);
    }

    public interface UpdateShoppingCartCallback
    {
        void onSuccess();
        void onFailure(Exception error);
    }

    public interface UpdateLikedItemsListCallback
    {
        void onSuccess();
        void onFailure(Exception error);
    }

    public interface NavigationCallback
    {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void Login(String i_Email, String i_Password, AuthCallback callback)
    {
        m_Auth.signInWithEmailAndPassword(i_Email, i_Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Current User ", m_Auth.getCurrentUser().getUid());
                            callback.onSuccess();
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void Register(User i_NewUser, AuthCallback callback)
    {
        m_Auth.createUserWithEmailAndPassword(i_NewUser.getEmail(), i_NewUser.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            m_FirebaseUser = m_Auth.getCurrentUser();
                            String userId = m_FirebaseUser.getUid();

                            m_Ref.child(userId).setValue(i_NewUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure(task.getException());
                                    }
                                }
                            });
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void GetUser(UserCallback callback)
    {
        // Read from the database
        String userId = getCurrentUserId();

        m_Ref.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User user = dataSnapshot.getValue(User.class);

                callback.onSuccess(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                callback.onFailure(error);
            }
        });
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = m_Auth.getCurrentUser();

        if (currentUser != null) {
            // If the user is signed in, return their email
            return currentUser.getUid();
        } else {
            // If no user is signed in, return null or an empty string
            return null;
        }
    }

    // Phương thức cập nhật thông tin người dùng
    public void updateUserProfile(String userId, String firstName, String lastName, String phoneNumber, String address, UpdateProfileCallback callback) {
        if (userId == null || userId.isEmpty()) {
            userId = getCurrentUserId();
        }

        // Tạo HashMap chứa các thông tin cần cập nhật
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("firstName", firstName);
        updates.put("lastName", lastName);
        updates.put("phoneNumber", phoneNumber);
        updates.put("address", address);

        m_Ref.child(userId).updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    public void UpdateShoppingCart(ShoppingCart i_UserShoppingCart, UpdateShoppingCartCallback callback)
    {
        String userId = m_FirebaseUser.getUid();
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("shoppingCart", i_UserShoppingCart);

        m_Ref.child(userId).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    callback.onSuccess();
                }
                else
                {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public void UpdateLikedItemsList(HashMap<String, Item> i_UserLikedItemsList, UpdateLikedItemsListCallback callback)
    {
        String userId = m_FirebaseUser.getUid();
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("likedItems", i_UserLikedItemsList);

        m_Ref.child(userId).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    callback.onSuccess();
                }
                else
                {
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public void GetUserShoppingCart(ShoppingCartCallback callback)
    {
        String userId = getCurrentUserId();

        m_Ref.child(userId).child("shoppingCart").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ShoppingCart userShoppingCart = new ShoppingCart();
//
//                if (snapshot.exists()) {
//                    // Extract total price if available
//                    if (snapshot.child("totalPrice").exists()) {
//                        double totalPrice = snapshot.child("totalPrice").getValue(Double.class);
//                        userShoppingCart.setTotalPrice(totalPrice);
//                    }
//
//                    // Extract shopping cart items (m_ShoppingCart)
//                    if (snapshot.child("shoppingCart").exists()) {
//                        for (DataSnapshot itemSnapshot : snapshot.child("shoppingCart").getChildren()) {
//                            Item item = itemSnapshot.getValue(Item.class);
//                                userShoppingCart.().put(item.getName(), item);
//                        }
//                    }
//                }

                ShoppingCart userShoppingCart = snapshot.getValue(ShoppingCart.class);

                if(userShoppingCart == null)
                {
                    userShoppingCart = new ShoppingCart();
                }


                callback.onSuccess(userShoppingCart);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }

    public void GetUserLikedItemsList(LikedItemsCallback callback)
    {
        String userId = getCurrentUserId();

        m_Ref.child(userId).child("likedItems").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, Item> userLikedItemsList = new HashMap<>();

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);

                    userLikedItemsList.put(item.getName(), item);
                }

                callback.onSuccess(userLikedItemsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error);
            }
        });
    }

    public void LoginWithCredential(String i_Email, String i_Password, NavigationCallback callback)
    {
        m_Auth.signInWithEmailAndPassword(i_Email, i_Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Current User ", m_Auth.getCurrentUser().getUid());
                            callback.onSuccess();
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }

    public void RegisterWithCredential(User i_NewUser, NavigationCallback callback)
    {
        m_Auth.createUserWithEmailAndPassword(i_NewUser.getEmail(), i_NewUser.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            m_FirebaseUser = m_Auth.getCurrentUser();
                            String userId = m_FirebaseUser.getUid();

                            m_Ref.child(userId).setValue(i_NewUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        callback.onSuccess();
                                    } else {
                                        callback.onFailure(task.getException());
                                    }
                                }
                            });
                        } else {
                            callback.onFailure(task.getException());
                        }
                    }
                });
    }
    // Thêm interface mới cho việc lưu và lấy OrderBill
    public interface SaveOrderBillCallback {
        void onSuccess(String orderBillId);
        void onFailure(Exception error);
    }

    public interface OrderBillsCallback {
        void onSuccess(List<OrderBill> orderBills);
        void onFailure(DatabaseError error);
    }

    public interface OrderDetailCallback {
        void onSuccess(OrderBill orderBill);
        void onFailure(Exception error);
    }

    // Thêm phương thức để lấy chi tiết đơn hàng dựa trên ID
    public void getOrderDetail(String orderBillId, OrderDetailCallback callback) {
        if (orderBillId == null || orderBillId.isEmpty()) {
            callback.onFailure(new Exception("Invalid order ID"));
            return;
        }
        
        m_Database.getReference("OrderBills").child(orderBillId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        OrderBill orderBill = snapshot.getValue(OrderBill.class);
                        callback.onSuccess(orderBill);
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.toException());
                    }
                });
    }

    // Thêm phương thức để lưu OrderBill vào Firebase
    public void saveOrderBill(OrderBill orderBill, SaveOrderBillCallback callback) {
        try {
            String userId = getCurrentUserId();
            if (userId == null) {
                callback.onFailure(new Exception("User not authenticated"));
                return;
            }

            // Kiểm tra orderBill không null
            if (orderBill == null) {
                callback.onFailure(new Exception("Invalid order data"));
                return;
            }

            // Tạo reference để lưu đơn hàng
            DatabaseReference orderRef = m_Database.getReference("OrderBills").push();
            String orderBillId = orderRef.getKey();

            // Thêm ID vào OrderBill
            orderBill.setOrderBillId(orderBillId);

            // Lưu OrderBill vào Firebase
            orderRef.setValue(orderBill)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Lưu tham chiếu đến OrderBill trong hồ sơ người dùng
                            m_Database.getReference("Users").child(userId)
                                    .child("orderBills").child(orderBillId)
                                    .setValue(true)
                                    .addOnCompleteListener(userOrderTask -> {
                                        if (userOrderTask.isSuccessful()) {
                                            callback.onSuccess(orderBillId);
                                        } else {
                                            callback.onFailure(userOrderTask.getException());
                                        }
                                    });
                        } else {
                            callback.onFailure(task.getException());
                        }
                    });
        } catch (Exception e) {
            // Bắt mọi exception để tránh crash
            callback.onFailure(e);
        }
    }


    // Phương thức lấy tất cả đơn hàng của người dùng
    public void getUserOrderBills(OrderBillsCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure(null);
            return;
        }

        // Lấy các ID đơn hàng của người dùng
        m_Database.getReference("Users").child(userId).child("orderBills")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<OrderBill> orderBills = new ArrayList<>();

                        if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                            callback.onSuccess(orderBills);
                            return;
                        }

                        final long[] orderCount = {snapshot.getChildrenCount()};
                        final long[] processedCount = {0};

                        for (DataSnapshot orderIdSnapshot : snapshot.getChildren()) {
                            String orderBillId = orderIdSnapshot.getKey();

                            // Lấy chi tiết từng đơn hàng
                            m_Database.getReference("OrderBills").child(orderBillId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot orderSnapshot) {
                                            OrderBill orderBill = orderSnapshot.getValue(OrderBill.class);
                                            if (orderBill != null) {
                                                orderBills.add(orderBill);
                                            }

                                            processedCount[0]++;
                                            if (processedCount[0] == orderCount[0]) {
                                                // Sắp xếp theo thời gian giảm dần (mới nhất đầu tiên)
                                                orderBills.sort((o1, o2) -> Long.compare(o2.getOrderDate(), o1.getOrderDate()));
                                                callback.onSuccess(orderBills);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            processedCount[0]++;
                                            if (processedCount[0] == orderCount[0]) {
                                                callback.onSuccess(orderBills);
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error);
                    }
                });
    }
}
