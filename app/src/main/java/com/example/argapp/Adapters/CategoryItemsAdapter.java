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
import java.util.HashMap;
import java.util.List;
/**
 * Adapter tùy chỉnh để hiển thị danh sách các sản phẩm của một danh mục trong RecyclerView
 * Xử lý hiển thị thông tin sản phẩm và các tương tác (yêu thích, thêm vào giỏ hàng)
 */
@SuppressWarnings("deprecation")
public class CategoryItemsAdapter extends RecyclerView.Adapter<CategoryItemsAdapter.ViewHolder> {
    private List<Item> m_CategoryItemsList;          // Danh sách các sản phẩm thuộc danh mục
    private ShoppingCart m_UserShoppingCart;         // Đối tượng giỏ hàng của người dùng
    private HashMap<String, Item> m_UserLikedItemsList; // Danh sách sản phẩm yêu thích của người dùng
    private MainActivity m_HostedActivity;           // Activity chứa RecyclerView
    private OnItemListener m_Listener;               // Interface callback để xử lý sự kiện của item

    /**
     * Constructor cho CategoryItemsAdapter
     * @param i_HostedActivity Activity chứa RecyclerView
     * @param i_CategoryItemsList Danh sách các sản phẩm thuộc danh mục
     * @param i_UserShoppingCart Giỏ hàng của người dùng
     * @param i_UserLikedItemsList Danh sách sản phẩm yêu thích của người dùng
     * @param i_Listener Listener để xử lý sự kiện trên các sản phẩm
     */
    public CategoryItemsAdapter(MainActivity i_HostedActivity, List<Item> i_CategoryItemsList, ShoppingCart i_UserShoppingCart,
                                HashMap<String, Item> i_UserLikedItemsList, OnItemListener i_Listener)
    {
        this.m_HostedActivity = i_HostedActivity;
        this.m_CategoryItemsList = i_CategoryItemsList;
        this.m_UserShoppingCart = i_UserShoppingCart;
        this.m_UserLikedItemsList = i_UserLikedItemsList;
        this.m_Listener = i_Listener;
    }

    /**
     * Tạo ViewHolder mới bằng cách inflate layout cho item sản phẩm
     * @param parent ViewGroup cha chứa ViewHolder mới
     * @param viewType Loại view (không sử dụng trong trường hợp này)
     * @return ViewHolder mới được khởi tạo
     */

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Gắn dữ liệu từ danh sách vào ViewHolder tại vị trí chỉ định
     * @param holder ViewHolder được gắn dữ liệu
     * @param position Vị trí của item trong danh sách
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item categoryItem = m_CategoryItemsList.get(position);  // Lấy đối tượng Item ở vị trí position
        
        // Lấy ID hình ảnh sản phẩm từ tên file trong resources
        int itemImage = m_HostedActivity.getResources().getIdentifier(
            categoryItem.getImage(), "drawable", m_HostedActivity.getPackageName());

        // Thiết lập hình ảnh và thông tin sản phẩm
        holder.m_CategoryItemImage.setImageResource(itemImage);
        holder.m_CategoryItemName.setText(categoryItem.getName());

        // Định dạng và hiển thị giá sản phẩm với 2 chữ số thập phân
       // double price = categoryItem.getPrice();
      //  holder.m_CategoryItemPrice.setText(String.format("%.2f", price));
        // Định dạng và hiển thị giá sản phẩm với 2 chữ số thập phân và đơn vị tính
        double price = categoryItem.getPrice();
        String priceText = String.format("%.2f", price);
        if (categoryItem.getUnit() != null && !categoryItem.getUnit().isEmpty()) {
            priceText += " / " + categoryItem.getUnit();
        }
        holder.m_CategoryItemPrice.setText(priceText);
        // Cập nhật icon trạng thái yêu thích dựa trên trạng thái hiện tại
        if(isItemLiked(categoryItem))
        {
            holder.m_IsLiked.setImageResource(R.drawable.filled_heart);  // Đã yêu thích
        }
        else
        {
            holder.m_IsLiked.setImageResource(R.drawable.blank_heart);   // Chưa yêu thích
        }

        // Cập nhật icon trạng thái giỏ hàng dựa trên trạng thái hiện tại
        if(isItemAddedToCart(categoryItem))
        {
            holder.m_IsAddedToCart.setImageResource(R.drawable.filled_shopping_cart);  // Đã thêm vào giỏ
        }
        else
        {
            holder.m_IsAddedToCart.setImageResource(R.drawable.blank_shopping_cart);   // Chưa thêm vào giỏ
        }

        // Thiết lập sự kiện click cho item sản phẩm
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Listener.onItemClick(categoryItem);  // Gọi callback với đối tượng Item
            }
        });

        // Thiết lập sự kiện click cho nút thêm vào giỏ hàng
        holder.m_IsAddedToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Listener.onAddToCartClick(categoryItem);  // Gọi callback thêm vào giỏ hàng
            }
        });

        // Thiết lập sự kiện click cho nút yêu thích
        holder.m_IsLiked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_Listener.onLikeClick(categoryItem);  // Gọi callback yêu thích
            }
        });
    }

    /**
     * Trả về số lượng item trong danh sách
     * @return Số lượng sản phẩm
     */
    @Override
    public int getItemCount() {
        return m_CategoryItemsList.size();
    }

    /**
     * Lớp ViewHolder để giữ và quản lý các view của mỗi item trong RecyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView m_CategoryItemImage;    // View hiển thị hình ảnh sản phẩm
        private TextView m_CategoryItemName;      // View hiển thị tên sản phẩm
        private TextView m_CategoryItemPrice;     // View hiển thị giá sản phẩm
        private ImageButton m_IsLiked;            // Nút yêu thích
        private ImageButton m_IsAddedToCart;      // Nút thêm vào giỏ hàng
        
        /**
         * Constructor của ViewHolder
         * @param itemView View đại diện cho một item trong danh sách
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các view từ layout
            m_CategoryItemImage = itemView.findViewById(R.id.itemImage);
            m_CategoryItemName = itemView.findViewById(R.id.itemName);
            m_CategoryItemPrice = itemView.findViewById(R.id.itemPrice);
            m_IsLiked = itemView.findViewById(R.id.likeButton);
            m_IsAddedToCart = itemView.findViewById(R.id.addToCartButton);
        }
    }

    /**
     * Kiểm tra xem một sản phẩm có trong danh sách yêu thích hay không
     * @param i_Item Đối tượng Item cần kiểm tra
     * @return true nếu sản phẩm đã được yêu thích, false nếu chưa
     */
    private boolean isItemLiked(Item i_Item)
    {
        return m_UserLikedItemsList.containsKey(i_Item.getName());
    }

    /**
     * Kiểm tra xem một sản phẩm có trong giỏ hàng hay không
     * @param i_Item Đối tượng Item cần kiểm tra
     * @return true nếu sản phẩm đã được thêm vào giỏ hàng, false nếu chưa
     */
    private boolean isItemAddedToCart(Item i_Item)
    {
        return m_UserShoppingCart.getShoppingCart().containsKey(i_Item.getName());
    }
}