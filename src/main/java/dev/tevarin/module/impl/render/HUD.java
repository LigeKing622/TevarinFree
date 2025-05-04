package dev.tevarin.module.impl.render;

import dev.tevarin.Client;
import dev.tevarin.event.annotations.EventTarget;
import dev.tevarin.event.impl.events.EventPreRender;
import dev.tevarin.event.impl.events.EventRender2D;
import dev.tevarin.event.impl.events.EventTick;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.combat.Gapple;
import dev.tevarin.module.impl.player.BalanceTimer;
import dev.tevarin.module.impl.player.Blink;
import dev.tevarin.module.impl.world.Scaffold;
import dev.tevarin.ui.font.RapeMasterFontManager;
import dev.tevarin.ui.gui.main.GuiNeedBlur;
import dev.tevarin.ui.hud.notification.Notification;
import dev.tevarin.ui.hud.notification.NotificationManager;
import dev.tevarin.utils.render.ColorUtil;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.shader.KawaseBloom;
import dev.tevarin.utils.render.shader.KawaseBlur;
import dev.tevarin.utils.render.shader.ShaderElement;
import dev.tevarin.value.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;

import java.awt.*;

import static dev.tevarin.ui.font.FontManager.*;
import static dev.tevarin.utils.render.shader.ShaderElement.createFrameBuffer;
import static net.minecraft.client.gui.GuiChat.openingAnimation;

public class HUD extends Module {
    public HUD() {
        super("HUD", Category.Render);
    }

