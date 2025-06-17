package com.example.argapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Adapters.CategoryItemsAdapter;
import com.example.argapp.Classes.Category;
import com.example.argapp.Classes.CategoryItemsList;
import com.example.argapp.Classes.CategoriesList;
import com.example.argapp.Classes.Item;
import com.example.argapp.Classes.ShoppingCart;
import com.example.argapp.Utils.SeasonUtil;
import com.example.argapp.Interfaces.OnCategoryItemsFetchedListener;
import com.example.argapp.Interfaces.OnCategoriesFetchedListener;
import com.example.argapp.Interfaces.OnItemListener;
import com.example.argapp.Interfaces.OnLikedItemsListUpdatedListener;
import com.example.argapp.Interfaces.OnShoppingCartUpdatedListener;
import com.example.argapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CategoryItems extends Fragment implements OnItemListener, OnShoppingCartUpdatedListener, OnLikedItemsListUpdatedListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "CategoryItems";

    private String mParam1;
    private String mParam2;

    public CategoryItems() {
    }

    public static CategoryItems newInstance(String param1, String param2) {
        CategoryItems fragment = new CategoryItems();
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
    private RecyclerView m_CategoryItemsRecyclerView;
    private List<Item> m_CategoryItemsList;
    private CategoryItemsAdapter m_Adapter;
    private ShoppingCart m_UserShoppingCart;
    private HashMap<String, Item> m_UserLikedItemsList;
    private MainActivity m_HostedActivity;
    private CartAction m_CurrentAction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Starting CategoryItems");
        m_View = inflater.inflate(R.layout.category_items_page, container, false);
        m_CategoryItemsRecyclerView = m_View.findViewById(R.id.categoryItemsRecyclerView);
        m_HostedActivity = (MainActivity) requireActivity();

        m_HostedActivity.SetLikedItemsListUpdateListener(this);
        m_HostedActivity.SetShoppingCartUpdatedListener(this);

        boolean isSeasonal = getArguments() != null ? getArguments().getBoolean("is_seasonal", false) : false;
        String categoryId = getArguments() != null ? getArguments().getString("category_id", "") : "";
        Log.d(TAG, "isSeasonal: " + isSeasonal + ", Category ID: " + categoryId);

        LinearLayout seasonHeader = m_View.findViewById(R.id.seasonHeader);
        TextView seasonTitle = m_View.findViewById(R.id.seasonTitle);
        ImageView seasonIcon = m_View.findViewById(R.id.seasonIcon);

        if (isSeasonal) {
            String currentSeason = SeasonUtil.getCurrentSeason();
            Log.d(TAG, "Current Season: " + currentSeason);

            m_View.setBackgroundResource(SeasonUtil.getSeasonBackgroundResourceId(currentSeason));
            String seasonName = currentSeason.substring(0, 1).toUpperCase() + currentSeason.substring(1).toLowerCase();
            seasonTitle.setText(seasonName + " Products");
            seasonIcon.setImageResource(SeasonUtil.getSeasonIconResourceId(currentSeason));
            seasonHeader.setVisibility(View.VISIBLE);

            CategoryItemsList.GetSeasonalItems(currentSeason, m_HostedActivity, new OnCategoryItemsFetchedListener() {
                @Override
                public void onCategoryItemsFetched(List<Item> seasonalItems) {
                    m_CategoryItemsList = seasonalItems;
                    Log.d(TAG, "Seasonal Products Count: " + seasonalItems.size());
                    m_UserShoppingCart = m_HostedActivity.GetUserShoppingCart();
                    m_UserLikedItemsList = m_HostedActivity.GetUserLikedItemsList();
                    createRecycleView();
                }
            });
        } else {
            seasonHeader.setVisibility(View.GONE);
            Log.d(TAG, "Applied non-seasonal UI: Header hidden, Category ID: " + categoryId);
            loadCategoryProducts(categoryId);
        }

        return m_View;
    }


    private void loadCategoryProducts(String categoryId) {
        Log.d(TAG, "Loading products for category: " + categoryId);
        CategoryItemsList.GetItemsListByCategoryId(categoryId, m_HostedActivity, new OnCategoryItemsFetchedListener() {
            @Override
            public void onCategoryItemsFetched(List<Item> categoryItems) {
                m_CategoryItemsList = categoryItems;
                Log.d(TAG, "Category Products Count: " + categoryItems.size());
                m_UserShoppingCart = m_HostedActivity.GetUserShoppingCart();
                m_UserLikedItemsList = m_HostedActivity.GetUserLikedItemsList();
                createRecycleView();
            }
        });
    }

    private void createRecycleView() {
        m_CategoryItemsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        m_Adapter = new CategoryItemsAdapter(m_HostedActivity, m_CategoryItemsList, m_UserShoppingCart, m_UserLikedItemsList, this);
        m_CategoryItemsRecyclerView.setAdapter(m_Adapter);
    }

    @Override
    public void onItemClick(Item item) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        Navigation.findNavController(m_View).navigate(R.id.action_category_items_page_to_edit_item_page, bundle);
    }

    @Override
    public void onLikedItemsListUpdated() {
        if (this.m_CurrentAction == CartAction.ITEM_LIKED) {
            m_Adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLikeClick(Item item) {
        this.m_CurrentAction = CartAction.ITEM_LIKED;

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
        if (m_CurrentAction == CartAction.ITEM_ADDED_TO_CART) {
            Toast.makeText(getContext(), "Item has been added to the shopping cart", Toast.LENGTH_SHORT).show();
            m_Adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAddToCartClick(Item item) {
        this.m_CurrentAction = CartAction.ITEM_ADDED_TO_CART;

        m_UserShoppingCart.AddToCart(item);

        if (m_UserLikedItemsList.containsKey(item.getName())) {
            m_UserLikedItemsList.put(item.getName(), item);
        }

        m_HostedActivity.UpdateShoppingCart(m_UserShoppingCart);
        m_HostedActivity.UpdateLikedItemsList(m_UserLikedItemsList);
    }

    public enum CartAction {
        ITEM_ADDED_TO_CART,
        ITEM_LIKED
    }
}