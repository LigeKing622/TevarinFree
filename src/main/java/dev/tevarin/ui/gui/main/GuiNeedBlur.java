package dev.tevarin.ui.gui.main;



import dev.tevarin.Client;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.utils.render.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.util.List;



public class GuiNeedBlur extends GuiScreen {
    public ParticleEngine pe = new ParticleEngine();
    public GuiButton yesButton;
    public GuiButton noButton;
    private static boolean isBlurEnabled;
    double anim, anim2, anim3 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
    public static boolean logined = false;
    @Override
    public void initGui(){
        int h = new ScaledResolution(this.mc).getScaledHeight();
        int w = new ScaledResolution(this.mc).getScaledWidth();
        this.yesButton = new UIFlatButton(1, (int) (w / 2f) - 20 - 25, (int) (h / 2f) + 15, 40, 20, "是的", new Color(25,25,25).getRGB());
        this.noButton = new UIFlatButton(3, (int) (w / 2f) - 20 + 25, (int) (h / 2f) + 15, 40, 20, "不要", new Color(25,25,25).getRGB());
        this.buttonList.add(this.yesButton);
        this.buttonList.add(this.noButton);
    }

    @Override
    protected void keyTyped(char var1, int var2) {

    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                isBlurEnabled = true;
                FileManager.save("NeedBlur.json","true",false);

                logined = true;
                mc.displayGuiScreen(new GuiWelcome());
                break;
            case 3:
                isBlurEnabled = false;
                FileManager.save("NeedBlur.json","false",false);
                logined = true;
                mc.displayGuiScreen(new GuiWelcome());
                break;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int h = new ScaledResolution(this.mc).getScaledHeight();
        int w = new ScaledResolution(this.mc).getScaledWidth();

        GlStateManager.translate(0, 0, 0);
        Gui.drawRect2(-10, -10, anim2 , h + 20, new Color(0, 0, 0).getRGB());

        if (isBlurConfiged()) {
            isBlurEnabled = setBlurEnabled();
            mc.displayGuiScreen(new GuiWelcome());
            return;
        }

            anim = AnimationUtils.animate(w, anim, 6.0f / Minecraft.getDebugFPS());
            anim3 = AnimationUtils.animate(w, anim3, 5.5f / Minecraft.getDebugFPS());
            anim2 = AnimationUtils.animate(w, anim2, 4.0f / Minecraft.getDebugFPS());

        RenderUtil.drawRectWH(-10, -10, anim, height + 10, new Color(203, 50, 255).getRGB());
        RenderUtil.drawRectWH(-10, -10, anim3, height + 10, new Color(0, 217, 255).getRGB());
        RenderUtil.drawRectWH(-10, -10, anim2, height + 10, new Color(47, 47, 47).getRGB());

        pe.render(0, 0);
        FontManager.font18.drawCenteredString("启用模糊效果?", w / 2f, h / 2f - 25 + 2, -1);
        //(部分电脑不支持模糊效果会导致黑屏)
        FontManager.font13.drawCenteredString("部分电脑配置比较低模糊效果会导致卡顿，如果您不清楚是否能流畅运行本效果请不要开启", w / 2f, h / 2f - 15 + 4, -1);
        FontManager.font13.drawCenteredString("如果卡顿请删除\".minecraft/tevarin/NeedBlur.json\"来再次访问本界面", w / 2f, h / 2f - 7 + 4, -1);
        FontManager.font16.drawCenteredString(Client.name+" made by TN (ChildHood Team)", width / 2f, height - FontManager.font16.getHeight() - 6f, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    private boolean setBlurEnabled() {
        List<String> names = FileManager.read("NeedBlur.json");
        for (String v : names) {
            return v.contains("true");
        }
        return false;
    }
    public static boolean isBlurEnabled() {
        return isBlurEnabled;
    }
    private boolean isBlurConfiged(){
        List<String> names = FileManager.read("NeedBlur.json");
        return !names.isEmpty();
    }
}
