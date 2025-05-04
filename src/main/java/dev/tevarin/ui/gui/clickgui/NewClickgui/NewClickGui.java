package dev.tevarin.ui.gui.clickgui.NewClickgui;


import dev.tevarin.Client;
import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.module.impl.render.ClickGUI;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.font.RapeMasterFontManager;
import dev.tevarin.utils.render.Rectangle;
import dev.tevarin.utils.render.*;
import dev.tevarin.utils.render.animation.AnimationUtils;
import dev.tevarin.utils.render.shader.ShaderElement;
import dev.tevarin.value.Value;
import dev.tevarin.value.impl.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.StringUtils;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;
import org.lwjglx.opengl.Display;

import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static dev.tevarin.ui.font.FontManager.iconFont16;


public class NewClickGui extends GuiScreen {
    public static NewClickGui INSTANCE = new NewClickGui();
    private short[] date;
    public float x = 200, y = 25;
    public float width = 400;
    public float height = 300;
    public float search = 300;
    public float visibleAnimation;
    private boolean quitting = false;
    private boolean dragging;
    private float dragX, dragY;
    public int wheel = Mouse.hasWheel() ? Mouse.getDWheel() * 4 : 0;

    private List<Module> leftModules = new CopyOnWriteArrayList<>();
    private List<Module> rightModules = new CopyOnWriteArrayList<>();

    private List[] lists = new List[]{};

    public NumberFormat nf = new DecimalFormat("0000");
    private Category.Pages current = Category.Pages.COMBAT;
    private NumberValue currentSliding = null;

    private Value<?> dropdownItem;
    private TextValue currentEditing;
    private Rectangle protectArea;

    private final InputField searchTextField = new InputField(FontManager.font16);
    private boolean searching = false;

    private final float[] moduleWheel = {0f, 0f};

    private float alphaAnimate = 10;

    private String tooltip = null;
    private float offsetY = 0;
    private boolean mouseDown = false;

    private final ArrayList<ItemStack> itemStacks = new ArrayList<>();

    public NewClickGui() {
        INSTANCE = this;
        dropdownItem = null;
        protectArea = null;

        init();
    }

    public void init() {
        font = FontManager.interSemiBold18;
        for (Item item : Item.itemRegistry) {
            itemStacks.add(new ItemStack(item));
        }
    }

    private float scrollAni;
    private float CscrollAni;

