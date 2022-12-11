package com.github.aakumykov.player_service;

import android.os.Binder;

import com.github.aakumykov.single_live_event.SingleLiveEvent;

public class ServicePayloadHolder<T> extends Binder implements CommandPublisher {

    private final T mPayload;
    private final SingleLiveEvent<NotificationCommand> mNotificationCommandSingleLiveEvent = new SingleLiveEvent<>();

    public ServicePayloadHolder(T payload) {
        mPayload = payload;
    }

    public T getPayload() {
        return mPayload;
    }

    @Override
    public SingleLiveEvent<NotificationCommand> getNotificationCommands() {
        return mNotificationCommandSingleLiveEvent;
    }

    public void sendCommandFromNotification(NotificationCommand command) {
        mNotificationCommandSingleLiveEvent.setValue(command);
    }
}
