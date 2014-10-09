package com.jmlb0003.prueba3.controlador;


import android.location.Location;
import android.util.Log;

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

import com.jmlb0003.prueba3.modelo.Marker;
import com.jmlb0003.prueba3.utilidades.Matrix;



/**
 * Clase que maneja la mayor�a de los datos no persistentes necesarios para las dem�s clases de 
 * la aplicaci�n como por ejemplo los markers, la posici�n actual, matrices de rotaci�n, etc.
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
//    private static final Object ZOOM_PROGRESS_LOCK = new Object();
    
    private static final Object AZIMUTH_LOCK = new Object();    
    private static final Object PITCH_LOCK = new Object();    
    private static final Object ROLL_LOCK = new Object();
    
    private static String sZoomLevel = new String();    
//    private static int sZoomProgress = 0;
    private static Location sCurrentLocation = HARD_FIX;
    private static Matrix sRotationMatrix = new Matrix();
    
    private static float sAzimuth = 0;
    private static float sPitch = 0;
    private static float sRoll = 0;
    
    private static float sRadius;
    
    
    /**
     * Modifica el nivel de zoom actual
     * @param zoomLevel Nuevo valor para el zoom
     */
    public static void setZoomLevel(String zoomLevel) {
    	if (zoomLevel == null) {
    		throw new NullPointerException();
    	}
    	
    	synchronized (ARDataSource.sZoomLevel) {
    		ARDataSource.sZoomLevel = zoomLevel;
    	}
    }
    
//    /**
//     * Asigna un nuevo porcentaje de barra seleccionada de la barra de zoom
//     * @param zoomProgress
//     */
//    public static void setZoomProgress(int zoomProgress) {
//        synchronized (ARData.zoomProgressLock) {
//            if (ARData.zoomProgress != zoomProgress) {
//                ARData.zoomProgress = zoomProgress;
//                if (dirty.compareAndSet(false, true)) {
//                    Log.v(TAG, "Setting DIRTY flag!");
//                    cache.clear();
//                }
//            }
//        }
//    }
    
    
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
     * M�todo para obtener el actual radio del radar
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
     * M�todo para obtener todos los markers que hay actualmente en memoria
     * @return Lista inmodificable con los markers que hay en memoria
     */
    public static List<Marker> getMarkers() {
        if (COMPUTING.compareAndSet(true, false)) {
            Log.v("ARDataSource", "DIRTY flag found, resetting all marker heights to zero.");
            for(Marker ma : MARKER_LIST.values()) {
                ma.getLocation().get(LOCATION_ARRAY);
                LOCATION_ARRAY[1] = ma.getInitialY();
                ma.getLocation().set(LOCATION_ARRAY);
            }

            Log.v("ARDataSource", "Populating the cache.");
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
    	
    	Log.d("ARDataSource", "New markers, updating markers. new markers="+markers.toString());
    	for(Marker marker : markers) {
    	    if (!MARKER_LIST.containsKey(marker.getName())) {
    	        marker.calcRelativePosition(ARDataSource.getCurrentLocation());
    	        MARKER_LIST.put(marker.getName(),marker);
    	    }
    	}

    	if (COMPUTING.compareAndSet(false, true)) {
    	    Log.v("ARDataSource", "Setting DIRTY flag!");
    	    CACHE.clear();
    	}
    }
    
    
    
    
    private static void onLocationChanged(Location location) {
        for(Marker ma: MARKER_LIST.values()) {
            ma.calcRelativePosition(location);
        }

        if (COMPUTING.compareAndSet(false, true)) {
            Log.v("ARDataSource", "Setting DIRTY flag!");
            CACHE.clear();
        }
    }
    
    
    /**
     * M�todo para filtrar los datos que se muestran en pantalla seg�n la distancia m�xima
     * @param newMaxDistance Distancia m�xima a la que pueden estar los markers que se muestren en pantalla
     */
    public static void updateDataWithMaxDistance(float newMaxDistance) {
		Log.d("ARDATA","se cambia la maxdistance a "+newMaxDistance);
        //float zoomLevel = calcZoomLevel();
        ARDataSource.setRadius(newMaxDistance);
        ARDataSource.setZoomLevel(FORMAT.format(newMaxDistance));
        //ARDataSource.setZoomProgress(myZoomBar.getProgress());
    }

}
