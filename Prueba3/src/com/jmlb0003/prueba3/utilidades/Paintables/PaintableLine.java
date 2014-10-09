package com.jmlb0003.prueba3.utilidades.Paintables;


import android.graphics.Canvas;


/**
 * Clase para dibujar una línea de un color específico
 * @author Jose
 *
 */
public class PaintableLine extends PaintableObject {
    private int mColor = 0;
    private float mX = 0;
    private float mY = 0;
    
    public PaintableLine(int color, float x, float y) {
    	set(color, x, y);
    }

    public void set(int color, float x, float y) {
        mColor = color;
        mX = x;
        mY = y;
    }

	@Override
    public void paint(Canvas canvas) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        setFill(false);
        setColor(mColor); 
        paintLine(canvas, 0, 0, mX, mY);
    }

	@Override
    public float getWidth() {
        return mX;
    }

	@Override
    public float getHeight() {
        return mY;
    }
}