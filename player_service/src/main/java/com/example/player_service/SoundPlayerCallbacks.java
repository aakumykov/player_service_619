package com.example.player_service;

public interface SoundPlayerCallbacks {
    default void onPlayerStateChanged(PlayerState playerState) {}

    default void onPlay(SoundItem soundItem) {}
    default void onPause(SoundItem soundItem) {}
    default void onStop(SoundItem soundItem) {}
    default void onError(Exception e) {}
}
