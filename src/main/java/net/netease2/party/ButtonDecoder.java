/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.netease2.party;

import net.netease2.party.ui.hyt.party.Request;
import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

public class ButtonDecoder {
    private final String[] elements;
    public final boolean invited;
    public final String inviter;
    public final boolean sign;
    public final boolean list;
    public final ArrayList<Request> requests;

    public ButtonDecoder(ByteBuf byteBuf) {
        int index;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String result = this.decode(bytes);
        if (!result.contains("[but]\u624b\u52a8\u8f93\u5165")) {
            this.sign = result.contains("[gui]https://img.166.net/gameyw-misc/opd/squash/20221221/104939-4q3d0pgm59.png");
            if (this.sign) {
                result = result.replace("[but]", "[but]sign");
            }
            this.list = result.contains("[gui]https://ok.166.net/gameyw-misc/opd/squash/20210915/195203-c2npy8skq6.png");
            this.requests = new ArrayList();
            if (this.list) {
                result = result.replace("null<#>[but]", "null<#>[denyButton]");
                result = result.replace("[but]", "[but]accept");
                result = result.replace("[denyButton]", "[but]deny");
            }
        } else {
            this.sign = false;
            this.list = false;
            this.requests = null;
        }
        this.elements = result.split("<&>");
        if (this.list) {
            boolean nextDeny = false;
            String cacheName = "";
            int cacheAccept = -1;
            for (int i = 0; i < this.elements.length; ++i) {
                String element = this.elements[i];
                if (!nextDeny) {
                    if (!element.contains("<#>[but]accept")) continue;
                    cacheName = element.substring(0, element.length() - 8);
                    cacheAccept = i += 6;
                    nextDeny = true;
                    continue;
                }
                if (!element.contains("<#>[but]deny")) continue;
                nextDeny = false;
                this.requests.add(new Request(cacheName, String.valueOf(cacheAccept), String.valueOf(i += 6)));
            }
        }
        if ((index = this.containsString("\u9080\u8bf7\u4f60\u52a0\u5165\u961f\u4f0d")) != -1) {
            this.invited = true;
            this.inviter = this.elements[index - 3].replace("\u00a76\uff1a<#>[txt]50", "").replace("\u00a76\u73a9\u5bb6 \u00a73\u00a7l", "");
        } else {
            this.invited = false;
            this.inviter = "";
        }
    }

    private String decode(byte[] bytes) {
        try {
            int read;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
            byte[] array = new byte[256];
            while ((read = gZIPInputStream.read(array)) >= 0) {
                byteArrayOutputStream.write(array, 0, read);
            }
            return byteArrayOutputStream.toString(String.valueOf(StandardCharsets.UTF_8));
        } catch (IOException iOException) {
            return "";
        }
    }

    public VexViewButton getButton(String name) {
        for (int i = 0; i < this.elements.length; ++i) {
            String e = this.elements[i];
            if (!e.endsWith("[but]" + name)) continue;
            return new VexViewButton(name, this.elements[i + 6]);
        }
        return null;
    }

    public int containsString(String s) {
        for (int i = 0; i < this.elements.length; ++i) {
            String e = this.elements[i];
            if (!e.contains(s)) continue;
            return i;
        }
        return -1;
    }

    public String getElement(int index) {
        return this.elements[index];
    }
}

