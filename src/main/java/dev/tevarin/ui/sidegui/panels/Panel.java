package dev.tevarin.ui.sidegui.panels;


import dev.tevarin.module.impl.render.HUD;
import dev.tevarin.ui.Screen;
import dev.tevarin.utils.render.ColorUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
public abstract class Panel implements Screen {
    private float x, y, width, height, alpha;

    public Color getTextColor() {
        return ColorUtil.applyOpacity(Color.WHITE, alpha);
    }

    public Color getAccentColor() {
        return ColorUtil.applyOpacity(HUD.color(1), alpha);
    }

}
