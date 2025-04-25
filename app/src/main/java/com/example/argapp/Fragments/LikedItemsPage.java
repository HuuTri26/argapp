package com.example.argapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Adapters.LikedItemsAdapter;
import com.example.argapp.Classes.Item;
import com.example.argapp.Interfaces.OnItemListener;
import com.example.argapp.Interfaces.OnLikedItemsListUpdatedListener;
import com.example.argapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikedItemsPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikedItemsPage extends Fragment implements OnItemListener, OnLikedItemsListUpdatedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LikedItemsPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LikedItemsPage.
     */
    // TODO: Rename and change types and number of parameters
    public static LikedItemsPage newInstance(String param1, String param2) {
        LikedItemsPage fragment = new LikedItemsPage();
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

    private MainActivity m_HostedActivity;
    private RecyclerView m_LikedItemsRecyclerView;
    private LikedItemsAdapter m_Adapter;
    private HashMap<String, Item> m_LikedItemsMap;
    private List<Item> m_LikedItemsList;
    private View m_View;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_View = inflater.inflate(R.layout.liked_items_page, container, false);
        m_HostedActivity = (MainActivity) requireActivity();
        m_LikedItemsRecyclerView = m_View.findViewById(R.id.likedItemsRecyclerView);
        m_LikedItemsMap = m_HostedActivity.GetUserLikedItemsList();
        m_LikedItemsList = new ArrayList<>(this.m_LikedItemsMap.values());
        m_HostedActivity.SetLikedItemsListUpdateListener(this);
        createRecyclerView();

        return m_View;
    }

    private void createRecyclerView()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        m_LikedItemsRecyclerView.setLayoutManager(layoutManager);
        m_Adapter = new LikedItemsAdapter(m_LikedItemsList, m_HostedActivity, this);
        m_LikedItemsRecyclerView.setAdapter(m_Adapter);
    }

    @Override
    public void onAddToCartClick(Item item) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        Navigation.findNavController(m_View).navigate(R.id.action_likedItemsPage_to_editItem, bundle);
    }

    @Override
    public void onLikedItemsListUpdated() {
        Toast.makeText(getContext(), "Item has been removed", Toast.LENGTH_SHORT).show();
        m_Adapter.notifyDataSetChanged();
    }

    @Override
    public void onLikeClick(Item item) {
        m_LikedItemsMap.remove(item.getName());
        m_LikedItemsList.remove(item);
        m_HostedActivity.UpdateLikedItemsList(m_LikedItemsMap);
    }

    @Override
    public void onItemClick(Item item) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        Navigation.findNavController(m_View).navigate(R.id.action_likedItemsPage_to_editItem, bundle);
    }
}