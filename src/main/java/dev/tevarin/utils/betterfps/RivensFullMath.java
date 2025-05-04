//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.tevarin.utils.betterfps;

public class RivensFullMath {
    private static final float BF_SIN_TO_COS = 1.5707964F;
    private static final int BF_SIN_BITS = 12;
    private static final int BF_SIN_MASK;
    private static final int BF_SIN_COUNT;
    private static final float BF_radFull;
    private static final float BF_radToIndex;
    private static final float[] BF_sinFull;

    public RivensFullMath() {
    }

    public static float sin(float rad) {
        return BF_sinFull[(int) (rad * BF_radToIndex) & BF_SIN_MASK];
    }

    public static float cos(float rad) {
        return sin(rad + BF_SIN_TO_COS);
    }

    static {
        BF_SIN_MASK = ~(-1 << BF_SIN_BITS);
        BF_SIN_COUNT = BF_SIN_MASK + 1;
        BF_radFull = 6.2831855F;
        BF_radToIndex = (float) BF_SIN_COUNT / BF_radFull;
        BF_sinFull = new float[BF_SIN_COUNT];

        for (int i = 0; i < BF_SIN_COUNT; ++i) {
            BF_sinFull[i] = (float) Math.sin(((double) i + (double) Math.min(1, i % (BF_SIN_COUNT / 4)) * 0.5) / (double) BF_SIN_COUNT * (double) BF_radFull);
        }

    }
}
