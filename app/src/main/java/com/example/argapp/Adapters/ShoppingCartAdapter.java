package com.example.argapp.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Classes.Coupon;
import com.example.argapp.Classes.CouponList;
import com.example.argapp.Classes.Item;
import com.example.argapp.Interfaces.OnCouponsFetchedListener;
import com.example.argapp.Interfaces.OnShoppingCartItemListener;
import com.example.argapp.R;

import java.util.Date;
import java.util.List;

/**
 * Adapter tùy chỉnh để hiển thị danh sách các sản phẩm trong giỏ hàng
 * Quản lý hiển thị và xử lý tương tác với các sản phẩm đã được thêm vào giỏ hàng
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private List<Item> m_ShoppingCartAsList;        // Danh sách các sản phẩm trong giỏ hàng
    private CouponList m_CouponsList;            // Danh sách các mã giảm giá (nếu có)
    private MainActivity m_HostedActivity;          // Activity chứa RecyclerView
    private OnShoppingCartItemListener m_Listener;  // Interface callback để xử lý sự kiện trên các sản phẩm trong giỏ hàng

    /**
     * Constructor cho ShoppingCartAdapter
     *
     * @param i_UserShoppingCartAsList Danh sách các sản phẩm trong giỏ hàng
     * @param i_HostedActivity         Activity chứa RecyclerView
     * @param i_Listener               Listener để xử lý sự kiện trên các sản phẩm trong giỏ hàng
     */
    public ShoppingCartAdapter(List<Item> i_UserShoppingCartAsList, MainActivity i_HostedActivity, OnShoppingCartItemListener i_Listener) {
        m_HostedActivity = i_HostedActivity;
        m_ShoppingCartAsList = i_UserShoppingCartAsList;
        m_Listener = i_Listener;
        m_CouponsList = new CouponList();
    }

    /**
     * Tạo ViewHolder mới bằng cách inflate layout cho item giỏ hàng
     *
     * @param parent   ViewGroup cha chứa ViewHolder mới
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
     *
     * @param holder   ViewHolder được gắn dữ liệu
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

        // Thiết lập sự kiện click cho nút áp dụng mã giảm giá (nếu có)
        holder.m_ApplyCouponButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy mã giảm giá từ EditText
                String i_CouponId = holder.m_CouponEditText.getText().toString().trim();

                if (i_CouponId.isEmpty()) {
                    Toast.makeText(m_HostedActivity, "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (m_CouponsList == null) {
                    m_CouponsList = new CouponList();
                }

                // Gọi callback để áp dụng mã giảm giá
                m_CouponsList.getCouponsByCouponId(m_HostedActivity, i_CouponId, new OnCouponsFetchedListener() {
                    @Override
                    public void onCouponsFetched(List<Coupon> coupons) {
                        if (coupons.isEmpty()) {
                            Toast.makeText(m_HostedActivity, "Mã giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                        } else {
                            Coupon coupon = coupons.get(0);  // Lấy mã giảm giá đầu tiên (nếu có)

                            // Kiểm tra xem coupon có áp dụng cho sản phẩm này không
                            if (coupon.getProductId() != null && !isProductMatched(coupon.getProductId(), shoppingCartItem)) {
                                Toast.makeText(m_HostedActivity, "Mã giảm giá không áp dụng cho sản phẩm này", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            if (coupon.getStartDate() != null && coupon.getEndDate() != null) {
                                // Kiểm tra xem mã giảm giá có còn hiệu lực không
                                Date currentDate = new Date();

                                // Debug logging để kiểm tra thời gian
                                Log.d("Coupon", "Current Date: " + currentDate);
                                Log.d("Coupon", "Coupon Start Date: " + coupon.getStartDate());
                                Log.d("Coupon", "Coupon End Date: " + coupon.getEndDate());
                                Log.d("Coupon", "Current time millis: " + currentDate.getTime());
                                Log.d("Coupon", "Start time millis: " + coupon.getStartDate().getTime());
                                Log.d("Coupon", "End time millis: " + coupon.getEndDate().getTime());

                                boolean isBeforeStart = currentDate.before(coupon.getStartDate());
                                boolean isAfterEnd = currentDate.after(coupon.getEndDate());

                                Log.d("Coupon", "Is before start: " + isBeforeStart);
                                Log.d("Coupon", "Is after end: " + isAfterEnd);

                                if (isBeforeStart || isAfterEnd) {
                                    String message;
                                    if (isBeforeStart) {
                                        message = "Mã giảm giá chưa có hiệu lực. Có hiệu lực từ: " + coupon.getStartDate();
                                    } else {
                                        message = "Mã giảm giá đã hết hạn vào: " + coupon.getEndDate();
                                    }
                                    Toast.makeText(m_HostedActivity, message, Toast.LENGTH_LONG).show();
                                    return;
                                }

                                // Áp dụng mã giảm giá
                                double originalPrice = shoppingCartItem.getPrice();
                                double discountValue = coupon.getDiscountValue();
                                double discountedPrice;

                                if ("percentage".equals(coupon.getType())) {
                                    // Giảm giá theo phần trăm
                                    discountedPrice = originalPrice - (originalPrice * discountValue / 100);
                                } else if ("fixed".equals(coupon.getType())) {
                                    // Giảm giá cố định
                                    discountedPrice = Math.max(0, originalPrice - discountValue);
                                } else {
                                    Toast.makeText(m_HostedActivity, "Loại mã giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Toast.makeText(m_HostedActivity, "Áp dụng mã giảm giá thành công!", Toast.LENGTH_SHORT).show();
                                shoppingCartItem.setPrice(discountedPrice);  // Cập nhật giá sản phẩm trong giỏ hàng
                                holder.m_ItemPrice.setText(String.format("%.2f", discountedPrice));  // Cập nhật giá hiển thị

                                // Xóa text trong EditText sau khi áp dụng thành công
                                holder.m_CouponEditText.setText("");
                            } else {
                                Toast.makeText(m_HostedActivity, "Mã giảm giá không có thông tin thời hạn", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    /**
     * Trả về số lượng item trong giỏ hàng
     *
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
        private EditText m_CouponEditText;  // EditText để nhập mã giảm giá (nếu cần)
        private Button m_ApplyCouponButton;  // Nút áp dụng mã giảm giá (nếu cần)


        /**
         * Constructor của ViewHolder
         *
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
            m_CouponEditText = itemView.findViewById(R.id.couponEditText);
            m_ApplyCouponButton = itemView.findViewById(R.id.applyCouponButton);
        }
    }

    /**
     * Kiểm tra xem sản phẩm trong giỏ hàng có khớp với mã giảm giá không
     *
     * @param i_CouponProductId  ID sản phẩm của mã giảm giá (format: "category/itemId")
     * @param i_ShoppingCartItem Sản phẩm trong giỏ hàng
     * @return true nếu sản phẩm khớp, false nếu không
     */
    private boolean isProductMatched(String i_CouponProductId, Item i_ShoppingCartItem) {
        if (i_CouponProductId == null || i_CouponProductId.isEmpty()) {
            return false;
        }

        // Debug thông tin item trước
        Log.d("Coupon", "Item Type: " + i_ShoppingCartItem.getType());
        Log.d("Coupon", "Item ID: " + i_ShoppingCartItem.getId());
        Log.d("Coupon", "Item Name: " + i_ShoppingCartItem.getName());

        // Nếu type và id không null, sử dụng format "category/itemId"
        if (i_ShoppingCartItem.getType() != null && i_ShoppingCartItem.getId() != null) {
            String itemIdentifier = i_ShoppingCartItem.getType() + "/" + i_ShoppingCartItem.getId();
            boolean isMatched = i_CouponProductId.equals(itemIdentifier);

            Log.d("Coupon", "Coupon ProductId: " + i_CouponProductId);
            Log.d("Coupon", "Item Identifier: " + itemIdentifier);
            Log.d("Coupon", "Match result: " + isMatched);

            return isMatched;
        } else {
            // Fallback: sử dụng name-based matching
            Log.d("Coupon", "Using name-based matching as fallback");
            String itemName = i_ShoppingCartItem.getName().toLowerCase();

            // Mapping dựa trên tên sản phẩm
            if (i_CouponProductId.equals("beverages/item1") && itemName.contains("orange juice")) {
                Log.d("Coupon", "Matched Orange Juice with beverages/item1");
                return true;
            } else if (i_CouponProductId.equals("beverages/item2") && itemName.contains("strawberry juice")) {
                Log.d("Coupon", "Matched Strawberry Juice with beverages/item2");
                return true;
            }

            Log.d("Coupon", "No match found for: " + itemName);
            return false;
        }
    }
}
