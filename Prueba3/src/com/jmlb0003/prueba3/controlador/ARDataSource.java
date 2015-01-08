package com.jmlb0003.prueba3.controlador;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.jmlb0003.prueba3.modelo.DetallesPoi;
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;
import com.jmlb0003.prueba3.utilidades.Matrix;



/**
 * Clase que maneja la mayoría de los datos no persistentes necesarios para las demás clases de 
 * la aplicación como por ejemplo los PIs, la posición actual, matrices de rotación, etc.
 * @author Jose
 *
 */
public abstract class ARDataSource {
	
	private static final Map<Long,Poi> POI_LIST = new ConcurrentHashMap<Long,Poi>();
    private static final List<Poi> CACHE = new CopyOnWriteArrayList<Poi>();
    private static final AtomicBoolean COMPUTING = new AtomicBoolean(false);
    private static final float[] LOCATION_ARRAY = new float[3];
	private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    
    public static final Location HARD_FIX = new Location("ATL");
    public static final int MAX_RADIUS = 50;
    
    
    
    private static final String LOG_TAG = "ARDataSource";
    
    static {
        //HARD_FIX.setLatitude(0);
        //HARD_FIX.setLongitude(0);
        //HARD_FIX.setAltitude(1);
        
        HARD_FIX.setLatitude(37.789);
        HARD_FIX.setLongitude(-3.779);
        HARD_FIX.setAltitude(700);
        
        HARD_FIX.setAccuracy(0.0f);
    }
    
    
    private static final Object RADIUS_LOCK = new Object();    
    
    private static final Object AZIMUTH_LOCK = new Object();
    
    /** Variable en la que se almacena la distancia máxima del radar en forma de cadena para ser
     * mostrada en el rótulo en pantalla **/
    private static String sZoomLevel = new String();
    /** Variable en la que se almacena la última posición del dispositivo conocida **/
    private static Location sCurrentLocation = HARD_FIX;
    /** Variable en la que se almacena la matriz de rotación dada la orientación del dispositivo **/
    private static Matrix sRotationMatrix = new Matrix();
    /** Variable donde se almacena el azimuth, variación del dispositivo en grados respecto del norte **/
    private static float sAzimuth = 0;
    /** Variable que almacena la distancia máxima del radar **/
    private static float sRadius;
    
    
    /** Variable donde se almacena el valor de densidad de píxel de la pantalla del dispositivo **/
    public static float PixelsDensity;
    
    /**Variable donde se almacena la referencia del PI seleccionado en pantalla**/
    public static Poi SelectedPoi = null;
    
    /** Colección que contiene los iconos de los PIs mostrados en pantalla. Muy importante 
     * inicializar en cuanto arranca la app
     */
    public static HashMap<String, Bitmap> sPoiIcons = new HashMap<>();
    
    /** FILTROS DE LOS PIs MOSTRADOS **/

    private static boolean mOpenFilter = false;

    /**
     * Método para obtener el actual radio del radar
     * @return Valor actual del radio del radar
     */
    public static float getRadius() {
        synchronized (ARDataSource.RADIUS_LOCK) {
            return ARDataSource.sRadius;
        }
    }

    
    public static void setCurrentLocation(Location currentLocation) {
    	if (currentLocation == null) {
    		throw new NullPointerException();
    	}


    	synchronized (currentLocation) {
    		ARDataSource.sCurrentLocation = currentLocation;
    	}
    	
        onLocationChanged(currentLocation);
    }
    
    public static Location getCurrentLocation() {
        synchronized (ARDataSource.sCurrentLocation) {
            return ARDataSource.sCurrentLocation;
        }
    }

    public static void setRotationMatrix(Matrix rotationMatrix) {
        synchronized (ARDataSource.sRotationMatrix) {
        	ARDataSource.sRotationMatrix = rotationMatrix;
        }
    }

