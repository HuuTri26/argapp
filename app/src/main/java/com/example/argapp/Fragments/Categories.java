package com.example.argapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Adapters.CategoriesAdapter;
import com.example.argapp.Classes.CategoriesList;
import com.example.argapp.Classes.Category;
import com.example.argapp.Interfaces.OnCategoriesFetchedListener;
import com.example.argapp.Interfaces.OnCategoryClickListener;
import com.example.argapp.R;

import java.util.List;

public class Categories extends Fragment implements OnCategoryClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public Categories() {
    }

    public static Categories newInstance(String param1, String param2) {
        Categories fragment = new Categories();
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
    private RecyclerView m_CategoriesRecyclerView;
    private CategoriesAdapter m_Adapter;
    private List<Category> m_CategoriesList;
    private MainActivity m_HostedActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        m_View = inflater.inflate(R.layout.categories_page, container, false);
        m_HostedActivity = (MainActivity) requireActivity();

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(m_View).navigate(R.id.action_categories_page_to_login_page);
                m_HostedActivity.DisableNavigationView();
            }
        });

        m_HostedActivity.GetUser();

        m_CategoriesRecyclerView = m_View.findViewById(R.id.categoriesRecyclerView);
        m_CategoriesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        CategoriesList.GetCategoriesList(m_HostedActivity, new OnCategoriesFetchedListener() {
            @Override
            public void onCategoriesFetched(List<Category> categories) {
                m_CategoriesList = categories;
                createAdapter();
            }
        });

        return m_View;
    }

    private void createAdapter() {
        m_Adapter = new CategoriesAdapter(m_HostedActivity, m_CategoriesList, this);
        m_CategoriesRecyclerView.setAdapter(m_Adapter);
    }

    @Override
    public void onCategoryClick(String categoryId) {
        Bundle bundle = new Bundle();
        bundle.putString("category_id", categoryId);
        boolean isSeasonal = categoryId.equals("seasonal_products");
        bundle.putBoolean("is_seasonal", isSeasonal);

        Navigation.findNavController(m_View)
                .navigate(R.id.action_categories_page_to_category_items_page, bundle);
    }
}