package com.example.argapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.argapp.Interfaces.OnCategoryItemsFetchedListener;
import com.example.argapp.Interfaces.OnItemListener;
import com.example.argapp.Interfaces.OnLikedItemsListUpdatedListener;
import com.example.argapp.Interfaces.OnShoppingCartUpdatedListener;
import com.example.argapp.R;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryItems#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryItems extends Fragment implements OnItemListener, OnShoppingCartUpdatedListener, OnLikedItemsListUpdatedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoryItems() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryItems.
     */
    // TODO: Rename and change types and number of parameters
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
    private HashMap<String, Item> m_UserLikedItemsList ;
    private MainActivity m_HostedActivity;
    private CartAction m_CurrentAction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_View = inflater.inflate(R.layout.category_items_page, container, false);
        m_CategoryItemsRecyclerView = m_View.findViewById(R.id.categoryItemsRecyclerView);
        m_HostedActivity = (MainActivity) requireActivity();

        m_HostedActivity.SetLikedItemsListUpdateListener(this);
        m_HostedActivity.SetShoppingCartUpdatedListener(this);

        String categoryId = getArguments().getString("category_id");

        CategoryItemsList.GetItemsListByCategoryId(categoryId, m_HostedActivity, new OnCategoryItemsFetchedListener() {
            @Override
            public void onCategoryItemsFetched(List<Item> categoryItems) {
                m_CategoryItemsList = categoryItems;
                m_UserShoppingCart = m_HostedActivity.GetUserShoppingCart();
                m_UserLikedItemsList = m_HostedActivity.GetUserLikedItemsList();

                createRecycleView();
            }
        });

        return m_View;
    }

    private void createRecycleView()
    {
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
        if(this.m_CurrentAction == CartAction.ITEM_LIKED) {
            m_Adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLikeClick(Item item) {
        this.m_CurrentAction = CartAction.ITEM_LIKED;

        if(m_UserLikedItemsList.containsKey(item.getName()))
        {
            m_UserLikedItemsList.remove(item.getName());
        }
        else
        {
            HashMap<String, Item> userShoppingCart = m_UserShoppingCart.getShoppingCart();
            if(userShoppingCart.containsKey(item.getName()))
            {
                item.setQuantity(userShoppingCart.get(item.getName()).getQuantity());
            }

            m_UserLikedItemsList.put(item.getName(), item);
        }

        m_HostedActivity.UpdateLikedItemsList(m_UserLikedItemsList);
    }
    @Override
    public void OnShoppingCartUpdated() {
        if(m_CurrentAction == CartAction.ITEM_ADDED_TO_CART) {
            Toast.makeText(getContext(), "Item has been added to the shopping cart", Toast.LENGTH_SHORT).show();
            m_Adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onAddToCartClick(Item item) {
        this.m_CurrentAction = CartAction.ITEM_ADDED_TO_CART;

        m_UserShoppingCart.AddToCart(item);

        if(m_UserLikedItemsList.containsKey(item.getName()))
        {
            m_UserLikedItemsList.put(item.getName(), item);
        }

        m_HostedActivity.UpdateShoppingCart(m_UserShoppingCart);
        m_HostedActivity.UpdateLikedItemsList(m_UserLikedItemsList);
    }

    public enum CartAction{
        ITEM_ADDED_TO_CART,
        ITEM_LIKED
    }
}