    public static Matrix getRotationMatrix() {
        synchronized (ARDataSource.sRotationMatrix) {
            return sRotationMatrix;
        }
    }
    
    
    /**
     * Método para establecer un filtro de PIs a mostrar por horas de apertura. Esto servirá
     * para que la app muestre solamente los PIs que están abiertos en el momento en que se está
     * usando.
     * @param poisFilteredByHours Indica si se quiere filtrar los PIs según si están abierts o 
     * 	cerrados
     */
    public static void setFilterByHours(boolean poisFilteredByHours) {
    	mOpenFilter = poisFilteredByHours;
    	if(mOpenFilter) {
    		filterByOpenHours();
    	}
    }
   
    
  
    /**
     * Método para eliminar de los PIs que hay actualmente en memoria los que no estén abiertos
     * actualmente.
     */
    private static void filterByOpenHours() {
    	Log.d(LOG_TAG,"filtrando...De "+POI_LIST.size()+" que hay...");
        for(Poi ma : POI_LIST.values()) {
        	if (ma.isOpen()) {
            	ma.getLocation().get(LOCATION_ARRAY);
                LOCATION_ARRAY[1] = ma.getInitialY();
                ma.getLocation().set(LOCATION_ARRAY);
            }else{
            	POI_LIST.remove(ma.getID());
            }
        }
        Log.d(LOG_TAG,"A "+POI_LIST.size()+" que quedan...");
    }
    
    
    /**
     * Método para establecer filtrar los PIs que corresponden a un proveedor dado.
     * @param providerID ID del proveedor de PIs
     */
    public static void setFilterByProvider(int providerID) {
    	removeProvider(providerID);
    }
    
    
    /**
     * Método para eliminar de los PIs que hay actualmente en memoria los que provienen de un
     * proveedor descartado en los ajustes.
     * @param providerId	ID del proveedor de PIs
     */
    private static void removeProvider(int providerId) {
    	for(Poi ma : POI_LIST.values()) {
        	if (ma.getUserId() != providerId) {
            	ma.getLocation().get(LOCATION_ARRAY);
                LOCATION_ARRAY[1] = ma.getInitialY();
                ma.getLocation().set(LOCATION_ARRAY);
            }else{
            	POI_LIST.remove(ma.getID());
            }
        }
    }
    
    /**
     * Método para obtener todos los PIs que hay actualmente en memoria
     * @return Lista inmodificable con los PIs que hay en memoria
     */
    public static List<Poi> getPois() {
    	//Si COMPUTING vale true, se le asigna false
        if (COMPUTING.compareAndSet(true, false)) {
            for(Poi ma : POI_LIST.values()) {
                ma.getLocation().get(LOCATION_ARRAY);
                LOCATION_ARRAY[1] = ma.getInitialY();
                ma.getLocation().set(LOCATION_ARRAY);
            }


            List<Poi> copy = new ArrayList<Poi>();
            copy.addAll(POI_LIST.values());
            Collections.sort(copy,distanceComparator);
            CACHE.clear();
            CACHE.addAll(copy);
        }
        
        return Collections.unmodifiableList(CACHE);
    }
    
    
    /**
     * Método que comprueba si hay algún PI seleccionado
     * @return
     */
    public static boolean hasSelectededPoi() {
    	if (POI_LIST == null) {
    		return false;
    	}
    	
        for(Poi ma : POI_LIST.values()) {
            if (ma.isSelected()) {
            	SelectedPoi = ma;
            	return true;
            }
        }
        SelectedPoi = null;
            	
    	return false;
    }
    
    
    
    
    public static void setAzimuth(float azimuth) {
        synchronized (AZIMUTH_LOCK) {
            ARDataSource.sAzimuth = azimuth;
        }
    }
    
    public static float getAzimuth() {
        synchronized (AZIMUTH_LOCK) {
            return ARDataSource.sAzimuth;
        }
    }
    
    
    private static final Comparator<Poi> distanceComparator = new Comparator<Poi>() {
        public int compare(Poi m1, Poi m2) {
            return Double.compare(m1.getDistance(),m2.getDistance());
        }
    };

    
    
