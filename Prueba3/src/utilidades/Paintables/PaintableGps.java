package utilidades.Paintables;

import android.graphics.Canvas;


/**
 * Clase para dibujar círculos con distinto tipo de relleno
 * @author Jose
 *
 */
public class PaintableGps extends PaintableObject {
    private float mStrokeWidth = 0;
    private int mColor = 0;
    private float mRadius = 0;
    private boolean mFill = false;
    
    
    /**
     * Constructor de un círculo al que se le puede indicar el tipo de relleno
     * @param radius
     * @param strokeWidth
     * @param fill
     * @param color
     */
    public PaintableGps(float radius, float strokeWidth, boolean fill, int color) {
    	set(radius, strokeWidth, fill, color);
    }

    public void set(float radius, float strokeWidth, boolean fill, int color) {
        mRadius = radius;
        mStrokeWidth = strokeWidth;
        mFill = fill;
        mColor = color;
    }

	@Override
    public void paint(Canvas canvas) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        setStrokeWidth(mStrokeWidth);
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