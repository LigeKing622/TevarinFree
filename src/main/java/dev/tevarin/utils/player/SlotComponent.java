package dev.tevarin.utils.player;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import static dev.tevarin.Client.mc;


public final class SlotComponent {


    public static ItemStack getItemStack() {
        return (mc.thePlayer == null || mc.thePlayer.inventoryContainer == null ? null : mc.thePlayer.inventoryContainer.getSlot(getItemIndex() + 36).getStack());
    }

    public static int getItemIndex() {
        final InventoryPlayer inventoryPlayer = mc.thePlayer.inventory;
        return inventoryPlayer.alternativeSlot ? inventoryPlayer.alternativeCurrentItem : inventoryPlayer.currentItem;
    }
}