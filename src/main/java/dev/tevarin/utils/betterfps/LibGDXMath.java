//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.tevarin.utils.betterfps;

public class LibGDXMath {
    public static final float BF_PI = 3.1415927F;
    private static final int BF_SIN_BITS = 14;
    private static final int BF_SIN_MASK = 16383;
    private static final int BF_SIN_COUNT = 16384;
    private static final float BF_radFull = 6.2831855F;
    private static final float BF_degFull = 360.0F;
    private static final float BF_radToIndex = 2607.5945F;
    private static final float BF_degToIndex = 45.511112F;
    public static final float BF_degreesToRadians = 0.017453292F;
    private static final float[] BF_table = new float[16384];

    public LibGDXMath() {
    }

    public static float sin(float radians) {
        return BF_table[(int) (radians * 2607.5945F) & 16383];
    }

    public static float cos(float radians) {
        return BF_table[(int) ((radians + 1.5707964F) * 2607.5945F) & 16383];
    }

    static {
        int i;
        for (i = 0; i < 16384; ++i) {
            BF_table[i] = (float) Math.sin((double) (((float) i + 0.5F) / 16384.0F * 6.2831855F));
        }

        for (i = 0; i < 360; i += 90) {
            BF_table[(int) ((float) i * 45.511112F) & 16383] = (float) Math.sin((double) ((float) i * 0.017453292F));
        }

    }
}
