

package com.gaps.restaurant.classes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.gaps.restaurant.R
import com.gaps.restaurant.api.Network
import com.gaps.restaurant.home.HomeActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CustomMessagingService: FirebaseMessagingService() {
    @Inject
    lateinit var api: Network
    lateinit var pref: SharedPreferences
    lateinit var edit: SharedPreferences.Editor

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        //sound=
        val title=p0.data["title"]
        val message=p0.data["body"]
        if (message != null) {
            if (title != null) {
                generateNotification(title,message)
            }
        }





    }

    private fun generateNotification(title: String, messageBody: String) {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelId:String = getString(R.string.default_notification_channel)
        val defaultSoundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+applicationContext.packageName+"/"+R.raw.alert)

        Log.d("defaultSound",defaultSoundUri.toString())

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.group_365)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)

            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Main notification",
                NotificationManager.IMPORTANCE_DEFAULT)
            val audioAttributes=AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.setSound(defaultSoundUri,audioAttributes)
            notificationManager.createNotificationChannel(channel)

        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
    override fun onNewToken(token: String) {
        pref=this.getSharedPreferences("appSharedPrefs", Context.MODE_PRIVATE)
        edit=pref.edit()
        CoroutineScope(Dispatchers.IO).launch {
            var phone=pref.getString("phone","99")
            if (phone != null) {
                api.login(phone,token,"name")
            }

        }


    }

}