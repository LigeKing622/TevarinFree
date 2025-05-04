package dev.tevarin.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class HYTUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static boolean isInLobby() {
        if (HYTUtils.mc.theWorld == null) {
            return false;
        }
        return HYTUtils.mc.theWorld.playerEntities.stream().anyMatch(e -> e.getName().contains("\u95ee\u9898\u53cd\u9988")) || HYTUtils.mc.theWorld.playerEntities.stream().anyMatch(e -> e.getName().contains("\u7ec3\u4e60\u573a")) || HYTUtils.mc.theWorld.playerEntities.stream().anyMatch(e -> e.getName().contains("\u5355\u4eba\u6a21\u5f0f"));
    }

    public static boolean isHoldingGodAxe(EntityPlayer player) {
        ItemStack holdingItem = player.getEquipmentInSlot(0);
        return HYTUtils.isGodAxe(holdingItem);
    }

    public static boolean isGodAxe(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getItem() != Items.golden_axe) {
            return false;
        }
        int durability = stack.getMaxDamage() - stack.getItemDamage();
        if (durability > 2) {
            return false;
        }
        NBTTagList enchantmentTagList = stack.getEnchantmentTagList();
        if (enchantmentTagList == null) {
            return false;
        }
        for (int i = 0; i < enchantmentTagList.tagCount(); ++i) {
            int level;
            NBTTagCompound nbt = (NBTTagCompound) enchantmentTagList.get(i);
            if (!nbt.hasKey("id") || !nbt.hasKey("lvl") || nbt.getInteger("id") != 16 || (level = nbt.getInteger("lvl")) < 666)
                continue;
            return true;
        }
        return false;
    }

    public static boolean isKBBall(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getItem() != Items.slime_ball) {
            return false;
        }
        NBTTagList enchantmentTagList = stack.getEnchantmentTagList();
        if (enchantmentTagList == null) {
            return false;
        }
        for (int i = 0; i < enchantmentTagList.tagCount(); ++i) {
            int level;
            NBTTagCompound nbt = (NBTTagCompound) enchantmentTagList.get(i);
            if (!nbt.hasKey("id") || !nbt.hasKey("lvl") || nbt.getInteger("id") != 19 || (level = nbt.getInteger("lvl")) < 2)
                continue;
            return true;
        }
        return false;
    }

    public static boolean isFireEnchantBall(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        if (stack.getItem() != Items.magma_cream) {
            return false;
        }
        NBTTagList enchantmentTagList = stack.getEnchantmentTagList();
        if (enchantmentTagList == null) {
            return false;
        }
        for (int i = 0; i < enchantmentTagList.tagCount(); ++i) {
            int level;
            NBTTagCompound nbt = (NBTTagCompound) enchantmentTagList.get(i);
            if (!nbt.hasKey("id") || !nbt.hasKey("lvl") || nbt.getInteger("id") != 20 || (level = nbt.getInteger("lvl")) < 1)
                continue;
            return true;
        }
        return false;
    }

    public static boolean isHoldingEnchantedGoldenApple(EntityPlayer player) {
        ItemStack holdingItem = player.getEquipmentInSlot(0);
        if (holdingItem == null) {
            return false;
        }
        if (holdingItem.getItem() != Items.golden_apple) {
            return false;
        }
        return holdingItem.hasEffect();
    }

    public static int hasEatenGoldenApple(EntityPlayer player) {
        PotionEffect regenPotion = player.getActivePotionEffect(Potion.regeneration);
        if (regenPotion == null) {
            return -1;
        }
        if (regenPotion.getAmplifier() < 4) {
            return -1;
        }
        return regenPotion.getDuration();
    }

    public static int isRegen(EntityPlayer player) {
        PotionEffect regenPotion = player.getActivePotionEffect(Potion.regeneration);
        if (regenPotion == null) {
            return -1;
        }
        return regenPotion.getDuration();
    }

    public static int isStrength(EntityPlayer player) {
        PotionEffect strengthPotion = player.getActivePotionEffect(Potion.damageBoost);
        if (strengthPotion == null) {
            return -1;
        }
        return strengthPotion.getDuration();
    }
}

