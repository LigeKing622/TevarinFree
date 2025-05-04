/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease2.party.ui.hyt.party;

import dev.tevarin.utils.render.RenderUtil;
import net.netease2.party.Sender;
import net.netease2.party.ui.hyt.germ.component.impl.ClickableButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

public class GuiHandleInvitation
        extends GuiScreen {
    private ClickableButton accept;
    private ClickableButton deny;
    private final VexViewButton acceptButton;
    private final VexViewButton denyButton;

    public GuiHandleInvitation(VexViewButton acceptButton, VexViewButton denyButton) {
        this.acceptButton = acceptButton;
        this.denyButton = denyButton;
    }

    @Override
    public void initGui() {
        this.accept = new ClickableButton(this.width / 2, this.height / 2 - 20, 100, 20, this.acceptButton.getName()) {

            @Override
            public void clicked() {
                Sender.clickButton(GuiHandleInvitation.this.acceptButton.getId());
            }
        };
        this.deny = new ClickableButton(this.width / 2, this.height / 2 + 20, 100, 20, this.denyButton.getName()) {

            @Override
            public void clicked() {
                Sender.clickButton(GuiHandleInvitation.this.denyButton.getId());
            }
        };
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableBlend();
        RenderUtil.drawImage(new ResourceLocation("tevarin/hyt/page/background.png"), (float) this.width / 2.0f - 100.0f, (float) this.height / 2.0f - 81.0f, 200.0f, 162.0f, -1);
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString("\u82b1\u96e8\u5ead\u7ec4\u961f\u7cfb\u7edf", (float) this.width / 2.0f, (float) this.height / 2.0f - 72.0f, new Color(216, 216, 216).getRGB());
        this.accept.drawScreen();
        this.deny.drawScreen();
        GlStateManager.disableBlend();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.accept.mouseClicked(mouseX, mouseY, mouseButton);
        this.deny.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}

