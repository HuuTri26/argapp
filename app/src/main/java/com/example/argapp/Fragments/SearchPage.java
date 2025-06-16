package com.example.argapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Adapters.CategoryItemsAdapter;
import com.example.argapp.Classes.CategoryItemsList;
import com.example.argapp.Classes.Item;
import com.example.argapp.Classes.ShoppingCart;
import com.example.argapp.Classes.SoldItemsMap;
import com.example.argapp.Interfaces.OnCategoryItemsFetchedListener;
import com.example.argapp.Interfaces.OnItemListener;
import com.example.argapp.Interfaces.OnLikedItemsListUpdatedListener;
import com.example.argapp.Interfaces.OnShoppingCartUpdatedListener;
import com.example.argapp.Interfaces.OnSoldItemsFetchedListener;
import com.example.argapp.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchPage extends Fragment implements OnItemListener, OnShoppingCartUpdatedListener, OnLikedItemsListUpdatedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchPage.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchPage newInstance(String param1, String param2) {
        SearchPage fragment = new SearchPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private View m_View;
    private MainActivity m_HostedActivity;
    private RecyclerView m_SearchItemsRecyclerView;
    private CategoryItemsAdapter m_Adapter;
    private List<Item> m_AllItemsList;
    private List<Item> m_SearchedItemList;
    private SearchView m_SearchView;
    private ShoppingCart m_UserShoppingCart;
    private HashMap<String, Item> m_UserLikedItemsList;

    private EditText minPriceEditText;
    private EditText maxPriceEditText;
    private Button priceFilterButton;
    private Button threeDaysButton;
    private Button sevenDaysButton;
    private Button fourteenDaysButton;
    private Button thirtyDaysButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.m_View = inflater.inflate(R.layout.search_page, container, false);
        this.m_HostedActivity = (MainActivity) requireActivity();
        this.m_SearchView = this.m_View.findViewById(R.id.searchView);
        this.m_SearchItemsRecyclerView = this.m_View.findViewById(R.id.searchItemRecyclerView);
        this.m_AllItemsList = new ArrayList<>();
        this.m_SearchedItemList = new ArrayList<>();

        minPriceEditText = m_View.findViewById(R.id.minPriceEditText);
        maxPriceEditText = m_View.findViewById(R.id.maxPriceEditText);
        priceFilterButton = m_View.findViewById(R.id.priceFilterButton);
        threeDaysButton = m_View.findViewById(R.id.threeDaysBtn);
        sevenDaysButton = m_View.findViewById(R.id.sevenDaysBtn);
        fourteenDaysButton = m_View.findViewById(R.id.fourteenDaysBtn);
        thirtyDaysButton = m_View.findViewById(R.id.thirtyDaysBtn);

        m_HostedActivity.SetLikedItemsListUpdateListener(this);
        m_HostedActivity.SetShoppingCartUpdatedListener(this);

        CategoryItemsList.GetAllItems(this.m_HostedActivity, new OnCategoryItemsFetchedListener() {
            @Override
            public void onCategoryItemsFetched(List<Item> allItems) {
                m_AllItemsList.addAll(allItems);
                m_SearchedItemList.addAll(allItems);
                m_UserShoppingCart = m_HostedActivity.GetUserShoppingCart();
                m_UserLikedItemsList = m_HostedActivity.GetUserLikedItemsList();

                createRecyclerView();

                m_SearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filterList(newText);

                        return true;
                    }
                });
            }
        });

        // tìm sản phẩm theo giá
        // Thêm sự kiện click cho nút lọc giá

