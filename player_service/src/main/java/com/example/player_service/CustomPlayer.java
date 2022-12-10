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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomPlayer extends Binder implements SoundPlayer {

    private final ExoPlayer mExoPlayer;
    private final MyExoPlayerListener mListener;
    @Nullable private SoundPlayerCallbacks mCallbacks;
    @Nullable private MutableLiveData<PlayerState> mPlayerStateMutableLiveData;
    private final Map<String, SoundItem> mSoundItemMap = new HashMap<>();

    public CustomPlayer(ExoPlayer exoPlayer) {
        mExoPlayer = exoPlayer;
        mExoPlayer.setPlayWhenReady(true);

        mListener = new MyExoPlayerListener();
        mExoPlayer.addListener(mListener);
    }

    @Override
    public void play(@NonNull SoundItem soundItem) {
        play(Collections.singletonList(soundItem));
    }

    @Override
    public void play(List<SoundItem> soundItemList) {
        mExoPlayer.stop();
        mExoPlayer.clearMediaItems();
        mSoundItemMap.clear();
        for (SoundItem soundItem : soundItemList)
            mExoPlayer.addMediaItem(soundItem2mediaItem(soundItem));
        mExoPlayer.prepare();
    }

    @Override
    public void playAgain() {
        play(new ArrayList<>(mSoundItemMap.values()));
    }

    @Override
    public void pause() {
        mExoPlayer.pause();
    }

    @Override
    public void resume() {
        mExoPlayer.play();
    }

    @Override
    public void stop() {
        mExoPlayer.stop();
        publishPlayerState(new PlayerState.Stopped());
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
    public boolean isPlaying() {
        return mExoPlayer.isPlaying();
    }

    @Override
    public boolean isStopped() {
        PlayerState playerState = null;
        if (null != mPlayerStateMutableLiveData)
            playerState = mPlayerStateMutableLiveData.getValue();
        return playerState instanceof PlayerState.Stopped;
    }


    @Override
    public void setCallbacks(SoundPlayerCallbacks callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public LiveData<PlayerState> getPlayerStateLiveData() {
        if (null == mPlayerStateMutableLiveData)
            mPlayerStateMutableLiveData = new MutableLiveData<>();

        return mPlayerStateMutableLiveData;
    }


    private void publishPlayerState(@NonNull PlayerState playerState) {
        if (null != mCallbacks)
            mCallbacks.onPlayerStateChanged(playerState);

        if (null != mPlayerStateMutableLiveData)
            mPlayerStateMutableLiveData.postValue(playerState);
    }

    private MediaItem soundItem2mediaItem(SoundItem soundItem) {

        mSoundItemMap.put(soundItem.getId(), soundItem);

        return new MediaItem.Builder()
                .setMediaId(soundItem.getId())
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
        public void onEvents(@NonNull Player player, @NonNull Player.Events events) {Player.Listener.super.onEvents(player, events);
        }

        @Override
        public void onPlaybackStateChanged(int playbackState) {
            switch (playbackState) {
                case Player.STATE_ENDED:
                    publishPlayerState(new PlayerState.Stopped());
                    break;
                default:
                    Player.Listener.super.onPlaybackStateChanged(playbackState);
            }
        }

        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            if (isPlaying)
                publishPlayerState(new PlayerState.Playing(currentSoundItem()));
            else
                publishPlayerState(new PlayerState.Paused(currentSoundItem()));
        }

        @Override
        public void onPlayerError(@NonNull PlaybackException error) {
            Player.Listener.super.onPlayerError(error);
        }

        @Override
        public void onPlayerErrorChanged(@Nullable PlaybackException error) {
            publishPlayerState(new PlayerState.Error(error));
        }
    }

    @Nullable
    private SoundItem currentSoundItem() {
        final MediaItem mediaItem = mExoPlayer.getCurrentMediaItem();

        if (null == mediaItem)
            return null;

        if (!mSoundItemMap.containsKey(mediaItem.mediaId))
            return null;

        return mSoundItemMap.get(mediaItem.mediaId);
    }
}
