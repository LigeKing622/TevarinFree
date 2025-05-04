package dev.tevarin.ui.hud.notification;

import dev.tevarin.module.impl.render.HUD;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.utils.MSTimer;
import dev.tevarin.utils.TimerUtil;
import dev.tevarin.utils.render.ColorUtil;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.RoundedUtil;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.impl.DecelerateAnimation;
import dev.tevarin.utils.render.shader.ShaderElement;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;

import java.awt.*;

@Getter
public class Notification {
    private final NotificationType notificationType;
    private final String title, description;
    private final float time;
    final MSTimer timer = new MSTimer();
    private final TimerUtil timerUtil;
    private final Animation animation;
    public String icon;

    public Notification(NotificationType type, String title, String description) {
        this(type, title, description, NotificationManager.getToggleTime());
    }

    public Notification(NotificationType type, String title, String description, float time) {
        this.title = title;
        this.description = description;
        this.time = (long) (time * 1000);
        timerUtil = new TimerUtil();
        this.notificationType = type;
        animation = new DecelerateAnimation(300, 1);

        switch (type) {
            case DISABLE:
                this.icon = "B";
                break;
            case SUCCESS:
                this.icon = "A";
                break;
            case INFO:
                this.icon = "C";
                break;
            case WARNING:
                this.icon = "D";
                break;
        }

    }

    public void drawTevarin(float x, float y, float width, float height) {

        Color notificationColor = ColorUtil.applyOpacity(getNotificationType().getColor(), 70);
        float finalx;

        if (getNotificationType() == NotificationType.INFO) {
            finalx = x + 3;
        } else {
            finalx = x;
        }
        float percentage = Math.min((timerUtil.getTime() / getTime()), 1);


        RenderUtil.drawRectWH(x, y + height - 5, width, height - 8, new Color(0, 0, 0, 139).getRGB());

        FontManager.icontestFont40.drawString(this.icon, finalx + 3.5f, y + height - 1, notificationColor.getRGB());

        FontManager.font16.drawString(getTitle(), x + 24, y + 30,-1);
        FontManager.font14.drawString(getDescription(), x + 24, y + 40, -1);
    }
    public MSTimer getTimer() {
        return timer;
    }
    public double getCount() {
        return MathHelper.clamp_float(getTimer().getCurrentMS() - getTimer().getLastMS(), 0, (float) getTime());
    }

}