package com.github.aakumykov.player_service;

import android.os.Binder;

public class ServicePayloadHolder<T> extends Binder {

    private final T mPayload;
    private final PlayerService mPlayerService;

    public ServicePayloadHolder(PlayerService playerService, T payload) {
        mPlayerService = playerService;
        mPayload = payload;
    }

    public PlayerService getPlayerService() {
        return mPlayerService;
    }

    public T getPayload() {
        return mPayload;
    }

}
