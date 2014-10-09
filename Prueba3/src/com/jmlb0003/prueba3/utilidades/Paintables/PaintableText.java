package com.jmlb0003.prueba3.utilidades.Paintables;

import android.graphics.Canvas;
import android.graphics.Color;


/**
 * Clase que sirve para dibujar texto.
 * Se usa para el texto del Radar
 * @author Jose
 *
 */
public class PaintableText extends PaintableObject {
    private static final float WIDTH_PAD = 4;
    private static final float HEIGHT_PAD = 2;
    
    private String mText = null;
    private int mColor = 0;
    private int mSize = 0;
    private float mWidth = 0;
    private float mHeight = 0;
    private boolean mBg = false;
    
    public PaintableText(String text, int color, int size, boolean paintBackground) {
    	set(text, color, size, paintBackground);
    }

    public void set(String text, int color, int size, boolean paintBackground) {
    	if (text == null) {
    		throw new NullPointerException();
    	}
    	
        mText = text;
        mBg = paintBackground;
        mColor = color;
        mSize = size;
        mWidth = getTextWidth(text) + WIDTH_PAD * 2;
        mHeight = getTextAsc() + getTextDesc() + HEIGHT_PAD * 2;
    }

	@Override
    public void paint(Canvas canvas) {
    	if (canvas == null || mText == null) {
    		throw new NullPointerException();
    	}
    	
        setColor(mColor);
        setFontSize(mSize);
        if (mBg) {
            setColor(Color.rgb(0, 0, 0));
            setFill(true);
            paintRect(canvas, -(mWidth/2), -(mHeight/2), mWidth, mHeight);
            setColor(Color.rgb(255, 255, 255));
            setFill(false);
            paintRect(canvas, -(mWidth/2), -(mHeight/2), mWidth, mHeight);
        }
        
        paintText(canvas, (WIDTH_PAD - mWidth/2), (HEIGHT_PAD + getTextAsc() - mHeight/2), mText);
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