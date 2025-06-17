package com.example.argapp.Utils;

import com.example.argapp.R;

import java.util.Calendar;

public class SeasonUtil {
    public static String getCurrentSeason() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH); // 0-based (0 = January, 11 = December)

        if (month >= 2 && month <= 4) { // March to May
            return "spring";
        } else if (month >= 5 && month <= 7) { // June to August
            return "summer";
        } else if (month >= 8 && month <= 10) { // September to November
            return "fall";
        } else { // December to February
            return "winter";
        }
    }

    public static int getSeasonBackgroundResourceId(String season) {
        switch (season.toLowerCase()) {
            case "spring":
                return R.drawable.spring_bg;
            case "summer":
                return R.drawable.summer_bg;
            case "fall":
                return R.drawable.fall_bg;
            case "winter":
                return R.drawable.winter_bg;
            default:
                return R.drawable.summer_bg;
        }
    }

    public static int getSeasonIconResourceId(String season) {
        switch (season.toLowerCase()) {
            case "spring":
                return R.drawable.spring_icon;
            case "summer":
                return R.drawable.summer_icon;
            case "fall":
                return R.drawable.fall_icon;
            case "winter":
                return R.drawable.winter_icon;
            default:
                return R.drawable.summer_icon;
        }
    }
}