package net.netease.packet.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.netease.PacketProcessor;
import net.netease.packet.GermPacket;

public class PacketNegative1
        implements GermPacket {
    private static int totalBytesCopied;
    private static byte[] bufferArray;
    private int bytesToCopy;
    private boolean isBooleanValue;
    private byte[] packetBytes;
    private boolean shouldProcess;

    @Override
    public void process() {
        if (this.isBooleanValue) {
            bufferArray = new byte[this.bytesToCopy];
        }
        System.arraycopy(this.packetBytes, 0, bufferArray, totalBytesCopied, this.packetBytes.length);
        totalBytesCopied += this.packetBytes.length;
        if (this.shouldProcess) {
            ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(bufferArray);
            PacketProcessor.INSTANCE.processPacket(new PacketBuffer(wrappedBuffer));
            totalBytesCopied = 0;
            bufferArray = null;
        }
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) {
        this.isBooleanValue = packetBuffer.readBoolean();
        this.bytesToCopy = packetBuffer.readInt();
        this.shouldProcess = packetBuffer.readBoolean();
        this.packetBytes = packetBuffer.readByteArray();
    }

    @Override
    public int getPacketId() {
        return -1;
    }
}
