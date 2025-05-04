/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease2.party.ui.hyt.germ.component.impl;

import net.netease2.party.ui.hyt.germ.component.GermComponent;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class GermText
        implements GermComponent {
    private final String text;

    public GermText(String text) {
        this.text = text;
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString(this.text, x, y - 5, new Color(216, 216, 216).getRGB());
    }

    @Override
    public void mouseClicked(String parentUuid) {
    }

    @Override
    public int getHeight() {
        return 16;
    }

    @Override
    public int getSeparation() {
        return 8;
    }
}

