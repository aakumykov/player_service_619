package com.github.aakumykov.player_service;

import com.github.aakumykov.single_live_event.SingleLiveEvent;

public interface CommandPublisher {
    SingleLiveEvent<NotificationCommand> getNotificationCommands();
}
