/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.Unpooled
 *  org.json.JSONObject
 */
package net.netease2.party;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;

public class Sender {
    private static void sendJson(JSONObject json) {
        byte[] data = Sender.encode(json.toString());
        ByteBuf buf = Unpooled.wrappedBuffer((byte[]) data);
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C17PacketCustomPayload("VexView", new PacketBuffer(buf)));
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static byte[] encode(String json) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));) {
            byte[] byArray;
            try (ByteArrayOutputStream bout = new ByteArrayOutputStream();) {
                int read;
                GZIPOutputStream out = new GZIPOutputStream(bout);
                byte[] array = new byte[256];
                while ((read = in.read(array)) >= 0) {
                    out.write(array, 0, read);
                }
                out.finish();
                byArray = bout.toByteArray();
            }
            return byArray;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public static void openGui() {
        Sender.sendJson(new JSONObject().put("packet_sub_type", (Object) "null").put("packet_data", (Object) "null").put("packet_type", (Object) "opengui"));
    }

    public static void closeGui() {
        Sender.sendJson(new JSONObject().put("packet_sub_type", (Object) "null").put("packet_data", (Object) "null").put("packet_type", (Object) "gui_close"));
    }

    public static void clickButton(String id) {
        Sender.openGui();
        Sender.sendJson(new JSONObject().put("packet_sub_type", (Object) id).put("packet_data", (Object) "null").put("packet_type", (Object) "button"));
        Sender.closeGui();
    }
}

