package com.jmlb0003.prueba3.utilidades;

/**
 * Clase abstracta que sirve para proporcionar el método getAngle que es necesario en otras 
 * clases (PitchAzimuthCalculator y Poi).
 * @author Jose
 *
 */
public abstract class Utilities {

    private Utilities() { }
    
    public static final float getAngle(float center_x, float center_y, float post_x, float post_y) {
        float tmpv_x = post_x - center_x;
        float tmpv_y = post_y - center_y;
        float d = (float) Math.sqrt(tmpv_x * tmpv_x + tmpv_y * tmpv_y);
        float cos = tmpv_x / d;
        float angle = (float) Math.toDegrees(Math.acos(cos));

        angle = (tmpv_y < 0) ? angle * -1 : angle;

        return angle;
    }
}