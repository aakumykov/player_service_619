package com.example.player_service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.exoplayer2.ExoPlayer;

public class PlayerService extends Service {

    private static final String CHANNEL_ID = "Player_service_notification_channel";
    private CustomPlayer mCustomPlayer;

    public static Intent getIntent(Context context) {
        return new Intent(context, PlayerService.class);
    }

    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return mCustomPlayer;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final ExoPlayer exoPlayer = new ExoPlayer.Builder(this).build();
        mCustomPlayer = new CustomPlayer(exoPlayer);
        prepareNotificationChannel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCustomPlayer.release();
    }

    public SoundPlayer getSoundPlayer() {
        return mCustomPlayer;
    }

    public void stop() {
        stopSelf();
    }


    private void prepareNotificationChannel() {
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel());
    }

    private NotificationChannelCompat notificationChannel() {
        return new NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(getString(R.string.PLAYER_SERVICE_notification_channel_name))
                .build();
    }

    /*private void showPersistentNotification() {
        startForeground(R.id.player_service_notification, notification());
    }

    private void hidePersistentNotification() {
        stopForeground(true);
    }*/

    /*private Notification notification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_service_notification)
                .setContentTitle(getString(R.string.PLAYER_SERVICE_notification_title))
                .setContentText(getString(R.string.PLAYER_SERVICE_notification_text))
                .build();
    }*/

}
