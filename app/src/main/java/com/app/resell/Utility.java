package com.app.resell;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by azza ahmed on 5/8/2017.
 */
public class Utility {
    public static boolean isOnline(Activity Activity) {
        ConnectivityManager cm =
                (ConnectivityManager) Activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}
