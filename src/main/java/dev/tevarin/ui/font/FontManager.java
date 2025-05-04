package dev.tevarin.ui.font;


import dev.tevarin.Client;

import java.awt.*;
import java.io.InputStream;

public class FontManager {
    //normal
    public static RapeMasterFontManager font10;
    public static RapeMasterFontManager font12;
    public static RapeMasterFontManager font13;
    public static RapeMasterFontManager font14;
    public static RapeMasterFontManager font15;
    public static RapeMasterFontManager font16;
    public static RapeMasterFontManager font18;
    public static RapeMasterFontManager font19;
    public static RapeMasterFontManager font20;
    public static RapeMasterFontManager font22;
    public static RapeMasterFontManager font24;
    public static RapeMasterFontManager font26;
    public static RapeMasterFontManager font28;
    public static RapeMasterFontManager font32;
    public static RapeMasterFontManager font34;
    public static RapeMasterFontManager font35;
    public static RapeMasterFontManager font40;
    //bold
    public static RapeMasterFontManager bold14;
    public static RapeMasterFontManager bold16;
    public static RapeMasterFontManager bold18;
    public static RapeMasterFontManager bold20;
    public static RapeMasterFontManager bold22;
    public static RapeMasterFontManager bold32;
    public static RapeMasterFontManager bold38;
    public static RapeMasterFontManager bold34;
    public static RapeMasterFontManager interSemiBold14;
    public static RapeMasterFontManager interSemiBold16;
    public static RapeMasterFontManager interSemiBold18;
    public static RapeMasterFontManager interSemiBold20;
    public static RapeMasterFontManager interSemiBold22;
    public static RapeMasterFontManager interSemiBold24;
    //icon
    public static RapeMasterFontManager other30;
    public static RapeMasterFontManager NovICON64;
    public static RapeMasterFontManager iconFont16;
    public static RapeMasterFontManager iconFont20;
    public static RapeMasterFontManager neverlose24;
    public static RapeMasterFontManager icontestFont22;
    public static RapeMasterFontManager icontestFont35;
    public static RapeMasterFontManager icontestFont40;
    public static RapeMasterFontManager icontestFont75;

    public static void init() {
        //normal
        font10 = getFont("font.ttf", 10);
        font12 = getFont("font.ttf", 12);
        font13 = getFont("font.ttf", 13);
        font14 = getFont("font.ttf", 14);
        font15 = getFont("font.ttf", 15);
        font16 = getFont("font.ttf", 16);
        font18 = getFont("font.ttf", 18);
        font19 = getFont("font.ttf", 19);
        font20 = getFont("font.ttf", 20);
        font22 = getFont("font.ttf", 22);
        font24 = getFont("font.ttf", 24);
        font26 = getFont("font.ttf", 26);
        font28 = getFont("font.ttf", 28);
        font32 = getFont("font.ttf", 32);
        font34 = getFont("font.ttf", 34);
        font35 = getFont("font.ttf", 35);
        font40 = getFont("font.ttf", 40);

        //bold
        interSemiBold14 = getFont("Inter_SemiBold.ttf", 14);
        interSemiBold16 = getFont("Inter_SemiBold.ttf", 16);
        interSemiBold18 = getFont("Inter_SemiBold.ttf", 18);
        interSemiBold20 = getFont("Inter_SemiBold.ttf", 20);
        interSemiBold22 = getFont("Inter_SemiBold.ttf", 22);
        interSemiBold24 = getFont("Inter_SemiBold.ttf", 24);
        bold14 = getFont("bold.ttf", 14);
        bold16 = getFont("bold.ttf", 16);
        bold18 = getFont("bold.ttf", 18);
        bold20 = getFont("bold.ttf", 20);
        bold22 = getFont("bold.ttf", 22);
        bold32 = getFont("bold.ttf", 32);
        bold34 = getFont("bold.ttf", 34);
        bold38 = getFont("bold.ttf", 38);
        //icon
        other30 = getFont("icont.ttf", 30);
        NovICON64 = getFont("NovICON.ttf", 34);
        iconFont16 = getFont("icon.ttf", 16);
        iconFont16 = getFont("icon.ttf", 20);
        neverlose24 = getFont("nlicon.ttf", 24);
        icontestFont22 = getFont("icont.ttf", 22);
        icontestFont35 = getFont("icont.ttf", 35);
        icontestFont40 = getFont("icont.ttf", 40);

        icontestFont75 = getFont("icont.ttf", 75);
    }

    private static RapeMasterFontManager getFont(String fontName, float fontSize) {
        Font font = null;
        try {

            InputStream inputStream = Client.class.getResourceAsStream("/assets/minecraft/tevarin/font/" + fontName);
            assert inputStream != null;
            font = Font.createFont(Font.PLAIN, inputStream);
            font = font.deriveFont(fontSize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RapeMasterFontManager(font);
    }
}
