package utilidades.Paintables;

import android.graphics.Canvas;


/**
 * Clase para dibujar un círculo en pantalla
 * @author Jose
 *
 */
public class PaintableCircle extends PaintableObject {
    private int mColor = 0;
    private float mRadius = 0;
    private boolean mFill = false;
    
    public PaintableCircle(int color, float radius, boolean fill) {
    	set(color, radius, fill);
    }

    public void set(int color, float radius, boolean fill) {
        mColor = color;
        mRadius = radius;
        mFill = fill;
    }

    @Override
    public void paint(Canvas canvas) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        setFill(mFill);
        setColor(mColor);
        paintCircle(canvas, 0, 0, mRadius);
    }

    @Override
    public float getWidth() {
        return mRadius*2;
    }

    @Override
    public float getHeight() {
        return mRadius*2;
    }
}
