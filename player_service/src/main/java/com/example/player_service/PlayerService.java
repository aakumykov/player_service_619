package com.example.player_service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlayer;

public class PlayerService extends Service {

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
}
