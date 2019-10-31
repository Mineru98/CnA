package com.cna.mineru.cna.Utils.Network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.cna.mineru.cna.SignupActivity

object getWhatKindOfNetwork {
    internal val WIFI_STATE: String = "WIFE"
    internal val MOBILE_STATE: String = "MOBILE"
    internal val NONE_STATE: String = "NONE"

    fun getWhatKindOfNetwork(context: Context?): String {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        if (activeNetwork != null) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                return WIFI_STATE
            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE
            }
        }
        return NONE_STATE
    }

    operator fun invoke(signupActivity: SignupActivity) {

    }
}
