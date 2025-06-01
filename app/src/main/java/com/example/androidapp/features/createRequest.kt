package com.example.androidapp.features

import android.util.Log
import com.example.androidapp.viewModels.SharedViewModel
import okhttp3.Request

fun createRequest(sharedViewModel: SharedViewModel): Request {
    Log.d("test", "$websocketUrl?login=${sharedViewModel.login}&password=${sharedViewModel.password}&id=${sharedViewModel.userId}")

    return Request.Builder()
        .url("$websocketUrl?login=${sharedViewModel.login}&password=${sharedViewModel.password}&id=${sharedViewModel.userId}")
        .build()
}