//        priceFilterButton.setOnClickListener(v -> {
//            String minPriceStr = minPriceEditText.getText().toString().trim();
//            String maxPriceStr = maxPriceEditText.getText().toString().trim();
//
//            // Kiểm tra giá trị nhập vào
//            if (minPriceStr.isEmpty() && maxPriceStr.isEmpty()) {
//                Toast.makeText(getContext(), "Please enter at least one price value", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Double minPrice = null;
//            Double maxPrice = null;
//            try {
//                if (!minPriceStr.isEmpty()) {
//                    minPrice = Double.parseDouble(minPriceStr);
//                }
//                if (!maxPriceStr.isEmpty()) {
//                    maxPrice = Double.parseDouble(maxPriceStr);
//                }
//                // Kiểm tra trường hợp cả minPrice và maxPrice đều được nhập
//                if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
//                    Toast.makeText(getContext(), "Minimum price cannot be greater than maximum price", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            } catch (NumberFormatException e) {
//                Toast.makeText(getContext(), "Please enter valid numbers for price", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Gọi hàm timSanPhamTheoGia với minPrice và maxPrice (có thể null)
//            CategoryItemsList.timSanPhamTheoGia(minPrice, maxPrice, getContext(), new OnCategoryItemsFetchedListener() {
//                @Override
//                public void onCategoryItemsFetched(List<Item> filteredItems) {
//                    // Cập nhật danh sách hiển thị
//                    m_SearchedItemList.clear();
//                    m_SearchedItemList.addAll(filteredItems);
//                    m_Adapter.notifyDataSetChanged();
//
//                    if (filteredItems.isEmpty()) {
//                        Toast.makeText(getContext(), "No items found in this price range", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        });

        // tìm sản phẩm theo giá + tên
        priceFilterButton.setOnClickListener(v -> {
            String minPriceStr = minPriceEditText.getText().toString().trim();
            String maxPriceStr = maxPriceEditText.getText().toString().trim();
            String query = m_SearchView.getQuery().toString().trim();

            // Kiểm tra giá trị nhập vào
            if (minPriceStr.isEmpty() && maxPriceStr.isEmpty() && query.isEmpty()) {
                Toast.makeText(getContext(), "Please enter at least one search criterion (name or price)", Toast.LENGTH_SHORT).show();
                return;
            }

            Double minPrice = null;
            Double maxPrice = null;
            try {
                if (!minPriceStr.isEmpty()) {
                    minPrice = Double.parseDouble(minPriceStr);
                }
                if (!maxPriceStr.isEmpty()) {
                    maxPrice = Double.parseDouble(maxPriceStr);
                }
                // Kiểm tra trường hợp cả minPrice và maxPrice đều được nhập
                if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
                    Toast.makeText(getContext(), "Minimum price cannot be greater than maximum price", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Please enter valid numbers for price", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi hàm tìm kiếm kết hợp tên và giá
            CategoryItemsList.searchItemsByNameAndPrice(query, minPrice, maxPrice, getContext(), new OnCategoryItemsFetchedListener() {
                @Override
                public void onCategoryItemsFetched(List<Item> filteredItems) {
                    // Cập nhật danh sách hiển thị
                    m_SearchedItemList.clear();
                    m_SearchedItemList.addAll(filteredItems);
                    m_Adapter.notifyDataSetChanged();

                    if (filteredItems.isEmpty()) {
                        Toast.makeText(getContext(), "No items found with the given criteria", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        threeDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoldItemsMap.getSoldItemMapRecently(getContext(), 3, new OnSoldItemsFetchedListener() {

                    @Override
                    public void onSuccess() {
                        Map<String, Integer> i_top_n_Items = SoldItemsMap.getTop(3);
                        CategoryItemsList.searchCategoryItemsByTheMostPopular(getContext(), i_top_n_Items, new OnCategoryItemsFetchedListener() {
                            @Override
                            public void onCategoryItemsFetched(List<Item> items) {
                                m_SearchedItemList.clear();
                                m_SearchedItemList.addAll(items);
                                m_Adapter.notifyDataSetChanged();

                                if (items.isEmpty()) {
                                    Toast.makeText(getContext(), "No items found in the last 3 days", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), "Error loading Sold Items: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        sevenDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoldItemsMap.getSoldItemMapRecently(getContext(), 7, new OnSoldItemsFetchedListener() {
                    @Override
                    public void onSuccess() {
                        Map<String, Integer> i_top_n_Items = SoldItemsMap.getTop(7);
                        CategoryItemsList.searchCategoryItemsByTheMostPopular(getContext(), i_top_n_Items, new OnCategoryItemsFetchedListener() {
                            @Override
                            public void onCategoryItemsFetched(List<Item> items) {
                                m_SearchedItemList.clear();
                                m_SearchedItemList.addAll(items);
                                m_Adapter.notifyDataSetChanged();

                                if (items.isEmpty()) {
                                    Toast.makeText(getContext(), "No items found in the last 7 days", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), "Error loading Sold Items: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        fourteenDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoldItemsMap.getSoldItemMapRecently(getContext(), 14, new OnSoldItemsFetchedListener() {
                    @Override
                    public void onSuccess() {
                        Map<String, Integer> i_top_n_Items = SoldItemsMap.getTop(10);
                        CategoryItemsList.searchCategoryItemsByTheMostPopular(getContext(), i_top_n_Items, new OnCategoryItemsFetchedListener() {
                            @Override
                            public void onCategoryItemsFetched(List<Item> items) {
                                m_SearchedItemList.clear();
                                m_SearchedItemList.addAll(items);
                                m_Adapter.notifyDataSetChanged();

                                if (items.isEmpty()) {
                                    Toast.makeText(getContext(), "No items found in the last 14 days", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), "Error loading Sold Items: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        thirtyDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SoldItemsMap.getSoldItemMapRecently(getContext(), 30, new OnSoldItemsFetchedListener() {
                    @Override
                    public void onSuccess() {
                        Map<String, Integer> i_top_n_Items = SoldItemsMap.getTop(15);
                        CategoryItemsList.searchCategoryItemsByTheMostPopular(getContext(), i_top_n_Items, new OnCategoryItemsFetchedListener() {
                            @Override
                            public void onCategoryItemsFetched(List<Item> items) {
                                m_SearchedItemList.clear();
                                m_SearchedItemList.addAll(items);
                                m_Adapter.notifyDataSetChanged();

                                if (items.isEmpty()) {
                                    Toast.makeText(getContext(), "No items found in the last 30 days", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(getContext(), "Error loading Sold Items: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Lấy danh sách tất cả sản phẩm ban đầu
        CategoryItemsList.GetAllItems(this.m_HostedActivity, new OnCategoryItemsFetchedListener() {
            @Override
            public void onCategoryItemsFetched(List<Item> allItems) {
                m_AllItemsList.clear();
                m_AllItemsList.addAll(allItems);
                m_SearchedItemList.clear();
                m_SearchedItemList.addAll(allItems);
                m_UserShoppingCart = m_HostedActivity.GetUserShoppingCart();
                m_UserLikedItemsList = m_HostedActivity.GetUserLikedItemsList();

                createRecyclerView();

                m_SearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filterList(newText);
                        return true;
                    }
                });
            }
        });

        return m_View;
    }

    private void createRecyclerView() {
        this.m_SearchItemsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        this.m_Adapter = new CategoryItemsAdapter(this.m_HostedActivity, this.m_SearchedItemList, this.m_UserShoppingCart, this.m_UserLikedItemsList, this);
        this.m_SearchItemsRecyclerView.setAdapter(m_Adapter);
    }

    private void filterList(String i_Query) {
        List<Item> filteredList = new ArrayList<>();

        for (Item item : this.m_AllItemsList) {
            if (item.getName().toLowerCase().contains(i_Query.toLowerCase())) {
                filteredList.add(item);
            }
        }

        this.m_SearchedItemList.clear();
        this.m_SearchedItemList.addAll(filteredList);
        this.m_Adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Item item) { //TODO : Create navigation to the edit page
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        Navigation.findNavController(m_View).navigate(R.id.action_searchPage_to_editItem, bundle);
    }

    @Override
    public void onLikedItemsListUpdated() {
        Toast.makeText(getContext(), "Item has been added to the liked items list", Toast.LENGTH_SHORT).show();
        m_Adapter.notifyDataSetChanged();
    }

    @Override
    public void onLikeClick(Item item) {
        if (m_UserLikedItemsList.containsKey(item.getName())) {
            m_UserLikedItemsList.remove(item.getName());
        } else {
            HashMap<String, Item> userShoppingCart = m_UserShoppingCart.getShoppingCart();
            if (userShoppingCart.containsKey(item.getName())) {
                item.setQuantity(userShoppingCart.get(item.getName()).getQuantity());
            }

            m_UserLikedItemsList.put(item.getName(), item);
        }

        m_HostedActivity.UpdateLikedItemsList(m_UserLikedItemsList);
    }

    @Override
    public void OnShoppingCartUpdated() {
        Toast.makeText(getContext(), "Item has been added to the shopping cart", Toast.LENGTH_SHORT).show();
        m_Adapter.notifyDataSetChanged();
    }

    @Override
    public void onAddToCartClick(Item item) {
        m_UserShoppingCart.AddToCart(item);

        if (m_UserLikedItemsList.containsKey(item.getName())) {
            m_UserLikedItemsList.put(item.getName(), item);
        }

        m_HostedActivity.UpdateShoppingCart(m_UserShoppingCart);
        m_HostedActivity.UpdateLikedItemsList(m_UserLikedItemsList);
    }
}
