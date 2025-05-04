package dev.tevarin.event.impl.events;


import dev.tevarin.event.impl.CancellableEvent;
import lombok.Getter;

@Getter
public class EventSendChatMessage extends CancellableEvent {
    String msg;

    public EventSendChatMessage(String msg) {
        this.msg = msg;
    }

}
