package de.thu.currencyconverter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class UpdateNotifier {

    private static final int NOTIFICATION_ID = 123;
    private static String CHANNEL_ID = "update_channel";
    private static String CHANNEL_DESCRIPTION = "Show currency update state";
    NotificationCompat.Builder notificationBuilder;
    NotificationManager notificationManager;

    public UpdateNotifier(Context context) {

        // Get access to NotificationManager (manages all notifications in the system)
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create NotificationChannel
        // Task: Since Android O sort notifications in channels which can be configured/muted by user
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel("update_channel");
            if (notificationChannel == null) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_DESCRIPTION,
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        // Prepare NotificationBuilder
        // Task: Create Notification (views,...)
        // Can be configured via setXX() methods
        notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Updated Currencies!")
                .setAutoCancel(false);

        // Create Intent for action to be invoked when notification is tapped and add it to NotificationBuilder
        // Here: Recipe to show current Activity
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);
        notificationBuilder.setContentIntent(resultPendingIntent);
    }

    // Show/update notification
    // Set (new) content text
    // Create notification object using NotificationBuilder
    // Pass notification toNotificationManager
    // Using same NOTIFICATION_ID allows to change / delete
    public void showOrUpdateNotification(int value) {
        //notificationBuilder.setContentText(value + " seconds");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    // Remove notification
    public void removeNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}

