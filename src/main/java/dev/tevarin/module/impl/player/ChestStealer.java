package dev.tevarin.module.impl.player;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventUpdate;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.HYTUtils;
import dev.tevarin.utils.player.ItemComponent;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.Random;

public class ChestStealer extends Module {
    private final BoolValue chest = new BoolValue("Chest", true);
    private final BoolValue furnace = new BoolValue("Furnace", true);
    private final BoolValue brewingStand = new BoolValue("BrewingStand", true);

    private final NumberValue stealdelay = new NumberValue("Steal Delay", 0.0, 0.0, 1000.0, 10.0);
    private final NumberValue delay = new NumberValue("Close Delay", 0.0, 0.0, 1000.0, 10.0);
    private final BoolValue trash = new BoolValue("Pick Trash", true);
    public static BoolValue silentValue = new BoolValue("Silent Value", true);

    public static int opentime = 0;

    public static int stealDelay = 0;
    public static boolean isChest = false;


    public ChestStealer() {
        super("ChestStealer", Category.Player);
    }

    @EventTarget
    public void onUpdateEvent(EventUpdate event) {
        setSuffix(delay.getValue().toString());
        int i;
        Container container;
        if (HYTUtils.isInLobby()) {
            return;
        }
        if (ChestStealer.mc.thePlayer.openContainer == null) {
            return;
        }
        opentime ++;
        stealDelay ++;

        if(stealDelay < Math.floor(stealdelay.getValue()/50)) return;

        if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerFurnace && this.furnace.get()) {
            container = ChestStealer.mc.thePlayer.openContainer;
            if (this.isFurnaceEmpty((ContainerFurnace)container) && opentime > Math.floor(delay.getValue()/50)) {
                ChestStealer.mc.thePlayer.closeScreen();
                opentime = 0;
                return;
            }
            for (i = 0; i < ((ContainerFurnace)container).tileFurnace.getSizeInventory(); ++i) {
                if (((ContainerFurnace)container).tileFurnace.getStackInSlot(i) == null) continue;
                if (new Random().nextInt(100) > 80) continue;
                ChestStealer.mc.playerController.windowClick(container.windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(container.windowId, (short) 1, true));
                stealDelay = 0;
            }
        }
        if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerBrewingStand && this.brewingStand.get()) {
            container = ChestStealer.mc.thePlayer.openContainer;
            if (this.isBrewingStandEmpty((ContainerBrewingStand)container)&& opentime > Math.floor(delay.getValue()/50)) {
                ChestStealer.mc.thePlayer.closeScreen();
                opentime = 0;
                return;
            }
            for (i = 0; i < ((ContainerBrewingStand)container).tileBrewingStand.getSizeInventory(); ++i) {
                if (((ContainerBrewingStand) container).tileBrewingStand.getStackInSlot(i) == null) continue;
                if (new Random().nextInt(100) > 80) continue;
                ChestStealer.mc.playerController.windowClick(container.windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(container.windowId, (short) 1, true));
                stealDelay = 0;
            }
        }
        if (ChestStealer.mc.thePlayer.openContainer instanceof ContainerChest && this.chest.get() && isChest) {
            container = ChestStealer.mc.thePlayer.openContainer;
            if (this.isChestEmpty((ContainerChest)container) && opentime > Math.floor(delay.getValue()/50)) {
                ChestStealer.mc.thePlayer.closeScreen();
                opentime = 0;
                return;
            }
            for (i = 0; i < ((ContainerChest)container).getLowerChestInventory().getSizeInventory(); ++i) {
                if (((ContainerChest)container).getLowerChestInventory().getStackInSlot(i) == null || this.isItemUseful((ContainerChest) container, i) && !this.trash.get()) continue;
                if (new Random().nextInt(100) > 80) continue;
                ChestStealer.mc.playerController.windowClick(container.windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                mc.getNetHandler().addToSendQueue(new C0FPacketConfirmTransaction(container.windowId, (short) 1, true));
                stealDelay = 0;
            }
        }
    }

    private boolean isChestEmpty(ContainerChest c) {
        for (int i = 0; i < c.getLowerChestInventory().getSizeInventory(); ++i) {
            if (c.getLowerChestInventory().getStackInSlot(i) == null || this.isItemUseful(c, i) && !this.trash.get()) continue;
            return false;
        }
        return true;
    }

    private boolean isFurnaceEmpty(ContainerFurnace c) {
        for (int i = 0; i < c.tileFurnace.getSizeInventory(); ++i) {
            if (c.tileFurnace.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    private boolean isBrewingStandEmpty(ContainerBrewingStand c) {
        for (int i = 0; i < c.tileBrewingStand.getSizeInventory(); ++i) {
            if (c.tileBrewingStand.getStackInSlot(i) == null) continue;
            return false;
        }
        return true;
    }

    private boolean isItemUseful(ContainerChest c, int i) {
        ItemStack itemStack = c.getLowerChestInventory().getStackInSlot(i);
        Item item = itemStack.getItem();
        if (item instanceof ItemAxe || item instanceof ItemPickaxe) {
            return false;
        }
        if (item instanceof ItemSnowball || item instanceof ItemEgg) {
            return false;
        }
        if (item instanceof ItemFood) {
            return false;
        }
        if (item instanceof ItemBow || item == Items.arrow) {
            return false;
        }
        if (item instanceof ItemPotion && !ItemComponent.isPotionNegative(itemStack)) {
            return false;
        }
        if (item instanceof ItemSword && ItemComponent.isBestSword(c, itemStack)) {
            return false;
        }
        if (item instanceof ItemArmor && ItemComponent.isBestArmor(c, itemStack)) {
            return false;
        }
        if (item instanceof ItemBlock) {
            return false;
        }
        return !(item instanceof ItemEnderPearl);
    }
}
