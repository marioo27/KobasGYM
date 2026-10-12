package com.example.kobasgym.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;

import androidx.core.app.NotificationCompat;

import com.example.kobasgym.R;
import com.example.kobasgym.ui.MainOptionsActivity;

public class DiablilloNotificationHelper {

    private static final String CHANNEL_ID = "diablillo_channel_id";
    private static final int NOTIFICATION_ID = 1001;

    public static void showDiablilloNotification(Context context, String message) {
        if (context == null) return;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Coach Diablillo Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones motivacionales y de entrenamiento de Diablillo Rojo");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(context, MainOptionsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Bitmap diablilloBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.red_devil);

        CharSequence coloredTitle = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? Html.fromHtml("<font color='#D32F2F'><b>Coach Diablillo Rojo 😈</b></font>", Html.FROM_HTML_MODE_LEGACY)
                : Html.fromHtml("<font color='#D32F2F'><b>Coach Diablillo Rojo 😈</b></font>");

        CharSequence coloredText = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? Html.fromHtml("<font color='#D32F2F'>" + message + "</font>", Html.FROM_HTML_MODE_LEGACY)
                : Html.fromHtml("<font color='#D32F2F'>" + message + "</font>");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.red_devil)
                .setLargeIcon(diablilloBitmap)
                .setContentTitle(coloredTitle)
                .setContentText(coloredText)
                .setColor(Color.RED)
                .setColorized(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(diablilloBitmap)
                        .bigLargeIcon((Bitmap) null)
                        .setSummaryText(coloredText))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}