    private static RapeMasterFontManager font;


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {

        tooltip = null;
        visibleAnimation = AnimationUtils.animateSmooth(visibleAnimation, quitting ? 0 : 100, .2F);
        if (quitting) {
            currentEditing = null;
            if (Display.isActive() && !mc.inGameHasFocus) {
                mc.inGameHasFocus = true;
                mc.mouseHelper.grabMouseCursor();
            }

            if (Math.round(visibleAnimation) <= 2) mc.displayGuiScreen(null);
        }

        if (!Mouse.isButtonDown(0) && dragging) dragging = false;
        if (dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }
        if (!quitting) {
            ShaderElement.addBlurTask(()-> Gui.drawRect2(x, y, width, height, new Color(255, 255, 255).getRGB()));
            ShaderElement.addBloomTask(()-> Gui.drawRect2(x, y, width, height, new Color(25, 25, 25).getRGB()));
            Gui.drawRect2(x, y, width, height, new Color(27, 27, 27).getRGB());
            Gui.drawRect2(x, y, 100, height, new Color(39, 39, 39).getRGB());

        }
        float width = Math.max(FontManager.bold38.getStringWidth(Client.name.toUpperCase()), FontManager.bold38.getStringWidth("NOVOLINE"));
        if (width > FontManager.bold38.getStringWidth("NOVOLINE")) {
            this.width = AnimationUtils.animateSmooth(this.width, 520 + width - FontManager.bold38.getStringWidth("NOVOLINE"), 0.5f);
        } else {
            this.width = AnimationUtils.animateSmooth(this.width, 520, 0.5f);
        }
        FontManager.bold34.drawString("Tevarin", (float) (x + 8.7 + width / 2) - 44.5f, y + 12.5f, Color.WHITE.getRGB());
        FontManager.bold22.drawCenteredString(Client.version, (float) (x + 4.7 + width / 2), y + 28.5f, Color.WHITE.getRGB());

        float pageY = 44;
        MaskUtil.defineMask();
        RenderUtil.drawRectWH(x, y + pageY - 4, width + 10, 344, -1);
        MaskUtil.finishDefineMask();
        MaskUtil.drawOnMask();
        float sb = 0;
        for (Category pageManager : Category.values()) {
            sb += 12;
            for (Category.Pages ignored : pageManager.getSubPages()) {
                sb += 26;
            }
            sb += 4;
        }
        CscrollY = Math.max(CscrollY, -sb + 344);
        if (RenderUtil.isHovering(x, y + pageY, width + 10, 340, mouseX, mouseY)) {
            CscrollAni = AnimationUtils.animateSmooth(CscrollAni, CscrollY, 0.3f);
        } else {
            CscrollY = CscrollAni;
        }
        pageY += CscrollAni;

        pageY += 12;
        for (Category.Pages cate : Category.Pages.values()) {
            String head = StringUtils.left(cate.name(), 1);
            String display = cate.name().substring(1);

            if (cate == current) {
                float finalPageY1 = pageY;
                Gui.drawRect2(x, y + pageY - 4, 100, 26, new Color(28, 26, 26, 228).getRGB());
            }
            ClickGUI clickGUIMod = Client.instance.getModuleManager().getModule(ClickGUI.class);
            boolean focusedConfigGui = Client.instance.getSideGui().isFocused();
            int fakeMouseX = focusedConfigGui ? 0 : mouseX, fakeMouseY = focusedConfigGui ? 0 : mouseY;
            float bannerHeight = 75 / 2f;
            float minus = (bannerHeight + 3) + 33;
            float catHeight = ((height - minus) / (Category.values().length));
            float finalPageY = pageY;

            boolean hovering = HoveringUtil.isHovering(x, y, 100, catHeight - 16, fakeMouseX, fakeMouseY);
            Color categoryColor = hovering ? ColorUtil.tripleColor(110).brighter() : ColorUtil.tripleColor(110);
            Color selectColor = (cate == current) ? Color.WHITE : categoryColor;
            String text = head + display.toLowerCase();
            text = text.replaceAll("_", " ");
            {
                FontManager.bold22.drawString(text, x + 10, y + pageY + 6f, selectColor.getRGB());

            }
            pageY += 26;
        }


        MaskUtil.resetMask();
        StencilUtil.initStencilToWrite();
        RenderUtil.drawRoundedRect(x, y, this.width, height, 6, getColor(255, 255, 255, 0).getRGB());
        StencilUtil.bindReadStencilBuffer(1);
        RenderUtil.rectangle2(x + width + 8 + 8, y, this.width - width, height, getColor(12, 12, 12, 0));
        StencilUtil.endStencilBuffer();
        {
            Gui.drawRect2(x + width + 16 + 10, y + 10, search, 20, new Color(42, 43, 43, 219).getRGB());
            if (!searching)
                FontManager.neverlose24.drawString("j", x + width + 16 + 10 + 3, y + 16, getColor(150, 150, 150, 100).getRGB());

            searchTextField.setBackgroundText("Press CTRL+F to search with in gui..");
            searchTextField.setDrawingLine(false);
            searchTextField.setxPosition(x + width + 16 + 10 + 3 + (searching ? 0 : 16));
            searchTextField.setyPosition(y + 13);
            searchTextField.setWidth(search);
            searchTextField.setHeight(20);
            searchTextField.setDrawingBackground(false);
            searchTextField.drawTextBox(mouseX, mouseY);
        }
        RenderUtil.drawRectWH(x + width+14, y + 41, this.width - 107, .5, new Color(91, 88, 88, 255).getRGB());

        StencilUtil.initStencilToWrite();
        RenderUtil.drawRectWH(x, y + 45, this.width, height - 46, getColor(201, 201, 201).getRGB());

        StencilUtil.bindReadStencilBuffer(1);
        wheel = Mouse.hasWheel() ? Mouse.getDWheel() * 12 : 0;

        if (current.module) {
            float left = render(leftModules, x + width + 24, mouseX, mouseY);
            float right = render(rightModules, x + width + 24 + 198, mouseX, mouseY);

            final float[] nextWheel = RenderUtil.getNextWheelPosition(wheel, moduleWheel, y + 10, y + 290, Math.max(left, right), 0, RenderUtil.isHovering(x + 120, y + 40, this.width - 120, this.height - 40, mouseX, mouseY));
            moduleWheel[0] = nextWheel[0];
            moduleWheel[1] = Math.max(left, right) > this.height ? Math.max(nextWheel[1], -Math.max(left, right) + this.height - 60) : nextWheel[1];

        } else {


        }

        StencilUtil.endStencilBuffer();


        RenderUtil.renderPlayer2D(mc.thePlayer, x + 16 - 11, y + height - 30, 27, 24, -1);

        GlStateManager.disableTexture2D();
        GlStateManager.color(1f, 1f, 1f);

        GlStateManager.enableTexture2D();

        GlStateManager.disableTexture2D();
        GlStateManager.color(1f, 1f, 1f);
        GlStateManager.disableTexture2D();
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        RenderUtil.drawRectWH(x, y + height - 40, 100, .5, new Color(91, 88, 88, 255).getRGB());

        font.drawString(Client.instance.user, x + 34, y + height - 30 + 6, getColor(255, 255, 255).getRGB());
        font.drawString(EnumChatFormatting.GRAY + "Time: ", x + 34, y + height - 30 + 18, getColor(3, 168, 245).getRGB());
        font.drawStringDynamic(hour + ":" + minute, x + 34 + font.getStringWidth("Time: "), y + height - 30 + 18, 1, 6);

        alphaAnimate = AnimationUtils.animateSmooth(alphaAnimate, 180, 0.4f);
        /*if (alphaAnimate > 20) {

         *//*ShaderElement.addBlurTask(()->RenderUtil.drawRectWH(0, 0, new ScaledResolution(mc).getScaledWidth(), new ScaledResolution(mc).getScaledHeight(), new Color(0, 0, 0, 255).getRGB()));*//*
            RenderUtil.drawRectWH(0, 0, new ScaledResolution(mc).getScaledWidth(), new ScaledResolution(mc).getScaledHeight(), new Color(0, 0, 0, ((int) alphaAnimate)).getRGB());
        }*/

        if (this.dropdownItem != null && this.protectArea != null) {
            if (this.dropdownItem instanceof ModeValue) {

                ModeValue property = (ModeValue) this.dropdownItem;
                Color disabledColor = new Color(64, 68, 75);
                property.animation = AnimationUtils.animateSmooth(property.animation, 255.0f, 0.5f);
                Gui.drawRect2(this.protectArea.getX(), this.protectArea.getY(), this.protectArea.getWidth(), this.protectArea.getHeight() + 1.0f, disabledColor.getRGB());
                int buttonY = 0;
                for (String s : property.getModes()) {
                    font.drawString(s, this.protectArea.getX() + 3.0f, this.protectArea.getY() + (float) buttonY + 6.0f, !property.is(s) ? this.getColor(RenderUtil.reAlpha(new Color(64, 103, 75), (int) property.animation)).getRGB() : this.getColor(RenderUtil.reAlpha(new Color(255, 248, 248), (int) property.animation)).getRGB());
                    buttonY += 14;
                }
            }
            if (dropdownItem instanceof ColorValue) {
                ColorValue cp = (ColorValue) dropdownItem;

                final Color valColor = cp.getColorC();

                HSBData hsbData = new HSBData(valColor);

                final float[] hsba = {
                        hsbData.getHue(),
                        hsbData.getSaturation(),
                        hsbData.getBrightness(),
                        hsbData.getAlpha(),
                };

                RoundedUtil.drawRoundOutline(protectArea.getX(), protectArea.getY(), protectArea.getWidth(), protectArea.getHeight() + 1, 2, 0.1F, getColor(5, 16, 26), getColor(217, 217, 217));
                RenderUtil.drawRectWH(protectArea.getX() + 3, protectArea.getY() + 3, 61, 61, getColor(0, 0, 0).getRGB());
                RenderUtil.drawRectWH(protectArea.getX() + 3.5, protectArea.getY() + 3.5, 60, 60, getColor(Color.getHSBColor(hsba[0], 1, 1)).getRGB());
                RenderUtil.drawHGradientRect(protectArea.getX() + 3.5, protectArea.getY() + 3.5, 60, 60, getColor(Color.getHSBColor(hsba[0], 0, 1)).getRGB(), 0x00F);
                RenderUtil.drawVGradientRect(protectArea.getX() + 3.5, protectArea.getY() + 3.5, 60, 60, 0x00F, getColor(Color.getHSBColor(hsba[0], 1, 0)).getRGB());

                RenderUtil.drawRectWH(protectArea.getX() + 3.5 + hsba[1] * 60 - .5, protectArea.getY() + 3.5 + ((1 - hsba[2]) * 60) - .5, 1.5, 1.5, getColor(0, 0, 0).getRGB());
                RenderUtil.drawRectWH(protectArea.getX() + 3.5 + hsba[1] * 60, protectArea.getY() + 3.5 + ((1 - hsba[2]) * 60), .5, .5, getColor(valColor).getRGB());

                final boolean onSB = RenderUtil.isHovering(protectArea.getX() + 3, protectArea.getY() + 3, 61, 61, mouseX, mouseY);

                if (onSB && Mouse.isButtonDown(0)) {
                    hsbData.setSaturation(Math.min(Math.max((mouseX - protectArea.getX() - 3) / 60F, 0), 1));
                    hsbData.setBrightness(1 - Math.min(Math.max((mouseY - protectArea.getY() - 3) / 60F, 0), 1));
                    cp.setColor(hsbData.getAsColor().getRGB());

                }

                RenderUtil.drawRectWH(protectArea.getX() + 67, protectArea.getY() + 3, 10, 61, getColor(0, 0, 0).getRGB());

                for (float f = 0F; f < 5F; f += 1F) {
                    final Color lasCol = Color.getHSBColor(f / 5F, 1F, 1F);
                    final Color tarCol = Color.getHSBColor(Math.min(f + 1F, 5F) / 5F, 1F, 1F);
                    RenderUtil.drawVGradientRect(protectArea.getX() + 67.5, protectArea.getY() + 3.5 + f * 12, 9, 12, getColor(lasCol).getRGB(), getColor(tarCol).getRGB());
                }

                RenderUtil.drawRectWH(protectArea.getX() + 67.5, protectArea.getY() + 2 + hsba[0] * 60, 9, 2, getColor(0, 0, 0).getRGB());
                RenderUtil.drawRectWH(protectArea.getX() + 67.5, protectArea.getY() + 2.5 + hsba[0] * 60, 9, 1, getColor(204, 198, 255).getRGB());

                final boolean onHue = RenderUtil.isHovering(protectArea.getX() + 67, protectArea.getY() + 3, 10, 61, mouseX, mouseY);

                if (onHue && Mouse.isButtonDown(0)) {
                    hsbData.setHue(Math.min(Math.max((mouseY - protectArea.getY() - 3) / 60F, 0), 1));
                    cp.setColor(hsbData.getAsColor().getRGB());
                    cp.setRainbowEnabled(false);
                }

                if (cp.isAlphaChangeable()) {

                    RenderUtil.drawRectWH(protectArea.getX() + 3, protectArea.getY() + 67, 61, 9, getColor(0, 0, 0).getRGB());

                    for (int xPosition = 0; xPosition < 30; xPosition++)
                        for (int yPosition = 0; yPosition < 4; yPosition++)
                            RenderUtil.drawRectWH(protectArea.getX() + 3.5 + (xPosition * 2), protectArea.getY() + 67.5 + (yPosition * 2), 2, 2, ((yPosition % 2 == 0) == (xPosition % 2 == 0)) ? getColor(255, 255, 255).getRGB() : getColor(190, 190, 190).getRGB());

                    RenderUtil.drawHGradientRect(protectArea.getX() + 3.5, protectArea.getY() + 67.5, 60, 8, 0x00F, getColor(Color.getHSBColor(hsba[0], 1, 1)).getRGB());

                    RenderUtil.drawRectWH(protectArea.getX() + 2.5 + hsba[3] * 60, protectArea.getY() + 67.5, 2, 8, getColor(0, 0, 0).getRGB());
                    RenderUtil.drawRectWH(protectArea.getX() + 3 + hsba[3] * 60, protectArea.getY() + 67.5, 1, 8, getColor(204, 198, 255).getRGB());

                    final boolean onAlpha = RenderUtil.isHovering(protectArea.getX() + 3, protectArea.getY() + 67, 61, 9, mouseX, mouseY);

                    if (onAlpha && Mouse.isButtonDown(0)) {
                        hsbData.setAlpha(Math.min(Math.max((mouseX - protectArea.getX() - 3) / 60F, 0), 1));
                    }
                }
            }
        }


        if (tooltip != null && !tooltip.isEmpty()) {

            ShaderElement.addBlurTask(() -> RoundedUtil.drawRound(mouseX + 6, mouseY + 6, font.getStringWidth(findLongestString(tooltip.split("\n"))) + 10, tooltip.split("\n").length * 14 + (tooltip.split("\n").length == 1 ? 0 : 4), 2, true, new Color(10, 19, 30, 255)));
            RoundedUtil.drawRound(mouseX + 6, mouseY + 6, font.getStringWidth(findLongestString(tooltip.split("\n"))) + 10, tooltip.split("\n").length * 14 + (tooltip.split("\n").length == 1 ? 0 : 4), 2, false, new Color(255, 255, 255, 30));

            float y = 5;
            for (String s : tooltip.split("\n")) {
                font.drawString(s, mouseX + 6 + 4, mouseY + 6 + y, getColor(255, 255, 255).getRGB());
                if (tooltip.split("\n").length != 1)
                    y += 14;
            }
        }
    }

