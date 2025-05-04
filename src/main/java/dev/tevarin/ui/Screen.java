package dev.tevarin.ui;


import dev.tevarin.utils.IMinecraft;

public interface Screen extends IMinecraft {

    default void onDrag(int mouseX, int mouseY) {

    }

    void initGui();

    void keyTyped(char typedChar, int keyCode);

    void drawScreen(int mouseX, int mouseY);

    void mouseClicked(int mouseX, int mouseY, int button);

    void mouseReleased(int mouseX, int mouseY, int state);

}
