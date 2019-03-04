package com.workfort.wallpaperworld.app.data.local.appconst;
/*
 *  ****************************************************************************
 *  * Created by : Arhan Ashik on 12/28/2018 at 4:28 PM.
 *  * Email : ashik.pstu.cse@gmail.com
 *  *
 *  * Last edited by : Arhan Ashik on 12/28/2018.
 *  *
 *  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 *  ****************************************************************************
 */

public interface Const {
    interface RequestCode {
        int SEARCH =  10;
        int IMAGE_PREVIEW =  11;
        int GOOGLE_SIGN_IN = 101;
        int FACEBOOK_SIGN_IN = 202;
    }

    interface Key {
        String USER = "user";
        String WALLPAPER_TYPE = "wallpaper_type";
        String WALLPAPER_LIST = "wallpaper_list";
        String SELECTED_WALLPAPER = "selected_wallpaper";
        String PAGE = "page";
        String SEARCH_QUERY = "search_query";
    }

    interface WallpaperType {
        String COLLECTION = "collection";
        String TOP_CHART = "top_chart";
        String POPULAR = "popular";
        String PREMIUM = "premium";
        String FAVORITE = "favorite";
        String SEARCH = "search";
        String OWN = "own";
    }

    interface AuthType {
        String FACEBOOK = "facebook";
        String GOOGLE = "google";
    }

    interface WallpaperStatus {
        int UNDEFINED = -2;
        int REJECTED = -1;
        int PENDING = 0;
        int PUBLISHED = 1;
        int HIDDEN = 2;
    }

    // Prefix
    String PREFIX_IMAGE = "IMG_";
    // Postfix
    String SUFFIX_IMAGE = ".jpg";
}
