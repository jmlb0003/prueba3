package com.jmlb0003.prueba3.utilidades.Paintables;


import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.modelo.Marker;
import com.jmlb0003.prueba3.vista.Radar;

import android.graphics.Canvas;

/**
 * Clase que dibuja los puntos del radar que representan los PIs del modo Realidad Aumentada
 * @author Jose
 *
 */
public class PaintableRadarPoints extends PaintableObject {
    private final float[] mLocationArray = new float[3];
	private PaintablePoint mPaintablePoint = null;
	private PaintablePosition mPointContainer = null;

	@Override
    public void paint(Canvas canvas) {
		if (canvas == null) {
			throw new NullPointerException();
		}

		float range = ARDataSource.getRadius() * 1000;
		float scale = range / Radar.getRadius();
		
		for (Marker pm : ARDataSource.getMarkers()) {
		    pm.getLocation().get(mLocationArray);
		    float x = mLocationArray[0] / scale;
		    float y = mLocationArray[2] / scale;
		    
		    if ((x*x+y*y) < (Radar.getRadius()*Radar.getRadius())) {
		        if (mPaintablePoint == null) {
		        	mPaintablePoint = new PaintablePoint(pm.getColor(),true);
		        	
		        }else{
		        	mPaintablePoint.set(pm.getColor(),true);
		        }

		        if (mPointContainer == null) {
		        	mPointContainer = new PaintablePosition(mPaintablePoint, (x+Radar.getRadius()-1),
		        											(y+Radar.getRadius()-1), 0, 1);
		        }else {
		        	mPointContainer.set(mPaintablePoint, (x+Radar.getRadius()-1), 
		        						(y+Radar.getRadius()-1), 0, 1);
		        }
		                 
		        mPointContainer.paint(canvas);
		    }
		}
    }

	@Override
    public float getWidth() {
        return Radar.getRadius() * 2;
    }

	@Override
    public float getHeight() {
        return Radar.getRadius() * 2;
    }
}