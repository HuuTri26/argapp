package com.example.argapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Classes.Item;
import com.example.argapp.Classes.ShoppingCart;
import com.example.argapp.Interfaces.OnShoppingCartUpdatedListener;
import com.example.argapp.R;
import com.google.android.material.button.MaterialButton;

import java.util.HashMap;

/**
 * Fragment EditItem hiển thị chi tiết một sản phẩm và cho phép người dùng
 * thực hiện các thao tác như thêm vào giỏ hàng, yêu thích và chỉnh số lượng
 */
public class EditItem extends Fragment implements OnShoppingCartUpdatedListener {

    // Tham số mặc định từ template Fragment của Android Studio
    // Thường được sử dụng để truyền dữ liệu vào fragment
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    /**
     * Constructor rỗng bắt buộc cho Fragment
     * Android yêu cầu mọi Fragment phải có constructor không tham số
     */
    public EditItem() {
        // Required empty public constructor
    }

    /**
     * Phương thức factory tạo một instance mới của fragment với tham số
     * Đây là cách được khuyến nghị để tạo Fragment với các tham số
     *
     * @param param1 Parameter 1
     * @param param2 Parameter 2
     * @return Fragment EditItem mới được tạo với các tham số
     */
    public static EditItem newInstance(String param1, String param2) {
        EditItem fragment = new EditItem();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Được gọi khi Fragment được tạo
     * Dùng để khôi phục trạng thái hoặc khởi tạo các thành phần không phải UI
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // Các biến thành viên của Fragment
    private View m_View;                      // View gốc của Fragment
    private Item m_CurrentItem;               // Sản phẩm hiện tại đang xem/chỉnh sửa
    private ShoppingCart m_UserShoppingCart;  // Giỏ hàng của người dùng hiện tại
    private HashMap<String, Item> m_UserLikedItemsList;  // Danh sách sản phẩm yêu thích
    private MainActivity m_HostedActivity;    // Activity chính chứa Fragment này
    
    // Các thành phần UI
    private TextView m_ItemName;              // TextView hiển thị tên sản phẩm
    private ImageView m_ItemImage;            // ImageView hiển thị hình ảnh sản phẩm
    private TextView m_ItemPrice;             // TextView hiển thị giá sản phẩm
    private ImageButton m_CloseButton;        // Nút đóng Fragment quay lại màn hình trước
    private MaterialButton m_AddToCart;       // Nút thêm vào giỏ hàng
    private ImageButton m_LikedButton;        // Nút yêu thích/bỏ yêu thích sản phẩm
    private ImageButton m_MinusButton;        // Nút giảm số lượng
    private ImageButton m_AddButton;          // Nút tăng số lượng 
    private TextView m_Quantity;              // TextView hiển thị số lượng

    /**
     * Được gọi để tạo giao diện của Fragment
     * Đây là nơi inflate layout và thiết lập UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout cho fragment và lưu trữ tham chiếu đến View chính
        m_View = inflater.inflate(R.layout.edit_item_page, container, false);
        
        // Lấy đối tượng Item được truyền qua arguments của Fragment
        m_CurrentItem = (Item) getArguments().getSerializable("item");
        
        // Lấy tham chiếu đến MainActivity chứa Fragment này
        m_HostedActivity = (MainActivity) requireActivity();
        
        // Ánh xạ các thành phần giao diện từ layout
        m_ItemName = m_View.findViewById(R.id.itemName);
        m_ItemImage = m_View.findViewById(R.id.itemImage);
        m_ItemPrice = m_View.findViewById(R.id.itemPrice);
        m_CloseButton = m_View.findViewById(R.id.closeButton);
        m_AddToCart = m_View.findViewById(R.id.addToCartButton);
        m_LikedButton = m_View.findViewById(R.id.likeButton);
        m_MinusButton = m_View.findViewById(R.id.minusButton);
        m_AddButton = m_View.findViewById(R.id.addButton);
        m_Quantity = m_View.findViewById(R.id.itemQuantity);

        // Đăng ký Fragment này làm listener cho sự kiện cập nhật giỏ hàng
        m_HostedActivity.SetShoppingCartUpdatedListener(this);
        
        // Không cần lắng nghe sự kiện cập nhật danh sách yêu thích nên đặt là null
        m_HostedActivity.SetLikedItemsListUpdateListener(null);

        // Lấy thông tin giỏ hàng và danh sách yêu thích của người dùng từ MainActivity
        getUserDetails();
        
        // Hiển thị thông tin sản phẩm lên giao diện
        showItem();

        // Thiết lập listener cho nút "Thêm vào giỏ hàng"
        m_AddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy số lượng hiện tại từ TextView
                int quantity = Integer.parseInt(m_Quantity.getText().toString());

                // Thêm sản phẩm vào giỏ hàng với số lượng đã chọn
                m_UserShoppingCart.AddToCart(m_CurrentItem, quantity);

                // Nếu sản phẩm này cũng đang nằm trong danh sách yêu thích,
                // cập nhật thông tin của nó trong danh sách yêu thích
                if(m_UserLikedItemsList.containsKey(m_CurrentItem.getName()))
                {
                    m_UserLikedItemsList.put(m_CurrentItem.getName(), m_CurrentItem);
                }

                // Cập nhật giỏ hàng và danh sách yêu thích trong MainActivity
                m_HostedActivity.UpdateShoppingCart(m_UserShoppingCart);
                m_HostedActivity.UpdateLikedItemsList(m_UserLikedItemsList);
            }
        });

        // Thiết lập listener cho nút "Yêu thích"
        m_LikedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_UserLikedItemsList.containsKey(m_CurrentItem.getName()))
                {
                    // Nếu sản phẩm đã được yêu thích, xóa khỏi danh sách và đổi icon thành trái tim trống
                    m_UserLikedItemsList.remove(m_CurrentItem.getName());
                    m_LikedButton.setImageResource(R.drawable.blank_heart);
                }
                else
                {
                    // Nếu sản phẩm chưa được yêu thích, thêm vào danh sách và đổi icon thành trái tim đầy
                    // Kiểm tra xem sản phẩm có trong giỏ hàng không để lấy số lượng chính xác
                    HashMap<String, Item> userShoppingCart = m_UserShoppingCart.getShoppingCart();
                    if(userShoppingCart.containsKey(m_CurrentItem.getName()))
                    {
                        m_CurrentItem.setQuantity(userShoppingCart.get(m_CurrentItem.getName()).getQuantity());
                    }

                    m_UserLikedItemsList.put(m_CurrentItem.getName(), m_CurrentItem);
                    m_LikedButton.setImageResource(R.drawable.filled_heart);
                }

                // Cập nhật danh sách yêu thích trong MainActivity
                m_HostedActivity.UpdateLikedItemsList(m_UserLikedItemsList);
            }
        });

        // Thiết lập listener cho nút "Tăng số lượng"
        m_AddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qunatity = Integer.parseInt(m_Quantity.getText().toString());
                qunatity += 1;  // Tăng số lượng lên 1
                m_Quantity.setText(qunatity + "");  // Cập nhật hiển thị số lượng

                // Cập nhật trạng thái nút giảm số lượng dựa trên số lượng mới
                updateMinusButtonState(qunatity);
            }
        });

        // Thiết lập listener cho nút "Giảm số lượng"
        m_MinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qunatity = Integer.parseInt(m_Quantity.getText().toString());
                qunatity -= 1;  // Giảm số lượng xuống 1
                m_Quantity.setText(qunatity + "");  // Cập nhật hiển thị số lượng

                // Cập nhật trạng thái nút giảm số lượng dựa trên số lượng mới
                updateMinusButtonState(qunatity);
            }
        });

        // Thiết lập listener cho nút "Đóng"
        m_CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đóng Fragment hiện tại và quay lại Fragment trước đó bằng cách pop back stack
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return m_View;
    }

    /**
     * Cập nhật trạng thái của nút giảm số lượng
     * Vô hiệu hóa nút khi số lượng là 1 và kích hoạt khi số lượng > 1
     * @param quantity Số lượng hiện tại
     */
    private void updateMinusButtonState(int quantity) {
        if (quantity <= 1) {
            // Nếu số lượng <= 1, vô hiệu hóa nút giảm
            m_MinusButton.setEnabled(false);
            m_MinusButton.setAlpha(0.5f);  // Làm mờ nút
            m_MinusButton.setBackgroundColor(0xB0B0B0);  // Đổi màu nền thành xám
        } else {
            // Nếu số lượng > 1, kích hoạt nút giảm
            m_MinusButton.setEnabled(true);
            m_MinusButton.setAlpha(1.0f);  // Hiển thị nút rõ nét
            m_MinusButton.setBackgroundColor(0x3D3C3C);  // Đổi màu nền thành màu gốc
        }
    }

    /**
     * Lấy thông tin giỏ hàng và danh sách yêu thích của người dùng từ MainActivity
     */
    private void getUserDetails() {
        m_UserShoppingCart = m_HostedActivity.GetUserShoppingCart();
        m_UserLikedItemsList = m_HostedActivity.GetUserLikedItemsList();
    }

    /**
     * Hiển thị thông tin sản phẩm lên giao diện
     */
    private void showItem()
    {
        // Lấy ID resource của hình ảnh sản phẩm từ tên file
        int itemImage = m_HostedActivity.getResources().getIdentifier(m_CurrentItem.getImage(), "drawable", m_HostedActivity.getPackageName());

        // Hiển thị thông tin sản phẩm lên giao diện
        m_ItemName.setText(m_CurrentItem.getName());  // Tên sản phẩm
        m_ItemImage.setImageResource(itemImage);      // Hình ảnh sản phẩm
        m_ItemPrice.setText(String.format("%.2f", m_CurrentItem.getPrice()));  // Giá sản phẩm (định dạng 2 số thập phân)

        // Cập nhật trạng thái nút yêu thích dựa trên việc sản phẩm có trong danh sách yêu thích hay không
        if(isItemLiked())
        {
           m_LikedButton.setImageResource(R.drawable.filled_heart);  // Hiển thị trái tim đầy nếu sản phẩm được yêu thích
        }
        else
        {
            m_LikedButton.setImageResource(R.drawable.blank_heart);  // Hiển thị trái tim trống nếu sản phẩm chưa được yêu thích
        }

        // Cập nhật trạng thái nút giảm với số lượng mặc định là 1
        updateMinusButtonState(1);
    }

    /**
     * Kiểm tra xem sản phẩm hiện tại có trong danh sách yêu thích hay không
     * @return true nếu sản phẩm được yêu thích, false nếu không
     */
    private boolean isItemLiked()
    {
        return m_UserLikedItemsList.containsKey(m_CurrentItem.getName());
    }

    /**
     * Được gọi khi giỏ hàng được cập nhật
     * Triển khai từ interface OnShoppingCartUpdatedListener
     */
    @Override
    public void OnShoppingCartUpdated() {
        // Hiển thị thông báo cho người dùng biết sản phẩm đã được thêm vào giỏ hàng
        Toast.makeText(getContext(), "Item has been added to cart", Toast.LENGTH_SHORT).show();
    }
}