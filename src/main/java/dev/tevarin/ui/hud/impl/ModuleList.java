package dev.tevarin.ui.hud.impl;

import dev.tevarin.Client;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.font.RapeMasterFontManager;
import dev.tevarin.ui.hud.HUD;
import dev.tevarin.utils.render.ColorUtil;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.shader.ShaderElement;
import dev.tevarin.value.impl.BoolValue;
import dev.tevarin.value.impl.ModeValue;
import dev.tevarin.value.impl.NumberValue;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleList extends HUD {
    public static final BoolValue importantModules = new BoolValue("Important", false);
    public static final BoolValue rectangle = new BoolValue("Rectangle", true);
    public static final BoolValue rainbow = new BoolValue("RainBow", true);


    public static final NumberValue height = new NumberValue("Height", 11, 9, 20, 1);
    public static final ModeValue fontValue = new ModeValue("Font", new String[]{"Client", "Minecraft"}, "Client");

    public static final ModeValue animation = new ModeValue("Animation", new String[]{"MoveIn", "ScaleIn"}, "ScaleIn");
    public static final ModeValue fontSize = new ModeValue("Font Size", new String[]{"16", "18", "20", "22", "bold18"}, "18");

    public static final BoolValue background = new BoolValue("Background", true);
    public static final NumberValue backgroundAlpha = new NumberValue("Background Alpha", .35, .01, 1, .01);

    public List<Module> modules;


    public ModuleList() {
        super(100, mc.fontRendererObj.FONT_HEIGHT + 10, "ArrayList");
    }

    @Override
    public void drawShader() {
    }

    @Override
    public void predrawhud() {

    }

    @Override
    public void onTick() {
        ArrayList<Module> moduleList = new ArrayList<>();
        moduleList.addAll(Client.instance.moduleManager.getModuleMap().values());
        if (modules == null) {
            modules = moduleList;

            modules.removeIf(module -> (module.getCategory() == Category.Render || module.getCategory() == Category.HUD) && importantModules.getValue());
        }
        modules.sort(Comparator.<Module>comparingDouble(m -> {
            String name = m.getName() + (m.getSuffix() != "" ? " " + m.getSuffix() : "");
            return getFont().getStringWidth(name);
        }).reversed());
    }

    private String formatModule(Module module) {
        String name = StringUtils.replace(module.getName(), " ", "");
        String formatText = "%s %s%s";
        String suffix = module.getSuffix();
        if (suffix == null || suffix.isEmpty()) {
            return name;
        } else {
            return String.format(formatText, name, EnumChatFormatting.AQUA, suffix);
        }
    }

    @Override
    public void drawHUD(int xPos, int yPos, float partialTicks) {
        if (modules == null) {
            // 或者初始化 modules，或者记录一个错误，或者直接返回避免异常
            return;
        }

        double yOffset = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        int count = 1;
        GlStateManager.pushMatrix();
        for (Module module : modules) {
            if (importantModules.getValue() && (module.getCategory() == Category.Render || module.getCategory() == Category.HUD))
                continue;
            final Animation moduleAnimation = module.getAnimation();
            if (moduleAnimation == null) {
                // 可以在这里记录错误或终止绘制
                continue;
            }
            moduleAnimation.setDirection(module.getState() ? Direction.FORWARDS : Direction.BACKWARDS);

            if (!module.getState() && moduleAnimation.finished(Direction.BACKWARDS)) continue;

            String displayText = formatModule(module);
            double textWidth = getFont().getStringWidth(displayText);

            double xValue = sr.getScaledWidth() - (10/*dragX*/);


            boolean flip = xValue <= sr.getScaledWidth() / 2f;
            double x = flip ? xValue : sr.getScaledWidth() - (textWidth + 3) - 4;


            float alphaAnimation = 1;

            double y = yOffset + 4 + 3;

            float heightVal = (float) (height.getValue() + 1);

            switch (animation.getValue()) {
                case "MoveIn":
                    if (flip) {
                        x -= Math.abs((moduleAnimation.getOutput() - 1) * (sr.getScaledWidth() - (2 - textWidth)));
                    } else {
                        x += Math.abs((moduleAnimation.getOutput() - 1) * (2 + textWidth));
                    }
                    break;
                case "ScaleIn":
                    RenderUtil.scaleStart((float) (x + getFont().getStringWidth(displayText) / 2f), (float) (y + heightVal / 2 - getFont().getHeight() / 2f), (float) moduleAnimation.getOutput());
                    alphaAnimation = (float) moduleAnimation.getOutput();
                    break;
            }

            if (background.getValue()) {
                Gui.drawRect3((float) (x - 2), (float) (y - 4), (float) (getFont().getStringWidth(displayText) + 5), (float) (heightVal),
                        ColorUtil.applyOpacity(new Color(20, 20, 20), backgroundAlpha.getValue().floatValue() * alphaAnimation).getRGB());

                //blur
                float finalAlphaAnimation = alphaAnimation;
                double finalX = x;
                ShaderElement.addBlurTask(() -> {
                    RenderUtil.scaleStart((float) (finalX + getFont().getStringWidth(displayText) / 2f), (float) (y + heightVal / 2 - getFont().getHeight() / 2f), (float) moduleAnimation.getOutput());
                    Gui.drawRect3((float) (finalX - 2), (float) (y - 4), (float) (getFont().getStringWidth(displayText) + 5), (float) (heightVal), Color.BLACK.getRGB());
                    RenderUtil.scaleEnd();
                });
                ShaderElement.addBloomTask(() -> {
                    RenderUtil.scaleStart((float) (finalX + getFont().getStringWidth(displayText) / 2f), (float) (y + heightVal / 2 - getFont().getHeight() / 2f), (float) moduleAnimation.getOutput());
                    Gui.drawRect3((float) (finalX - 2), (float) (y - 4), (float) (getFont().getStringWidth(displayText) + 5), (float) (heightVal), Color.BLACK.getRGB());
                    RenderUtil.scaleEnd();
                });

            }
            int textcolor;
            if (rainbow.get()) {
                textcolor = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) count / 50.0 * 1.6)) % 1.0F, 0.3F, 1F)).getRGB();
            } else {
                textcolor = dev.tevarin.module.impl.render.HUD.color(count).getRGB();
            }
            boolean usingVanillaFont = fontValue.get().equals("Minecraft");

            if (rectangle.getValue())
                RenderUtil.drawRectWH(RenderUtil.width() - (1f) - 4, (float) (y - 4), 1f, (float) (heightVal),
                        textcolor);


