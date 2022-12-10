package com.example.player_service;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public interface SoundPlayer {

    void play(@NonNull SoundItem soundItem);
    void play(List<SoundItem> soundItemList);
    void pause();
    void stop();
    void skipToNext();
    void skipToPrev();

    default void setCallbacks(SoundPlayerCallbacks callbacks) {};
    default LiveData<PlayerState> getPlayerStateLiveData() { return new MutableLiveData<>(); }
}