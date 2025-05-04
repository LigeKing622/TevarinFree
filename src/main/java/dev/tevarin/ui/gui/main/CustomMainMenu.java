package dev.tevarin.ui.gui.main;


import dev.tevarin.Client;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.font.RapeMasterFontManager;
import dev.tevarin.ui.gui.alt.GuiAltManager;

import dev.tevarin.utils.math.TimerUtil;
import dev.tevarin.utils.render.AnimationUtils;
import dev.tevarin.utils.render.ParticleEngine;
import dev.tevarin.utils.render.RenderUtil;
import dev.tevarin.utils.render.RoundedUtil;
import dev.tevarin.utils.render.shader.KawaseBloom;
import dev.tevarin.utils.render.shader.KawaseBlur;
import dev.tevarin.utils.render.shader.ShaderElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.texture.DynamicTexture;

import net.minecraft.client.shader.Framebuffer;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;



import static dev.tevarin.ui.font.FontManager.font18;




public class CustomMainMenu extends GuiScreen {
    private final List<CustomMenuButton> buttons=Arrays.asList(new CustomMenuButton("L"), new CustomMenuButton("M"),
            new CustomMenuButton("O"), new CustomMenuButton("N"), new CustomMenuButton("P"));;
    boolean clientsetting = false;
    private final RapeMasterFontManager productSansRegular = font18;
    private Framebuffer stencilFramebuffer = new Framebuffer(1, 1, false);

    public CustomMainMenu() {
        needTranss = false;

    }

    /**
     * Counts the number of screen updates.
     */
    public static double introTrans;

    /**
     * Texture allocated for the current viewport of the main menu's panorama background.
     */
    private DynamicTexture viewportTexture;
    private final Object field_104025_t = new Object();
    private String field_92025_p;
    private String field_146972_A;

    /**
     * An array of all the paths to the panorama pictures.
     */

    private int field_92024_r;
    private int field_92023_s;
    private int field_92022_t;
    private int field_92021_u;
    private int field_92020_v;
    private int field_92019_w;

    private static final TimerUtil timer = new TimerUtil();
    boolean rev = false;
    double anim, anim2, anim3 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();


    public ParticleEngine pe = new ParticleEngine();



    boolean needTranss;

    public CustomMainMenu(boolean needTrans) {
        needTranss = needTrans;
    }

    /**
     * Returns true if this GUI should pause the game when it is displayed in single-player
     */
    public boolean doesGuiPauseGame() {
        return false;
    }

