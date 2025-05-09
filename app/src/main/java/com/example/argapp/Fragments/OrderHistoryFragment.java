package com.example.argapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.argapp.Adapters.OrderBillAdapter;
import com.example.argapp.Classes.OrderBill;
import com.example.argapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderHistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // khai báo bién
    private View view;
    private List<OrderBill> orderBillList;
    private RecyclerView orderBillItemRecycleView;
    private OrderBillAdapter orderBillAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_order_history, container, false);

        this.orderBillItemRecycleView = view.findViewById(R.id.recyclerViewOrderBills);
        this.orderBillList = new ArrayList<>();
        this.orderBillList.add(new OrderBill("3123291", 1714904000, "SUCCESS", 10000));
        this.orderBillList.add(new OrderBill("74935435", 543665646, "SUCCESS", 20000));
        this.orderBillList.add(new OrderBill("23435454", 878675858, "SUCCESS", 30000));
        this.orderBillList.add(new OrderBill("43549u68", 244535235, "SUCCESS", 40000));
        this.orderBillList.add(new OrderBill("65465477", 887658657, "SUCCESS", 50000));
        this.orderBillAdapter = new OrderBillAdapter(this.orderBillList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        this.orderBillItemRecycleView.setLayoutManager(linearLayoutManager);
        this.orderBillItemRecycleView.setAdapter(this.orderBillAdapter);

        return view;
        // return inflater.inflate(R.layout.fragment_order_history, container, false);
    }
}