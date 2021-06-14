

package com.gaps.restaurant.classes

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.gaps.restaurant.api.Network

import com.google.firebase.messaging.FirebaseMessagingService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MyFirebaseInstanceIdService : FirebaseMessagingService() {
    @Inject
    lateinit var api: Network
    lateinit var pref: SharedPreferences
    lateinit var edit: SharedPreferences.Editor

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        pref=this.getSharedPreferences("appSharedPrefs", Context.MODE_PRIVATE)
        edit=pref.edit()
        CoroutineScope(Dispatchers.IO).launch {
            var phone=pref.getString("phone","99")
            if (phone != null) {
                api.login(phone,p0,"name")
            }

        }




    }


}