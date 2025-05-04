package dev.tevarin.ui.hud.impl;

import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.font.RapeMasterFontManager;
import dev.tevarin.ui.hud.HUD;
import dev.tevarin.utils.render.*;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.animation.impl.ContinualAnimation;
import dev.tevarin.utils.render.animation.impl.EaseBackIn;
import dev.tevarin.utils.render.shader.ShaderElement;
import dev.tevarin.value.impl.ModeValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static dev.tevarin.utils.render.RenderUtil.*;
//import static dev.ethereal.module.impl.render.HUD.styleValue;

public class Effects extends HUD {

    public Effects() {
        super(25, 120, "Effects");
    }

    public static int offsetValue = 0;
    private final Map<Potion, PotionData> potionMap = new HashMap<>();
    private final Map<Integer, Integer> potionMaxDurations = new HashMap<>();
    private final ContinualAnimation widthanimation = new ContinualAnimation();
    private final ContinualAnimation heightanimation = new ContinualAnimation();
    private final EaseBackIn animation = new EaseBackIn(200, 1F, 1.3F);
    List<PotionEffect> effects = new ArrayList<>();
    public static Color color1;
    public static Color color2;
    @Override
    public void drawShader() {

    }


    @Override
    public void onTick() {

    }

    private int maxString = 0;

    @Override
    public void drawHUD(int x, int y, float partialTicks) {

                setHeight(25);
                setWidth(85);
                java.util.List<PotionEffect> potions = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
                potions.sort(Comparator.comparingDouble(e -> -mc.fontRendererObj.getStringWidth(I18n.format(e.getEffectName()))));
                int count = 0;
                for (PotionEffect effect : potions) {
                    Potion potion = Potion.potionTypes[effect.getPotionID()];
                    String name = I18n.format(potion.getName());
                    String time = get(" " + Potion.getDurationString(effect) + "");
                    float w = FontManager.font18.getStringWidth(name);
                    int finalCount = count;


                    FontManager.font18.drawStringWithShadow(
                                get(name),
                                x + 21.5f,
                                y + (count + 5) - 2,
                              -1);

                    FontManager.font18.drawStringWithShadow(time, x + 20,
                            y + (count + 15),
                            -1);

                    if (potion.hasStatusIcon()) {
                        mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));
                        int i1 = potion.getStatusIconIndex();
                        GlStateManager.enableBlend();
                        Gui.drawTexturedModalRect(x + 2, y + (count + 3), i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
                    }

                    count += 29;
                    offsetValue = count * FontManager.font18.getHeight();
                }
            }

    @Override
    public void predrawhud() {

    }

    public static String get(String text) {
        return text;
    }

    private String intToRomanByGreedy(int num) {
        final int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        final String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < values.length && num >= 0; i++)
            while (values[i] <= num) {
                num -= values[i];
                stringBuilder.append(symbols[i]);
            }

        return stringBuilder.toString();
    }
}
