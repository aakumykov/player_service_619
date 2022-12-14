package com.github.aakumykov.player_service;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public interface SoundPlayer {

    void play(@NonNull SoundItem soundItem);
    void play(List<SoundItem> soundItemList);
    void playAgain();
    void pause();
    void resume();
    void stop();
    void skipToNext();
    void skipToPrev();

    boolean isPlaying();
    boolean isPaused();
    boolean isStopped();

    void release();

    default void setCallbacks(SoundPlayerCallbacks callbacks) {}
    default void unsetCallbacks(SoundPlayerCallbacks customPlayerCallbacks) {}

    default LiveData<PlayerState> getPlayerStateLiveData() { return new MutableLiveData<>(); }
}