/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 */
package net.netease2.party.ui.hyt.party;

import dev.tevarin.utils.render.RenderUtil;
import net.netease2.party.ui.hyt.germ.GuiGermScreen;
import net.netease2.party.ui.hyt.germ.component.impl.GermModButton;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;

public class GuiInput
        extends GuiScreen {
    private InputField inputField;
    private GermModButton confirm;
    @Nullable
    private final GuiGermScreen parent;
    private final String guiUuid;
    private final String parentUuid;

    public GuiInput(@Nullable GuiGermScreen parent, String guiUuid, String parentUuid) {
        this.parent = parent;
        this.guiUuid = guiUuid;
        this.parentUuid = parentUuid;
    }

    @Override
    public void initGui() {
        this.inputField = new InputField(this.width / 2 - 50, this.height / 2 - 30, 100, 20);
        this.confirm = new GermModButton("submit", "\u63d0\u4ea4") {

            @Override
            protected void beforeClick() {
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(10)).writeString(GuiInput.this.guiUuid).writeString("input").writeString("").writeInt(1))));
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(10)).writeString(GuiInput.this.guiUuid).writeString("input").writeString(GuiInput.this.inputField.getText().trim()).writeInt(0))));
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(10)).writeString(GuiInput.this.guiUuid).writeString("input").writeString(GuiInput.this.inputField.getText().trim()).writeInt(3))));
            }

            @Override
            protected void whenClick() {
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(26)).writeString("GUI$" + GuiInput.this.guiUuid + "@input").writeString("{\"input\":\"" + GuiInput.this.inputField.getText() + "\"}")));
            }
        };
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.enableBlend();
        RenderUtil.drawImage(new ResourceLocation("tevarin/hyt/page/background.png"), (float) (this.width / 2 - 100), (float) (this.height / 2 - 81), 200.0f, 162.0f, -1);
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString("\u82b1\u96e8\u5ead\u7ec4\u961f\u7cfb\u7edf", this.width / 2, this.height / 2 - 72, new Color(216, 216, 216).getRGB());
        this.inputField.drawTextBox();
        this.confirm.drawComponent(this.guiUuid, this.width / 2, this.height / 2 + 30, mouseX, mouseY);
        GlStateManager.disableBlend();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.inputField.isFocused()) {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(10)).writeString(this.guiUuid).writeString("input").writeString(this.inputField.getText().trim()).writeInt(2))));
        }
        this.confirm.mouseClicked(this.guiUuid);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.inputField.keyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        this.inputField.updateCursorCounter();
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11)).writeString(this.parentUuid)));
        if (this.parent != null) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(Unpooled.buffer().writeInt(11)).writeString(this.parent.getUUID())));
        }
    }
}