    protected boolean check(double x, double y, double x2, double y2, double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    private boolean checkClick() {
        if (!mouseDown && Mouse.isButtonDown(0)) {
            mouseDown = true;
            return true;
        }

        return false;
    }

    public static double round(final double value, final double inc) {
        if (inc == 0.0) return value;
        else if (inc == 1.0) return Math.round(value);
        else {
            final double halfOfInc = inc / 2.0;
            final double floored = Math.floor(value / inc) * inc;

            if (value >= floored + halfOfInc)
                return new BigDecimal(Math.ceil(value / inc) * inc)
                        .doubleValue();
            else return new BigDecimal(floored)
                    .doubleValue();
        }
    }

    private float render(List<Module> modules, float offset, int mouseX, int mouseY) {
        float moduleY = 0 + moduleWheel[1];


        for (Module module : modules) {

            int predictionHeight = 16;
            for (Value<?> property : module.getValues()) {
                if (property.isAvailable()) predictionHeight += property.getHeight();
            }
            Gui.drawRect2(offset, y + 46 + FontManager.font16.getHeight() + 4 + moduleY - 10, 190, predictionHeight + 10, new Color(35, 35, 35, 250).getRGB());
            Gui.drawRect2(offset, y + 46 + FontManager.font16.getHeight() + 4 + moduleY - 10, 190, 30, new Color(35, 35, 35, 166).getRGB());
            moduleY += 10;
            int textColor = new Color(255, 255, 255, 255).getRGB();

            FontManager.bold20.drawString(module.getName().replaceAll("(?<=[a-z])(?=[A-Z])", " ").toUpperCase(), offset + 4, y + 54 + moduleY, new Color(255, 255, 255).getRGB());

            if (RenderUtil.isHovering(offset + 168, y + 50 + moduleY + 5, 16, 8, mouseX, mouseY))
                tooltip = "";
            if (module.state) {
                textColor = new Color(37, 142, 40, 202).getRGB();
            }


            module.cGUIAnimation = AnimationUtils.animateSmooth(module.cGUIAnimation, module.getState() ? 10 : 0, 0.5f);
            RenderUtil.drawImage(new ResourceLocation("tevarin/images/shadow.png"), offset + 160 + module.cGUIAnimation, 2f + y + 44 + moduleY + 9, 16, 16);
            RenderUtil.drawCircleCGUI(offset + 168 + 8, y + 57 + moduleY + 2, 10, textColor);
            if (module.state) {
                iconFont16.drawString("o", (offset + 168 + 4), (y + 55 + 2 + moduleY), Color.WHITE.getRGB());
            }
            if (!module.getValues().isEmpty())
                RenderUtil.drawRectWH(offset + 4, y + 50 + moduleY + 18, 190 - 8, .5, new Color(68, 70, 72, 100).getRGB());
            moduleY += 18;

            for (Value<?> property : module.getValues()) {
                if (!property.isAvailable()) continue;
                float boolWH = 10;
                if (property instanceof BoolValue) {
                    Color accentColor = new Color(37, 142, 40, 202);
                    Color disabledColor = new Color(64, 68, 75);
                    final BoolValue bp = (BoolValue) property;
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 6 + 2f, bp.get() ? getColor(255, 255, 255).getRGB() : getColor(115, 112, 112).getRGB());

                    Color rectColor = bp.get() ? accentColor : disabledColor.brighter();
                    Gui.drawRect2(offset + 162 + 5, 2f + y + 48 + moduleY + 5, boolWH, boolWH, rectColor.getRGB());
                    Gui.drawRect2(offset + 162 + .5f + 5, 2f + y + 48 + moduleY + 5 + .5f, boolWH - 1, boolWH - 1, disabledColor.getRGB());
                    if (bp.getValue()) {
                        iconFont16.drawCenteredString("o", offset + 161 + 5 + boolWH / 2f, 2f + y + 51 + moduleY + 5 + iconFont16.getMiddleOfBox(boolWH) + .5f, Color.WHITE.getRGB());
                    }
                }

                /*if (property instanceof LabelProperty) {
                    font.drawCenteredString(translateManager.trans("module." + module.getName().toLowerCase() + "." + property.getName().toLowerCase(), property.getName().toUpperCase()), offset + 94, y + 50 + moduleY + 6, getColor(255, 255, 255).getRGB());
                }*/

                if (property instanceof ColorValue) {
                    ColorValue cp = (ColorValue) property;
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 6 + 2f, getColor(77, 77, 77).getRGB());
                    RenderUtil.drawCircleCGUI(offset + 175, 2f + y + 50 + moduleY + 9, 11, getColor(new Color(cp.getColor())).getRGB());

                    if (dropdownItem == cp) {
                        protectArea = new Rectangle(offset + 100, 2f + y + moduleY + 50 + 24, 80, cp.isAlphaChangeable() ? 80 : 67);
                    }

                }
                if (property instanceof NumberValue) {
                    DecimalFormat df = new DecimalFormat("#.#");

                    final NumberValue dp = (NumberValue) property;
                    String display = String.valueOf(dp.getValue());
                    if (display.endsWith(".0")) display = display.substring(0, display.length() - 2);
                    else if (display.startsWith("0.")) display = "." + display.substring(2);
                    else if (display.startsWith("-0.")) display = "-" + display.substring(2);
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 6 + 2f, dp.sliding ? Color.WHITE.getRGB() : getColor(115, 112, 112).getRGB());
                    FontManager.font14.drawCenteredString(display, offset + 190 - 13, 2f + y + 50 + moduleY + 6, getColor(255, 255, 255).getRGB());
                    Gui.drawRect2(offset + 96, 2f + y + 50 + moduleY + 8, 70, 2, new Color(41, 39, 41, 122).getRGB());
                    final double ratio = (dp.getValue() - dp.getMin()) / (dp.getMax() - dp.getMin());
                    int displayLength = (int) (ratio * 70);
                    displayLength = Math.min(displayLength, 70);
                    dp.animatedPercentage = AnimationUtils.animateSmooth(dp.animatedPercentage, displayLength, 0.2F);
                    Gui.drawRect2(offset + 92 + 3, 2f + y + 50 + moduleY + 8, dp.animatedPercentage - 3, 2, new Color(193, 193, 193, 232).getRGB());
                    Gui.drawRect2(offset + 92 + 3, 2f + y + 50 + moduleY + 8, dp.animatedPercentage - 3, 2, new Color(94, 92, 94, 197).getRGB());
                    Gui.drawRect2(92 + offset + dp.animatedPercentage, 2f + y + 50 + moduleY + 6, 2, 6, new Color(255, 255, 255, 232).getRGB());

                    if (dp.sliding) {

                        double num = Math.max(dp.getMin(), Math.min(dp.getMax(), round((mouseX - (offset + 92)) * (dp.getMax() - dp.getMin()) / 70 + dp.getMin(), dp.getInc())));
                        num = (double) Math.round(num * (1.0D / dp.getInc())) / (1.0D / dp.getInc());
                        dp.setValue(num);
                    }
                }

                if (property instanceof ModeValue) {
                    final ModeValue sp = (ModeValue) property;
                    Color accentColor = new Color(28, 39, 50, 139);
                    Color disabledColor = new Color(64, 68, 75);
                    sp.height = 24f;
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 9 + 1f, getColor(115, 112, 112).getRGB());

                    Gui.drawRect2(offset + 100, y + 50 + moduleY + 3.5F, 80, 16, accentColor.getRGB());
                    Gui.drawRect2(offset + 100 + .5f, y + 50 + moduleY + 3.5F + .5f, 80 - 1, 16 - 1, disabledColor.getRGB());
                    MaskUtil.drawOnMask();
                    try {
                        font.drawString(sp.get(), offset + 104, y + 50 + moduleY + 9, new Color(255, 255, 255, 205).getRGB());
                    } catch (Exception e) {
                    }
                    MaskUtil.resetMask();
                    //
                    float spmaxWidth = 0;
                    for (String s : sp.getModes()) {
                        float f = font.getStringWidth(s) + 12;
                        if (f > spmaxWidth) {
                            spmaxWidth = f;
                        }
                    }
                    //
                    if (dropdownItem == sp) {
                        protectArea = new Rectangle(offset + 100, y + moduleY + 50 + 24, spmaxWidth > 80 ? spmaxWidth : 80, 14 * sp.getModes().length);
                    }

                }
                if (property instanceof TextValue) {
                    final TextValue tp = (TextValue) property;
                    Color accentColor = new Color(28, 39, 50, 139);
                    Color disabledColor = new Color(64, 68, 75);
                    boolean isMe = currentEditing == tp;
                    font.drawString(property.getName(), offset + 4, y + 50 + moduleY + 9 + 2f, getColor(255, 255, 255).getRGB());
                    Gui.drawRect2(offset + 100, y + 50 + moduleY + 3.5F, 80, 18, accentColor.getRGB());
                    Gui.drawRect2(offset + 100 + .5f, y + 50 + moduleY + 3.5F + .5f, 80 - 1, 16 - 1, isMe ? disabledColor.brighter().getRGB() : disabledColor.getRGB());
                    MaskUtil.defineMask();
                    Gui.drawRect2(offset + 100, y + 50 + moduleY + 3.5F, 80, 18, accentColor.getRGB());
                    Gui.drawRect2(offset + 100 + .5f, y + 50 + moduleY + 3.5F + .5f, 80 - 1, 16 - 1, isMe ? disabledColor.brighter().getRGB() : disabledColor.getRGB());
                    MaskUtil.finishDefineMask();
                    MaskUtil.drawOnMask();
                    font.drawString(tp.get() + (isMe ? "_" : ""), offset + 104, y + 50 + moduleY + 9, getColor(255, 255, 255).getRGB());

                    RenderUtil.drawRectWH(offset + 104, y + 50 + moduleY + 9, font.getStringWidth(tp.getSelectedString()), font.getHeight(), getColor(255, 255, 255, 100).getRGB());
                    MaskUtil.resetMask();
                }
                moduleY += property.getHeight();

