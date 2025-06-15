package com.example.argapp.Classes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.argapp.Interfaces.OnCategoriesFetchedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesList {

    private static List<Category> m_CategoriesList = new ArrayList<>();

    public static void GetCategoriesList(Context context, OnCategoriesFetchedListener callback) {
        FirebaseDatabase m_Database;
        DatabaseReference m_Ref;

        m_Database = FirebaseDatabase.getInstance();
        m_Ref = m_Database.getReference("Data/Categories");

        m_CategoriesList.clear();

        m_Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String categoryId = itemSnapshot.child("Id").getValue(String.class);
                    String categoryName = itemSnapshot.child("Name").getValue(String.class);
                    String categoryImage = itemSnapshot.child("Image").getValue(String.class);
                    String categorySeason = itemSnapshot.child("Season").getValue(String.class);
                    Log.d("CategoriesList", "Category: " + categoryId + ", Season: " + categorySeason);

                    Category category = new Category(categoryId, categoryName, categoryImage, categorySeason);
                    m_CategoriesList.add(category);
                }

                callback.onCategoriesFetched(m_CategoriesList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                callback.onCategoriesFetched(new ArrayList<>());
            }
        });
    }
}