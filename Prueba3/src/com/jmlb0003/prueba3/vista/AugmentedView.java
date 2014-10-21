package com.jmlb0003.prueba3.vista;



import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.modelo.Poi;


/**
 * Clase que implementa la vista utilizada para incorporar todos los componentes de la interfaz
 * en el modo Realidad Aumentada.
 * @author Jose
 *
 */
public class AugmentedView extends View {
	
    private static final float[] LOCATION_ARRAY = new float[3];
    private static final List<Poi> COLLECTION_CACHE = new ArrayList<Poi>();
    private static final int COLLISION_ADJUSTMENT = 100;
    
    private static TreeSet<Poi> sUpdatedPois = new TreeSet<Poi>();
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
		ARDataSource.PixelsDensity = sPixelDensity;
		
		sRadar  = new Radar(sPixelDensity);
	}// Fin del constructor dinámico
	
	
	/**
	 * Constructor de la clase cuando se ha insertado en un layout XML
	 * @param context
	 * @param attrs
	 */
	public AugmentedView(Context context, AttributeSet attrs) {
		super(context, attrs);

		sPixelDensity = context.getResources().getDisplayMetrics().density;
		ARDataSource.PixelsDensity = sPixelDensity;
		
		sRadar  = new Radar(sPixelDensity);
	}// Fin del constructor con XML
	
	
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
	        List<Poi> collection = ARDataSource.getPois();

	        COLLECTION_CACHE.clear();
            for (Poi m : collection) {
                m.update(canvas, 0, 0);
                if (m.isOnRadar()) {
                	COLLECTION_CACHE.add(m);
                }
	        }
            collection = COLLECTION_CACHE;

	        adjustForCollisions(canvas,collection);
	        
	        
	        ListIterator<Poi> iter = collection.listIterator(collection.size());
	        while (iter.hasPrevious()) {
	            Poi poi = iter.previous();
	            poi.draw(canvas);
	        }

	        sRadar.draw(canvas);
	        
	        sDrawing.set(false);		
        }
	}// Fin de onDraw
	
	
	/**
	 * TODO:Hay que cambiar la resolución de colisiones. Pensar en métodos para evitar los solapamientos
	 * Método con el que se ajustan los marcadores para que no se superpongan los que se encuentran
	 * muy cercanos en la pantalla.
	 * @param canvas Canvas donde se va a dibujar
	 * @param collection Conjunto de PIs dentro del rango actual que se van a comprobar
	 */
	private static void adjustForCollisions(Canvas canvas, List<Poi> collection) {
		
		sUpdatedPois.clear();
		
        for (Poi poi1 : collection) {
            if (sUpdatedPois.contains(poi1) || !poi1.isInView()) {
            	continue;
            }

            int collisions = 1;
            for (Poi poi2 : collection) {
                if (poi1.equals(poi2) || sUpdatedPois.contains(poi2) || !poi2.isInView()) {
                	continue;
                }

                //Si los marcadores se solapan, se corrige la altura (en coordenadas de pantalla)
                if (poi1.isPoiOnPoi(poi2)) {
                    poi2.getLocation().get(LOCATION_ARRAY);
                    float y = LOCATION_ARRAY[1];
                    float h = collisions*COLLISION_ADJUSTMENT;
                    LOCATION_ARRAY[1] = y+h;
                    poi2.getLocation().set(LOCATION_ARRAY);
                    poi2.update(canvas, 0, 0);
                    collisions++;
                    sUpdatedPois.add(poi2);
                }
            }
            sUpdatedPois.add(poi1);
        }
	}
	
	

}
