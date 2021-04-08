package com.example.codecompanion.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.example.codecompanion.MainActivity;
import com.example.codecompanion.R;

public class DeadlineIntent extends IntentService {

    private static final int NOTIFICATION_ID = 3;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    public DeadlineIntent(){
        super("DeadlineIntent");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Code Companion");
        builder.setContentText("Du musst bald dein Projekt abgeben");
        builder.setSmallIcon(R.drawable.ic_cc_foreground);
        builder.setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }
}
