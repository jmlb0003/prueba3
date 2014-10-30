package com.jmlb0003.prueba3.vista;



import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
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
	
	/**Se usa para modificar las posiciones en pantalla de los PIs que aparecen superpuestos**/
    private static final float[] LOCATION_ARRAY = new float[3];
    private static final List<Poi> COLLECTION_CACHE = new ArrayList<Poi>();
    /**Se usa para ajustar la altura de un Poi que colisiona con otro**/
    private static final int COLLISION_ADJUSTMENT = 100;//= 100;
    
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
//TODO: aqui hay que mejorar esto para el manejo de los PIs que hay en memoria y tal...
        if (sDrawing.compareAndSet(false, true)) { 
	        List<Poi> collection = ARDataSource.getPois();

	        COLLECTION_CACHE.clear();
            for (Poi m : collection) {
                m.update(canvas, 0, 0);
                if (m.isOnRadar() && m.isInView()) {
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
                	Log.d("AugmentedView","El poi "+poi1.getName()+" se solapa con "+poi2.getName());
                    poi2.getLocation().get(LOCATION_ARRAY);
                    float y = LOCATION_ARRAY[1];
                    float h = collisions * COLLISION_ADJUSTMENT;
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
	
	private static void adjustForCollisions2(Canvas canvas, List<Poi> collection) {
		sUpdatedPois.clear();
		
        for (Poi poi1 : collection) {
            if (sUpdatedPois.contains(poi1) || !poi1.isInView()) {
            	Log.d("AugmentedView","1 con El poi "+poi1.getName());
            	continue;
            }

            int collisions = 1;
            for (Poi poi2 : collection) {
                if (poi1.equals(poi2) || sUpdatedPois.contains(poi2) || !poi2.isInView()) {
                	Log.d("AugmentedView","2 con El poi "+poi2.getName());
                	continue;
                }
                
                Log.d("AugmentedView","3 con El poi "+poi1.getName()+" y el poi2 "+poi2.getName());
                //Si los marcadores se solapan, se corrige la altura (en coordenadas de pantalla)
                if (poi1.isPoiOnPoi(poi2)) {
                	Log.d("AugmentedView","4El poi "+poi1.getName()+" se solapa con "+poi2.getName());
                    poi2.getScreenPosition().get(LOCATION_ARRAY);
                    float y = LOCATION_ARRAY[1];
                    float h = collisions * COLLISION_ADJUSTMENT;
                    Log.d("AugmentedView","4El y es "+y+" y se le suma "+h);
                    Log.d("AugmentedView","4De LOCATION_ARRAY[0]"+LOCATION_ARRAY[0]+" LOCATION_ARRAY[1]"+LOCATION_ARRAY[1]);
                    LOCATION_ARRAY[1] = y+h;
                    Log.d("AugmentedView","4De "+poi1.getName()+" X"+poi1.getScreenPosition().getX()+" Y"+poi1.getScreenPosition().getY()+" Z"+poi1.getScreenPosition().getZ());
                    LOCATION_ARRAY[0] = poi1.getScreenPosition().getX()-100;
                    Log.d("AugmentedView","4 y SE METE____ LOCATION_ARRAY[0]"+LOCATION_ARRAY[0]+" LOCATION_ARRAY[1]"+LOCATION_ARRAY[1]);
                    poi2.getScreenPosition().set(LOCATION_ARRAY);
                    
                    poi2.update(canvas, 0, 0);
                    collisions++;
                    sUpdatedPois.add(poi2);
                }
                Log.d("AugmentedView","5 con El poi "+poi2.getName());
            }
            sUpdatedPois.add(poi1);
            Log.d("AugmentedView","6 con El poi "+poi1.getName());
        }
	}
	
private static void adjustForCollisions_FUNCIONA_(Canvas canvas, List<Poi> collection) {
		
		sUpdatedPois.clear();
		
        for (Poi poi1 : collection) {
            if (sUpdatedPois.contains(poi1) || !poi1.isInView()) {
            	Log.d("AugmentedView","1 con El poi "+poi1.getName());
            	continue;
            }

            int collisions = 1;
            for (Poi poi2 : collection) {
                if (poi1.equals(poi2) || sUpdatedPois.contains(poi2) || !poi2.isInView()) {
                	Log.d("AugmentedView","2 con El poi "+poi2.getName());
                	continue;
                }
                
                Log.d("AugmentedView","3 con El poi "+poi1.getName()+" y el poi2 "+poi2.getName());
                //Si los marcadores se solapan, se corrige la altura (en coordenadas de pantalla)
                if (poi1.isPoiOnPoi(poi2)) {
                	Log.d("AugmentedView","4El poi "+poi1.getName()+" se solapa con "+poi2.getName());
                    poi2.getLocation().get(LOCATION_ARRAY);
                    float y = LOCATION_ARRAY[1];
                    float h = collisions * COLLISION_ADJUSTMENT;
                    Log.d("AugmentedView","4El y es "+y+" y se le suma "+h);
                    Log.d("AugmentedView","4De LOCATION_ARRAY[0]"+LOCATION_ARRAY[0]+" LOCATION_ARRAY[1]"+LOCATION_ARRAY[1]+" LOCATION_ARRAY[2]"+LOCATION_ARRAY[2]);
                    LOCATION_ARRAY[1] = y+h;
                    Log.d("AugmentedView","4De "+poi1.getName()+" X"+poi1.getLocation().getX()+" Y"+poi1.getLocation().getY()+" Z"+poi1.getLocation().getZ());
                    LOCATION_ARRAY[0] = poi1.getLocation().getX();
                    LOCATION_ARRAY[2] = poi1.getLocation().getZ();
                    Log.d("AugmentedView","4 y SE METE____ LOCATION_ARRAY[0]"+LOCATION_ARRAY[0]+" LOCATION_ARRAY[1]"+LOCATION_ARRAY[1]+" LOCATION_ARRAY[2]"+LOCATION_ARRAY[2]);
                    poi2.getLocation().set(LOCATION_ARRAY);
                    
                    poi2.update(canvas, 0, 0);
                    collisions++;
                    sUpdatedPois.add(poi2);
                }
                Log.d("AugmentedView","5 con El poi "+poi2.getName());
            }
            sUpdatedPois.add(poi1);
            Log.d("AugmentedView","6 con El poi "+poi1.getName());
        }
	}
	
	

}
