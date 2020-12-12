package com.example.chameleody

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.chameleody.ApplicationClass.Companion.ACTION_NEXT
import com.example.chameleody.ApplicationClass.Companion.ACTION_PLAY
import com.example.chameleody.ApplicationClass.Companion.ACTION_PREVIOUS

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context, "Received", Toast.LENGTH_LONG).show()
        Log.v("Notification", "ReceivedReceivedReceivedReceivedReceivedReceivedReceivedReceivedReceivedReceived")
        val actionName = intent?.action
        val serviceIntent = Intent(context, MusicService::class.java)
        if (actionName != null){
            when(actionName){
                ACTION_PLAY -> {
                    Toast.makeText(context,  "play pressed omg", Toast.LENGTH_LONG).show()
                    serviceIntent.putExtra("ActionName", "playPause")
                    context?.startService(serviceIntent)
                }
                ACTION_NEXT -> {
                    serviceIntent.putExtra("ActionName", "next")
                    context?.startService(serviceIntent)
                }
                ACTION_PREVIOUS -> {
                    serviceIntent.putExtra("ActionName", "previous")
                    context?.startService(serviceIntent)
                }
            }
        }
    }
}