//                if (background.get() || hud.blur.get() || hud.bloom.get()) {
//                    RenderUtil.drawRectWH(x - 2, (y - 3) + heightVal / 2F - 3, 1, 6, ColorUtil.applyOpacity(textcolor, alphaAnimation));
//                }
            getFont().drawStringWithShadow(displayText, (float) x, (float) ((y - 1 - (usingVanillaFont ? 2 : 0)) + getFont().getMiddleOfBox((float) heightVal)), ColorUtil.applyOpacity(textcolor, alphaAnimation));


            if (animation.getValue().equals("ScaleIn")) {
                RenderUtil.scaleEnd();
            }

            yOffset += moduleAnimation.getOutput() * heightVal;
            count++;
        }
        GlStateManager.popMatrix();
    }

    private FontRenderer getFont() {
        if (fontValue.is("Minecraft")) {
            return mc.fontRendererObj;
        } else {
            RapeMasterFontManager font = FontManager.font18;
            switch (fontSize.getValue()) {
                case "16":
                    font = FontManager.font16;
                    break;
                case "18":
                    font = FontManager.font18;
                    break;
                case "20":
                    font = FontManager.font20;
                    break;
                case "22":
                    font = FontManager.font22;
                    break;
                case "bold18":
                    font = FontManager.interSemiBold18;
                    break;
            }

            return font;
        }

    }


}
