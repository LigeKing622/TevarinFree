package dev.tevarin.utils.render;

import lombok.Getter;
import net.minecraft.potion.Potion;

@Getter
public class PotionData {
    public final Potion potion;
    public int maxTimer = 0;
    public final int level;

    public PotionData(Potion potion, int level) {
        this.potion = potion;
        this.level = level;
    }

}
