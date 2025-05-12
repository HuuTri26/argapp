package com.example.argapp.Activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.argapp.Classes.Item;
import com.example.argapp.Classes.ShoppingCart;
import com.example.argapp.Classes.User;
import com.example.argapp.Controllers.UserController;
import com.example.argapp.Interfaces.OnLikedItemsListUpdatedListener;
import com.example.argapp.Interfaces.OnShoppingCartUpdatedListener;
import com.example.argapp.Models.UserModel;
import com.example.argapp.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;

/**
 * MainActivity là activity chính của ứng dụng, quản lý tất cả các fragment và chức năng chính
 * Lớp này xử lý:
 * - Đăng nhập/Đăng ký người dùng
 * - Quản lý giỏ hàng và danh sách yêu thích
 * - Điều hướng giữa các màn hình khác nhau
 * - Cập nhật giao diện người dùng dựa trên dữ liệu từ Firebase
 */
public class MainActivity extends AppCompatActivity {

    // Biến điều khiển người dùng, xử lý tương tác với Firebase
    private UserController m_UserController;
    // Đối tượng chứa thông tin người dùng hiện tại
    private User m_User;
    // Giỏ hàng của người dùng hiện tại
    private ShoppingCart m_UserShoppingCart;
    // Danh sách các mặt hàng được yêu thích, với key là tên sản phẩm
    private HashMap<String, Item> m_UserLikedItemsList;
    // Interface lắng nghe sự kiện cập nhật danh sách yêu thích
    private OnLikedItemsListUpdatedListener m_LikedItemsListUpdatedListener;
    // Interface lắng nghe sự kiện cập nhật giỏ hàng
    private OnShoppingCartUpdatedListener m_ShoppingCartUpdatedListener;
    // Thanh điều hướng dưới của ứng dụng
    private BottomNavigationView m_BottomNavigationView;
    // Badge hiển thị số lượng trên các nút điều hướng
    private BadgeDrawable m_BadgeDrawable;
    // Điều khiển Navigation giữa các fragment
    private NavController m_NavContorller;

    /**
     * Khởi tạo activity và thiết lập các thành phần giao diện
     * @param savedInstanceState Trạng thái đã lưu của activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kích hoạt chế độ edge-to-edge cho hiển thị toàn màn hình
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Xử lý inset để hiển thị UI phù hợp với thanh trạng thái và thanh điều hướng
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Khởi tạo NavHostFragment và NavController để điều hướng giữa các fragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        m_NavContorller = navHostFragment.getNavController();
        // Tìm và gán thanh điều hướng dưới
        m_BottomNavigationView = findViewById(R.id.navigationView);
        // Thêm code này để loại bỏ màu tint mặc định
        m_BottomNavigationView.setItemIconTintList(null);
        // Khởi tạo UserController để tương tác với Firebase
        m_UserController = new UserController();

        // Kiểm tra xem có cần tự động đăng nhập không
        checkAutoLogin();

        // Vô hiệu hóa các nút điều hướng giỏ hàng và danh sách yêu thích ban đầu
        // (được kích hoạt sau khi đăng nhập thành công)
        m_BottomNavigationView.getMenu().findItem(R.id.shoppingCartBarButton).setEnabled(false);
        m_BottomNavigationView.getMenu().findItem(R.id.likedItemsBarButton).setEnabled(false);

        // Thiết lập listener để xử lý sự kiện khi người dùng chọn các mục trong thanh điều hướng
        m_BottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                boolean isHandled = false;

                // Điều hướng đến fragment tương ứng dựa trên nút được nhấn
                if (itemId == R.id.homeButton) {
                    m_NavContorller.navigate(R.id.categories_page);
                    isHandled = true;
                } else if (itemId == R.id.shoppingCartBarButton) {
                    m_NavContorller.navigate(R.id.shoppingCartPage);
                    isHandled = true;
                } else if (itemId == R.id.likedItemsBarButton) {
                    m_NavContorller.navigate(R.id.likedItemsPage);
                    isHandled = true;
                }
                else if(itemId == R.id.searchBarButton)
                {
                    m_NavContorller.navigate(R.id.searchPage);
                    isHandled = true;
                }
                else if(itemId == R.id.profileBarButton)
                {
                    m_NavContorller.navigate(R.id.profilePage);
                    isHandled = true;
                }

                return isHandled;
            }
        });

        // Thiết lập listener để cập nhật trạng thái của thanh điều hướng dựa trên fragment hiện tại
        m_NavContorller.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destinationId = destination.getId();
            // Đánh dấu nút tương ứng dựa trên fragment hiện tại
            if (destinationId == R.id.categories_page) {
                m_BottomNavigationView.getMenu().findItem(R.id.homeButton).setChecked(true);
            }
            else if (destinationId == R.id.shoppingCartPage) {
                m_BottomNavigationView.getMenu().findItem(R.id.shoppingCartBarButton).setChecked(true);
            }
            else if (destinationId == R.id.likedItemsPage) {
                m_BottomNavigationView.getMenu().findItem(R.id.likedItemsBarButton).setChecked(true);
            }
            else if(destinationId == R.id.searchPage)
            {
                m_BottomNavigationView.getMenu().findItem(R.id.searchBarButton).setChecked(true);
            }
            else if (destinationId == R.id.profileBarButton)
            {
                m_BottomNavigationView.getMenu().findItem(R.id.profileBarButton).setChecked(true);
            } else {
                // Bỏ đánh dấu tất cả các nút nếu fragment hiện tại không phải là một trong các fragment chính
                m_BottomNavigationView.getMenu().findItem(R.id.homeButton).setChecked(false);
                m_BottomNavigationView.getMenu().findItem(R.id.shoppingCartBarButton).setChecked(false);
                m_BottomNavigationView.getMenu().findItem(R.id.likedItemsBarButton).setChecked(false);
                m_BottomNavigationView.getMenu().findItem(R.id.searchBarButton).setChecked(false);
                m_BottomNavigationView.getMenu().findItem(R.id.profileBarButton).setChecked(false);
            }
        });
    }
    /**
     * Trả về đối tượng UserController để sử dụng trong các fragment
     * @return UserController hiện tại
     */
    public UserController getUserController() {
        return m_UserController;
    }
    /**
     * Thiết lập listener cho sự kiện cập nhật danh sách yêu thích
     * @param listener Interface lắng nghe sự kiện
     */
    public void SetLikedItemsListUpdateListener(OnLikedItemsListUpdatedListener listener) {
        this.m_LikedItemsListUpdatedListener = listener;
    }

