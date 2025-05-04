package dev.tevarin.ui.sidegui.panels.infopanel;


import dev.tevarin.ui.Screen;
import dev.tevarin.utils.render.*;
import dev.tevarin.utils.render.animation.Animation;
import dev.tevarin.utils.render.animation.Direction;
import dev.tevarin.utils.render.animation.impl.DecelerateAnimation;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.List;

import static dev.tevarin.ui.font.FontManager.*;

@Getter
@Setter
public class InfoButton implements Screen {
    private final String question, answer;

    private float x, y, width, height, alpha, count = 1;

    private final Animation openAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);
    private final Animation hoverAnimation = new DecelerateAnimation(250, 1).setDirection(Direction.BACKWARDS);

    public InfoButton(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    @Override
    public void initGui() {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {

    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        Color textColor = ColorUtil.applyOpacity(Color.WHITE, alpha);
        boolean hovering = HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY);
        hoverAnimation.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        hoverAnimation.setDuration(hovering ? 200 : 400);


        float additionalCount = 0;
        if (!openAnimation.isDone() || openAnimation.finished(Direction.FORWARDS)) {
            float heightIncrement = 3;
            float openAnim = (float) openAnimation.getOutput();
            List<String> lines = font16.getWrappedLines(answer, x + 5, (width - 5), heightIncrement);
            int spacing = 3;
            float totalAnswerHeight = (lines.size() * (font16.getHeight() + heightIncrement)) + 4;
            float additionalHeight = height + totalAnswerHeight + (spacing * 2);

            RenderUtil.scissorStart(x - 1, y + 5, width + 2, additionalHeight - 5);

            float answerY = (y + height + spacing) - (((spacing + totalAnswerHeight) * (1 - openAnim)));
            RoundedUtil.drawRound(x, answerY, width, totalAnswerHeight, 5, ColorUtil.tripleColor(55, alpha));


            for (String line : lines) {
                font16.drawString(line, x + 3, answerY + 3.5f, textColor.getRGB());
                answerY += font16.getHeight() + heightIncrement;
            }
            RenderUtil.scissorEnd();


            additionalCount = ((totalAnswerHeight + spacing) * openAnim) / height;
        }


        int additionalColor = (int) (5 * hoverAnimation.getOutput());
        RoundedUtil.drawRound(x, y, width, height, 5, ColorUtil.tripleColor(37 + additionalColor, alpha));
        //Question Text
        bold18.drawString(question, x + 5, y + bold18.getMiddleOfBox(height), textColor.getRGB());


        float iconX = x + width - (iconFont20.getStringWidth(FontUtil.DROPDOWN_ARROW) + 5);
        float iconY = y + iconFont20.getMiddleOfBox(height) + 1;

        RenderUtil.rotateStart(iconX, iconY, (float) iconFont20.getStringWidth(FontUtil.DROPDOWN_ARROW), (float) iconFont20.getHeight(), (float) (180 * openAnimation.getOutput()));
        iconFont20.drawString(FontUtil.DROPDOWN_ARROW, iconX, iconY, textColor.getRGB());
        RenderUtil.rotateEnd();


        count = 1 + additionalCount;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (HoveringUtil.isHovering(x, y, width, height, mouseX, mouseY) && button == 1) {
            openAnimation.changeDirection();
        }

    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {

    }
}
