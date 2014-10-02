package utilidades.Paintables;


import android.graphics.Canvas;

/**
 * Clase que permite dibujar un objeto en pantalla y además permite varias sus coordenadas
 * para moverlo y aplicar rotaciones.
 * @author Jose
 *
 */
public class PaintablePosition extends PaintableObject {
    private float mWidth = 0, mHeight = 0;
    private float mObjX = 0, mObjY = 0, mObjRotation = 0, mObjScale = 0;
    private PaintableObject mObj = null;
    
    
    /**
     * Constructor de un objeto que se puede mover en la pantalla.
     * @param drawObj
     * @param x
     * @param y
     * @param rotation
     * @param scale
     */
    public PaintablePosition(PaintableObject drawObj, float x, float y, float rotation, float scale) {
    	set(drawObj, x, y, rotation, scale);
    }

    public void set(PaintableObject drawObj, float x, float y, float rotation, float scale) {
    	if (drawObj == null) {
    		throw new NullPointerException();
    	}
    	
        mObj = drawObj;
        mObjX = x;
        mObjY = y;
        mObjRotation = rotation;
        mObjScale = scale;
        mWidth = mObj.getWidth();
        mHeight = mObj.getHeight();
    }

    public void move(float x, float y) {
        mObjX = x;
        mObjY = y;
    }

    public float getObjectsX() {
        return mObjX;
    }

    public float getObjectsY() {
        return mObjY;
    }
    
	@Override
    public void paint(Canvas canvas) {
    	if (canvas == null || mObj == null) {
    		throw new NullPointerException();
    	}
    	
        paintObj(canvas, mObj, mObjX, mObjY, mObjRotation, mObjScale);
    }

	@Override
    public float getWidth() {
        return mWidth;
    }

	@Override
    public float getHeight() {
        return mHeight;
    }

    @Override
	public String toString() {
	    return "objX="+mObjX+" objY="+mObjY+" width="+mWidth+" height="+mHeight;
	}
}