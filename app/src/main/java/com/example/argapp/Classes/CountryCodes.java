package com.example.argapp.Classes;

import java.util.Arrays;
import java.util.List;

/**
 * Lớp CountryCodes cung cấp danh sách mã quốc gia điện thoại được sử dụng trong ứng dụng
 * Sắp xếp các mã theo thứ tự số tăng dần để thuận tiện cho người dùng
 */
public class CountryCodes {
    /**
     * Phương thức tĩnh để lấy danh sách các mã quốc gia đã được sắp xếp
     * @return Danh sách các mã quốc gia dạng String (có dấu + ở đầu)
     */
    public static List<String> GetCountryCodes()
    {
        // Khởi tạo danh sách các mã quốc gia với Arrays.asList
        List<String> countryCodes = Arrays.asList(
                // Bắc Mỹ
                "+1",   // Hoa Kỳ, Canada

                // Tây Âu
                "+44",  // Vương quốc Anh
                "+49",  // Đức
                "+33",  // Pháp
                "+34",  // Tây Ban Nha
                "+39",  // Ý
                "+31",  // Hà Lan
                "+32",  // Bỉ
                "+41",  // Thụy Sĩ
                "+43",  // Áo
                "+351", // Bồ Đào Nha
                "+353", // Ireland

                // Bắc Âu
                "+46",  // Thụy Điển
                "+45",  // Đan Mạch
                "+47",  // Na Uy
                "+358", // Phần Lan
                "+354", // Iceland

                // Đông Âu
                "+48",  // Ba Lan
                "+36",  // Hungary
                "+420", // Cộng hòa Séc
                "+421", // Slovakia
                "+40",  // Romania
                "+371", // Latvia
                "+372", // Estonia
                "+370", // Lithuania
                "+380", // Ukraine
                "+375", // Belarus
                "+373", // Moldova

                // Nam Âu
                "+30",  // Hy Lạp
                "+357", // Cyprus
                "+378", // San Marino
                "+386", // Slovenia
                "+385", // Croatia
                "+387", // Bosnia và Herzegovina
                "+389", // Bắc Macedonia
                "+382", // Montenegro
                "+381", // Serbia
                "+383", // Kosovo
                "+356", // Malta
                "+350", // Gibraltar

                // Các khu vực Châu Âu khác
                "+378", // Andorra
                "+352", // Luxembourg
                "+423", // Liechtenstein

                // **Châu Á**
                "+60",  // Malaysia
                "+61",  // Australia (thuộc Châu Đại Dương nhưng liệt kê ở đây)
                "+62",  // Indonesia
                "+63",  // Philippines
                "+64",  // New Zealand (thuộc Châu Đại Dương nhưng liệt kê ở đây)
                "+65",  // Singapore
                "+66",  // Thái Lan
                "+81",  // Nhật Bản
                "+82",  // Hàn Quốc
                "+84",  // Việt Nam
                "+86",  // Trung Quốc
                "+90",  // Thổ Nhĩ Kỳ
                "+91",  // Ấn Độ
                "+92",  // Pakistan
                "+93",  // Afghanistan
                "+94",  // Sri Lanka
                "+95",  // Myanmar (Burma)
                "+98",  // Iran
                "+212", // Morocco (Châu Phi, nhưng gần với Châu Á)
                "+962", // Jordan
                "+963", // Syria
                "+964", // Iraq
                "+965", // Kuwait
                "+966", // Saudi Arabia
                "+967", // Yemen
                "+971", // Các Tiểu vương quốc Ả Rập Thống nhất
                "+972", // Israel
                "+973", // Bahrain
                "+974", // Qatar
                "+975", // Bhutan
                "+976", // Mongolia
                "+977", // Nepal
                "+98",  // Iran (trùng lặp trong danh sách)
                "+880", // Bangladesh
                "+981", // Maldives
                "+994", // Azerbaijan
                "+992", // Tajikistan
                "+993", // Turkmenistan
                "+994", // Azerbaijan (trùng lặp trong danh sách)
                "+995", // Georgia
                "+996", // Kyrgyzstan
                "+998"  // Uzbekistan
        );

        // Sắp xếp danh sách các mã quốc gia theo thứ tự số tăng dần
        countryCodes.sort((firstCode, secondCode) -> {
            // Bỏ dấu '+' ở đầu mỗi mã quốc gia và chuyển thành số nguyên để so sánh
            int firstCodeAsInt = Integer.parseInt(firstCode.substring(1));
            int secondCodeAsInt = Integer.parseInt(secondCode.substring(1));

            // So sánh các số nguyên và trả về kết quả (-1, 0, 1) theo quy tắc so sánh
            return Integer.compare(firstCodeAsInt, secondCodeAsInt);
        });

        // Trả về danh sách đã được sắp xếp
        return countryCodes;
    }
}