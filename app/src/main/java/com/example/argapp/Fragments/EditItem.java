package com.example.argapp.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.argapp.Activities.MainActivity;
import com.example.argapp.Classes.Item;
import com.example.argapp.Classes.ShoppingCart;
import com.example.argapp.Interfaces.OnShoppingCartUpdatedListener;
import com.example.argapp.R;
import com.example.argapp.Adapters.ReviewAdapter;
import com.example.argapp.Classes.Review;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    // Các biến cho chức năng đánh giá
    private ReviewAdapter reviewAdapter;
    private RecyclerView reviewsRecyclerView;
    private TextView noReviewsText;
    private CardView feedbackSection;
    private Button ratingButton;
    private Button submitFeedbackButton;
    private RatingBar productRating;
    private EditText feedbackInput;
    private String itemId;
    private String categoryId;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

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

    private TextView m_ItemDescription;  // Thêm biến thành viên mới
    private TextView m_ItemUnit;         // Thêm biến thành viên mới
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
        Bundle args = getArguments();
        if (args != null) {
            m_CurrentItem = (Item) args.getSerializable("item");
            itemId = args.getString("itemId");
            categoryId = args.getString("categoryId");
        }

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Lấy tham chiếu đến MainActivity chứa Fragment này
        m_HostedActivity = (MainActivity) requireActivity();

        // Ánh xạ các view cho chức năng đánh giá
        reviewsRecyclerView = m_View.findViewById(R.id.reviewsRecyclerView);
        noReviewsText = m_View.findViewById(R.id.noReviewsText);
        feedbackSection = m_View.findViewById(R.id.feedbackSection);
        ratingButton = m_View.findViewById(R.id.ratingButton);
        submitFeedbackButton = m_View.findViewById(R.id.submitFeedbackButton);
        productRating = m_View.findViewById(R.id.productRating);
        feedbackInput = m_View.findViewById(R.id.feedbackInput);

        // Thiết lập RecyclerView
        setupReviewsRecyclerView();

        // Thiết lập sự kiện cho nút đánh giá
        ratingButton.setOnClickListener(v -> toggleFeedbackSection());

        // Thiết lập sự kiện cho nút gửi đánh giá
        submitFeedbackButton.setOnClickListener(v -> submitReview());

        // Thêm code xử lý nút đóng form đánh giá
        ImageButton closeRatingButton = m_View.findViewById(R.id.closeRatingButton);
        closeRatingButton.setOnClickListener(v -> toggleFeedbackSection());

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


        m_ItemDescription = m_View.findViewById(R.id.itemDescription);  // Thêm dòng này
        m_ItemUnit = m_View.findViewById(R.id.itemUnit);  // Thêm dòng này
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

        // Hiển thị giá và đơn vị
        String priceWithUnit = String.format("%.2f", m_CurrentItem.getPrice());
        if (m_CurrentItem.getUnit() != null && !m_CurrentItem.getUnit().isEmpty()) {
            priceWithUnit += " / " + m_CurrentItem.getUnit();
        }
        m_ItemPrice.setText(priceWithUnit);

        // Hiển thị mô tả nếu có
        if (m_CurrentItem.getDescription() != null && !m_CurrentItem.getDescription().isEmpty()) {
            m_ItemDescription.setText(m_CurrentItem.getDescription());
            m_ItemDescription.setVisibility(View.VISIBLE);
        } else {
            m_ItemDescription.setVisibility(View.GONE);
        }
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
            * Thiết lập RecyclerView cho phần hiển thị đánh giá
 */
    private void setupReviewsRecyclerView() {
        try {
            // Thiết lập LayoutManager
            reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            // Tạo danh sách rỗng ban đầu
            List<Review> emptyList = new ArrayList<>();

            // Khởi tạo adapter với danh sách rỗng
            reviewAdapter = new ReviewAdapter(emptyList);
            reviewsRecyclerView.setAdapter(reviewAdapter);

            // Hiển thị thông báo "Đang tải đánh giá..."
            noReviewsText.setText("Đang tải đánh giá...");
            noReviewsText.setVisibility(View.VISIBLE);

            // Tải đánh giá từ Firebase
            loadReviews();

        } catch (Exception e) {
            Log.e("ReviewsDebug", "Error in setupReviewsRecyclerView: " + e.getMessage());
            e.printStackTrace();
            // Hiển thị thông báo lỗi
            if (noReviewsText != null) {
                noReviewsText.setText("Không thể tải đánh giá");
                noReviewsText.setVisibility(View.VISIBLE);
            }
            if (reviewsRecyclerView != null) {
                reviewsRecyclerView.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Hiển thị/ẩn phần nhập đánh giá
     */
    /**
     * Hiển thị/ẩn phần nhập đánh giá
     */
    /**
     * Hiển thị/ẩn phần nhập đánh giá
     */
    private void toggleFeedbackSection() {
        try {
            // Kiểm tra trạng thái hiện tại của phần nhập đánh giá
            if (feedbackSection.getVisibility() == View.VISIBLE) {
                // Nếu đang hiển thị, ẩn đi
                feedbackSection.setVisibility(View.GONE);
                ratingButton.setText("Đánh giá sản phẩm");
            } else {
                // Nếu đang ẩn, hiển thị lên
                feedbackSection.setVisibility(View.VISIBLE);
                ratingButton.setText("Đóng đánh giá");

                // Kiểm tra trạng thái đăng nhập
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(getContext(), "Bạn cần đăng nhập để đánh giá sản phẩm", Toast.LENGTH_SHORT).show();
                    feedbackSection.setVisibility(View.GONE);
                    ratingButton.setText("Đánh giá sản phẩm");
                }
            }
        } catch (Exception e) {
            Log.e("ReviewsDebug", "Error in toggleFeedbackSection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Xử lý việc gửi đánh giá mới
     */
    /**
     * Xử lý việc gửi đánh giá mới
     */
    private void submitReview() {
        try {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser == null) {
                Toast.makeText(getContext(), "Bạn cần đăng nhập để đánh giá sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            float rating = productRating.getRating();
            String comment = feedbackInput.getText().toString().trim();

            // Kiểm tra đánh giá hợp lệ
            if (rating == 0) {
                Toast.makeText(getContext(), "Vui lòng đánh giá sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            if (comment.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập nhận xét của bạn", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hiển thị tiến trình đang xử lý
            Toast.makeText(getContext(), "Đang gửi đánh giá...", Toast.LENGTH_SHORT).show();

            // Xác định categoryId và itemId nếu chưa có
            if (categoryId == null || itemId == null) {
                if (m_CurrentItem != null) {
                    String itemName = m_CurrentItem.getName();
                    // Tìm category và itemId dựa trên tên sản phẩm
                    mDatabase.child("Data").child("CategoriesItems")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                                        String foundCategoryId = categorySnapshot.getKey();
                                        for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                                            if (itemSnapshot.child("Name").getValue(String.class) != null &&
                                                    itemSnapshot.child("Name").getValue(String.class).equals(itemName)) {
                                                String foundItemId = itemSnapshot.getKey();
                                                Log.d("ReviewsDebug", "Found item for submission in category: " + foundCategoryId + ", itemId: " + foundItemId);
                                                submitReviewToDatabase(currentUser, rating, comment, foundCategoryId, foundItemId);
                                                return;
                                            }
                                        }
                                    }
                                    // Không tìm thấy sản phẩm
                                    Toast.makeText(getContext(), "Không thể tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Lỗi khi tìm sản phẩm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                    return;
                } else {
                    Toast.makeText(getContext(), "Không thể gửi đánh giá: Thiếu thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // Nếu có sẵn categoryId và itemId
                submitReviewToDatabase(currentUser, rating, comment, categoryId, itemId);
            }

        } catch (Exception e) {
            Log.e("ReviewsDebug", "Error in submitReview: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getContext(), "Có lỗi xảy ra: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gửi đánh giá lên database
     */
    private void submitReviewToDatabase(FirebaseUser currentUser, float rating, String comment, String catId, String itmId) {
        mDatabase.child("Data/Users").child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                String firstName = "";
                                String lastName = "";

                                // Kiểm tra cả hai cách viết (viết hoa/thường) để tương thích với database hiện tại
                                if (snapshot.hasChild("FirstName")) {
                                    firstName = snapshot.child("FirstName").getValue(String.class);
                                } else if (snapshot.hasChild("firstName")) {
                                    firstName = snapshot.child("firstName").getValue(String.class);
                                }

                                if (snapshot.hasChild("LastName")) {
                                    lastName = snapshot.child("LastName").getValue(String.class);
                                } else if (snapshot.hasChild("lastName")) {
                                    lastName = snapshot.child("lastName").getValue(String.class);
                                }

                                String userName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                                if (userName.trim().isEmpty()) {
                                    userName = "Người dùng";
                                }

                                // Tạo đối tượng Review mới
                                Review newReview = new Review(
                                        currentUser.getUid(),
                                        userName,
                                        "drawable/apple", // Sử dụng hình mặc định
                                        rating,
                                        comment,
                                        System.currentTimeMillis()
                                );

                                // Tạo node Reviews nếu chưa tồn tại
                                final DatabaseReference reviewsRef = mDatabase.child("Reviews").child(catId).child(itmId).push();

                                reviewsRef.setValue(newReview).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        if (isAdded() && getContext() != null) {
                                            Toast.makeText(getContext(), "Đánh giá của bạn đã được gửi thành công", Toast.LENGTH_SHORT).show();
                                            productRating.setRating(0);
                                            feedbackInput.setText("");
                                            toggleFeedbackSection();
                                            loadReviews();
                                        }
                                    } else {
                                        if (isAdded() && getContext() != null) {
                                            Toast.makeText(getContext(), "Gửi đánh giá thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                if (isAdded() && getContext() != null) {
                                    Toast.makeText(getContext(), "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("ReviewsDebug", "Error processing user data: " + e.getMessage());
                            e.printStackTrace();
                            if (isAdded() && getContext() != null) {
                                Toast.makeText(getContext(), "Lỗi xử lý thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Tải danh sách đánh giá từ Firebase
     */
    /**
     * Tải danh sách đánh giá từ Firebase
     */
    private void loadReviews() {
        try {
            if (categoryId == null || itemId == null) {
                // Lấy thông tin từ currentItem nếu không có categoryId và itemId
                if (m_CurrentItem != null) {
                    String itemName = m_CurrentItem.getName();
                    // Log để debug
                    Log.d("ReviewsDebug", "Loading reviews for item: " + itemName);

                    // Tìm category và itemId dựa trên tên sản phẩm
                    mDatabase.child("Data").child("CategoriesItems")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                                        String foundCategoryId = categorySnapshot.getKey();
                                        for (DataSnapshot itemSnapshot : categorySnapshot.getChildren()) {
                                            if (itemSnapshot.child("Name").getValue(String.class) != null &&
                                                    itemSnapshot.child("Name").getValue(String.class).equals(itemName)) {
                                                String foundItemId = itemSnapshot.getKey();
                                                Log.d("ReviewsDebug", "Found item in category: " + foundCategoryId + ", itemId: " + foundItemId);
                                                loadReviewsFromDatabase(foundCategoryId, foundItemId);
                                                return;
                                            }
                                        }
                                    }
                                    // Không tìm thấy sản phẩm
                                    updateNoReviewsVisibility(new ArrayList<>());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("ReviewsDebug", "Error finding item: " + error.getMessage());
                                    Toast.makeText(getContext(), "Lỗi khi tìm sản phẩm: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                    return;
                } else {
                    Log.e("ReviewsDebug", "Missing item info for reviews");
                    Toast.makeText(getContext(), "Không thể tải đánh giá: Thiếu thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                    updateNoReviewsVisibility(new ArrayList<>());
                    return;
                }
            }

            // Nếu có sẵn categoryId và itemId thì load trực tiếp
            loadReviewsFromDatabase(categoryId, itemId);

        } catch (Exception e) {
            Log.e("ReviewsDebug", "Error loading reviews: " + e.getMessage());
            e.printStackTrace();
            updateNoReviewsVisibility(new ArrayList<>());
        }
    }

    /**
     * Tải đánh giá từ đường dẫn cụ thể trong database
     */
    private void loadReviewsFromDatabase(String catId, String itmId) {
        Log.d("ReviewsDebug", "Loading reviews from database for category: " + catId + ", item: " + itmId);

        // Kiểm tra xem node Reviews có tồn tại hay không
        mDatabase.child("Reviews").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Nếu node Reviews chưa tồn tại
                    Log.d("ReviewsDebug", "Reviews node does not exist yet");
                    updateNoReviewsVisibility(new ArrayList<>());
                    return;
                }

                // Tiếp tục kiểm tra và load reviews
                DatabaseReference reviewsRef = mDatabase.child("Reviews").child(catId).child(itmId);

                reviewsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Review> reviews = new ArrayList<>();

                        Log.d("ReviewsDebug", "Reviews snapshot has " + snapshot.getChildrenCount() + " items");

                        for (DataSnapshot reviewSnapshot : snapshot.getChildren()) {
                            try {
                                Review review = reviewSnapshot.getValue(Review.class);
                                if (review != null) {
                                    reviews.add(review);
                                    Log.d("ReviewsDebug", "Loaded review from: " + review.getUserName());
                                }
                            } catch (Exception e) {
                                Log.e("ReviewsDebug", "Error parsing review: " + e.getMessage());
                            }
                        }

                        // Cập nhật adapter chỉ khi fragment vẫn attached
                        if (isAdded() && getContext() != null) {
                            reviewAdapter.updateReviews(reviews);
                            updateNoReviewsVisibility(reviews);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ReviewsDebug", "Database error: " + error.getMessage());
                        if (isAdded() && getContext() != null) {
                            Toast.makeText(getContext(), "Lỗi khi tải đánh giá: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReviewsDebug", "Error checking Reviews node: " + error.getMessage());
            }
        });
    }

    /**
     * Cập nhật hiển thị thông báo khi không có đánh giá
     */
    /**
     * Cập nhật hiển thị thông báo khi không có đánh giá
     */
    private void updateNoReviewsVisibility(List<Review> reviews) {
        try {
            if (isAdded() && getContext() != null) {
                if (reviews == null || reviews.isEmpty()) {
                    noReviewsText.setText("Chưa có đánh giá nào cho sản phẩm này");
                    noReviewsText.setVisibility(View.VISIBLE);
                    reviewsRecyclerView.setVisibility(View.GONE);

                    Log.d("ReviewsDebug", "No reviews found, showing message");
                } else {
                    noReviewsText.setVisibility(View.GONE);
                    reviewsRecyclerView.setVisibility(View.VISIBLE);

                    Log.d("ReviewsDebug", "Found " + reviews.size() + " reviews, showing list");
                }
            }
        } catch (Exception e) {
            Log.e("ReviewsDebug", "Error in updateNoReviewsVisibility: " + e.getMessage());
            e.printStackTrace();
        }
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