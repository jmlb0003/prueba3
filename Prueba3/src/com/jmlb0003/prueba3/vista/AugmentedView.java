package com.jmlb0003.prueba3.vista;



import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.modelo.Marker;


/**
 * Clase que implementa la vista utilizada para incorporar todos los componentes de la interfaz
 * en el modo Realidad Aumentada.
 * @author Jose
 *
 */
public class AugmentedView extends View {
	
    private static final float[] LOCATION_ARRAY = new float[3];
    private static final List<Marker> COLLECTION_CACHE = new ArrayList<Marker>();
    private static final int COLLISION_ADJUSTMENT = 100;
    
    private static TreeSet<Marker> sUpdatedMarkers = new TreeSet<Marker>();
    private static AtomicBoolean sDrawing = new AtomicBoolean(false);
    private static float sPixelDensity;
    private static Radar sRadar;
    

	/**
	 * Constructor de la clase
	 * @param context
	 */
	public AugmentedView(Context context) {
		super(context);

		sPixelDensity = context.getResources().getDisplayMetrics().density;
		sRadar  = new Radar(sPixelDensity);        
	}// Fin del constructor
	
	
	/**
	 * Recorre la lista de marcadores disponibles para dibujar en la pantalla los que 
	 * correspondan y actualizar el radar.
	 * @param canvas Canvas donde se va a dibujar
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		
		if (canvas == null) {
    		return;
    	}

        if (sDrawing.compareAndSet(false, true)) { 
	        List<Marker> collection = ARDataSource.getMarkers();

	        COLLECTION_CACHE.clear();
            for (Marker m : collection) {
                m.update(canvas, 0, 0);
                if (m.isOnRadar()) {
                	COLLECTION_CACHE.add(m);
                }
	        }
            collection = COLLECTION_CACHE;

	        adjustForCollisions(canvas,collection);
	        
	        
	        ListIterator<Marker> iter = collection.listIterator(collection.size());
	        while (iter.hasPrevious()) {
	            Marker marker = iter.previous();
	            marker.draw(canvas);
	        }

	        sRadar.draw(canvas);
	        
	        sDrawing.set(false);		
        }
	}// Fin de onDraw
	
	
	/**
	 * TODO:Este habría que cambiarlo...Pensar en métodos para evitar los solapamientos
	 * Método con el que se ajustan los marcadores para que no se superpongan los que se encuentran
	 * muy cercanos en la pantalla.
	 * @param canvas Canvas donde se va a dibujar
	 * @param collection Conjunto de Markers dentro del rango actual que se van a comprobar
	 */
	private static void adjustForCollisions(Canvas canvas, List<Marker> collection) {
		
		sUpdatedMarkers.clear();
		
        for (Marker marker1 : collection) {
            if (sUpdatedMarkers.contains(marker1) || !marker1.isInView()) {
            	continue;
            }

            int collisions = 1;
            for (Marker marker2 : collection) {
                if (marker1.equals(marker2) || sUpdatedMarkers.contains(marker2) || !marker2.isInView()) {
                	continue;
                }

                //Si los marcadores se solapan, se corrige la altura (en coordenadas de pantalla)
                if (marker1.isMarkerOnMarker(marker2)) {
                    marker2.getLocation().get(LOCATION_ARRAY);
                    float y = LOCATION_ARRAY[1];
                    float h = collisions*COLLISION_ADJUSTMENT;
                    LOCATION_ARRAY[1] = y+h;
                    marker2.getLocation().set(LOCATION_ARRAY);
                    marker2.update(canvas, 0, 0);
                    collisions++;
                    sUpdatedMarkers.add(marker2);
                }
            }
            sUpdatedMarkers.add(marker1);
        }
	}
	
	

}
