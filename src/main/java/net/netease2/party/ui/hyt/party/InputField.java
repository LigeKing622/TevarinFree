/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package net.netease2.party.ui.hyt.party;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

import java.awt.*;

public class InputField {
    public int x;
    public int y;
    private final int width;
    private final int height;
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean isFocused;
    private boolean isEnabled = true;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private boolean visible = true;
    private final boolean hidden;
    private char replacedChar;

    public InputField(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hidden = false;
    }

    public InputField(int x, int y, int width, int height, char replacedChar) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.replacedChar = replacedChar;
        this.hidden = true;
    }

    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    public void setText(String textIn) {
        this.text = textIn.length() > this.maxStringLength ? textIn.substring(0, this.maxStringLength) : textIn;
        this.setCursorPositionEnd();
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(i, j);
    }

    public void writeText(String textToWrite) {
        int l;
        Object s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
        int i = Math.min(this.cursorPosition, this.selectionEnd);
        int j = Math.max(this.cursorPosition, this.selectionEnd);
        int k = this.maxStringLength - this.text.length() - (i - j);
        if (!this.text.isEmpty()) {
            s = (String) s + this.text.substring(0, i);
        }
        if (k < s1.length()) {
            s = (String) s + s1.substring(0, k);
            l = k;
        } else {
            s = (String) s + s1;
            l = s1.length();
        }
        if (!this.text.isEmpty() && j < this.text.length()) {
            s = (String) s + this.text.substring(j);
        }
        this.text = (String) s;
        this.moveCursorBy(i - this.selectionEnd + l);
    }

    public void deleteWords(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }
    }

    public void deleteFromCursor(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = num < 0;
                int i = flag ? this.cursorPosition + num : this.cursorPosition;
                int j = flag ? this.cursorPosition : this.cursorPosition + num;
                Object s = "";
                if (i >= 0) {
                    s = this.text.substring(0, i);
                }
                if (j < this.text.length()) {
                    s = (String) s + this.text.substring(j);
                }
                this.text = (String) s;
                if (flag) {
                    this.moveCursorBy(num);
                }
            }
        }
    }

    public int getNthWordFromCursor(int numWords) {
        return this.getNthWordFromPos(numWords, this.getCursorPosition());
    }

    public int getNthWordFromPos(int n, int pos) {
        return this.getNthWordFromPosWS(n, pos, true);
    }

    public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
        int i = pos;
        boolean flag = n < 0;
        int j = Math.abs(n);
        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.text.length();
                if ((i = this.text.indexOf(32, i)) == -1) {
                    i = l;
                    continue;
                }
                while (skipWs && i < l && this.text.charAt(i) == ' ') {
                    ++i;
                }
                continue;
            }
            while (skipWs && i > 0 && this.text.charAt(i - 1) == ' ') {
                --i;
            }
            while (i > 0 && this.text.charAt(i - 1) != ' ') {
                --i;
            }
        }
        return i;
    }

    public void moveCursorBy(int num) {
        this.setCursorPosition(this.selectionEnd + num);
    }

    public void setCursorPosition(int pos) {
        this.cursorPosition = pos;
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        if (!this.isFocused) {
            return false;
        }
        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        }
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            if (this.isEnabled) {
                this.writeText(GuiScreen.getClipboardString());
            }
            return true;
        }
        if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            if (this.isEnabled) {
                this.writeText("");
            }
            return true;
        }
        switch (keyCode) {
            case 14: {
                if (GuiScreen.isCtrlKeyDown()) {
                    if (this.isEnabled) {
                        this.deleteWords(-1);
                    }
                } else if (this.isEnabled) {
                    this.deleteFromCursor(-1);
                }
                return true;
            }
            case 199: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(0);
                } else {
                    this.setCursorPositionZero();
                }
                return true;
            }
            case 203: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() - 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(-1));
                } else {
                    this.moveCursorBy(-1);
                }
                return true;
            }
            case 205: {
                if (GuiScreen.isShiftKeyDown()) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                    } else {
                        this.setSelectionPos(this.getSelectionEnd() + 1);
                    }
                } else if (GuiScreen.isCtrlKeyDown()) {
                    this.setCursorPosition(this.getNthWordFromCursor(1));
                } else {
                    this.moveCursorBy(1);
                }
                return true;
            }
            case 207: {
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(this.text.length());
                } else {
                    this.setCursorPositionEnd();
                }
                return true;
            }
            case 211: {
                if (GuiScreen.isCtrlKeyDown()) {
                    if (this.isEnabled) {
                        this.deleteWords(1);
                    }
                } else if (this.isEnabled) {
                    this.deleteFromCursor(1);
                }
                return true;
            }
        }
        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
            if (this.isEnabled) {
                this.writeText(Character.toString(typedChar));
            }
            return true;
        }
        return false;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        boolean flag = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;
        this.setFocused(flag);
        if (this.isFocused && flag && mouseButton == 0) {
            int i = mouseX - this.x;
            String s = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(s, i -= 4).length() + this.lineScrollOffset);
            return true;
        }
        return false;
    }

    public void drawTextBox() {
        if (this.getVisible()) {
            Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, new Color(0, 0, 0, 102).getRGB());
            Gui.drawRect(this.x, this.y, (float) this.x + 0.5f, this.y + this.height, new Color(44, 44, 44, 102).getRGB());
            Gui.drawRect((float) this.x + 0.5f, (float) (this.y + this.height) - 0.5f, (float) (this.x + this.width) - 0.5f, this.y + this.height, new Color(44, 44, 44, 102).getRGB());
            Gui.drawRect((float) (this.x + this.width) - 0.5f, this.y, this.x + this.width, this.y + this.height, new Color(44, 44, 44, 102).getRGB());
            Gui.drawRect((float) this.x + 0.5f, this.y, (float) (this.x + this.width) - 0.5f, (float) this.y + 0.5f, new Color(44, 44, 44, 102).getRGB());
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            if (this.hidden) {
                s = s.replaceAll(".", String.valueOf(this.replacedChar));
            }
            boolean flag = j >= 0 && j <= s.length();
            boolean showCursor = this.isFocused && this.cursorCounter / 10 % 2 == 0 && flag;
            int l = this.x + 4;
            int i1 = this.y + (this.height - 8) / 2;
            int j1 = l;
            if (k > s.length()) {
                k = s.length();
            }
            if (!s.isEmpty()) {
                String s1 = flag ? s.substring(0, j) : s;
                Minecraft.getMinecraft().fontRendererObj.drawString(s1, l, i1, new Color(216, 216, 216).getRGB());
                j1 += Minecraft.getMinecraft().fontRendererObj.getStringWidth(s1);
            }
            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int cursorX = j1;
            if (!flag) {
                cursorX = j > 0 ? l + this.width : l;
            } else if (flag2) {
                cursorX = j1 - 1;
                ++j1;
            }
            if (!s.isEmpty() && flag && j < s.length()) {
                Minecraft.getMinecraft().fontRendererObj.drawString(s.substring(j), j1, i1, new Color(216, 216, 216).getRGB());
            }
            if (showCursor) {
                if (flag2) {
                    Gui.drawRect(cursorX + 1, i1 - 1, cursorX + 2, i1 + 1 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT, -3092272);
                } else {
                    Minecraft.getMinecraft().fontRendererObj.drawString(this.text.isEmpty() ? "|" : "_", (cursorX + 2), this.y + 5, new Color(216, 216, 216).getRGB());
                }
            }
            if (k != j) {
                int l1 = l + Minecraft.getMinecraft().fontRendererObj.getStringWidth(s.substring(0, k));
                this.drawSelectionBox(cursorX, i1 - 1, l1 - 1, (int) (i1 + 1 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT));
            }
        }
    }

    private void drawSelectionBox(int x, int startY, int endX, int endY) {
        if (x < endX) {
            int i = x;
            x = endX;
            endX = i;
        }
        if (startY < endY) {
            int j = startY;
            startY = endY;
            endY = j;
        }
        if (endX > this.x + this.width) {
            endX = this.x + this.width;
        }
        if (x > this.x + this.width) {
            x = this.x + this.width;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0f, 0.0f, 255.0f, 255.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE.ordinal());
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x, endY, 0.0).endVertex();
        worldrenderer.pos(endX, endY, 0.0).endVertex();
        worldrenderer.pos(endX, startY, 0.0).endVertex();
        worldrenderer.pos(x, startY, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int length) {
        this.maxStringLength = length;
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
        }
    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }

    public void setFocused(boolean isFocusedIn) {
        if (isFocusedIn && !this.isFocused) {
            this.cursorCounter = 0;
        }
        this.isFocused = isFocusedIn;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public int getSelectionEnd() {
        return this.selectionEnd;
    }

    public int getWidth() {
        return this.width - 8;
    }

    public void setSelectionPos(int position) {
        int i = this.text.length();
        if (position > i) {
            position = i;
        }
        if (position < 0) {
            position = 0;
        }
        this.selectionEnd = position;
        if (this.lineScrollOffset > i) {
            this.lineScrollOffset = i;
        }
        int j = this.getWidth();
        String s = Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
        int k = s.length() + this.lineScrollOffset;
        if (position == this.lineScrollOffset) {
            this.lineScrollOffset -= Minecraft.getMinecraft().fontRendererObj.trimStringToWidth(this.text, j, true).length();
        }
        if (position > k) {
            this.lineScrollOffset += position - k;
        } else if (position <= this.lineScrollOffset) {
            this.lineScrollOffset -= this.lineScrollOffset - position;
        }
        this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setVisible(boolean isVisible) {
        this.visible = isVisible;
    }

    public boolean isFocused() {
        return this.isFocused;
    }

    public boolean isHidden() {
        return this.hidden;
    }
}

