package utilidades.Paintables;

import android.graphics.Bitmap;
import android.graphics.Canvas;


/**
 * Clase que permite dibujar iconos a partir de un bitmap determinado
 * @author Jose
 *
 */
public class PaintableIcon extends PaintableObject {
    private Bitmap mBitmap = null;

    public PaintableIcon(Bitmap bitmap, int width, int height) {
    	set(bitmap,width,height);
    }

    public void set(Bitmap bitmap, int width, int height) {
    	if (bitmap == null) {
    		throw new NullPointerException();
    	}
    	
        mBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

	@Override
    public void paint(Canvas canvas) {
    	if (canvas == null || mBitmap == null) {
    		throw new NullPointerException();
    	}

        paintBitmap(canvas, mBitmap, -(mBitmap.getWidth()/2), -(mBitmap.getHeight()/2));
    }

	@Override
    public float getWidth() {
        return mBitmap.getWidth();
    }

	@Override
    public float getHeight() {
        return mBitmap.getHeight();
    }
}