    /**
     * Método para añadir a la lista de PIs en memoria un conjunto de PIs contenido en un cursor
     * @param c Cursor que contiene los PIs. Deberá inicializarse y cerrarse desde donde se 
     * llame a este método.
     */
    public static void addPoisFromCursor(Cursor c) {
    	if (c == null || !c.moveToFirst() ) {
    		return;
    	}
    	
    	ArrayList<ContentValues> toRet = new ArrayList<>();
    	for (int i = 0; i < c.getCount(); i++) {
    		c.moveToPosition(i);
    		ContentValues cv = new ContentValues();
    		
    		cv.put(DetallesPoi.DETALLESPI_ID_POI,
    				c.getLong(c.getColumnIndex(PoiEntry._ID)));
    		cv.put(DetallesPoi.DETALLESPI_NAME, 
    				c.getString(c.getColumnIndex(PoiEntry.COLUMN_POI_NAME)));    		
    		cv.put(DetallesPoi.DETALLESPI_LATITUDE, 
    				c.getDouble(c.getColumnIndex(PoiEntry.COLUMN_POI_LATITUDE)));
    		cv.put(DetallesPoi.DETALLESPI_LONGITUDE,
    				c.getDouble(c.getColumnIndex(PoiEntry.COLUMN_POI_LONGITUDE)));
    		cv.put(DetallesPoi.DETALLESPI_ALTITUDE,
    				c.getDouble(c.getColumnIndex(PoiEntry.COLUMN_POI_ALTITUDE)));

    		cv.put(DetallesPoi.DETALLESPI_USER_ID, 
    				c.getLong(c.getColumnIndex(PoiEntry.COLUMN_POI_USER_ID)));
    		cv.put(DetallesPoi.DETALLESPI_COLOR, 
    				c.getInt(c.getColumnIndex(PoiEntry.COLUMN_POI_COLOR)));
    		cv.put(DetallesPoi.DETALLESPI_IMAGE, 
    				c.getString(c.getColumnIndex(PoiEntry.COLUMN_POI_IMAGE)));
    		cv.put(DetallesPoi.DETALLESPI_DESCRIPTION, 
    				c.getString(c.getColumnIndex(PoiEntry.COLUMN_POI_DESCRIPTION)));
    		cv.put(DetallesPoi.DETALLESPI_WEBSITE,
    				c.getString(c.getColumnIndex(PoiEntry.COLUMN_POI_WEBSITE)));
    		cv.put(DetallesPoi.DETALLESPI_PRICE,
    				c.getFloat(c.getColumnIndex(PoiEntry.COLUMN_POI_PRICE)));
    		cv.put(DetallesPoi.DETALLESPI_OPEN_HOURS,
    				c.getString(c.getColumnIndex(PoiEntry.COLUMN_POI_OPEN_HOURS)));
    		cv.put(DetallesPoi.DETALLESPI_CLOSE_HOURS,
    				c.getString(c.getColumnIndex(PoiEntry.COLUMN_POI_CLOSE_HOURS)));
    		cv.put(DetallesPoi.DETALLESPI_MAX_AGE,
    				c.getFloat(c.getColumnIndex(PoiEntry.COLUMN_POI_MAX_AGE)));
    		cv.put(DetallesPoi.DETALLESPI_MIN_AGE,
    				c.getFloat(c.getColumnIndex(PoiEntry.COLUMN_POI_MIN_AGE)));
    		
    		toRet.add(cv);
    	}
    	
    	if (!toRet.isEmpty()) {
    		ARDataSource.addPoisFromValues(toRet);
    	}
    }
    
    
    private static void addPoisFromValues(Collection<ContentValues> poiList) {
    	if (poiList == null || poiList.size() <= 0) {
    		return;
    	}

    	for(ContentValues pv : poiList) {
    		
    		Map<String, Object> details = new HashMap<>();
    		details.put(DetallesPoi.DETALLESPI_ID_POI,
    				pv.getAsLong(DetallesPoi.DETALLESPI_ID_POI));
    		details.put(DetallesPoi.DETALLESPI_USER_ID, 
    				pv.getAsLong(DetallesPoi.DETALLESPI_USER_ID));
    		details.put(DetallesPoi.DETALLESPI_COLOR, 
    				pv.getAsInteger(DetallesPoi.DETALLESPI_COLOR));
    		details.put(DetallesPoi.DETALLESPI_IMAGE, 
    				pv.getAsString(DetallesPoi.DETALLESPI_IMAGE));
    		details.put(DetallesPoi.DETALLESPI_DESCRIPTION, 
    				pv.getAsString(DetallesPoi.DETALLESPI_DESCRIPTION));
    		details.put(DetallesPoi.DETALLESPI_WEBSITE,
    				pv.getAsString(DetallesPoi.DETALLESPI_WEBSITE));
    		details.put(DetallesPoi.DETALLESPI_PRICE,
    				pv.getAsFloat(DetallesPoi.DETALLESPI_PRICE));
    		details.put(DetallesPoi.DETALLESPI_OPEN_HOURS,
    				pv.getAsString(DetallesPoi.DETALLESPI_OPEN_HOURS));
    		details.put(DetallesPoi.DETALLESPI_CLOSE_HOURS,
    				pv.getAsString(DetallesPoi.DETALLESPI_CLOSE_HOURS));
    		details.put(DetallesPoi.DETALLESPI_MAX_AGE,
    				pv.getAsFloat(DetallesPoi.DETALLESPI_MAX_AGE));
    		details.put(DetallesPoi.DETALLESPI_MIN_AGE,
    				pv.getAsFloat(DetallesPoi.DETALLESPI_MIN_AGE));
    		
    		
    		Poi poi = new Poi(
    				pv.getAsString(DetallesPoi.DETALLESPI_NAME),
    				pv.getAsDouble(DetallesPoi.DETALLESPI_LATITUDE), 
    				pv.getAsDouble(DetallesPoi.DETALLESPI_LONGITUDE), 
    				pv.getAsDouble(DetallesPoi.DETALLESPI_ALTITUDE), 
    				new DetallesPoi(details),
    				sPoiIcons.get(DetallesPoi.DETALLESPI_ICON),
    				sPoiIcons.get(DetallesPoi.DETALLESPI_SELECTED_ICON));


    	    if (!POI_LIST.containsKey(poi.getID())) {
    	        poi.calcRelativePosition(ARDataSource.getCurrentLocation());
    	        POI_LIST.put(poi.getID(), poi);
    	    }
    	}

    	if (COMPUTING.compareAndSet(false, true)) {
    	    CACHE.clear();
    	}
    }
    
    
    
    
    private static void onLocationChanged(Location location) {
        for(Poi ma: POI_LIST.values()) {
            ma.calcRelativePosition(location);
        }

        if (COMPUTING.compareAndSet(false, true)) {
            CACHE.clear();
        }
    }
    
    
    /** 
     * Método para filtrar los datos que se muestran en pantalla según la distancia máxima
     * @param newMaxDistance Distancia máxima a la que pueden estar los PIs que se muestren en pantalla
     */
    public static void updateRadarDistance(float newMaxDistance) {
    	if (newMaxDistance <= 0) {
    		return;
    	}
    	
        synchronized (ARDataSource.RADIUS_LOCK) {
        	ARDataSource.sRadius = newMaxDistance;
        }
        
        synchronized (ARDataSource.sZoomLevel){
        	ARDataSource.sZoomLevel = FORMAT.format(newMaxDistance);
        }
    }
    
    
    /**
     * Método para obtener un Poi de la lista de PIs dado su ID.
     * @param idPoi ID del PI que se está buscando
     * @return PoI de la lista con el ID dado o null si no hay ninguno.
     */
    public static Poi getPoi(long idPoi) {
    	return POI_LIST.get(idPoi);
    }
    
    /**
     * Método para obtener un Poi de la lista de PIs cuyo nombre coincida con el que se pasa como
     * parámetro en PoiName.
     * @param poiName Nombre del PI que se está buscando
     * @return PoI de la lista cuyo nombre coincida con PoiName o null si no hay ninguno.
     */
    public static Poi getPoiByName(String poiName) {    
    	for (long id:POI_LIST.keySet()) {
    		Poi p = POI_LIST.get(id);
    		
    		if (p.getName().equals(poiName)) {
    			return p;
    		}
    	}
    	
    	return null;
    }

}
