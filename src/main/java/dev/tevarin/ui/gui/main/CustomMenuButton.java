package dev.tevarin.ui.gui.main;

import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.font.RapeMasterFontManager;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.animation.impl.DecelerateAnimation;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

@Getter
@Setter

public class CustomMenuButton extends GuiScreen {

    public final String text;
    private Animation displayAnimation;

    private Animation hoverAnimation = new DecelerateAnimation(500, 1);
    ;
    public float x, y, width, height;
    public Runnable clickAction;
    public RapeMasterFontManager font = FontManager.font20;


    public CustomMenuButton(String text) {
        this.text = text;
        displayAnimation = new DecelerateAnimation(1000, 255);
        font = FontManager.other30;
    }

    @Override
    public void initGui() {
        hoverAnimation = new DecelerateAnimation(500, 1);
        displayAnimation.setDirection(Direction.FORWARDS);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float ticks) {
        boolean hovered = RenderUtil.isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);

        font.drawCenteredString(text, x + width / 2f, y + font.getMiddleOfBox(height) + 2f, new Color(255, 255, 255, (int) displayAnimation.getOutput()).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovered = RenderUtil.isHovering(x, y, width, height, mouseX, mouseY);
        if (hovered) clickAction.run();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    @Override
    public void onGuiClosed() {
        displayAnimation.setDirection(Direction.BACKWARDS);
    }
}