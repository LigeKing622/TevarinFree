package dev.tevarin.ui.sidegui.utils;


import dev.tevarin.ui.Screen;
import dev.tevarin.ui.font.RapeMasterFontManager;
import dev.tevarin.ui.sidegui.SideGUI;
import dev.tevarin.utils.render.ColorUtil;
import dev.tevarin.utils.render.HoveringUtil;
import dev.tevarin.utils.render.RoundedUtil;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.animation.impl.DecelerateAnimation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;

import static dev.tevarin.ui.font.FontManager.bold18;
import static dev.tevarin.ui.font.FontManager.font18;

@Setter
@Getter
@RequiredArgsConstructor
public class ActionButton implements Screen {
    private float x, y, width, height, alpha;
    private boolean bypass = false;
    private final String name;
    private boolean bold = false;
    private RapeMasterFontManager font;
    private Color color = ColorUtil.tripleColor(55);
    private Runnable clickAction;

    private final Animation hoverAnimation = new DecelerateAnimation(250, 1);

    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        boolean hovering = SideGUI.isHovering(x, y, width, height, mouseX, mouseY);

        if (bypass) {
            hovering = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        }

        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);

        Color rectColor = ColorUtil.interpolateColorC(color, color.brighter(), (float) hoverAnimation.getOutput());
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.applyOpacity(rectColor, alpha));
        if (font != null) {
            font.drawCenteredString(name, x + width / 2f, y + font.getMiddleOfBox(height), ColorUtil.applyOpacity(-1, alpha));
        } else {
            if (bold) {
                bold18.drawCenteredString(name, x + width / 2f, y + font.getMiddleOfBox(height), ColorUtil.applyOpacity(-1, alpha));
            } else {
                font18.drawCenteredString(name, x + width / 2f, y + font.getMiddleOfBox(height), ColorUtil.applyOpacity(-1, alpha));
            }
        }

    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovering = SideGUI.isHovering(x, y, width, height, mouseX, mouseY);
        if (bypass) {
            hovering = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        }
        if (hovering && button == 0) {
            //TODO: remove this if statement
            if (clickAction != null) {
                clickAction.run();
            }
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
