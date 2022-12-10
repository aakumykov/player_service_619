package com.example.player_service;

import android.net.Uri;
import android.os.Binder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;

import java.util.List;

public class CustomPlayer extends Binder implements SoundPlayer {

    private final ExoPlayer mExoPlayer;
    private final MyExoPlayerListener mListener;
    @Nullable private SoundPlayerCallbacks mCallbacks;
    @Nullable private MutableLiveData<PlayerState> mPlayerStateMutableLiveData;

    public CustomPlayer(ExoPlayer exoPlayer) {
        mExoPlayer = exoPlayer;
        mExoPlayer.setPlayWhenReady(true);

        mListener = new MyExoPlayerListener();
        mExoPlayer.addListener(mListener);
    }

    // FIXME: где освобождать Listener?

    @Override
    public void play(@NonNull SoundItem soundItem) {
        mExoPlayer.stop();
        mExoPlayer.clearMediaItems();
        mExoPlayer.addMediaItem(soundItem2mediaItem(soundItem));
        mExoPlayer.prepare();
    }

    @Override
    public void play(List<SoundItem> soundItemList) {
        mExoPlayer.stop();
        mExoPlayer.clearMediaItems();
        for (SoundItem soundItem : soundItemList)
            mExoPlayer.addMediaItem(soundItem2mediaItem(soundItem));
        mExoPlayer.prepare();
    }

    @Override
    public void pause() {
        mExoPlayer.pause();
    }

    @Override
    public void stop() {
        mExoPlayer.stop();
    }

    @Override
    public void skipToNext() {
        if (mExoPlayer.hasNextMediaItem())
            mExoPlayer.seekToNext();
    }

    @Override
    public void skipToPrev() {
        if (mExoPlayer.hasPreviousMediaItem())
            mExoPlayer.seekToPrevious();
    }


    @Override
    public void setCallbacks(SoundPlayerCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public LiveData<PlayerState> getPlayerStateLiveData() {
        if (null == mPlayerStateMutableLiveData)
            mPlayerStateMutableLiveData = new MutableLiveData<>();

        return SoundPlayer.super.getPlayerStateLiveData();
    }


    private void publishPlayerState(@NonNull PlayerState playerState) {
        if (null != mCallbacks)
            mCallbacks.onPlayerStateChanged(playerState);

        mPlayerStateMutableLiveData.setValue(playerState);
    }

    private MediaItem soundItem2mediaItem(SoundItem soundItem) {
        return new MediaItem.Builder()
                .setUri(Uri.fromFile(soundItem.getFile()))
                .setMediaMetadata(soundItem2mediaMetadata(soundItem))
                .build();
    }

    private MediaMetadata soundItem2mediaMetadata(SoundItem soundItem) {
        return new MediaMetadata.Builder()
                .setTitle(soundItem.getTitle())
                .build();
    }

    public void release() {
        mExoPlayer.stop();
        mExoPlayer.removeListener(mListener);
        mExoPlayer.release();
    }


    private class MyExoPlayerListener implements Player.Listener {

        @Override
        public void onEvents(@NonNull Player player, @NonNull Player.Events events) {
            Player.Listener.super.onEvents(player, events);
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            switch (playbackState) {
                case Player.STATE_IDLE:
                    break;
                case Player.STATE_BUFFERING:
                    break;
                case Player.STATE_READY:
                    break;
                case Player.STATE_ENDED:
                    break;
                default:
                    Player.Listener.super.onPlaybackStateChanged(playbackState);
            }
        }

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            Player.Listener.super.onPlayerError(error);
        }

        @Override
        public void onPlayerErrorChanged(@Nullable PlaybackException error) {
            Player.Listener.super.onPlayerErrorChanged(error);
        }
    }
}
