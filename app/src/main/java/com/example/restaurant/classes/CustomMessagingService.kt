@file:Suppress("DEPRECATION")

package com.example.restaurant.classes

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.restaurant.BuildConfig
import com.example.restaurant.R
import com.example.restaurant.auth.LoginActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CustomMessagingService: FirebaseMessagingService() {

    lateinit var notificationManager: NotificationManager
    lateinit var notification:Notification
    var sound:Uri?=null

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        if(p0.notification!=null){
            val data: Map<String, String> = p0.getData()
            val title = data["title"]
            val message = data["body"]
            if (title != null) {

            }
            generateNotification(title!!,message!!)

        }


    }

    fun generateNotification(title: String, message: String){
        val intent = Intent(this@CustomMessagingService, LoginActivity::class.java)
        var pendingIntent = PendingIntent.getActivity(this@CustomMessagingService, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        notificationManager= getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var audioAttributes = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val CHANNEL_ID =  "AjubaRider_notification_id"
            val CHANNEL_NAME = BuildConfig.APPLICATION_ID + "_notification_name"
            var notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableVibration(true)
            notificationChannel.setVibrationPattern(longArrayOf(400, 400, 400, 400, 500, 400, 400, 400, 400))


            notificationChannel.setSound(sound,audioAttributes)
            notificationManager.createNotificationChannel(notificationChannel)
            var builder : NotificationCompat.Builder = NotificationCompat.Builder(this,CHANNEL_ID)

            builder.setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setSound(sound)
                .setSound(sound)
            notification = builder.build()
            Log.d("notot",notification.toString()+"   "+sound.toString())
            notificationManager.notify(1,notification)


        }
        else{
            var nb : Notification.Builder= Notification.Builder(this)
            nb.setSmallIcon(R.mipmap.ic_launcher_round)
                    .setAutoCancel(true)
                    .setSound(sound,audioAttributes)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .build()
            notification= nb.notification
            notification.flags= Notification.FLAG_AUTO_CANCEL
            notificationManager.notify(0,notification)
        }




    }
}