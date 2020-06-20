package at.fhjoanneum.picturl.util

import android.content.Context
import android.net.ConnectivityManager

object CheckConnectionUtil {

    fun isConnected(context: Context): Boolean {
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}