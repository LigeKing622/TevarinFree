package dev.tevarin.module.impl.player;

import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventMotion;
import dev.tevarin.event.impl.events.PacketReceiveEvent;
import dev.tevarin.event.impl.events.PacketSendEvent;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.player.InventoryUtil;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


public class InvCleaner extends Module {
    public final ModeValue mode = new ModeValue("Mode", new String[]{"Basic", "OpenInv"},"Basic" );
    private final NumberValue delay = new NumberValue("Delay", 0, 0, 300, 10);
    private final NumberValue slotWeapon = new NumberValue("Weapon Slot", 1, 1, 9, 1);
    private final NumberValue slotBow = new NumberValue("Bow Slot", 2, 1, 9, 1);
    private final NumberValue slotGapple = new NumberValue("Gapple Slot", 3, 1, 9, 1);
    private final NumberValue slotPick = new NumberValue("Pickaxe Slot", 4, 1, 9, 1);
    private final NumberValue slotAxe = new NumberValue("Axe Slot", 5, 1, 9, 1);
    private final NumberValue slotShovel = new NumberValue("Shovel Slot", 6, 1, 9, 1);
    public NumberValue slotThrowables = new NumberValue("Throwables Slot", 7, 1, 9, 1);
    private final NumberValue slotBlock = new NumberValue("Block Slot", 8, 1, 9, 1);
    private final NumberValue slotPearl = new NumberValue("Pearl Slot", 9, 1, 9, 1);

    private int bestSwordSlot;
    private int bestPearlSlot;
    private int bestBowSlot;
    private int bestThrowablesSlot;
    private boolean serverOpen;
    private boolean clientOpen;
    private int ticksSinceLastClick;
    private boolean nextTickCloseInventory;
    private final int[] bestToolSlots = new int[3];
    private final int[] bestArmorPieces = new int[4];
    private final ArrayList<Integer> gappleStackSlots = new ArrayList<>();
    private final ArrayList<Integer> trash = new ArrayList<>();
    private final String[] serverItems = new String[]{ "\u9009\u62e9\u6e38\u620f", "\u52a0\u5165\u6e38\u620f", "\u804c\u4e1a\u9009\u62e9\u83dc\u5355", "\u79bb\u5f00\u5bf9\u5c40", "\u518d\u6765\u4e00\u5c40", "selector", "tracking compass", "(right click)", "tienda ", "perfil", "salir", "shop", "collectibles", "game", "profil", "lobby", "show all", "hub", "friends only", "cofre", "(click", "teleport", "play", "exit", "hide all", "jeux", "gadget", " (activ", "emote", "amis", "bountique", "choisir", "choose " };
    public InvCleaner() {
        super("InvManager", Category.Player);

    }

    @EventTarget
    public void onPacketReceiveEvent(final PacketReceiveEvent event) {
        if (mc.thePlayer != null) {
            final Packet<?> packet = event.getPacket();
            if (mc.thePlayer.isSpectator()) {
                return;
            }
            if (packet instanceof S2DPacketOpenWindow) {
                this.clientOpen = false;
                this.serverOpen = false;
            }
        }
    }

    @EventTarget
    public void onPacketSendEvent(final PacketSendEvent event) {
        final Packet<?> packet = event.getPacket();
        if (mc.thePlayer != null && mc.thePlayer.isSpectator()) {
            return;
        }
        if (packet instanceof C16PacketClientStatus) {
            final C16PacketClientStatus clientStatus = (C16PacketClientStatus)packet;
            if (clientStatus.getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                this.clientOpen = true;
                this.serverOpen = true;
            }
        }
        else if (packet instanceof C0DPacketCloseWindow) {
            final C0DPacketCloseWindow packetCloseWindow = (C0DPacketCloseWindow) packet;
            if (mc.thePlayer != null && packetCloseWindow.windowId == mc.thePlayer.inventoryContainer.windowId) {
                this.clientOpen = false;
                this.serverOpen = false;
            }
        }
        else if (mc.thePlayer != null && packet instanceof C0EPacketClickWindow && !mc.thePlayer.isUsingItem()) {
            this.ticksSinceLastClick = 0;
        }
    }

    private boolean dropItem(final List<Integer> listOfSlots) {
        if (!listOfSlots.isEmpty()) {
            final int slot = listOfSlots.remove(0);
            InventoryUtil.windowClick(mc, slot, 1, InventoryUtil.ClickType.DROP_ITEM);
            return true;
        }
        return false;
    }

