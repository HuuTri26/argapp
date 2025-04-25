package com.example.argapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Classes.Category;
import com.example.argapp.Interfaces.OnCategoryClickListener;
import com.example.argapp.R;

import java.util.List;

/**
 * Adapter cho RecyclerView hiển thị danh sách các danh mục
 * Kết nối dữ liệu danh mục với giao diện người dùng
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<Category> m_CategoriesList;      // Danh sách các đối tượng Category để hiển thị
    private OnCategoryClickListener m_Listener;    // Interface callback để xử lý sự kiện click
    private MainActivity m_HostedActivity;         // Activity chứa RecyclerView để truy cập resources

    /**
     * Constructor cho CategoriesAdapter
     * @param i_HostedActivity Activity chứa RecyclerView
     * @param i_CategoriesList Danh sách các đối tượng Category
     * @param i_Listener Listener để xử lý sự kiện click trên danh mục
     */
    public CategoriesAdapter(MainActivity i_HostedActivity, List<Category> i_CategoriesList, OnCategoryClickListener i_Listener)
    {
        this.m_HostedActivity = i_HostedActivity;
        this.m_CategoriesList = i_CategoriesList;
        this.m_Listener = i_Listener;
    }

    /**
     * Tạo ViewHolder mới bằng cách inflate layout cho item danh mục
     * @param parent ViewGroup cha chứa ViewHolder mới
     * @param viewType Loại view (không sử dụng trong trường hợp này)
     * @return ViewHolder mới được khởi tạo
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new ViewHolder(view, m_Listener);
    }

    /**
     * Gắn dữ liệu từ danh sách vào ViewHolder tại vị trí chỉ định
     * @param holder ViewHolder được gắn dữ liệu
     * @param position Vị trí của item trong danh sách
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = m_CategoriesList.get(position);  // Lấy đối tượng Category ở vị trí position
        
        // Lấy ID hình ảnh từ tên file trong resources
        int categoryImage = m_HostedActivity.getResources().getIdentifier(
                category.getImage(), "drawable", m_HostedActivity.getPackageName());

        // Thiết lập hình ảnh và tên danh mục cho ViewHolder
        holder.m_CategoryImage.setImageResource(categoryImage);
        holder.m_CategoryName.setText(category.getName());

        // Thiết lập sự kiện click cho item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(m_Listener != null)
                {
                    m_Listener.onCategoryClick(category.getId());  // Gọi callback với ID của danh mục
                }
            }
        });
    }

    /**
     * Trả về số lượng item trong danh sách
     * @return Số lượng danh mục
     */
    @Override
    public int getItemCount() {
        return m_CategoriesList.size();
    }

    /**
     * Lớp ViewHolder để giữ và quản lý các view của mỗi item trong RecyclerView
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView m_CategoryImage;  // View hiển thị hình ảnh danh mục
        private TextView m_CategoryName;    // View hiển thị tên danh mục
        
        /**
         * Constructor của ViewHolder
         * @param itemView View đại diện cho một item trong danh sách
         * @param i_Listener Listener xử lý sự kiện click (không dùng trực tiếp trong ViewHolder)
         */
        public ViewHolder(@NonNull View itemView, OnCategoryClickListener i_Listener) {
            super(itemView);
            // Ánh xạ các view từ layout
            m_CategoryImage = itemView.findViewById(R.id.categoryImage);
            m_CategoryName = itemView.findViewById(R.id.categoryName);
        }
    }
}