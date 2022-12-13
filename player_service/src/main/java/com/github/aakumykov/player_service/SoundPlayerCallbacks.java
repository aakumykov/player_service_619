package com.github.aakumykov.player_service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface SoundPlayerCallbacks {

    default void onPlayerStateChanged(@NonNull PlayerState playerState) {}

    void onWait();
    default void onPlay(@Nullable SoundItem soundItem) {}
    default void onPause(@Nullable SoundItem soundItem) {}
    default void onStop() {}
    default void onError(@Nullable SoundItem soundItem, @NonNull Throwable throwable) {}
}
