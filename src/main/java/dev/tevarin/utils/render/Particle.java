package dev.tevarin.utils.render;

import net.minecraft.client.gui.ScaledResolution;

import java.util.Random;

/**
 * Particle API
 * This Api is free2use
 * But u have to mention me.
 *
 * @author Vitox
 * @version 3.0
 */
class Particle {

    public float x,y,radius,speed,ticks, opacity;

    public Particle(ScaledResolution sr, float r, float s){
        x = new Random().nextFloat()*sr.getScaledWidth();
        y = new Random().nextFloat()*sr.getScaledHeight();
        ticks = new Random().nextFloat()*sr.getScaledHeight()/2;
        radius = r;
        speed = s;
    }
}

