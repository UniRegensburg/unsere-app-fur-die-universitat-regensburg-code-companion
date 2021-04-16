package com.example.codecompanion.util;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.example.codecompanion.MainActivity;
import com.example.codecompanion.R;

/**
 * Intent for deadline notifications
 */
public class DeadlineIntent extends IntentService {

    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    public DeadlineIntent(){

        super("DeadlineIntent");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String deadlineString = intent.getStringExtra("deadline");
        int code = intent.getIntExtra("code",0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Deadline: " + deadlineString + " remaining");
        builder.setContentText("Don't miss your deadline!");
        builder.setSmallIcon(R.drawable.ic_not_icon);
        builder.setAutoCancel(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
        }
        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, code, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(code, notificationCompat);
    }
}
