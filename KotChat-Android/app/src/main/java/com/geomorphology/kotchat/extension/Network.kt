package com.geomorphology.kotchat.extension

import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AppCompatActivity

public fun AppCompatActivity.hasNetworkConnection(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
}