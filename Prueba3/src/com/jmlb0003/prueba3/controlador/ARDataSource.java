package com.jmlb0003.prueba3.controlador;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.location.Location;

import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.utilidades.Matrix;



/**
 * Clase que maneja la mayoría de los datos no persistentes necesarios para las demás clases de 
 * la aplicación como por ejemplo los PIs, la posición actual, matrices de rotación, etc.
 * @author Jose
 *
 */
public abstract class ARDataSource {
	
	private static final Map<String,Poi> POI_LIST = new ConcurrentHashMap<String,Poi>();
    private static final List<Poi> CACHE = new CopyOnWriteArrayList<Poi>();
    private static final AtomicBoolean COMPUTING = new AtomicBoolean(false);
    private static final float[] LOCATION_ARRAY = new float[3];
	private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    
    public static final Location HARD_FIX = new Location("ATL");
    
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
    private static final Object PITCH_LOCK = new Object();    
    private static final Object ROLL_LOCK = new Object();
    
    private static String sZoomLevel = new String();
    private static Location sCurrentLocation = HARD_FIX;
    private static Matrix sRotationMatrix = new Matrix();
    
    private static float sAzimuth = 0;
    private static float sPitch = 0;
    private static float sRoll = 0;
    
    private static float sRadius;
    
    
    /**Variable donde se almacena el valor de densidad de píxel de la pantalla del dispositivo**/
    public static float PixelsDensity;
    
    /**Variable donde se almacena la referencia del PI seleccionado en pantalla**/
    public static Poi SelectedPoi = null;
    
    /**
     * Modifica el texto del radio de búsqueda del Radar
     * @param zoomLevel Nuevo valor para el radio de búsqueda de PIs
     */
    public static void setZoomLevel(String zoomLevel) {
    	if (zoomLevel == null) {
    		throw new NullPointerException();
    	}
    	
    	synchronized (ARDataSource.sZoomLevel) {
    		ARDataSource.sZoomLevel = zoomLevel;
    	}
    }
    
    
    
    /**
     * Modifica el radio actual del radar con uno nuevo
     * @param radius	Nuevo valor del radio del radar en metros
     */
    public static void setRadius(float radius) {
        synchronized (ARDataSource.RADIUS_LOCK) {
        	ARDataSource.sRadius = radius;
        }
    }

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
    
    
    public static void setPitch(float pitch) {
        synchronized (PITCH_LOCK) {
        	ARDataSource.sPitch = pitch;
        }
    }

    public static float getPitch() {
        synchronized (PITCH_LOCK) {
            return ARDataSource.sPitch;
        }
    }

    public static void setRoll(float roll) {
        synchronized (ROLL_LOCK) {
        	ARDataSource.sRoll = roll;
        }
    }

    public static float getRoll() {
        synchronized (ROLL_LOCK) {
            return ARDataSource.sRoll;
        }
    }
    
    
    private static final Comparator<Poi> distanceComparator = new Comparator<Poi>() {
        public int compare(Poi m1, Poi m2) {
            return Double.compare(m1.getDistance(),m2.getDistance());
        }
    };

    
    public static void addPois(Collection<Poi> pois) {
    	if (pois == null) {
    		throw new NullPointerException();
    	}

    	if (pois.size() <= 0) {
    		return;
    	}

    	for(Poi poi : pois) {
    	    if (!POI_LIST.containsKey(poi.getName())) {
    	        poi.calcRelativePosition(ARDataSource.getCurrentLocation());
    	        POI_LIST.put(poi.getName(),poi);
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
     *  //TODO: Aquí hay que hacer que se actualice menos. Si no,cada vez que cambia la posición se descarga todo.
     *  1º solo se actualiza si la posición varía más de un margen (p.e. 100 metros)
     *  2º Además de la opción 1º, guardar los PIs y crear una función que descargue aplicando como intersecciones... (tengo los PIs de la posición X y dame los de X+1 que no estén en X) 
     * Método para filtrar los datos que se muestran en pantalla según la distancia máxima
     * @param newMaxDistance Distancia máxima a la que pueden estar los PIs que se muestren en pantalla
     */
    public static void updateDataWithMaxDistance(float newMaxDistance) {
        ARDataSource.setRadius(newMaxDistance);
        ARDataSource.setZoomLevel(FORMAT.format(newMaxDistance));
    }
    
    
    /**
     * Método para obtener un Poi de la lista de PIs cuyo nombre coincida con el que se pasa como
     * parámetro en PoiName.
     * @param PoiName Nombre del PI que se está buscando
     * @return PoI de la lista cuyo nombre coincida con PoiName o null si no hay ninguno.
     */
    public static Poi getPoiByName(String PoiName) {
    	return POI_LIST.get(PoiName);
    }

}
