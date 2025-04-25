package com.example.argapp.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.argapp.Classes.Item;
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
}
