package dev.tevarin.ui.gui.alt;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tevarin.Client;
import dev.tevarin.module.impl.render.HUD;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.hud.notification.NotificationManager;
import dev.tevarin.ui.hud.notification.NotificationType;
import dev.tevarin.utils.WebUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.Base64;

public class GuiCookieGen
        extends GuiScreen {
    private final GuiScreen previousScreen;
    private GuiTextFieldNoLimit cookieField;

    public GuiCookieGen(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            }
            case 1145: {
                this.getCookie();
            }
        }
    }

    private static String fixJsonSyntax(String corruptedJson) {
        return corruptedJson.replaceAll("[^\\x00-\\x7F]", "");
    }


    private void getCookie() {
        String result = WebUtils.get(new String(Base64.getDecoder().decode("aHR0cHM6Ly9jbG91ZC5qcy5tY2Rkcy5jbi9hcGkvQUNDT1VOVC91c2VyL3NodWltZW5nL2dldC5waHA/dG9rZW49JTIxNVklMjVHSllmN05OJTJBMUBNJTVFOVJzSWlyayU1RVA2dnlkaCUyNSUyM0Jua2hmd0F5JTIxYXQlMjVQSXZkJTVFWWM1JTVFQnRPb0pEYiUyNmlZYSU1RVhBJTVFMDFXMSUyQTdpMkp0c3ZaOVklMjVTT0NFQVFwc1glMjFUUXEzWiZ0eXBlPXNhdXRo")));
        if (result != null) {
            try {
                JsonParser jsonParsere = new JsonParser();
                JsonObject jsonObject = jsonParsere.parse(result).getAsJsonObject();
                String msg = jsonObject.getAsJsonPrimitive("msg").getAsString();
                JsonObject dataObject = jsonObject.getAsJsonObject("data");
                String corruptedSauthJson = dataObject.getAsJsonPrimitive("account").getAsString();
                String decodedMsg = StringEscapeUtils.unescapeJava(msg);
                String sauthJson = GuiCookieGen.fixJsonSyntax(corruptedSauthJson);
                NotificationManager.post(NotificationType.INFO, "Result", decodedMsg + ", \u5df2\u81ea\u52a8\u590d\u5236");
                this.cookieField.setText(sauthJson);
                GuiCookieGen.setClipboardString(sauthJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void drawScreen(int x, int y, float z) {
        this.drawDefaultBackground();
        this.drawBackground(0);
        this.cookieField.drawTextBox();
        FontManager.font20.drawCenteredStringWithShadow("Cookie generator", this.width / 2, 20.0f, -1);
        Client.instance.getModuleManager().getModule(HUD.class).drawNotifications();
        super.drawScreen(x, y, z);
    }

    @Override
    public void initGui() {
        int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.buttonList.add(new GuiButton(1145, this.width / 2 - 100, var3 + 72 + 12 + 48, "Generate Cookie"));
        this.cookieField = new GuiTextFieldNoLimit(1, this.mc.fontRendererObj, this.width / 2 - 100, var3 + 72 - 12, 200, 20);
        this.cookieField.setFocused(true);
        this.cookieField.setMaxStringLength(200);
    }

    @Override
    protected void keyTyped(char character, int key) throws IOException {
        super.keyTyped(character, key);
        if (character == '\t' && this.cookieField.isFocused()) {
            this.cookieField.setFocused(!this.cookieField.isFocused());
        }
        if (character == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
        this.cookieField.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException {
        super.mouseClicked(x, y, button);
        this.cookieField.mouseClicked(x, y, button);
    }

    @Override
    public void updateScreen() {
        this.cookieField.updateCursorCounter();
    }
}