    /**
     * Fired when a key is typed (except F11 who toggle full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar,keyCode);
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui() {
        buttons.forEach(CustomMenuButton::initGui);
        timer.reset();
        pe.particles.clear();
        ScaledResolution sr = new ScaledResolution(this.mc);
        introTrans = sr.getScaledHeight();
        this.viewportTexture = new DynamicTexture(256, 256);
        Calendar var1 = Calendar.getInstance();
        var1.setTime(new Date());

        synchronized (this.field_104025_t) {
            this.field_92023_s = this.fontRendererObj.getStringWidth(this.field_92025_p);
            this.field_92024_r = this.fontRendererObj.getStringWidth(this.field_146972_A);
            int var5 = Math.max(this.field_92023_s, this.field_92024_r);
            this.field_92022_t = (this.width - var5) / 2;
            this.field_92021_u = 0;///((GuiButton)this.buttonList.get(0)).yPosition - 24;
            this.field_92020_v = this.field_92022_t + var5;
            this.field_92019_w = this.field_92021_u + 24;
        }
    }
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 84757) {
            rev = true;
            clientsetting = true;
            needTranss = true;
        }
    }



    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    boolean hovered = false;

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Client.instance.wallpaperEngine.render(width,height);
        float buttonWidth = 50;
        float buttonHeight = 25;
        float midX = width / 2F;
        float totalButtonWidth = buttonWidth * 5 + 5 * 4;

        int count = 0;
        ScaledResolution sr = new ScaledResolution(mc);
        float x = sr.getScaledWidth();
        float y = sr.getScaledHeight() - this.productSansRegular.getHeight() - 1;
        FontManager.font34.drawCenteredString("Welcome to Tevarin", midX - totalButtonWidth / 2F - 5+140, ((height / 2f - buttonHeight / 2f - 60)), new Color(255, 255, 255).getRGB());
        //background glow
        stencilFramebuffer = ShaderElement.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
        RoundedUtil.drawRound(midX - totalButtonWidth / 2F - 5, ((height / 2f - buttonHeight / 2f) - 30), 280, buttonHeight + 10, 6f, Color.WHITE);
        stencilFramebuffer.unbindFramebuffer();
        KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, 3, 1);
        KawaseBloom.shadow(() -> RoundedUtil.drawRound(midX - totalButtonWidth / 2F - 5, ((height / 2f - buttonHeight / 2f) - 30), 280, buttonHeight + 10, 6f, new Color(0, 0, 0,220)), 2, 1);
        RoundedUtil.drawRound(midX - totalButtonWidth / 2F - 5, ((height / 2f - buttonHeight / 2f) - 30), 280, buttonHeight + 10, 6f, new Color(32, 32, 32,110));
        //font glow
        final float coordX = 5;
        stencilFramebuffer = ShaderElement.createFrameBuffer(stencilFramebuffer);
        stencilFramebuffer.framebufferClear();
        stencilFramebuffer.bindFramebuffer(false);
        productSansRegular.drawString("©OptiFine HD M6_pre2 Ultra",coordX,y,Color.WHITE.getRGB());
        productSansRegular.drawString("Minecraft 1.8.9",coordX,y-13,Color.WHITE.getRGB());
        productSansRegular.drawString(Client.name+" "+Client.version,coordX,y-13*2,Color.WHITE.getRGB());
        stencilFramebuffer.unbindFramebuffer();
        KawaseBlur.renderBlur(stencilFramebuffer.framebufferTexture, 3, 1);
        KawaseBloom.shadow(() -> productSansRegular.drawString("©OptiFine HD M6_pre2 Ultra",coordX,y, Color.BLACK.getRGB()), 2, 1);
        KawaseBloom.shadow(() -> productSansRegular.drawString("Minecraft 1.8.9",coordX,y-13,Color.BLACK.getRGB()), 2, 1);
        KawaseBloom.shadow(() -> productSansRegular.drawString(Client.name+" "+Client.version,coordX,y-13*2,Color.BLACK.getRGB()), 2, 1);
        productSansRegular.drawString("©OptiFine HD M6_pre2 Ultra",coordX,y, new Color(255, 255, 255).getRGB());
        productSansRegular.drawString("Minecraft 1.8.9",coordX,y-13, new Color(255, 255, 255).getRGB());
        productSansRegular.drawString(Client.name+" "+Client.version,coordX,y-13*2, new Color(255, 255, 255).getRGB());

        if (!timer.hasReached(200) && needTranss) {
            anim = anim2 = anim3 = new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth();
            rev = true;
        } else if (!hovered || !clientsetting) {
            rev = false;
        }
        if (!needTranss && (!hovered || !clientsetting)) {
            anim = anim2 = anim3 = 0;
        }
        if (hovered) {
            rev = true;
            if (anim2 >= width - 5) {
                mc.displayGuiScreen(new GuiGoodBye());
            }
        } else if (clientsetting) {
            rev = true;

        }
        if (rev) {
            anim = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim, 7.0f / Minecraft.getDebugFPS());
            anim2 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim2, 5.0f / Minecraft.getDebugFPS());
            anim3 = AnimationUtils.animate(new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth(), anim3, 6.5f / Minecraft.getDebugFPS());
        } else {
            anim = AnimationUtils.animate(0, anim, 4.0f / Minecraft.getDebugFPS());
            anim2 = AnimationUtils.animate(0, anim2, 6.0f / Minecraft.getDebugFPS());
            anim3 = AnimationUtils.animate(0, anim3, 5.0f / Minecraft.getDebugFPS());
        }
        if (introTrans > 0) {
            introTrans -= introTrans / 7;
        }

        for (CustomMenuButton button : buttons) {
            button.x = midX - totalButtonWidth / 2 + count;
            button.y = ((height / 2f - buttonHeight / 2f) - 25);
            button.width = buttonWidth;
            button.height = buttonHeight;
            button.clickAction = () -> {
                switch (button.text) {
                    case "L":
                        mc.displayGuiScreen(new GuiSelectWorld(this));
                        break;
                    case "M":
                        mc.displayGuiScreen(new GuiMultiplayer(this));
                        break;
                    case "O":
                        mc.displayGuiScreen(new GuiAltManager(this));
                        break;
                    case "N":
                        mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
                        break;
                    case "P":
                        mc.shutdown();
                        break;
                }
            };
            button.drawScreen(mouseX, mouseY, partialTicks);
            count += buttonWidth + 5;
        }


        super.drawScreen(mouseX, mouseY, partialTicks);

        RenderUtil.drawRect(-10, -10, anim, (height * 2) + 10, new Color(203, 50, 255).getRGB());
        RenderUtil.drawRect(-10, -10, anim3, (height * 2) + 10, new Color(0, 217, 255).getRGB());
        RenderUtil.drawRect(-10, -10, anim2, (height * 2) + 10, new Color(47, 47, 47).getRGB());

    }


    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        buttons.forEach(button -> button.mouseClicked(mouseX, mouseY, mouseButton));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
