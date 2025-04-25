package com.example.argapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Classes.Item;
import com.example.argapp.Interfaces.OnShoppingCartItemListener;
import com.example.argapp.R;

import java.util.List;

/**
 * Adapter tùy chỉnh để hiển thị danh sách các sản phẩm trong giỏ hàng
 * Quản lý hiển thị và xử lý tương tác với các sản phẩm đã được thêm vào giỏ hàng
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private List<Item> m_ShoppingCartAsList;        // Danh sách các sản phẩm trong giỏ hàng
    private MainActivity m_HostedActivity;          // Activity chứa RecyclerView
    private OnShoppingCartItemListener m_Listener;  // Interface callback để xử lý sự kiện của item trong giỏ hàng

    /**
     * Constructor cho ShoppingCartAdapter
     * @param i_UserShoppingCartAsList Danh sách các sản phẩm trong giỏ hàng
     * @param i_HostedActivity Activity chứa RecyclerView
     * @param i_Listener Listener để xử lý sự kiện trên các sản phẩm trong giỏ hàng
     */
    public ShoppingCartAdapter(List<Item> i_UserShoppingCartAsList, MainActivity i_HostedActivity, OnShoppingCartItemListener i_Listener)
    {
        m_HostedActivity = i_HostedActivity;
        m_ShoppingCartAsList = i_UserShoppingCartAsList;
        m_Listener = i_Listener;
    }

    /**
     * Tạo ViewHolder mới bằng cách inflate layout cho item giỏ hàng
     * @param parent ViewGroup cha chứa ViewHolder mới
     * @param viewType Loại view (không sử dụng trong trường hợp này)
     * @return ViewHolder mới được khởi tạo
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho một item trong giỏ hàng
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_cart_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Gắn dữ liệu từ danh sách vào ViewHolder tại vị trí chỉ định
     * @param holder ViewHolder được gắn dữ liệu
     * @param position Vị trí của item trong danh sách
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int itemPosition = position;  // Lưu lại vị trí hiện tại để sử dụng trong các listener
        Item shoppingCartItem = m_ShoppingCartAsList.get(position);  // Lấy đối tượng Item ở vị trí position
        String itemImage = shoppingCartItem.getImage();  // Lấy tên file hình ảnh

        // Thiết lập tên sản phẩm
        holder.m_ItemName.setText(shoppingCartItem.getName());
        
        // Lấy ID hình ảnh từ tên file và thiết lập cho ImageView
        holder.m_ItemImage.setImageResource(m_HostedActivity.getResources().getIdentifier(
            itemImage, "drawable", m_HostedActivity.getPackageName()));

        // Định dạng và hiển thị giá sản phẩm với 2 chữ số thập phân
        double price = shoppingCartItem.getPrice();
        holder.m_ItemPrice.setText(String.format("%.2f", price));

        // Hiển thị số lượng sản phẩm trong giỏ hàng
        holder.m_ItemQuantity.setText("x" + " " + shoppingCartItem.getQuantity());

        // Thiết lập sự kiện click cho nút xóa sản phẩm
        holder.m_RemoveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xóa item khỏi danh sách hiển thị
                m_ShoppingCartAsList.remove(itemPosition);
                // Gọi callback để xóa item khỏi giỏ hàng
                m_Listener.onRemoveItem(shoppingCartItem);
            }
        });

        // Thiết lập sự kiện click cho phần hiển thị số lượng
        holder.m_ItemQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi callback khi người dùng muốn thay đổi số lượng sản phẩm
                m_Listener.onQuantitySelected(shoppingCartItem);
            }
        });

        // Thiết lập sự kiện click cho toàn bộ item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi callback khi click vào item để xem chi tiết hoặc thực hiện hành động khác
                m_Listener.onItemClicked(shoppingCartItem);
            }
        });
    }

    /**
     * Trả về số lượng item trong giỏ hàng
     * @return Số lượng sản phẩm trong giỏ hàng
     */
    @Override
    public int getItemCount() {
        return m_ShoppingCartAsList.size();
    }

    /**
     * Lớp ViewHolder để giữ và quản lý các view của mỗi item trong RecyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView m_ItemName;          // View hiển thị tên sản phẩm
        private ImageView m_ItemImage;        // View hiển thị hình ảnh sản phẩm
        private TextView m_ItemPrice;         // View hiển thị giá sản phẩm
        private TextView m_ItemQuantity;      // View hiển thị số lượng sản phẩm
        private ImageButton m_RemoveItemButton;  // Nút xóa sản phẩm khỏi giỏ hàng
        
        /**
         * Constructor của ViewHolder
         * @param itemView View đại diện cho một item trong giỏ hàng
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            m_ItemName = itemView.findViewById(R.id.itemName);
            m_ItemImage = itemView.findViewById(R.id.itemImage);
            m_ItemPrice = itemView.findViewById(R.id.itemPrice);
            m_ItemQuantity = itemView.findViewById(R.id.itemQuantity);
            m_RemoveItemButton = itemView.findViewById(R.id.removeItem);
        }
    }
}