package com.example.argapp.Controllers;

import com.example.argapp.Classes.Item;
import com.example.argapp.Classes.OrderBill;
import com.example.argapp.Classes.OrderBillItem;
import com.example.argapp.Classes.ShoppingCart;
import com.example.argapp.Classes.User;
import com.example.argapp.Models.UserModel;

import java.util.HashMap;

/**
 * Lớp UserController đóng vai trò trung gian giữa giao diện người dùng và UserModel
 * Áp dụng mô hình MVC (Model-View-Controller) để tách biệt logic xử lý và dữ liệu
 */
public class UserController {
    private UserModel m_UserModel;  // Đối tượng UserModel để tương tác với dữ liệu người dùng trong Firebase

    /**
     * Constructor khởi tạo UserController
     * Tạo một instance mới của UserModel
     */
    public UserController()
    {
        m_UserModel = new UserModel();  // Khởi tạo model để xử lý dữ liệu người dùng
    }
    
    /**
     * Xử lý quá trình đăng nhập người dùng
     * @param i_Email Email người dùng nhập vào
     * @param i_Password Mật khẩu người dùng nhập vào
     * @param callback Interface callback để trả về kết quả xác thực
     */
    public void Login(String i_Email, String i_Password, UserModel.AuthCallback callback) {
        m_UserModel.Login(i_Email, i_Password, callback);  // Chuyển yêu cầu đăng nhập cho UserModel xử lý
    }

    /**
     * Xử lý quá trình đăng ký người dùng mới
     * @param i_FirstName Tên của người dùng
     * @param i_LastName Họ của người dùng
     * @param i_Email Email đăng ký
     * @param i_Password Mật khẩu đăng ký
     * @param i_PhoneNumber Số điện thoại người dùng
     * @param callback Interface callback để trả về kết quả đăng ký
     */
    public void Register(String i_FirstName, String i_LastName, String i_Email, String i_Password, String i_PhoneNumber, UserModel.AuthCallback callback)
    {
        // Tạo đối tượng User mới với thông tin người dùng cung cấp
        User newUser = new User(i_FirstName, i_LastName, i_Password, i_Email, i_PhoneNumber);
        // Chuyển yêu cầu đăng ký và đối tượng User cho UserModel xử lý
        m_UserModel.Register(newUser, callback);
    }

    /**
     * Lấy thông tin người dùng hiện tại từ cơ sở dữ liệu
     * @param callback Interface callback để trả về thông tin người dùng
     */
    public void GetUser(UserModel.UserCallback callback)
    {
        m_UserModel.GetUser(callback);  // Chuyển yêu cầu lấy thông tin người dùng cho UserModel xử lý
    }

    /**
     * Cập nhật thông tin người dùng
     * @param firstName Tên người dùng
     * @param lastName Họ người dùng
     * @param phoneNumber Số điện thoại
     * @param address Địa chỉ
     * @param callback Callback thông báo kết quả
     */
    public void updateUserProfile(String firstName, String lastName, String phoneNumber, String address, UserModel.UpdateProfileCallback callback) {
        m_UserModel.updateUserProfile(null, firstName, lastName, phoneNumber, address, callback);
    }

    /**
     * Cập nhật giỏ hàng của người dùng vào cơ sở dữ liệu
     * @param i_UserShoppingCart Giỏ hàng mới cần cập nhật
     * @param callback Interface callback để thông báo kết quả cập nhật
     */
    public void UpdateShoppingCart(ShoppingCart i_UserShoppingCart, UserModel.UpdateShoppingCartCallback callback)
    {
        m_UserModel.UpdateShoppingCart(i_UserShoppingCart, callback);  // Chuyển yêu cầu cập nhật giỏ hàng cho UserModel
    }

    /**
     * Cập nhật danh sách sản phẩm yêu thích của người dùng vào cơ sở dữ liệu
     * @param i_UserLikedItemList Danh sách sản phẩm yêu thích mới cần cập nhật
     * @param callback Interface callback để thông báo kết quả cập nhật
     */
    public void UpdateLikedItemsList(HashMap<String, Item> i_UserLikedItemList, UserModel.UpdateLikedItemsListCallback callback)
    {
        m_UserModel.UpdateLikedItemsList(i_UserLikedItemList, callback);  // Chuyển yêu cầu cập nhật ds yêu thích cho UserModel
    }

    /**
     * Lấy giỏ hàng của người dùng hiện tại từ cơ sở dữ liệu
     * @param callback Interface callback để trả về giỏ hàng
     */
    public void GetUserShoppingCart(UserModel.ShoppingCartCallback callback)
    {
        m_UserModel.GetUserShoppingCart(callback);  // Chuyển yêu cầu lấy giỏ hàng cho UserModel xử lý
    }

    /**
     * Lấy danh sách sản phẩm yêu thích của người dùng hiện tại từ cơ sở dữ liệu
     * @param callback Interface callback để trả về danh sách sản phẩm yêu thích
     */
    public void GetUserLikedItemsList(UserModel.LikedItemsCallback callback)
    {
        m_UserModel.GetUserLikedItemsList(callback);  // Chuyển yêu cầu lấy ds yêu thích cho UserModel xử lý
    }


    /**
     * Lưu đơn hàng vào cơ sở dữ liệu
     * @param orderBill Đơn hàng cần lưu
     * @param callback Interface callback để thông báo kết quả
     */
    public void saveOrderBill(OrderBill orderBill, UserModel.SaveOrderBillCallback callback) {
        m_UserModel.saveOrderBill(orderBill, callback);
    }
    /**
     * Lấy tất cả đơn hàng của người dùng từ cơ sở dữ liệu
     * @param callback Interface callback để trả về danh sách đơn hàng
     */
    public void getUserOrderBills(UserModel.OrderBillsCallback callback) {
        m_UserModel.getUserOrderBills(callback);
    }

    /**
     * Lấy chi tiết đơn hàng theo ID
     * @param orderBillId ID của đơn hàng cần lấy thông tin
     * @param callback Interface callback để trả về chi tiết đơn hàng
     */
    public void getOrderDetail(String orderBillId, UserModel.OrderDetailCallback callback) {
        m_UserModel.getOrderDetail(orderBillId, callback);
    }
}