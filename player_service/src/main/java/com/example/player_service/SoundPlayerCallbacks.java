package com.example.player_service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface SoundPlayerCallbacks {

    default void onPlayerStateChanged(@NonNull PlayerState playerState) {}

    default void onPlay(@Nullable SoundItem soundItem) {}
    default void onPause(@Nullable SoundItem soundItem) {}
    default void onStop() {}
    default void onError(@Nullable SoundItem soundItem, @NonNull Throwable throwable) {}
}
