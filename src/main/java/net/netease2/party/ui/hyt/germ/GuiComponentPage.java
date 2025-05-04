/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  org.lwjgl.opengl.GL11
 */
package net.netease2.party.ui.hyt.germ;

import dev.tevarin.utils.render.RenderUtil;
import net.netease2.party.ui.hyt.germ.component.GermComponent;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public class GuiComponentPage
        extends GuiGermScreen {
    private final String uuid;
    private final LinkedHashSet<GermComponent> components = new LinkedHashSet();
    private String title;
    private int componentHeight;

    public GuiComponentPage(String uuid, ArrayList<GermComponent> components) {
        this.uuid = uuid;
        if (components.isEmpty()) {
            Minecraft.getMinecraft().displayGuiScreen(null);
            return;
        }
        this.title = "\u82b1\u96e8\u5ead\u83dc\u5355";
        this.components.addAll(components);
    }

    @Override
    public void initGui() {
        ArrayList<GermComponent> components = new ArrayList<GermComponent>(this.components);
        for (int i = 0; i < components.size(); ++i) {
            GermComponent component = components.get(i);
            this.componentHeight += component.getHeight();
            if (i == components.size()) continue;
            this.componentHeight += component.getSeparation();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.pushMatrix();
        RenderUtil.drawImage(new ResourceLocation("tevarin/hyt/page/header.png"), (float) this.width / 2.0f - 100.0f, (float) this.height / 2.0f - 50.0f - (float) this.componentHeight / 2.0f + 20.0f, 200.0f, 40.0f, -1);
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString(this.title, (float) this.width / 2.0f, (float) this.height / 2.0f - 40.0f - (float) this.componentHeight / 2.0f + 20.0f, new Color(216, 216, 216).getRGB());
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int y = this.height / 2 - this.componentHeight / 2 + 30;
        ArrayList<GermComponent> components = new ArrayList<GermComponent>(this.components);
        for (int i = 0; i < components.size(); ++i) {
            GermComponent component = components.get(i);
            int backgroundHeight = component.getHeight() + (i == components.size() ? 0 : component.getSeparation());
            RenderUtil.drawImage(new ResourceLocation("tevarin/hyt/page/body.png"), (float) this.width / 2.0f - 100.0f, (float) (y - 20), 200.0f, (float) backgroundHeight, -1);
            GL11.glEnable((int) 3042);
            component.drawComponent(this.uuid, this.width / 2, y, mouseX, mouseY);
            y += backgroundHeight;
        }
        RenderUtil.drawImage(new ResourceLocation("tevarin/hyt/page/footer.png"), (float) this.width / 2.0f - 100.0f, (float) (y - 20), 200.0f, 30.0f, -1);
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (GermComponent component : this.components) {
                component.mouseClicked(this.uuid);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
            this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11)).writeString(this.uuid)));
        }
    }

    public GuiComponentPage title(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String getUUID() {
        return this.uuid;
    }
}

