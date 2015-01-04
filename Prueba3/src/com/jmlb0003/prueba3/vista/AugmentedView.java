package com.jmlb0003.prueba3.vista;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.modelo.Poi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase que implementa la vista utilizada para incorporar todos los componentes de la interfaz
 * en el modo Realidad Aumentada.
 * @author Jose
 *
 */
public class AugmentedView extends View {
	
	private static final String LOG_CAT = "AugmentedView";
	
	/**Se usa para modificar las posiciones en pantalla de los PIs que aparecen superpuestos**/
    private static final float[] LOCATION_ARRAY = new float[3];
    private static final List<Poi> POIS_ON_SCREEN = new ArrayList<Poi>();
    /**Se usa para ajustar la altura de un Poi que colisiona con otro**/
    private static final int COLLISION_ADJUSTMENT = 5;//= 100;
    
    @SuppressLint("UseSparseArrays")
	private static HashMap<Long,Poi> sUpdatedPois = new HashMap<Long,Poi>();
    public static List<List<Poi>> sGroupedPois = new ArrayList<>();
    private static AtomicBoolean sDrawing = new AtomicBoolean(false);
    private static float sPixelDensity;
    private static Radar sRadar;
    

	/**
	 * Constructor de la clase
	 * @param context
	 */
	public AugmentedView(Context context) {
		super(context);
		
		initView(context);
	}// Fin del constructor dinámico
	
	
	/**
	 * Constructor de la clase cuando se ha insertado en un layout XML
	 * @param context
	 * @param attrs
	 */
	public AugmentedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initView(context);
		
	}// Fin del constructor con XML
	
	
	private void initView(Context context) {
		sPixelDensity = context.getResources().getDisplayMetrics().density;
		ARDataSource.PixelsDensity = sPixelDensity;
		
		sRadar  = new Radar(context);
	}
	
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
        	POIS_ON_SCREEN.clear();
            for (Poi m : ARDataSource.getPois()) {
                m.update(canvas, 0, 0);
                if (m.isOnRadar() && m.isInView()) {
                	POIS_ON_SCREEN.add(m);
                }
	        }
          

	        adjustForCollisions(canvas, POIS_ON_SCREEN);

	        ListIterator<Poi> iter = POIS_ON_SCREEN.listIterator(POIS_ON_SCREEN.size());
	        while (iter.hasPrevious()) {
	            Poi poi = iter.previous();
	            poi.draw(canvas);
	        }

	        sRadar.draw(canvas);
	        
	        sDrawing.set(false);		
        }
	}// Fin de onDraw
	
	
	/**
	 * Método con el que se ajustan los marcadores para que no se superpongan los que se encuentran
	 * muy cercanos en la pantalla.
	 * @param canvas Canvas donde se va a dibujar
	 * @param collection Conjunto de PIs dentro del rango actual que se van a comprobar
	 */
	private static void adjustForCollisions(Canvas canvas, List<Poi> collection) {

		Collections.sort(collection);
		sUpdatedPois.clear();
		sGroupedPois.clear();

        for (Poi poi1 : collection) {
            if (sUpdatedPois.containsKey(poi1.getID()) || !poi1.isInView()) {
            	continue;
            }
            ArrayList<Poi> poisWithpoi1 = new ArrayList<>();	//Nuevo item para la lista de pois
            poisWithpoi1.add(poi1);
            poi1.getLocation().get(LOCATION_ARRAY);
            int collisions = 1;
            for (Poi poi2 : collection) {
                if (poi1.equals(poi2) || sUpdatedPois.containsKey(poi2.getID()) || !poi2.isInView()) {
                	continue;
                }
                //Si los marcadores se solapan, se corrige la posicion
                if (poi1.isPoiOnPoi(poi2)) {
                	poi2.getLocation().set(LOCATION_ARRAY);
                	
                	float f = collisions * COLLISION_ADJUSTMENT * sPixelDensity;
                    poi2.update(canvas, f, f);
                    collisions++;
                    sUpdatedPois.put(poi2.getID(), poi2);
                    poisWithpoi1.add(poi2);	//Lo añadimos al grupo de poi1
                    
                    poi1.setAdjusted(true);
                    poi2.setAdjusted(true);
                }
            }
            sUpdatedPois.put(poi1.getID(), poi1);
            sGroupedPois.add(poisWithpoi1);
        }
	}
	
	
	public List<Poi> getPoiGroup(Poi p) {
		ListIterator<List<Poi>> l = sGroupedPois.listIterator();
		while(l.hasNext()) {
			List<Poi> toRet = l.next();
			if (toRet.contains(p)) {
				return toRet;
			}
		}
		return null;
	}
	

}
