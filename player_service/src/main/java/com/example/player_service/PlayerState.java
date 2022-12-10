package com.example.player_service;

import androidx.annotation.Nullable;

public abstract class PlayerState {

    private final Mode mMode;
    @Nullable private final String mTrackTitle;

    protected PlayerState(final Mode mode, @Nullable final String trackTitle) {
        mMode = mode;
        mTrackTitle = trackTitle;
    }


    public Mode getMode() {
        return mMode;
    }

    @Nullable
    public String getTrackTitle() {
        return mTrackTitle;
    }


    public static class Stopped extends PlayerState {
        protected Stopped() {
            super(Mode.STOPPED, null);
        }
    }

    public static class Paused extends PlayerState {
        protected Paused(String trackTitle) {
            super(Mode.PAUSED, trackTitle);
        }
    }

    public static class Playing extends PlayerState {
        protected Playing(String trackTitle) {
            super(Mode.PLAYING, trackTitle);
        }
    }

    public static class Error extends PlayerState {
        private final Throwable mError;
        protected Error(Throwable error) {
            super(Mode.ERROR, null);
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
