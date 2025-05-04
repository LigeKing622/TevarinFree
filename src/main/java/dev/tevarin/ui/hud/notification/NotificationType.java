package dev.tevarin.ui.hud.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.FontUtils;

import java.awt.*;

@Getter
@AllArgsConstructor
public enum NotificationType {
    SUCCESS(new Color(17, 243, 87, 229) , new ResourceLocation("tevarin/ICON/SUCCESS.png")),
    DISABLE(new Color(229, 11, 11, 219),  new ResourceLocation("tevarin/ICON/DANGER.png")),
    INFO(Color.YELLOW,  new ResourceLocation("tevarin/ICON/INFO.png")),
    WARNING(Color.ORANGE,  new ResourceLocation("tevarin/ICON/WARNING.png"));
    private final Color color;
    private final ResourceLocation icon;
}