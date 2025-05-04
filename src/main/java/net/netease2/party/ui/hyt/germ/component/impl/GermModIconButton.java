/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 */
package net.netease2.party.ui.hyt.germ.component.impl;


import dev.tevarin.utils.render.RenderUtil;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;

public class GermModIconButton
        extends GermModButton {
    private final ResourceLocation image;
    private final int width;
    private final int height;

    public GermModIconButton(String path, String imagePath, int width, int height) {
        super(path, "");
        this.image = new ResourceLocation(imagePath);
        this.width = width;
        this.height = height;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    public static boolean isHovered(float x, float y, float x1, float y1, int mouseX, int mouseY) {
        return (float) mouseX >= x && (float) mouseX <= x1 && (float) mouseY >= y && (float) mouseY <= y1;
    }

    @Override
    public void drawComponent(String parentUuid, int x, int y, int mouseX, int mouseY) {
        if (GermModIconButton.isHovered(x - this.width / 2, y - this.height / 2, x + this.width / 2, y + this.height / 2, mouseX, mouseY)) {
            if (!this.hovered) {
                this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13)).writeString(parentUuid).writeString(this.path).writeInt(2))));
                this.hovered = true;
            }
        } else if (this.hovered) {
            this.mc.thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("germmod-netease", new PacketBuffer(new PacketBuffer(Unpooled.buffer().writeInt(13)).writeString(parentUuid).writeString(this.path).writeInt(3))));
            this.hovered = false;
        }
        RenderUtil.drawImage(this.image, (float) x - (float) this.width / 2.0f, (float) y - (float) this.height / 2.0f, (float) this.width, (float) this.height, -1);
    }
}

