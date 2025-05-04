package dev.tevarin.ui.sidegui.panels.searchpanel;


import dev.tevarin.Client;
import dev.tevarin.ui.font.FontManager;
import dev.tevarin.ui.sidegui.panels.Panel;
import dev.tevarin.ui.sidegui.utils.ToggleButton;
import dev.tevarin.utils.render.ColorUtil;
import dev.tevarin.utils.render.RoundedUtil;
import net.netease.gui.Scroll;

public class SearchPanel extends Panel {
    private String searchType = "";
    private String searchTypeHold = "";
    private String searchHold = "";
    private final ToggleButton compactMode = new ToggleButton("Compact Mode");

    private final Scroll searchScroll = new Scroll();

    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        FontManager.font18.drawString("Press ESC to return to the menu", getX() + 8, getY() + 8 + FontManager.bold34.getHeight() + 2, ColorUtil.applyOpacity(getTextColor(), .3f).getRGB());

        FontManager.bold34.drawString("Search Results", getX() + 8, getY() + 8, getTextColor().getRGB());

        float spacing = 8;
        float backgroundX = getX() + spacing, backgroundY = getY() + (45 + spacing), backgroundWidth = getWidth() - (spacing * 2), backgroundHeight = getHeight() - (45 + spacing * 2);
        RoundedUtil.drawRound(getX() + spacing, getY() + (45 + spacing), getWidth() - (spacing * 2), getHeight() - (45 + (spacing * 2)), 5, ColorUtil.tripleColor(27, getAlpha()));

        compactMode.setX(getX() + getWidth() - (compactMode.getWH() + 15));
        compactMode.setY(getY() + 33);
        compactMode.setAlpha(getAlpha());
        compactMode.drawScreen(mouseX, mouseY);

        String search = Client.instance.getSideGui().getHotbar().searchField.getText();


    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        compactMode.mouseClicked(mouseX, mouseY, button);

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
