package com.example.player_service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.gitlab.aakumykov.exception_utils_module.ExceptionUtils;
import com.google.android.exoplayer2.ExoPlayer;

public class PlayerService extends Service {

    private static final String CHANNEL_ID = "Player_service_notification_channel";
    private static final int NOTIFICATION_ID = R.id.player_notification;
    private CustomPlayer mCustomPlayer;
    private SoundPlayerCallbacks mCustomPlayerCallbacks;
    @Nullable private NotificationCompat.Builder mNotificationsBuilder;

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
        preparePlayer();
        prepareNotificationChannel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCustomPlayer.unsetCallbacks(mCustomPlayerCallbacks);
        mCustomPlayer.release();
    }


    private void preparePlayer() {
        final ExoPlayer exoPlayer = new ExoPlayer.Builder(this).build();
        mCustomPlayer = new CustomPlayer(exoPlayer);

        mCustomPlayerCallbacks = new CustomPlayerCallbacks();
        mCustomPlayer.setCallbacks(mCustomPlayerCallbacks);
    }


    private void prepareNotificationChannel() {
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel());
    }

    private NotificationChannelCompat notificationChannel() {
        return new NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_LOW)
                .setName(getString(R.string.PLAYER_SERVICE_notification_channel_name))
                .build();
    }


    private class CustomPlayerCallbacks implements SoundPlayerCallbacks {

        @Override
        public void onPlay(@Nullable SoundItem soundItem) {
            showPlayingNotification(titleFromSoundItem(soundItem));
        }

        @Override
        public void onPause(@Nullable SoundItem soundItem) {
            showPauseNotification(titleFromSoundItem(soundItem));
        }

        @Override
        public void onStop() {
            hideNotification();
        }

        @Override
        public void onError(@Nullable SoundItem soundItem, @NonNull Throwable throwable) {
            hidePersistentNotification();
            showErrorNotification(throwable);
        }
    }

    private void showPlayingNotification(String trackTitle) {
        showPersistentNotification(trackTitle, getString(R.string.sound_track_playing), R.drawable.ic_baseline_audiotrack_24);
    }

    private void showPauseNotification(String trackTitle) {
        showPersistentNotification(trackTitle, getString(R.string.sound_track_paused), R.drawable.ic_baseline_audiotrack_24);
    }

    private void hideNotification() {
        hidePersistentNotification();
    }

    private void showErrorNotification(Throwable throwable) {

        final NotificationCompat.Builder nb = prepareNotification(
                getString(R.string.playing_error),
                ExceptionUtils.getErrorMessage(throwable),
                R.drawable.ic_baseline_error_outline_24);

        nb.setStyle(new NotificationCompat.BigTextStyle());

        NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, nb.build());
    }

    private void showPersistentNotification(@NonNull String title, @NonNull String message,
                                            @DrawableRes int iconRes) {
        startForeground(NOTIFICATION_ID, prepareNotification(title, message, iconRes).build());
    }

    private void hidePersistentNotification() {
        stopForeground(true);
    }


    private  NotificationCompat.Builder prepareNotification(@NonNull String title, @NonNull String message,
                                                            @DrawableRes int iconRes) {
        if (null == mNotificationsBuilder)
            mNotificationsBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        return mNotificationsBuilder
                .setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(message);
    }


    @NonNull
    private String titleFromSoundItem(@Nullable SoundItem soundItem) {
        String title = (null != soundItem) ? soundItem.getTitle() : getString(R.string.no_title);
        if (null == title)
            title = getString(R.string.no_title);
        return title;
    }
}
