package dev.tevarin.event.impl.events;

import dev.tevarin.event.impl.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

/**
 * @author TG_format
 * @since 2024/6/1 0:26
 */
@AllArgsConstructor
@Getter
@Setter
public class PacketReceiveEvent extends CancellableEvent {
    private Packet<?> packet;
    private INetHandler netHandler;
}
