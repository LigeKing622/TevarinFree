package dev.tevarin.utils.render;

import dev.tevarin.utils.TimerUtil;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * @author AquaVase
 * @since 2/8/2024 - 8:59 PM
 */
@Getter
public class WallpaperEngine {
    private final TimerUtil timer = new TimerUtil();

    private int framerate;
    private int grabbedFrames;
    private int lastFrameTexID;

    private FFmpegFrameGrabber grabber;
    private Frame currentFrame;

    public void setup(File videoFile, int framerate) {
        this.framerate = framerate;

        try {
            avutil.av_log_set_level(avutil.AV_LOG_ERROR);
            grabber = FFmpegFrameGrabber.createDefault(videoFile);
            grabber.start();
        } catch (FFmpegFrameGrabber.Exception e) {
            // Ignored
        }
    }

    public void render(int width, int height) {
        if (this.timer.hasTimeElapsed((1000 / this.framerate), true)) {
            try {
                if (this.grabbedFrames >= this.grabber.getLengthInFrames() - 1) {
                    this.grabber.setFrameNumber(0);
                    this.grabbedFrames = 0;
                }
                this.currentFrame = this.grabber.grabImage();
                if (this.currentFrame != null) {
                    GL11.glDeleteTextures(this.lastFrameTexID);
                    int texID = GL11.glGenTextures();
                    GL11.glBindTexture(3553, texID);
                    GL11.glTexParameteri(3553, 10241, 9729);
                    GL11.glTexParameteri(3553, 10240, 9729);
                    GL11.glTexImage2D(3553, 0, 6408, this.currentFrame.imageWidth, this.currentFrame.imageHeight, 0, 32992, 5121, (ByteBuffer) this.currentFrame.image[0]);
                    this.lastFrameTexID = texID;
                    this.grabbedFrames++;
                }
            } catch (FFmpegFrameGrabber.Exception exception) {
                exception.printStackTrace();
            }
        }
        GlStateManager.bindTexture(this.lastFrameTexID);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, width, height, width, height);
    }

    public void close() {
        try {
            if (grabber != null) {
                grabber.stop();
                grabber.close();
            }
        } catch (FrameGrabber.Exception e) {
            // Ignored
        }
    }
}