    @EventTarget
    public void onMotionEvent(final EventMotion event) {
        if (mc.thePlayer.isSpectator()) {
            return;
        }
        if (event.isPost() && !mc.thePlayer.isUsingItem() && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiIngameMenu)) {
            ++this.ticksSinceLastClick;
            if (this.ticksSinceLastClick < Math.floor(this.delay.getValue() / 50.0)) {
                return;
            }
            if (this.clientOpen || (mc.currentScreen == null && !mode.is("OpenInv"))) {
                this.clear();
                for (int slot = 5; slot < 45; ++slot) {
                    final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(slot).getStack();
                    if (stack != null) {
                        if (stack.getItem() instanceof ItemSword && InventoryUtil.isBestSword(mc.thePlayer, stack)) {
                            this.bestSwordSlot = slot;
                        } else
                        if ((stack.getItem() instanceof ItemEgg || stack.getItem() instanceof ItemSnowball) && slot != bestThrowablesSlot) {
                            this.bestThrowablesSlot = slot;
                        } else
                        if (stack.getItem() instanceof ItemTool && InventoryUtil.isBestTool(mc.thePlayer, stack)) {
                            final int toolType = InventoryUtil.getToolType(stack);
                            if (toolType != -1 && slot != this.bestToolSlots[toolType]) {
                                this.bestToolSlots[toolType] = slot;
                            }
                        } else if (stack.getItem() instanceof ItemArmor && InventoryUtil.isBestArmor(mc.thePlayer, stack)) {
                            final ItemArmor armor = (ItemArmor)stack.getItem();
                            final int pieceSlot = this.bestArmorPieces[armor.armorType];
                            if (pieceSlot == -1 || slot != pieceSlot) {
                                this.bestArmorPieces[armor.armorType] = slot;
                            }
                        }
                        else if (stack.getItem() instanceof ItemBow && InventoryUtil.isBestBow(mc.thePlayer, stack)) {
                            if (slot != this.bestBowSlot) {
                                this.bestBowSlot = slot;
                            }
                        }
                        else if (stack.getItem() instanceof ItemAppleGold) {
                            this.gappleStackSlots.add(slot);
                        }
                        else if (stack.getItem() instanceof ItemEnderPearl) {
                            this.bestPearlSlot = slot;
                        }
                        else if (!this.trash.contains(slot) && !isValidStack(stack)) {
                            if (stack.getItem() instanceof ItemEgg || stack.getItem() instanceof ItemSnowball) {
                                continue;
                            }
                            if (Arrays.stream(this.serverItems).noneMatch(stack.getDisplayName()::contains)) {
                                this.trash.add(slot);
                            }
                        }
                    }
                }
                final boolean busy = !this.trash.isEmpty() || this.equipArmor(false) || this.sortItems(false);
                if (!busy) {
                    if (this.nextTickCloseInventory) {
                        this.close();
                        this.nextTickCloseInventory = false;
                    }
                    else {
                        this.nextTickCloseInventory = true;
                    }
                    return;
                }
                final boolean waitUntilNextTick = !this.serverOpen;
                this.open();
                if (this.nextTickCloseInventory) {
                    this.nextTickCloseInventory = false;
                }
                if (waitUntilNextTick) {
                    return;
                }
                if (this.equipArmor(true)) {
                    return;
                }
                if (this.dropItem(this.trash)) {
                    return;
                }
                this.sortItems(true);
            }
        }
    }

    private boolean sortItems(final boolean moveItems) {
        final int goodSwordSlot = (int) (this.slotWeapon.getValue() + 35);
        if (this.bestSwordSlot != -1 && this.bestSwordSlot != goodSwordSlot) {
            if (moveItems) {
                this.putItemInSlot(goodSwordSlot, this.bestSwordSlot);
                this.bestSwordSlot = goodSwordSlot;
            }
            return true;
        }
        final int goodBowSlot = (int) (this.slotBow.getValue() + 35);
        if (this.bestBowSlot != -1 && this.bestBowSlot != goodBowSlot) {
            if (moveItems) {
                this.putItemInSlot(goodBowSlot, this.bestBowSlot);
                this.bestBowSlot = goodBowSlot;
            }
            return true;
        }
        final int goodGappleSlot = (int) (this.slotGapple.getValue() + 35);
        if (!this.gappleStackSlots.isEmpty()) {
            this.gappleStackSlots.sort(Comparator.comparingInt(slot -> mc.thePlayer.inventoryContainer.getSlot(slot).getStack().stackSize));
            final int bestGappleSlot = this.gappleStackSlots.get(0);
            if (bestGappleSlot != goodGappleSlot) {
                if (moveItems) {
                    this.putItemInSlot(goodGappleSlot, bestGappleSlot);
                    this.gappleStackSlots.set(0, goodGappleSlot);
                }
                return true;
            }
        }
        final int[] toolSlots = {(int) (this.slotPick.getValue() + 35), (int) (this.slotAxe.getValue() + 35), (int) (this.slotShovel.getValue().doubleValue() + 35)};
        for (final int toolSlot : this.bestToolSlots) {
            if (toolSlot != -1) {
                final int type = InventoryUtil.getToolType(mc.thePlayer.inventoryContainer.getSlot(toolSlot).getStack());
                if (type != -1 && toolSlot != toolSlots[type]) {
                    if (moveItems) {
                        this.putToolsInSlot(type, toolSlots);
                    }
                    return true;
                }
            }
        }
        final int goodBlockSlot = (int) (this.slotBlock.getValue().doubleValue() + 35);
        final int mostBlocksSlot = this.getMostBlocks();
        if (mostBlocksSlot != -1 && mostBlocksSlot != goodBlockSlot) {
            final Slot dss = mc.thePlayer.inventoryContainer.getSlot(goodBlockSlot);
            final ItemStack dsis = dss.getStack();
            if (dsis == null || !(dsis.getItem() instanceof ItemBlock) || dsis.stackSize < mc.thePlayer.inventoryContainer.getSlot(mostBlocksSlot).getStack().stackSize || Arrays.stream(this.serverItems).anyMatch(dsis.getDisplayName().toLowerCase()::contains)) {
                this.putItemInSlot(goodBlockSlot, mostBlocksSlot);
            }
        }
        final int goodThrowables = (int) (this.slotThrowables.getValue() + 35);
        if (this.bestThrowablesSlot != -1 && this.bestThrowablesSlot != goodThrowables) {
            if (moveItems) {
                this.putItemInSlot(goodThrowables, this.bestThrowablesSlot);
                this.bestThrowablesSlot = goodThrowables;
            }
            return true;
        }
        final int goodPearlSlot = (int) (this.slotPearl.getValue() + 35);
        if (this.bestPearlSlot != -1 && this.bestPearlSlot != goodPearlSlot) {
            if (moveItems) {
                this.putItemInSlot(goodPearlSlot, this.bestPearlSlot);
                this.bestPearlSlot = goodPearlSlot;
            }
            return true;
        }
        return false;
    }

    public int getMostBlocks() {
        int stack = 0;
        int biggestSlot = -1;
        for (int i = 9; i < 45; ++i) {
            final Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            final ItemStack is = slot.getStack();
            if (is != null && is.getItem() instanceof ItemBlock && is.stackSize > stack && Arrays.stream(this.serverItems).noneMatch(is.getDisplayName().toLowerCase()::contains)) {
                stack = is.stackSize;
                biggestSlot = i;
            }
        }
        return biggestSlot;
    }

    private boolean equipArmor(final boolean moveItems) {
        for (int i = 0; i < this.bestArmorPieces.length; ++i) {
            final int piece = this.bestArmorPieces[i];
            if (piece != -1) {
                final int armorPieceSlot = i + 5;
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(armorPieceSlot).getStack();
                if (stack == null) {
                    if (moveItems) {
                        InventoryUtil.windowClick(mc, piece, 0, InventoryUtil.ClickType.SHIFT_CLICK);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    private void putItemInSlot(final int slot, final int slotIn) {
        InventoryUtil.windowClick(mc, slotIn, slot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
    }

    private void putToolsInSlot(final int tool, final int[] toolSlots) {
        final int toolSlot = toolSlots[tool];
        InventoryUtil.windowClick(mc, this.bestToolSlots[tool], toolSlot - 36, InventoryUtil.ClickType.SWAP_WITH_HOT_BAR_SLOT);
        this.bestToolSlots[tool] = toolSlot;
    }

    private static boolean isValidStack(final ItemStack stack) {
        return (stack.getItem() instanceof ItemBlock && InventoryUtil.isStackValidToPlace(stack)) || (stack.getItem() instanceof ItemPotion && InventoryUtil.isBuffPotion(stack)) || (stack.getItem() instanceof ItemFood && InventoryUtil.isGoodFood(stack)) || InventoryUtil.isGoodItem(stack.getItem());
    }

    public void onEnable() {
        this.ticksSinceLastClick = 0;
        this.clientOpen = (mc.currentScreen instanceof GuiInventory);
        this.serverOpen = this.clientOpen;
        super.onEnable();
    }

    public void onDisable() {
        this.close();
        this.clear();
        super.onDisable();
    }

    private void open() {
        if (!this.clientOpen && !this.serverOpen) {
            mc.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            this.serverOpen = true;
        }
    }

    private void close() {
        if (!this.clientOpen && this.serverOpen) {
            mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
            this.serverOpen = false;
        }
    }

    private void clear() {
        this.trash.clear();
        this.bestBowSlot = -1;
        this.bestSwordSlot = -1;
        this.gappleStackSlots.clear();
        Arrays.fill(this.bestArmorPieces, -1);
        Arrays.fill(this.bestToolSlots, -1);
    }

}
