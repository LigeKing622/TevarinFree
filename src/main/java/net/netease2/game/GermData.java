/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 *
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 */
package net.netease2.game;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GermData {
    public static volatile byte[] fullData;
    public static volatile int currentIndex;
    public static volatile boolean finalData;
    static Map<String, List<GermMenuItem>> hytMainMenuItems;
    private boolean start;
    private int fullLength;
    private byte[] data;
    private boolean end;

    public int getGermId() {
        return -1;
    }

    public boolean getStart() {
        return this.start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public int getFullLength() {
        return this.fullLength;
    }

    public void setFullLength(int fullLength) {
        this.fullLength = fullLength;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean getEnd() {
        return this.end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public Object deserializeGermData(ByteBuf buffer) {
        this.start = buffer.readBoolean();
        this.fullLength = buffer.readInt();
        this.end = buffer.readBoolean();
        this.data = new byte[buffer.readableBytes()];
        buffer.readBytes(this.data);
        if (this.start) {
            fullData = new byte[this.fullLength];
            currentIndex = 0;
            finalData = false;
        } else if (this.end) {
            finalData = true;
        }
        System.arraycopy(this.data, 0, fullData, currentIndex, this.data.length);
        currentIndex += this.data.length;
        return this;
    }

    static {
        hytMainMenuItems = new HashMap<String, List<GermMenuItem>>();
    }
}

