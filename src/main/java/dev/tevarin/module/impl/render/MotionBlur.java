package dev.tevarin.module.impl.render;


import dev.tevarin.module.Category;
import dev.tevarin.module.Module;
import dev.tevarin.value.impl.NumberValue;

public class MotionBlur extends Module {

    public final NumberValue blurAmount = new NumberValue("Amount", 7, 0.0, 10.0, 0.1);


    public MotionBlur() {
        super("MotionBlur", Category.Render);
    }


}
