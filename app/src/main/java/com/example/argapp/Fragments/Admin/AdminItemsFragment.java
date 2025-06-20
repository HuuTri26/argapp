package com.example.argapp.Fragments.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Adapters.AdminItemsAdapter;
import com.example.argapp.Classes.Item;
import com.example.argapp.Classes.Category;
import com.example.argapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminItemsFragment extends Fragment implements AdminItemsAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private AdminItemsAdapter adapter;
    private List<Item> itemsList;
    private List<Category> categoriesList;
    private DatabaseReference itemsRef, categoriesRef;
    private FloatingActionButton fabAddItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_items, container, false);

        initViews(view);
        setupRecyclerView();
        setupDatabase();
        loadCategories();
        loadItems();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_items);
        fabAddItem = view.findViewById(R.id.fab_add_item);

        fabAddItem.setOnClickListener(v -> showAddItemDialog());
    }

    private void setupRecyclerView() {
        itemsList = new ArrayList<>();
        adapter = new AdminItemsAdapter(itemsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupDatabase() {
        itemsRef = FirebaseDatabase.getInstance().getReference("Data/CategoriesItems");
        categoriesRef = FirebaseDatabase.getInstance().getReference("Data/Categories");
    }

    private void loadCategories() {
        categoriesList = new ArrayList<>();
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoriesList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryId = categorySnapshot.child("Id").getValue(String.class);
                    String categoryName = categorySnapshot.child("Name").getValue(String.class);
                    String categoryImage = categorySnapshot.child("Image").getValue(String.class);
                    String categorySeason = categorySnapshot.child("Season").getValue(String.class);

                    Category category = new Category(categoryId, categoryName, categoryImage, categorySeason);
                    categoriesList.add(category);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải categories: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadItems() {
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemsList.clear();
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                        String itemId = itemSnapshot.child("Id").getValue(String.class);
                        String itemType = itemSnapshot.child("Type").getValue(String.class);
                        String itemName = itemSnapshot.child("Name").getValue(String.class);
                        Double itemPrice = itemSnapshot.child("Price").getValue(Double.class);
                        String itemImage = itemSnapshot.child("Image").getValue(String.class);
                        String itemDescription = itemSnapshot.child("Description").getValue(String.class);
                        String itemUnit = itemSnapshot.child("Unit").getValue(String.class);

                        if (itemPrice == null) itemPrice = 0.0;

                        Item item = new Item(itemId, itemType, itemName, itemPrice, 0, itemImage,
                                itemDescription != null ? itemDescription : "",
                                itemUnit != null ? itemUnit : "");
                        itemsList.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_item, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.et_item_name);
        EditText etPrice = dialogView.findViewById(R.id.et_item_price);
        EditText etDescription = dialogView.findViewById(R.id.et_item_description);
        EditText etUnit = dialogView.findViewById(R.id.et_item_unit);
        EditText etImage = dialogView.findViewById(R.id.et_item_image);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_category);

        // Setup category spinner
        List<String> categoryNames = new ArrayList<>();
        for (Category category : categoriesList) {
            categoryNames.add(category.getName());
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categoryNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(spinnerAdapter);

        builder.setTitle("Thêm Item mới")
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String unit = etUnit.getText().toString().trim();
                    String image = etImage.getText().toString().trim();
                    int categoryPosition = spinnerCategory.getSelectedItemPosition();

                    if (name.isEmpty() || priceStr.isEmpty() || categoryPosition == -1) {
                        Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        Category selectedCategory = categoriesList.get(categoryPosition);
                        addItem(selectedCategory.getId(), name, price, description, unit, image, selectedCategory.getName());
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void addItem(String categoryId, String name, double price, String description, String unit, String image, String type) {
        DatabaseReference newItemRef = itemsRef.child(categoryId).push();
        String itemId = newItemRef.getKey();

        Item newItem = new Item(itemId, type, name, price, 0, image, description, unit);

        newItemRef.setValue(newItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã thêm item thành công", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi thêm item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onEditItem(Item item) {
        showEditItemDialog(item);
    }

    private void showEditItemDialog(Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_edit_item, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.et_item_name);
        EditText etPrice = dialogView.findViewById(R.id.et_item_price);
        EditText etDescription = dialogView.findViewById(R.id.et_item_description);
        EditText etUnit = dialogView.findViewById(R.id.et_item_unit);
        EditText etImage = dialogView.findViewById(R.id.et_item_image);

        // Fill current data
        etName.setText(item.getName());
        etPrice.setText(String.valueOf(item.getPrice()));
        etDescription.setText(item.getDescription());
        etUnit.setText(item.getUnit());
        etImage.setText(item.getImage());

        builder.setTitle("Chỉnh sửa Item")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String name = etName.getText().toString().trim();
                    String priceStr = etPrice.getText().toString().trim();
                    String description = etDescription.getText().toString().trim();
                    String unit = etUnit.getText().toString().trim();
                    String image = etImage.getText().toString().trim();

                    if (name.isEmpty() || priceStr.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double price = Double.parseDouble(priceStr);
                        updateItem(item, name, price, description, unit, image);
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateItem(Item item, String name, double price, String description, String unit, String image) {
        // Find and update item in database
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                        String currentItemId = itemSnapshot.child("Id").getValue(String.class);
                        if (currentItemId != null && currentItemId.equals(item.getId())) {
                            DatabaseReference itemRef = itemSnapshot.getRef();
                            itemRef.child("Name").setValue(name);
                            itemRef.child("Price").setValue(price);
                            itemRef.child("Description").setValue(description);
                            itemRef.child("Unit").setValue(unit);
                            itemRef.child("Image").setValue(image);

                            Toast.makeText(getContext(), "Đã cập nhật item", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi cập nhật: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDeleteItem(Item item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa item: " + item.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteItem(item))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteItem(Item item) {
        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                        String currentItemId = itemSnapshot.child("Id").getValue(String.class);
                        if (currentItemId != null && currentItemId.equals(item.getId())) {
                            itemSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), "Đã xóa item", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Lỗi xóa item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}