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

import com.jmlb0003.prueba3.modelo.Marker;
import com.jmlb0003.prueba3.utilidades.Matrix;



/**
 * Clase que maneja la mayoría de los datos no persistentes necesarios para las demás clases de 
 * la aplicación como por ejemplo los markers, la posición actual, matrices de rotación, etc.
 * @author Jose
 *
 */
public abstract class ARDataSource {
	
	private static final Map<String,Marker> MARKER_LIST = new ConcurrentHashMap<String,Marker>();
    private static final List<Marker> CACHE = new CopyOnWriteArrayList<Marker>();
    private static final AtomicBoolean COMPUTING = new AtomicBoolean(false);
    private static final float[] LOCATION_ARRAY = new float[3];
	private static final DecimalFormat FORMAT = new DecimalFormat("#.##");
    
    //TODO: aqui hay que hacer algo...meter la ultima ubicacion, o por lo menos un mensaje de que no hay ubicaciones
    public static final Location HARD_FIX = new Location("ATL");  
    
    static {
        //hardFix.setLatitude(0);
        //hardFix.setLongitude(0);
        //hardFix.setAltitude(1);
        
//        hardFix.setLatitude(37.789);
//        hardFix.setLongitude(-3.779);
//        hardFix.setAltitude(700);
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
     * Método para obtener todos los markers que hay actualmente en memoria
     * @return Lista inmodificable con los markers que hay en memoria
     */
    public static List<Marker> getMarkers() {
    	//Si COMPUTING vale true, se le asigna false
        if (COMPUTING.compareAndSet(true, false)) {
            for(Marker ma : MARKER_LIST.values()) {
                ma.getLocation().get(LOCATION_ARRAY);
                LOCATION_ARRAY[1] = ma.getInitialY();
                ma.getLocation().set(LOCATION_ARRAY);
            }


            List<Marker> copy = new ArrayList<Marker>();
            copy.addAll(MARKER_LIST.values());
            Collections.sort(copy,distanceComparator);
            CACHE.clear();
            CACHE.addAll(copy);
        }
        
        return Collections.unmodifiableList(CACHE);
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
    
    
    private static final Comparator<Marker> distanceComparator = new Comparator<Marker>() {
        public int compare(Marker m1, Marker m2) {
            return Double.compare(m1.getDistance(),m2.getDistance());
        }
    };

    
    public static void addMarkers(Collection<Marker> markers) {
    	if (markers == null) {
    		throw new NullPointerException();
    	}

    	if (markers.size() <= 0) {
    		return;
    	}

    	for(Marker marker : markers) {
    	    if (!MARKER_LIST.containsKey(marker.getName())) {
    	        marker.calcRelativePosition(ARDataSource.getCurrentLocation());
    	        MARKER_LIST.put(marker.getName(),marker);
    	    }
    	}

    	if (COMPUTING.compareAndSet(false, true)) {
    	    CACHE.clear();
    	}
    }
    
    
    
    
    private static void onLocationChanged(Location location) {
        for(Marker ma: MARKER_LIST.values()) {
            ma.calcRelativePosition(location);
        }

        if (COMPUTING.compareAndSet(false, true)) {
            CACHE.clear();
        }
    }
    
    
    /**
     * Método para filtrar los datos que se muestran en pantalla según la distancia máxima
     * @param newMaxDistance Distancia máxima a la que pueden estar los markers que se muestren en pantalla
     */
    public static void updateDataWithMaxDistance(float newMaxDistance) {
        ARDataSource.setRadius(newMaxDistance);
        ARDataSource.setZoomLevel(FORMAT.format(newMaxDistance));
    }

}
