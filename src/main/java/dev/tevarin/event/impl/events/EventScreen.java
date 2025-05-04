package dev.tevarin.event.impl.events;

import dev.tevarin.event.impl.Event;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;

@Getter
public class EventScreen
        implements Event {
    private final GuiScreen guiScreen;

    public EventScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

}
