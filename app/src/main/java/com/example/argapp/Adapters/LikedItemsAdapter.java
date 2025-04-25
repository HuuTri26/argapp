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
import com.example.argapp.Classes.ShoppingCart;
import com.example.argapp.Interfaces.OnItemListener;
import com.example.argapp.R;

import java.util.List;

/**
 * Adapter tùy chỉnh để hiển thị danh sách các sản phẩm yêu thích của người dùng trong RecyclerView
 * Quản lý hiển thị và xử lý tương tác với các sản phẩm đã được người dùng đánh dấu yêu thích
 */
public class LikedItemsAdapter extends RecyclerView.Adapter<LikedItemsAdapter.ViewHolder>{
    private List <Item> m_UserLikedItemsList;     // Danh sách các sản phẩm yêu thích
    private ShoppingCart m_UserShoppingCart;      // Giỏ hàng của người dùng
    private MainActivity m_HostedActivity;        // Activity chứa RecyclerView
    private OnItemListener m_Listener;            // Interface callback để xử lý sự kiện của item

    /**
     * Constructor cho LikedItemsAdapter
     * @param i_UserLikedItemsList Danh sách các sản phẩm yêu thích của người dùng
     * @param i_HostedActivity Activity chứa RecyclerView
     * @param i_Listener Listener để xử lý sự kiện trên các sản phẩm
     */
    public LikedItemsAdapter(List<Item> i_UserLikedItemsList, MainActivity i_HostedActivity, OnItemListener i_Listener)
    {
        m_HostedActivity = i_HostedActivity;
        m_UserShoppingCart = m_HostedActivity.GetUserShoppingCart();  // Lấy giỏ hàng từ MainActivity
        m_UserLikedItemsList = i_UserLikedItemsList;
        m_Listener = i_Listener;
    }

    /**
     * Tạo ViewHolder mới bằng cách inflate layout cho item yêu thích
     * @param parent ViewGroup cha chứa ViewHolder mới
     * @param viewType Loại view (không sử dụng trong trường hợp này)
     * @return ViewHolder mới được khởi tạo
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.liked_list_item, parent, false);
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
        Item likedItem = m_UserLikedItemsList.get(position);  // Lấy đối tượng Item ở vị trí position
        String itemImage = likedItem.getImage();  // Lấy tên file hình ảnh

        // Thiết lập tên sản phẩm
        holder.m_ItemName.setText(likedItem.getName());
        
        // Lấy ID hình ảnh từ tên file và thiết lập cho ImageView
        holder.m_ItemImage.setImageResource(m_HostedActivity.getResources().getIdentifier(
            itemImage, "drawable", m_HostedActivity.getPackageName()));

        double price = likedItem.getPrice();

        // Định dạng và hiển thị giá sản phẩm với 2 chữ số thập phân
        holder.m_ItemPrice.setText(String.format("%.2f", price));
        
        // Thiết lập icon yêu thích luôn là trái tim đầy (vì đây là danh sách yêu thích)
        holder.m_LikeButton.setImageResource(R.drawable.filled_heart);

        // Cập nhật icon giỏ hàng dựa trên trạng thái hiện tại
        if(isItemAddedToCart(likedItem))
        {
            holder.m_AddToCartButton.setImageResource(R.drawable.filled_shopping_cart);  // Đã thêm vào giỏ
        }
        else
        {
            holder.m_AddToCartButton.setImageResource(R.drawable.blank_shopping_cart);   // Chưa thêm vào giỏ
        }

        // Thiết lập sự kiện click cho nút thêm vào giỏ hàng
        holder.m_AddToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Listener.onAddToCartClick(likedItem);  // Gọi callback thêm vào giỏ hàng
            }
        });

        // Thiết lập sự kiện click cho nút yêu thích
        holder.m_LikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_UserLikedItemsList.remove(itemPosition);  // Xóa khỏi danh sách yêu thích
                m_Listener.onLikeClick(likedItem);  // Gọi callback hủy yêu thích
            }
        });

        // Thiết lập sự kiện click cho toàn bộ item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Listener.onItemClick(likedItem);  // Gọi callback khi click vào item
            }
        });
    }

    /**
     * Trả về số lượng item trong danh sách yêu thích
     * @return Số lượng sản phẩm yêu thích
     */
    @Override
    public int getItemCount() {
        return m_UserLikedItemsList.size();
    }

    /**
     * Kiểm tra xem một sản phẩm có trong giỏ hàng hay không
     * @param item Đối tượng Item cần kiểm tra
     * @return true nếu sản phẩm đã được thêm vào giỏ hàng, false nếu chưa
     */
    public boolean isItemAddedToCart(Item item)
    {
        return m_UserShoppingCart.getShoppingCart().containsKey(item.getName());
    }

    /**
     * Lớp ViewHolder để giữ và quản lý các view của mỗi item trong RecyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView m_ItemName;       // View hiển thị tên sản phẩm
        private ImageView m_ItemImage;     // View hiển thị hình ảnh sản phẩm
        private TextView m_ItemPrice;      // View hiển thị giá sản phẩm
        private ImageButton m_LikeButton;  // Nút yêu thích/bỏ yêu thích
        private ImageButton m_AddToCartButton;  // Nút thêm vào giỏ hàng
        
        /**
         * Constructor của ViewHolder
         * @param itemView View đại diện cho một item trong danh sách
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            m_ItemName = itemView.findViewById(R.id.itemName);
            m_ItemImage = itemView.findViewById(R.id.itemImage);
            m_ItemPrice = itemView.findViewById(R.id.itemPrice);
            m_LikeButton = itemView.findViewById(R.id.likeButton);
            m_AddToCartButton = itemView.findViewById(R.id.addToCartButton);
        }
    }
}