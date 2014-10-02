package utilidades.Paintables;

import android.graphics.Canvas;


/**
 * Clase que permite dibujar un punto, aunque en realidad es un rectángulo de pequeño tamaño.
 * Se utiliza para los puntos del Radar.
 * @author Jose
 *
 */
public class PaintablePoint extends PaintableObject {
    private static int mWidth=3;
    private static int mHeight=3;
    private int mColor = 0;
    private boolean mFill = false;
    
    public PaintablePoint(int color, boolean fill) {
    	set(color, fill);
    }

    public void set(int color, boolean fill) {
        mColor = color;
        mFill = fill;
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