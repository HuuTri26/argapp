package com.example.argapp.Fragments;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Adapters.ShoppingCartAdapter;
import com.example.argapp.Classes.Item;
import com.example.argapp.Classes.ShoppingCart;
import com.example.argapp.Interfaces.OnShoppingCartItemListener;
import com.example.argapp.Interfaces.OnShoppingCartUpdatedListener;
import com.example.argapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.Angle;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.Spread;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;
import nl.dionsegijn.konfetti.xml.image.ImageUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingCartPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingCartPage extends Fragment implements OnShoppingCartItemListener, OnShoppingCartUpdatedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShoppingCartPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoppingCartPage.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoppingCartPage newInstance(String param1, String param2) {
        ShoppingCartPage fragment = new ShoppingCartPage();
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
    private RecyclerView m_ShoppingCartRecyclerView;
    private ShoppingCartAdapter m_Adapter;
    private MainActivity m_HostedActivity;
    private ShoppingCart m_UserShoppingCart;
    private List<Item> m_UserShoppingCartAsList;
    private HashMap<String , Item> m_UserLikedItemsList;
    private MaterialButton m_PaymentButton;

    //Dialog Section
    private Dialog m_QuantityDialog;
    private ImageButton m_CloseButton;
    private ImageButton m_MinusButton;
    private ImageButton m_AddButton;
    private TextView m_QuantityTextView;
    private MaterialButton m_ConfirmQuantityButton;
    private KonfettiView m_KonfettiView = null;
    private List<Shape.DrawableShape> m_DrawableShape = null;

    private CartAction m_CurrentAction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        m_View =  inflater.inflate(R.layout.shopping_cart_page, container, false);
        m_ShoppingCartRecyclerView = m_View.findViewById(R.id.shoppingCartRecyclerView);
        m_HostedActivity = (MainActivity) requireActivity();
        m_PaymentButton = m_View.findViewById(R.id.paymentButton);
        m_UserShoppingCart = m_HostedActivity.GetUserShoppingCart();
        m_UserLikedItemsList = m_HostedActivity.GetUserLikedItemsList();

        m_UserShoppingCartAsList = m_UserShoppingCart.ToList();

        m_HostedActivity.SetShoppingCartUpdatedListener(this);

        prepareQuantityDialog();
        updatePaymentButton();
        createRecyclerView();

        m_PaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!m_UserShoppingCart.getShoppingCart().isEmpty()){
                    showConfetti();

                    m_UserShoppingCart.Clear();
                    m_UserShoppingCartAsList.clear();
                    m_HostedActivity.UpdateShoppingCart(m_UserShoppingCart);
                    m_CurrentAction = CartAction.PAYMENT_SUCCEEDED;
                }

            }
        });

        return m_View;
    }

    private void showConfetti(){
        m_KonfettiView =  m_View.findViewById(R.id.konfettiView);
        Drawable bananaDrawable = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.banana);
        Drawable appleDrawable = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.apple);
        Drawable orangeDrawable = ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.orange);

        m_DrawableShape = new ArrayList<>();
        m_DrawableShape.add(ImageUtil.loadDrawable(bananaDrawable, true, true));
        m_DrawableShape.add(ImageUtil.loadDrawable(appleDrawable, true, true));
        m_DrawableShape.add(ImageUtil.loadDrawable(orangeDrawable, true, true));

        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        m_KonfettiView.start(
                new PartyFactory(emitterConfig)
                        .angle(Angle.RIGHT - 45)
                        .spread(Spread.WIDE)
                        .shapes(m_DrawableShape)
                        .sizes(new Size(12, 5f, 0.2f))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(10f, 30f)
                        .position(new Position.Relative(0.0, 0.5))
                        .build(),
                new PartyFactory(emitterConfig)
                        .angle(Angle.LEFT + 45)
                        .spread(Spread.WIDE)
                        .shapes(m_DrawableShape)
                        .sizes(new Size(12, 5f, 0.2f))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(10f, 30f)
                        .position(new Position.Relative(1.0, 0.5))
                        .build());
    }

    private void prepareQuantityDialog()
    {
        m_QuantityDialog = new Dialog(getContext());
        m_QuantityDialog.setContentView(R.layout.edit_quantity_dialog);
        m_QuantityDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        m_QuantityDialog.getWindow().setBackgroundDrawableResource(R.drawable.quantity_dialog_background);
    }

    private void updatePaymentButton() {
        m_PaymentButton.setText("PAYMENT (" + m_UserShoppingCart.getTotalPrice() + ")");
    }
    private void createRecyclerView()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        m_ShoppingCartRecyclerView.setLayoutManager(layoutManager);
        m_Adapter = new ShoppingCartAdapter(m_UserShoppingCartAsList, m_HostedActivity, this);
        m_ShoppingCartRecyclerView.setAdapter(m_Adapter);
    }

    @Override
    public void onItemClicked(Item item) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("item", item);
        Navigation.findNavController(m_View).navigate(R.id.action_shoppingCartPage_to_editItem, bundle);
    }

    @Override
    public void onRemoveItem(Item item) {
        m_UserShoppingCartAsList.remove(item);
        m_HostedActivity.RemoveItem(item);
        m_CurrentAction = CartAction.ITEM_REMOVED;
    }

    @Override
    public void onQuantitySelected(Item item) {
        m_QuantityDialog.show();

        m_CloseButton = m_QuantityDialog.findViewById(R.id.closeButton);
        m_MinusButton = m_QuantityDialog.findViewById(R.id.minusButton);
        m_AddButton = m_QuantityDialog.findViewById(R.id.addButton);
        m_QuantityTextView = m_QuantityDialog.findViewById(R.id.itemQuantity);
        m_ConfirmQuantityButton = m_QuantityDialog.findViewById(R.id.confirmQuantity);

        m_QuantityTextView.setText(String.valueOf(item.getQuantity()));
        updateMinusButtonState(item.getQuantity());

        m_CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_QuantityDialog.dismiss();
            }
        });

        m_AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qunatity = Integer.parseInt(m_QuantityTextView.getText().toString());
                qunatity += 1;
                m_QuantityTextView.setText(qunatity + "");

                updateMinusButtonState(qunatity);
            }
        });

        m_MinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qunatity = Integer.parseInt(m_QuantityTextView.getText().toString());
                qunatity -= 1;
                m_QuantityTextView.setText(qunatity + "");

                updateMinusButtonState(qunatity);
            }
        });

        m_ConfirmQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(m_QuantityTextView.getText().toString());

                m_UserShoppingCart.UpdateItemQuantityOnCart(item, quantity);

                if(m_UserLikedItemsList.containsKey(item.getName()))
                {
                    m_UserLikedItemsList.put(item.getName(), item);
                }

                m_HostedActivity.UpdateShoppingCart(m_UserShoppingCart);
                m_HostedActivity.UpdateLikedItemsList(m_UserLikedItemsList);
                m_CurrentAction = CartAction.ITEM_UPDATED;
            }
        });

    }
    private void updateMinusButtonState(int quantity) {
        if (quantity <= 1) {
            m_MinusButton.setEnabled(false);
            m_MinusButton.setAlpha(0.5f);
            m_MinusButton.setBackgroundColor(0xB0B0B0);
        } else {
            m_MinusButton.setEnabled(true);
            m_MinusButton.setAlpha(1.0f);
            m_MinusButton.setBackgroundColor(0x3D3C3C);
        }
    }

    @Override
    public void OnShoppingCartUpdated() {
        switch (m_CurrentAction)
        {
            case ITEM_REMOVED:
                Toast.makeText(getContext(), "Item has been removed", Toast.LENGTH_SHORT).show();
                break;
            case ITEM_UPDATED:
                Toast.makeText(getContext(), "Item has been updated", Toast.LENGTH_SHORT).show();
                m_QuantityDialog.dismiss();
                break;
            case PAYMENT_SUCCEEDED:
                Toast.makeText(getContext(), "Payment processed successfully!", Toast.LENGTH_SHORT).show();
                break;
        }

        m_Adapter.notifyDataSetChanged();
        updatePaymentButton();
    }
    public enum CartAction {
        ITEM_REMOVED,
        ITEM_UPDATED,
        PAYMENT_SUCCEEDED
    }
}