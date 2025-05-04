package dev.tevarin.manager;

import lombok.Getter;
import dev.tevarin.Client;
import net.minecraft.item.ItemStack;

public class SlotSpoofManager {

    private int spoofedSlot;

    @Getter
    private boolean spoofing;

    public void startSpoofing(int slot) {
        this.spoofing = true;
        this.spoofedSlot = slot;
    }

    public void stopSpoofing() {
        this.spoofing = false;
    }

    public int getSpoofedSlot() {
        return spoofing ? spoofedSlot : Client.mc.thePlayer.inventory.currentItem;
    }

    public ItemStack getSpoofedStack() {
        return spoofing ? Client.mc.thePlayer.inventory.getStackInSlot(spoofedSlot) : Client.mc.thePlayer.inventory.getCurrentItem();
    }

}