    /**
     * Thiết lập listener cho sự kiện cập nhật giỏ hàng
     * @param listener Interface lắng nghe sự kiện
     */
    public void SetShoppingCartUpdatedListener(OnShoppingCartUpdatedListener listener)
    {
        this.m_ShoppingCartUpdatedListener = listener;
    }

    /**
     * Xử lý đăng nhập người dùng với email và mật khẩu
     * @param email Email đăng nhập
     * @param password Mật khẩu
     */
    public void Login(String email, String password) {
        m_UserController.Login(email, password, new UserModel.AuthCallback() {
            @Override
            public void onSuccess() {
                // Hiển thị thông báo đăng nhập thành công
                Toast.makeText(MainActivity.this, "Login Succeeded", Toast.LENGTH_SHORT).show();
                // Điều hướng đến trang danh mục
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_login_page_to_categories_page);

                // Lấy giỏ hàng của người dùng từ Firebase
                FetchUserShoppingCartFromFB(new UserModel.ShoppingCartCallback() {
                    @Override
                    public void onSuccess(ShoppingCart userShoppingCart) {
                        m_UserShoppingCart = userShoppingCart;
                        // Cập nhật hiển thị số lượng trên nút giỏ hàng
                        UpdateShoppingCartNavigationView();
                    }

                    @Override
                    public void onFailure(DatabaseError error) {
                        Toast.makeText(MainActivity.this, "The email or password are invalid", Toast.LENGTH_SHORT).show();
                    }
                });

                // Lấy danh sách yêu thích của người dùng từ Firebase
                FetchUserLikedItemsListFromFB(new UserModel.LikedItemsCallback() {
                    @Override
                    public void onSuccess(HashMap<String, Item> userLikedItemsList) {
                        m_UserLikedItemsList = userLikedItemsList;
                        // Cập nhật hiển thị số lượng trên nút danh sách yêu thích
                        UpdateLikedItemsNavigationView();
                    }

                    @Override
                    public void onFailure(DatabaseError error) {
                        Toast.makeText(MainActivity.this, "The email or password are invalid", Toast.LENGTH_SHORT).show();
                    }
                });

                // Kích hoạt thanh điều hướng sau khi đăng nhập thành công
                EnableNavigationView();
            }

            @Override
            public void onFailure(Exception i_Exception) {
                // Hiển thị thông báo nếu đăng nhập thất bại
                Toast.makeText(MainActivity.this, "The email or password are invalid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Xử lý đăng ký người dùng mới
     * @param firstName Tên
     * @param lastName Họ
     * @param email Email
     * @param password Mật khẩu
     * @param phoneNumber Số điện thoại
     */
    public void Register(String firstName, String lastName, String email, String password, String phoneNumber) {
        m_UserController.Register(firstName, lastName, email, password, phoneNumber, new UserModel.AuthCallback() {
            @Override
            public void onSuccess() {
                // Hiển thị thông báo đăng ký thành công
                Toast.makeText(MainActivity.this, "Registration Succeeded", Toast.LENGTH_SHORT).show();
                // Điều hướng đến trang đăng nhập
                NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
                NavController navController = navHostFragment.getNavController();
                navController.navigate(R.id.action_signup_page_to_login_page);
            }

            @Override
            public void onFailure(Exception i_Exception) {
                // Hiển thị thông báo nếu đăng ký thất bại
                Toast.makeText(MainActivity.this, "Authentication failed, please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Lấy thông tin người dùng hiện tại từ Firebase
     */
    public void GetUser()
    {
        m_UserController.GetUser(new UserModel.UserCallback() {
            @Override
            public void onSuccess(User user) {
                m_User = user;
                ActionBar actionBar = getSupportActionBar();

                // Cập nhật tiêu đề ActionBar với tên người dùng
                if (actionBar != null) {
                    actionBar.setTitle("Welcome, " + m_User.getFirstName() + " " + m_User.getLastName());
                }
            }

            @Override
            public void onFailure(DatabaseError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Lấy giỏ hàng của người dùng từ Firebase
     * @param callback Callback xử lý kết quả
     */
    public void FetchUserShoppingCartFromFB(UserModel.ShoppingCartCallback callback)
    {
        m_UserController.GetUserShoppingCart(callback);
    }

    /**
     * Lấy danh sách yêu thích của người dùng từ Firebase
     * @param callback Callback xử lý kết quả
     */
    public void FetchUserLikedItemsListFromFB(UserModel.LikedItemsCallback callback)
    {
        m_UserController.GetUserLikedItemsList(callback);
    }

    /**
     * Xóa một mặt hàng khỏi giỏ hàng
     * @param i_Item Mặt hàng cần xóa
     */
    public void RemoveItem(Item i_Item)
    {
        // Xóa mặt hàng khỏi giỏ hàng
        m_UserShoppingCart.getShoppingCart().remove(i_Item.getName());
        // Cập nhật tổng giá trị giỏ hàng
        m_UserShoppingCart.setTotalPrice(m_UserShoppingCart.getTotalPrice() - (i_Item.getPrice() * i_Item.getQuantity()));

        // Nếu mặt hàng cũng có trong danh sách yêu thích, cập nhật số lượng thành 0
        if(m_UserLikedItemsList.containsKey(i_Item.getName())) {
            m_UserLikedItemsList.get(i_Item.getName()).setQuantity(0);
        }

        // Cập nhật giỏ hàng và danh sách yêu thích lên Firebase
        UpdateShoppingCart(m_UserShoppingCart);
        UpdateLikedItemsList(m_UserLikedItemsList);
    }

    /**
     * Cập nhật danh sách yêu thích lên Firebase và thông báo cho các listener
     * @param i_UserLikedItemsList Danh sách yêu thích mới
     */
    public void UpdateLikedItemsList(HashMap<String, Item> i_UserLikedItemsList)
    {
        m_UserLikedItemsList = i_UserLikedItemsList;

        m_UserController.UpdateLikedItemsList(i_UserLikedItemsList, new UserModel.UpdateLikedItemsListCallback() {
            @Override
            public void onSuccess() {
                // Thông báo cho listener nếu có
                if(m_LikedItemsListUpdatedListener != null) {
                    m_LikedItemsListUpdatedListener.onLikedItemsListUpdated();
                }

                // Cập nhật hiển thị số lượng trên nút danh sách yêu thích
                UpdateLikedItemsNavigationView();
            }

            @Override
            public void onFailure(Exception error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Cập nhật giỏ hàng lên Firebase và thông báo cho các listener
     * @param i_UserShoppingCart Giỏ hàng mới
     */
    public void UpdateShoppingCart(ShoppingCart i_UserShoppingCart)
    {
        m_UserShoppingCart = i_UserShoppingCart;

        m_UserController.UpdateShoppingCart(i_UserShoppingCart, new UserModel.UpdateShoppingCartCallback() {
            @Override
            public void onSuccess() {
                // Thông báo cho listener nếu có
                if(m_ShoppingCartUpdatedListener != null) {
                    m_ShoppingCartUpdatedListener.OnShoppingCartUpdated();
                }

                // Cập nhật hiển thị số lượng trên nút giỏ hàng
                UpdateShoppingCartNavigationView();
            }

            @Override
            public void onFailure(Exception error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Thiết lập dữ liệu giỏ hàng và danh sách yêu thích
     * @param i_UserShoppingCart Giỏ hàng
     * @param i_UserLikedItemsList Danh sách yêu thích
     */
    public void SetData(ShoppingCart i_UserShoppingCart, HashMap<String, Item> i_UserLikedItemsList)
    {
        m_UserShoppingCart = i_UserShoppingCart;
        m_UserLikedItemsList = i_UserLikedItemsList;
    }

    /**
     * Lấy giỏ hàng của người dùng hiện tại
     * @return Giỏ hàng của người dùng
     */
    public ShoppingCart GetUserShoppingCart()
    {
        return m_UserShoppingCart;
    }

    /**
     * Lấy danh sách yêu thích của người dùng hiện tại
     * @return Danh sách yêu thích của người dùng
     */
    public HashMap<String, Item> GetUserLikedItemsList()
    {
        return m_UserLikedItemsList;
    }

    /**
     * Ẩn thanh điều hướng dưới
     */
    public void DisableNavigationView()
    {
        m_BottomNavigationView.setVisibility(View.GONE);
    }
    
    /**
     * Hiện thanh điều hướng dưới và chọn nút Home
     */
    public void EnableNavigationView()
    {
        m_BottomNavigationView.setVisibility(View.VISIBLE);
        m_BottomNavigationView.setSelectedItemId(R.id.homeButton);
    }
    
    /**
     * Cập nhật hiển thị số lượng mặt hàng trên nút giỏ hàng trong thanh điều hướng
     */
    public void UpdateShoppingCartNavigationView()
    {
        // Lấy số lượng mặt hàng trong giỏ hàng
        int shoppingCartQuantity = m_UserShoppingCart.getShoppingCart().size();

        // Tạo hoặc lấy badge cho nút giỏ hàng
        m_BadgeDrawable = m_BottomNavigationView.getOrCreateBadge(R.id.shoppingCartBarButton);

        // Hiển thị số lượng nếu có mặt hàng, ngược lại xóa badge
        if(shoppingCartQuantity > 0) {
            m_BadgeDrawable.setNumber(shoppingCartQuantity);
        }else
        {
            m_BottomNavigationView.removeBadge(R.id.shoppingCartBarButton);
        }

        // Kích hoạt nút giỏ hàng
        m_BottomNavigationView.getMenu().findItem(R.id.shoppingCartBarButton).setEnabled(true);
    }

    /**
     * Cập nhật hiển thị số lượng mặt hàng trên nút danh sách yêu thích trong thanh điều hướng
     */
    public void UpdateLikedItemsNavigationView()
    {
        // Lấy số lượng mặt hàng trong danh sách yêu thích
        int likedItemsListQuantity = m_UserLikedItemsList.size();

        // Tạo hoặc lấy badge cho nút danh sách yêu thích
        m_BadgeDrawable = m_BottomNavigationView.getOrCreateBadge(R.id.likedItemsBarButton);

        // Hiển thị số lượng nếu có mặt hàng, ngược lại xóa badge
        if(likedItemsListQuantity > 0) {
            m_BadgeDrawable.setNumber(likedItemsListQuantity);
        }else
        {
            m_BottomNavigationView.removeBadge(R.id.likedItemsBarButton);
        }

        // Kích hoạt nút danh sách yêu thích
        m_BottomNavigationView.getMenu().findItem(R.id.likedItemsBarButton).setEnabled(true);
    }

    /**
     * Kiểm tra và thực hiện đăng nhập tự động nếu có thông tin lưu trữ
     */
    private void checkAutoLogin() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean("isRemembered", false);
        
        if (rememberMe) {
            String email = sharedPreferences.getString("emailAddress", "");
            String password = sharedPreferences.getString("password", "");
            
            if (!email.isEmpty() && !password.isEmpty()) {
                // Tự động đăng nhập với thông tin đã lưu
                Login(email, password);
            }
        }
    }
}