    public static ModeValue colorMode = new ModeValue("Color Mode", new String[]{"Fade", "Static", "Double", "RainBow"}, "Fade");
    public static ModeValue hotbarMode = new ModeValue("Hotbar Mode", new String[]{"Normal", "New"}, "Normal");
    public static ModeValue statsMode = new ModeValue("Stats Mode", new String[]{"Normal", "New"}, "Normal");
    public static ColorValue mainColor = new ColorValue("Main Color", new Color(255, 175, 63).getRGB());
    public static ColorValue secondColor = new ColorValue("Second Color", new Color(255, 175, 63).getRGB(), () -> colorMode.is("Double"));
    public ModeValue notiMode = new ModeValue("Notification Mode", new String[]{"Tevarin"}, "Tevarin");
    public static TextValue markTextValue = new TextValue("Text", "new SilenceFix()");
    public static BoolValue lowercase = new BoolValue("Lowercase", false);
    public final NumberValue iterations = new NumberValue("Blur Iterations", 2, 1, 8, 1, GuiNeedBlur::isBlurEnabled);
    public final NumberValue offset = new NumberValue("Blur Offset", 3, 1, 10, 1, GuiNeedBlur::isBlurEnabled);
    public final NumberValue shadowRadius = new NumberValue("Bloom Iterations", 3, 1, 8, 1, GuiNeedBlur::isBlurEnabled);
    public final NumberValue shadowOffset = new NumberValue("Bloom Offset", 1, 1, 10, 1, GuiNeedBlur::isBlurEnabled);
    public int offsetValue = 0;
    private Framebuffer bloomFramebuffer = new Framebuffer(1, 1, false);
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);
    private final RapeMasterFontManager productSansRegular = interSemiBold18;
    private Scaffold scaffold;

    private String username, fps;
    private float userWidth, fpsWidth, bpsWidth;

    @Override
    public void onEnable() {
        scaffold = getModule(Scaffold.class);

    }

    public static Color color(int tick) {
        Color textColor = new Color(-1);
        switch (colorMode.get()) {
            case "Fade":
                textColor = ColorUtil.fade(5, tick * 20, new Color(mainColor.getColor()), 1);
                break;
            case "Static":
                textColor = mainColor.getColorC();
                break;
            case "Double":
                tick *= 200;
                textColor = new Color(RenderUtil.colorSwitch(mainColor.getColorC(), secondColor.getColorC(), 5000, -tick / 40, 75, 2));
                break;
            case "RainBow":
                tick *= 4;
                textColor = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) tick / 50.0 * 1.6)) % 1.0F, 0.6F, 1.0F));
                break;
        }

        return textColor;
    }


    public void drawBlur() {
        stencilFramebuffer = createFrameBuffer(stencilFramebuffer);

        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);

        for (Runnable runnable : ShaderElement.getTasks()) {
            runnable.run();
        }
        ShaderElement.getTasks().clear();

        stencilFramebuffer.unbindFramebuffer();

        KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, iterations.getValue().intValue(), offset.getValue().intValue());
    }

    public void drawBloom() {

        bloomFramebuffer = createFrameBuffer(bloomFramebuffer);
        bloomFramebuffer.framebufferClear();
        bloomFramebuffer.bindFramebuffer(false);

        for (Runnable runnable : ShaderElement.getBloomTasks()) {
            runnable.run();
        }
        ShaderElement.getBloomTasks().clear();

        bloomFramebuffer.unbindFramebuffer();

        KawaseBloom.renderBlur(bloomFramebuffer.framebufferTexture, shadowRadius.getValue().intValue(), shadowOffset.getValue().intValue());
    }


    public void drawNotifications() {
        ScaledResolution sr = new ScaledResolution(mc);
        float yOffset = 0;
        int notificationHeight = 0, notificationWidth = 0, actualOffset;

        NotificationManager.setToggleTime(2f);

        for (Notification notification : NotificationManager.getNotifications()) {
            Animation animation = notification.getAnimation();
            animation.setDirection(notification.getTimerUtil().hasTimeElapsed((long) notification.getTime()) ? Direction.BACKWARDS : Direction.FORWARDS);

            if (animation.finished(Direction.BACKWARDS)) {
                NotificationManager.getNotifications().remove(notification);
                continue;
            }

            float x, y;
            animation.setDuration(200);
            actualOffset = notiMode.is("Tevarin") ? -7 : 3;
            switch (notiMode.getValue()) {
                case "Tevarin":
                    notificationHeight = 32;
                    notificationWidth = Math.max(font20.getStringWidth(notification.getDescription()), font20.getStringWidth(notification.getTitle())) + 15;
                    break;


            }

            x = (float) (sr.getScaledWidth() - (notificationWidth) * animation.getOutput());
            y = sr.getScaledHeight() - (yOffset + 18 + offsetValue+2 + notificationHeight + 15);
            switch (notiMode.getValue()) {
                case "Tevarin":
                    notification.drawTevarin(x, y, notificationWidth, notificationHeight);
                    break;



            }
            yOffset += (float) ((notificationHeight + actualOffset) * animation.getOutput());
        }
    }

    @EventTarget
    public void onPreRender(EventPreRender e) {
        for (dev.tevarin.ui.hud.HUD hud : Client.instance.hudManager.hudObjects.values()) {
            if (hud.m.getState())
                hud.predraw();
        }
    }

    @EventTarget
    public void onTick(EventTick e) {
        for (dev.tevarin.ui.hud.HUD hud : Client.instance.hudManager.hudObjects.values()) {
            if (hud.m.getState())
                hud.onTick();
        }
        username = mc.getSession() == null || mc.getSession().getUsername() == null ? "null" : mc.getSession().getUsername();
        userWidth = this.productSansRegular.getStringWidth("Ehereal - " + Client.instance.user + " - " + "Dev") + 2;
        fps = String.valueOf(Minecraft.getDebugFPS());
        fpsWidth = this.productSansRegular.getStringWidth("FPS:") + 2;
        bpsWidth = this.productSansRegular.getStringWidth("BPS:") + 2;

    }

    @EventTarget
    public void onRender2D(EventRender2D e) {

        for (dev.tevarin.ui.hud.HUD hud : Client.instance.hudManager.hudObjects.values()) {
            if (hud.m.getState())
                hud.draw(e.getPartialTicks());
        }
        drawNotifications();

        if (mc.thePlayer != null && mc.theWorld != null) {
            scaffold.renderCounter();
        }


    }

    public static class CounterBar {


    }
    private void drawBottomRight() {

        ScaledResolution sr = new ScaledResolution(mc);
        float yOffset = (float)(12.5 * (double)GuiChat.openingAnimation.getOutput());
        RapeMasterFontManager fr = font18 ;
            String text;{
                text = Client.version + " | " + "§r" + " | " + "Dev";
            }
            text = HUD.get(text);
            float x = (float)sr.getScaledWidth() - (fr.getStringWidth(text) + 3.0f);
            float y = (float)(sr.getScaledHeight() - (fr.getHeight() + 3)) - yOffset;
            String finalText = text;
            float f =  1.0f;
            fr.drawString(finalText, x + f, y + f, -16777216);
    }
    private double calculateBPS() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        return Math.round(bps * 100.0) / 100.0;
    }
    public static String get(String text) {
        return lowercase.get() ? text.toLowerCase() : text;
    }

}
