package com.jmlb0003.prueba3.utilidades.Paintables;

import android.graphics.Canvas;


/**
 * Clase que permite dibujar un punto, aunque en realidad es un rectángulo de pequeño tamaño.
 * Se utiliza para los puntos del Radar.
 * @author Jose
 *
 */
public class PaintablePoint extends PaintableObject {
	/**Anchura del cuadrado que representa al punto**/
    private static int mWidth=2;
    /**Altura del cuadrado que representa al punto**/
    private static int mHeight=2;
    private int mColor = 0;
    private boolean mFill = false;
    
    public PaintablePoint(int color, boolean fill, float scale) {
    	set(color, fill, scale);
    }

    /**
     * Por defecto el tamaño es 2dp pero se escala mediante el parámetro scale
     * @param color Color del punto que se dibuja en el radar. Viene fijado desde la declaración
     * del Poi
     * @param fill
     * @param scale Factor por el que se multiplica el tamaño por defecto del punto del radar
     */
    public void set(int color, boolean fill, float scale) {
        mColor = color;
        mFill = fill;
        mWidth = Math.round(2*scale);
        mHeight = mWidth;
    }

	@Override
    public void paint(Canvas canvas) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        setFill(mFill);
        setColor(mColor);
        paintRect(canvas, -1, -1, mWidth, mHeight);
    }

	@Override
    public float getWidth() {
        return mWidth;
    }

	@Override
    public float getHeight() {
        return mHeight;
    }
}