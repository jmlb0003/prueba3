package utilidades.Paintables;

import android.graphics.Canvas;
import android.graphics.Color;

/**
 * Clase descendiente de PaintableObject con las operaciones necesarias para dibujar el contorno 
 * de un cuadrado
 * @author Jose
 *
 */
public class PaintableBox extends PaintableObject {
    private float mWidth=0, mHeight=0;
	private int mBorderColor = Color.rgb(255, 255, 255);
	private int mBackgroundColor = Color.argb(128, 0, 0, 0);

	public PaintableBox(float width, float height) {
		this(width, height, Color.rgb(255, 255, 255), Color.argb(128, 0, 0, 0));
	}

	public PaintableBox(float width, float height, int borderColor, int bgColor) {
		set(width, height, borderColor, bgColor);
	}

    public void set(float width, float height) {
        set(width, height, mBorderColor, mBackgroundColor);
    }

	public void set(float width, float height, int borderColor, int bgColor) {
	    mWidth = width;
	    mHeight = height;
	    mBorderColor = borderColor;
		mBackgroundColor = bgColor;
	}

	@Override
	public void paint(Canvas canvas) {
		if (canvas == null) {
			throw new NullPointerException();
		}

		setFill(true);
		setColor(mBackgroundColor);
		paintRect(canvas, 0, 0, mWidth, mHeight);

		setFill(false);
		setColor(mBorderColor);
		paintRect(canvas, 0, 0, mWidth, mHeight);
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
