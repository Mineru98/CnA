package com.cna.mineru.cna.Utils.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class getWhatKindOfNetwork {
    static final String WIFI_STATE = "WIFE";
    static final String MOBILE_STATE = "MOBILE";
    static final String NONE_STATE = "NONE";

    public static String getWhatKindOfNetwork(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFI_STATE;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE;
            }
        }
        return NONE_STATE;
    }
}
