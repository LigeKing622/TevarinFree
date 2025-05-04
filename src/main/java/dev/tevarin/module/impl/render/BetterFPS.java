//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.tevarin.module.impl.render;


import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.utils.betterfps.*;
import dev.tevarin.value.impl.ModeValue;

public class BetterFPS extends Module {
    public final ModeValue sinMode = new ModeValue("SinMode", new String[]{"LibGDX", "RivensFull", "RivensHalf", "Rivens", "Java", "1.16"}, "Java");
    public final ModeValue cosMode = new ModeValue("CosMode", new String[]{"LibGDX", "RivensFull", "RivensHalf", "Rivens", "Java", "1.16"}, "Java");

    public BetterFPS() {
        super("BetterFPS", Category.Render);

    }

    public Float cos(float value) {
        switch ((String) this.cosMode.getValue()) {
            case "Taylor":
                return TaylorMath.cos(value);
            case "LibGDX":
                return LibGDXMath.cos(value);
            case "RivensFull":
                return RivensFullMath.cos(value);
            case "RivensHalf":
                return RivensHalfMath.cos(value);
            case "Rivens":
                return RivensMath.cos(value);
            case "Java":
                return (float) Math.cos((double) value);
            case "1.16":
                return NewMCMath.cos(value);
            default:
                return (float) Math.cos((double) value);
        }
    }

    public Float sin(float value) {
        switch ((String) this.sinMode.getValue()) {
            case "Taylor":
                return TaylorMath.sin(value);
            case "LibGDX":
                return LibGDXMath.sin(value);
            case "RivensFull":
                return RivensFullMath.sin(value);
            case "RivensHalf":
                return RivensHalfMath.sin(value);
            case "Rivens":
                return RivensMath.sin(value);
            case "Java":
                return (float) Math.sin((double) value);
            case "1.16":
                return NewMCMath.sin(value);
            default:
                return (float) Math.cos((double) value);
        }
    }
}
