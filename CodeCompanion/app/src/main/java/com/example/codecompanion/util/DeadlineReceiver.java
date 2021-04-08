package com.example.codecompanion.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeadlineReceiver extends BroadcastReceiver {

    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    public DeadlineReceiver(){

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID , "CodeCompanion" , importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent newIntent = new Intent(context, DeadlineIntent.class);
        context.startService(newIntent);
    }

}
