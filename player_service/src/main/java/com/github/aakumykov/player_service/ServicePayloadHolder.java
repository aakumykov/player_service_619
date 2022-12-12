package com.github.aakumykov.player_service;

import android.os.Binder;

public class ServicePayloadHolder extends Binder {

    private final SoundPlayer mSoundPlayer;
    private final PlayerService mPlayerService;

    public ServicePayloadHolder(PlayerService playerService, SoundPlayer soundPlayer) {
        mPlayerService = playerService;
        mSoundPlayer = soundPlayer;
    }

    public PlayerService getPlayerService() {
        return mPlayerService;
    }

    public SoundPlayer getSoundPlayer() {
        return mSoundPlayer;
    }

}
