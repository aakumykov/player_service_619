package com.example.player_service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract class PlayerState {

    private final Mode mMode;
    @Nullable private final SoundItem mSoundItem;

    protected PlayerState(final Mode mode, @Nullable final SoundItem soundItem) {
        mMode = mode;
        mSoundItem = soundItem;
    }


    public Mode getMode() {
        return mMode;
    }

    @Nullable
    public SoundItem getSoundItem() {
        return mSoundItem;
    }

    @Nullable
    public String getTrackTitle() {
        if (null != mSoundItem)
            return mSoundItem.getTitle();
        return null;
    }


    public static class Playing extends PlayerState {
        protected Playing(@Nullable SoundItem soundItem) {
            super(Mode.PLAYING, soundItem);
        }
    }

    public static class Paused extends PlayerState {
        protected Paused(@Nullable SoundItem soundItem) {
            super(Mode.PAUSED, soundItem);
        }
    }

    public static class Stopped extends PlayerState {
        protected Stopped() {
            super(Mode.STOPPED, null);
        }
    }

    public static class Error extends PlayerState {

        private final Throwable mError;

        protected Error(@Nullable SoundItem soundItem, @NonNull Throwable error) {
            super(Mode.ERROR, soundItem);
            mError = error;
        }

        public Throwable getError() {
            return mError;
        }
    }


    public enum Mode {
        STOPPED,
        PLAYING,
        PAUSED,
        ERROR
    }

}
