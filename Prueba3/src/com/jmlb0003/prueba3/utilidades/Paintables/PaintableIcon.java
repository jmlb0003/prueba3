package com.jmlb0003.prueba3.utilidades.Paintables;

import android.graphics.Bitmap;
import android.graphics.Canvas;


/**
 * Clase que permite dibujar iconos a partir de un bitmap determinado
 * @author Jose
 *
 */
public class PaintableIcon extends PaintableObject {
    private Bitmap mBitmap = null;
    private float mScale = 1.0f;

    
    /**
     * Constructor de PaintableIcon con un bitmap. Las medidas serán las que tenga el icono 
     * original
     * @param bitmap Icono que se usará para construir el PaintableIcon
     * @param scale Factor de escalado para el bitmap en caso de que esté muy alejado del usuario
     */
    public PaintableIcon(Bitmap bitmap, float scale) {
    	mScale = scale;
    	set(bitmap,-1,-1);
    }
    
    /**
     * Constructor de PaintableIcon con un bitmap escalado a unas medidas concretas.
     * @param bitmap Icono que se usará para construir el PaintableIcon
     * @param width Anchura a la que se ha de escalar el bitmap
     * @param height Altura a la que se ha de escalar el bitmap
     */
    public PaintableIcon(Bitmap bitmap, int width, int height) {
    	set(bitmap,width,height);
    }

    public void set(Bitmap bitmap, int width, int height) {
    	if (bitmap == null) {
    		throw new NullPointerException();
    	}
    	
    	
    	if ( mScale < 1 && (width < 0 && height < 0) ) {
    		mBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(bitmap.getWidth() * mScale),
											Math.round(bitmap.getHeight() * mScale), true);

    	}else {
    		
    		if (mScale >= 1) {
    			mBitmap = Bitmap.createBitmap(bitmap);
    			
    		}else{
    			mBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(width*mScale), 
						Math.round(height*mScale), true);
    		}
    	}
    	
    	
    	
//    	if (width > 0 && height > 0) {
//    		mBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(width*mScale), 
//    												Math.round(height*mScale), true);
//    	} else {
//    		if (mScale < 1) {
//    			mBitmap = Bitmap.createScaledBitmap(bitmap, Math.round(mBitmap.getWidth()*mScale), 
//    												Math.round(mBitmap.getHeight()*mScale), true);
//    		}else {
//    			mBitmap = Bitmap.createBitmap(bitmap);
//    		}
//    	}
    	
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