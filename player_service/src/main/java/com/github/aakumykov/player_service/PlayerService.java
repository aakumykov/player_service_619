package com.github.aakumykov.player_service;

import android.app.PendingIntent;
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
    private static final int OPEN_ACTIVITY_REQUEST_CODE = R.id.open_main_activity_request_code;
    private ServicePayloadHolder mServicePayloadHolder;
    private SoundPlayerCallbacks mCustomPlayerCallbacks;
    @Nullable private NotificationCompat.Builder mNotificationsBuilder;
    @Nullable private PendingIntent mContentIntent;

    public static Intent getIntent(Context context) {
        return new Intent(context, PlayerService.class);
    }

    public static SoundPlayer getSoundPlayer(IBinder binder) {
        return ((ServicePayloadHolder) binder).getSoundPlayer();
    }

    public static void setContentIntent(IBinder binder, Intent intent) {
        PlayerService playerService = playerServiceFromBinder(binder);
        if (null != playerService)
            playerServiceFromBinder(binder).setContentIntent(intent);
    }


    @Nullable @Override
    public IBinder onBind(Intent intent) {
        return mServicePayloadHolder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prepareServicePayload();
        prepareNotificationChannel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mServicePayloadHolder.getSoundPlayer().unsetCallbacks(mCustomPlayerCallbacks);
        mServicePayloadHolder.getSoundPlayer().release();
    }


    public void setContentIntent(@NonNull Intent contentIntent) {
        mContentIntent = PendingIntent.getActivity(this, OPEN_ACTIVITY_REQUEST_CODE,
                contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private static PlayerService playerServiceFromBinder(IBinder binder) {
        return ((ServicePayloadHolder) binder).getPlayerService();
    }


    private void prepareServicePayload() {
        final ExoPlayer exoPlayer = new ExoPlayer.Builder(this).build();
        final CustomPlayer customPlayer = new CustomPlayer(exoPlayer);

        mCustomPlayerCallbacks = new CustomPlayerCallbacks();
        customPlayer.setCallbacks(mCustomPlayerCallbacks);

        mServicePayloadHolder = new ServicePayloadHolder(this, customPlayer);
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
        public void onIdle() {
            hidePersistentNotification();
        }

        @Override
        public void onWait() {
            showWaitingNotification();
        }

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
            hidePersistentNotification();
        }

        @Override
        public void onError(@Nullable SoundItem soundItem, @NonNull Throwable throwable) {
            hidePersistentNotification();
            showErrorNotification(throwable);
        }
    }

    private void showWaitingNotification() {
        showPersistentNotification(
                getString(R.string.sound_track_waiting_title),
                getString(R.string.sound_track_waiting_message),
                R.drawable.ic_waiting
        );
    }

    private void showPlayingNotification(String trackTitle) {
        showPersistentNotification(trackTitle, getString(R.string.sound_track_playing), R.drawable.ic_baseline_audiotrack_24);
    }

    private void showPauseNotification(String trackTitle) {
        showPersistentNotification(trackTitle, getString(R.string.sound_track_paused), R.drawable.ic_baseline_audiotrack_24);
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

        if (null != mContentIntent)
            mNotificationsBuilder.setContentIntent(mContentIntent);

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