                final List<Value<?>> visible = new ArrayList<>(module.getValues());
                visible.removeIf(Value::isHidden);


            }
            moduleY += 4;
        }


        return moduleY - moduleWheel[1];
    }

    public void setQuitting(boolean quitting) {
        this.quitting = quitting;
    }

    public boolean isOpened() {
        return !quitting;
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        try {
            Client.instance.configManager.saveAllConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dropdownItem = null;
        searching = false;
        quitting = false;
    }


    private float scrollY;
    private float CscrollY;

    @Override
    public void handleMouseInput() throws IOException {
        this.scrollY += (float) Mouse.getEventDWheel();
        if (this.scrollY >= 0.0f) {
            this.scrollY = 0.0f;
        }


        this.CscrollY += (float) Mouse.getEventDWheel();
        if (this.CscrollY >= 0.0f) {
            this.CscrollY = 0.0f;
        }

        int i = Mouse.getEventX() * new ScaledResolution(mc).getScaledWidth() / this.mc.displayWidth;
        int j = new ScaledResolution(mc).getScaledHeight() - Mouse.getEventY() * new ScaledResolution(mc).getScaledHeight() / this.mc.displayHeight - 1;
        super.handleMouseInput();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        if (keyCode == Keyboard.KEY_RETURN && searching)
            return;

        if (keyCode == Keyboard.KEY_ESCAPE)
            mc.displayGuiScreen(null);

        if (GuiScreen.isKeyComboCtrlF(keyCode)) {
            searching = !searching;
            if (searching) {
                lists = new List[]{leftModules, rightModules};
                searchTextField.setText("");
            } else {
                leftModules = lists[0];
                rightModules = lists[1];
                resetModuleList();
            }
            return;
        }

        if (currentEditing != null) {
            try {
                if (keyCode == Keyboard.KEY_BACK && !currentEditing.get().isEmpty()) {
                    currentEditing.setValue(!currentEditing.getSelectedString().isEmpty() ? "" : currentEditing.get().substring(0, currentEditing.get().length() - 1));
                    currentEditing.setSelectedString("");
                    return;
                }

                if (GuiScreen.isKeyComboCtrlA(keyCode)) {
                    currentEditing.setSelectedString(currentEditing.get());
                    return;
                }

                if (GuiScreen.isKeyComboCtrlC(keyCode)) {
                    GuiScreen.setClipboardString(currentEditing.getSelectedString());
                    return;
                }

                if (GuiScreen.isKeyComboCtrlV(keyCode)) {
                    if (currentEditing.getSelectedString().isEmpty() && (currentEditing.get() + GuiScreen.getClipboardString()).length() > 22) {
                        currentEditing.setSelectedString("");
                        return;
                    }
                    currentEditing.setValue(!currentEditing.getSelectedString().isEmpty() ? GuiScreen.getClipboardString() : currentEditing.get() + GuiScreen.getClipboardString());
                    currentEditing.setSelectedString("");
                    return;
                }

                if (GuiScreen.isCtrlKeyDown()) return;
                if (keyCode == Keyboard.KEY_ESCAPE) {
                    currentEditing.setSelectedString("");
                    currentEditing = null;
                    return;
                }
                if (currentEditing.get().length() > 22) return;

                currentEditing.setValue(!currentEditing.getSelectedString().isEmpty() ? ChatAllowedCharacters.filterAllowedCharacters(String.valueOf(typedChar)) : currentEditing.get() + ChatAllowedCharacters.filterAllowedCharacters(String.valueOf(typedChar)));
                currentEditing.setSelectedString("");
                return;
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        if (searching) {
            searchTextField.setFocused(true);
            searchTextField.keyTyped(typedChar, keyCode);
            resetModuleList();
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }


    private Color getColor(int r, int g, int b) {
        return RenderUtil.reAlpha(new Color(r, g, b), Math.round((visibleAnimation / 100F) * 255F));
    }

    private Color getColor(int r, int g, int b, int a) {
        return RenderUtil.reAlpha(new Color(r, g, b), Math.round((visibleAnimation / 100F) * a));
    }

    private Color getColor(Color color) {
        return RenderUtil.reAlpha(color, Math.round((visibleAnimation / 100F) * color.getAlpha()));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (currentSliding != null) {
            currentSliding.sliding = false;
            currentSliding = null;
        }
    }

    public String findLongestString(String[] strArray) {
        String longestString = "";
        for (String str : strArray) {
            if (font.getStringWidth(str) > font.getStringWidth(longestString)) {
                longestString = str;
            }
        }
        return longestString;
    }

    @Override
    public void initGui() {
        super.initGui();
        resetModuleList();
    }

    private void click(List<Module> modules, float offset, int mouseX, int mouseY, int mouseButton) {
        float moduleY = 0 + moduleWheel[1];


        for (Module module : modules) {

            moduleY += 10;

            if (RenderUtil.isHovering(offset + 4, y + 50 + moduleY + 4, 184, 12, mouseX, mouseY)) {
                if (mouseButton == 0) {
                    module.toggle();
                }
                return;
            }

            moduleY += 18;

            for (Value<?> property : module.getValues()) {
                if (property.isHidden())
                    continue;

                if (property instanceof BoolValue) {
                    final BoolValue bp = (BoolValue) property;
                    if (RenderUtil.isHovering(offset + 4, y + 50 + moduleY + 5, 184, 4f + 8, mouseX, mouseY) && mouseButton == 0) {
                        bp.set(!bp.get());
                        return;
                    }
                }

                if (property instanceof NumberValue) {
                    final NumberValue dp = (NumberValue) property;
                    if (RenderUtil.isHovering(offset + 88, y + 50 + moduleY + 2, 78, 16, mouseX, mouseY) && mouseButton == 0) {
                        dp.sliding = true;
                        currentSliding = dp;
                    }
                }

                if (property instanceof ModeValue) {
                    final ModeValue sp = (ModeValue) property;
                    if (RenderUtil.isHovering(offset + 4, y + 50 + moduleY + 6, 184, 18, mouseX, mouseY)) {
                        if (mouseButton == 0) {
                            if (dropdownItem != property) {
                                dropdownItem = sp;
                                sp.animation = 100;
                                protectArea = new Rectangle(offset + 100, y + moduleY + 50 + 24, 80, 14 * sp.getModes().length);
                            } else {
                                dropdownItem = null;
                                protectArea = null;
                            }
                        }
                    }
                }

                if (property instanceof TextValue) {
                    final TextValue tp = (TextValue) property;
                    if (RenderUtil.isHovering(offset + 4, y + 50 + moduleY + 6, 184, 18, mouseX, mouseY) && mouseButton == 0) {
                        currentEditing = tp;
                    } else if (currentEditing == tp) {
                        currentEditing = null;
                    }
                }
                if (property instanceof ColorValue) {
                    final ColorValue cp = (ColorValue) property;
                    if (RenderUtil.isHovering((float) (offset + 175 - 5.5), (float) (y + 50 + moduleY + 9 - 5.5), 11, 11, mouseX, mouseY)) {
                        if (mouseButton == 0) {
                            dropdownItem = cp;
                            protectArea = new Rectangle(offset + 100, y + moduleY + 50 + 24, 80, cp.isAlphaChangeable() ? 80 : 67);
                        } else {
                            cp.setRainbowEnabled(!cp.isEnabledRainbow());
                        }
                    }
                }
                moduleY += property.getHeight();
            }
            moduleY += 4;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (dropdownItem != null && protectArea != null) {
            if (dropdownItem instanceof ModeValue) {
                final ModeValue property = (ModeValue) dropdownItem;
                int buttonY = 0;
                for (String s : property.getModes()) {
                    final boolean isHovering = RenderUtil.isHovering(protectArea.getX() + .5F, protectArea.getY() + buttonY + .5F, protectArea.getWidth() - 1, 14, mouseX, mouseY);
                    if (isHovering && mouseButton == 0) {
                        property.set(s);
                        dropdownItem = null;
                        protectArea = null;
                        return;
                    }
                    buttonY += 14;
                }
            }

            if (dropdownItem instanceof ColorValue) {
                if (!RenderUtil.isHovering(protectArea.getX(), protectArea.getY(), protectArea.getWidth(), protectArea.getHeight() + 1, mouseX, mouseY)) {
                    dropdownItem = null;
                    protectArea = null;
                }
                return;
            }

        }

        if (!RenderUtil.isHovering(x, y, width, height, mouseX, mouseY)) return;
        float width = Math.max(FontManager.bold38.getStringWidth(Client.name.toUpperCase()), FontManager.bold38.getStringWidth("NOVOLINE"));


        float pageY = 44 + CscrollAni;


        if (RenderUtil.isHovering(x, y + 44, width + 10, 240, mouseX, mouseY)) {
            for (Category page : Category.values()) {
                pageY += 12;
                for (Category.Pages cate : page.getSubPages()) {
                    if (RenderUtil.isHovering(x + 8, y + pageY, width, 16, mouseX, mouseY) && mouseButton == 0) {
                        if (cate != current) cate.animation.mouseClicked(mouseX, mouseY);
                        scrollAni = 0;
                        scrollY = 0;
                        current = cate;
                        dropdownItem = null;
                        protectArea = null;
                        currentEditing = null;
                        moduleWheel[0] = 0;
                        moduleWheel[1] = 0;
                        resetModuleList();
                        return;
                    }
                    pageY += 26;
                }
                pageY += 4;
            }
        }


        if (current.module && RenderUtil.isHovering(x + width + 16 + 8, y + 40, 400, 400, mouseX, mouseY)) {
            click(leftModules, x + width + 24, mouseX, mouseY, mouseButton);
            click(rightModules, x + width + 24 + 198, mouseX, mouseY, mouseButton);
        }
        if (mouseButton == 0 && RenderUtil.isHovering(x, y, 520, 43, mouseX, mouseY)) {
            dragX = mouseX - x;
            dragY = mouseY - y;
            dragging = true;
        }
    }

    public void setCurrent(Category.Pages current) {
        this.current = current;
    }

    public void resetModuleList() {
        leftModules.clear();
        rightModules.clear();

        final List<Module> allList = new ArrayList<>();

        if (searching) {
            for (Module module : Client.instance.getModuleManager().getModuleMap().values()) {
                if (module.getName().toLowerCase().replace(" ", "").contains(searchTextField.getText().toLowerCase().replace(" ", "")))
                    allList.add(module);
            }
        } else {
            allList.addAll(Client.instance.getModuleManager().getModsByPage(current));
        }

        allList.sort((o1, o2) -> o2.getValues().size() - o1.getValues().size());

        int updateIndex = 0;
        while (updateIndex <= allList.size() - 1) {
            leftModules.add(allList.get(updateIndex));
            updateIndex += 2;
        }

        updateIndex = 1;
        while (updateIndex <= allList.size() - 1) {
            rightModules.add(allList.get(updateIndex));
            updateIndex += 2;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }


    public static class BindScreen extends GuiScreen {
        private final Module target;
        private final GuiScreen parent;

        public BindScreen(Module module, GuiScreen parent) {
            this.target = module;
            this.parent = parent;
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);

            if (keyCode == 1) {
                this.mc.displayGuiScreen(parent);
            }


            if (keyCode != 1 && keyCode != Keyboard.KEY_DELETE) {
                this.target.setKey(keyCode);
                this.mc.displayGuiScreen(parent);
            }

            if (keyCode == Keyboard.KEY_DELETE) {
                this.target.setKey(Keyboard.KEY_NONE);
                this.mc.displayGuiScreen(parent);
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, "Press any key to bind " + EnumChatFormatting.YELLOW + target.getName(), this.width / 2, 150, 0xFFFFFF);
            this.drawCenteredString(this.fontRendererObj, "Press Delete key to remove the bind.", this.width / 2, 170, 0xFFFFFF);

            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    public static class SavePresetScreen extends GuiScreen {
        private final GuiScreen parent;
        private GuiTextField nameField;

        public SavePresetScreen(GuiScreen parent) {
            this.parent = parent;
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) throws IOException {
            super.keyTyped(typedChar, keyCode);

            this.nameField.textboxKeyTyped(typedChar, keyCode);

            if (keyCode == 1) {
                this.mc.displayGuiScreen(parent);
            }

            this.nameField.setText(this.nameField.getText().replace(" ", "").replace("#", "").replace("_NONE", ""));
        }

        public void initGui() {
            this.nameField = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, this.width / 2 - 100, this.height / 6 + 20, 200, 20);
            this.buttonList.add(new GuiButton(3, this.width / 2 - 100, this.height / 6 + 40 + 22 * 5, "Add"));
            this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 6 + 40 + 22 * 6, "Cancel"));
        }

        protected void actionPerformed(GuiButton button) throws IOException {

            if (button.id == 3) {
                Client.instance.configManager.saveConfig(this.nameField.getText());
                mc.displayGuiScreen(this.parent);
            }

            if (button.id == 4) {
                mc.displayGuiScreen(this.parent);
            }
        }


        @Override
        protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
            this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }

        @Override
        public void updateScreen() {
            this.nameField.updateCursorCounter();
            super.updateScreen();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, "Name", this.width / 2 - 89, this.height / 6 + 10, 0xFFFFFF);
            this.nameField.drawTextBox();
            this.drawCenteredString(this.fontRendererObj, "Adding Preset", this.width / 2, 30, 0xFFFFFF);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}
