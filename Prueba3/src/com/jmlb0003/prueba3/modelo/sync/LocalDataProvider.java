package com.jmlb0003.prueba3.modelo.sync;


import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.ContentValues;
import android.graphics.Color;
import android.util.Log;

import com.jmlb0003.prueba3.modelo.data.PoiContract;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;


/**
 * Clase encargada de proporcionar los PIs que están almacenados en el dispositivo
 * @author Jose
 *
 */
public class LocalDataProvider extends NetworkDataProvider {
	private static final String LOG_TAG = "LocalDataProvider";
    

    
    /*
    public List<Poi> getPois() {
    	Map<String, Object> datos = new HashMap<String, Object>();
    	datos.put(DetallesPI.DETALLESPI_ID_POI, 0);
    	datos.put(DetallesPI.DETALLESPI_COLOR, Color.DKGRAY);
    	datos.put(DetallesPI.DETALLESPI_IMAGE, sIcon);
    	datos.put(DetallesPI.DETALLESPI_DESCRIPTION, "Este es el PI alternativo que está situado en Nueva York");
    	datos.put(DetallesPI.DETALLESPI_WEBSITE, "www.ninguno.com");
    	datos.put(DetallesPI.DETALLESPI_PRICE, 0);
    	datos.put(DetallesPI.DETALLESPI_OPEN_HOURS, "00:00");
    	datos.put(DetallesPI.DETALLESPI_CLOSE_HOURS, "01:00");
    	datos.put(DetallesPI.DETALLESPI_MAX_AGE, 43);
    	datos.put(DetallesPI.DETALLESPI_MIN_AGE, 3);
    	    	
    	Poi atl = new Poi("ATL", 39.931269, -75.051261, 0, new DetallesPI(datos), sIcon, sSelectedIcon);
        cachedPois.add(atl);
        
        datos.put(DetallesPI.DETALLESPI_ID_POI, 1);
    	datos.put(DetallesPI.DETALLESPI_COLOR, Color.YELLOW);
    	datos.put(DetallesPI.DETALLESPI_IMAGE, sIcon);
    	datos.put(DetallesPI.DETALLESPI_DESCRIPTION, "Este es mi casa. Vivo en Cambil...Y esta es la descripción más larga que voy a poner");
    	datos.put(DetallesPI.DETALLESPI_WEBSITE, "joselopez.hol.es");
    	datos.put(DetallesPI.DETALLESPI_PRICE, 10);
    	datos.put(DetallesPI.DETALLESPI_OPEN_HOURS, "00:00");
    	datos.put(DetallesPI.DETALLESPI_CLOSE_HOURS, "23:00");
    	datos.put(DetallesPI.DETALLESPI_MAX_AGE, 25);
    	datos.put(DetallesPI.DETALLESPI_MIN_AGE, 1);

        Poi home = new Poi("Casa", 37.6759861, -3.5661972, 763.0, new DetallesPI(datos));
        cachedPois.add(home);

        
        datos = new HashMap<String, Object>();
        datos.put("ID", 2);
    	datos.put("color", Color.DKGRAY);
    	datos.put("imagen", sIcon);
    	datos.put("descripcion", "Este es el pico de Mágina. Tiene 2300 metros o así y es donde compruebo si apuntan bien los pinchicos");
    	datos.put("sitio_web", "joselopez.hol.es");
    	datos.put("precio", 10);
    	datos.put("horario_apertura", "00:00");
    	datos.put("horario_cierre", "00:00");
    	datos.put("edad_maxima", 69);
    	datos.put("edad_minima", 1);
    	
        Poi picoMagina = new Poi("Magina", 37.725048, -3.466663, 2132.0, new DetallesPI(datos), sIcon, sSelectedIcon);
        cachedPois.add(picoMagina);
        
        
        datos = new HashMap<String, Object>();
        datos.put("ID", 3);
    	datos.put("color", Color.DKGRAY);
    	datos.put("imagen", sSelectedIcon);
    	datos.put("descripcion", "Esto es donde está mi cortijo");
    	datos.put("sitio_web", "fiestas de la loma.com");
    	datos.put("precio", 10);
    	datos.put("horario_apertura", "00:00");
    	datos.put("horario_cierre", "00:00");
    	datos.put("edad_maxima", 69);
    	datos.put("edad_minima", 1);
    	
        Poi cortijo = new Poi("cortijo", 37.692997,-3.565028, 912.0, new DetallesPI(datos), sIcon, sSelectedIcon);
        cachedPois.add(cortijo);
        
        datos = new HashMap<String, Object>();
        datos.put("ID", 4);
    	datos.put("color", Color.DKGRAY);
    	datos.put("imagen", sSelectedIcon);
    	datos.put("descripcion", "Esto es donde vive Blas...El día que me cabree, en esa dirección vive");
    	datos.put("sitio_web", "www.aBlasLeGustaLaFiesta.es");
    	datos.put("precio", 123);
    	datos.put("horario_apertura", "22:00");
    	datos.put("horario_cierre", "08:00");
    	datos.put("edad_maxima", 34);
    	datos.put("edad_minima", 10);
    	
        Poi carcheles = new Poi("carcheles", 37.644594, -3.638578, 825.0, new DetallesPI(datos), sIcon, sSelectedIcon);
        cachedPois.add(carcheles);
        
        
        return cachedPois;
    }*/


/*
	protected ArrayList<ContentValues> fetchData() {
		ArrayList<ContentValues> toRet = new ArrayList<>();
		ContentValues poiValues = new ContentValues();
		

		poiValues.put(PoiEntry.COLUMN_POI_NAME, "Casa");
    	poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.6759861).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.5661972).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 763.0);
        
    	poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, Color.YELLOW);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, "");
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Este es mi casa. Vivo en Cambil...Y esta es la descripción más larga que voy a poner");
        poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "joselopez.hol.es");	                
        poiValues.put(PoiEntry.COLUMN_POI_PRICE, 10);
        poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "00:00");
        poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "23:00");
        poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 25);
        poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 1);
        
        toRet.add(poiValues);

        
        
        poiValues = new ContentValues();
        poiValues.put(PoiEntry.COLUMN_POI_NAME, "cortijo");
        poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.692997).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.565028).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 912.0);
        
        poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, Color.DKGRAY);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, "");
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Esto es donde está mi cortijo");
    	poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "fiestas de la loma.com");
    	poiValues.put(PoiEntry.COLUMN_POI_PRICE, 10);
    	poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "00:00");
    	poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "00:00");
    	poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 69);
    	poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 1);

    	toRet.add(poiValues);
    	

    	poiValues = new ContentValues();
    	poiValues.put(PoiEntry.COLUMN_POI_NAME, "carcheles");
    	poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.644594).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.638578).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 825.0);
        
        poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, Color.DKGRAY);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, "");
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Esto es donde vive Blas...El día que me cabree, en esa dirección vive");
    	poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "www.aBlasLeGustaLaFiesta.es");
    	poiValues.put(PoiEntry.COLUMN_POI_PRICE, 123);
    	poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "22:00");
    	poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "08:00");
    	poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 34);
    	poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 10);
    	
    	
    	toRet.add(poiValues);

        
        
        return toRet;
	}*/


	@Override
	protected String createRequestURL(double latitude, double longitude,
			double altitude, float radius, String locale, String username) {
		return "http://api.geonames.org/findNearbyWikipediaJSON?lat=37.6759861&lng=-3.5661972&radius=15&maxRows=1&lang=es&username=jmlb0003";
	}


	@Override
	protected ArrayList<ContentValues> getDataFromJSON(JSONObject root) {
		Log.d(LOG_TAG,"estamos con un json pa local");
		ArrayList<ContentValues> toRet = new ArrayList<>();
		ContentValues poiValues = new ContentValues();
		

		poiValues.put(PoiEntry.COLUMN_POI_NAME, "Casa");
    	poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.6759861).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.5661972).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 763.0);
        
    	poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, Color.YELLOW);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, "");
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Este es mi casa. Vivo en Cambil...Y esta es la descripción más larga que voy a poner");
        poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "joselopez.hol.es");	                
        poiValues.put(PoiEntry.COLUMN_POI_PRICE, 10);
        poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "00:00");
        poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "23:00");
        poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 25);
        poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 1);
        
        toRet.add(poiValues);

        
        
        poiValues = new ContentValues();
        poiValues.put(PoiEntry.COLUMN_POI_NAME, "cortijo");
        poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.692997).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.565028).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 912.0);
        
        poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, Color.DKGRAY);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, "");
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Esto es donde está mi cortijo");
    	poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "fiestas de la loma.com");
    	poiValues.put(PoiEntry.COLUMN_POI_PRICE, 10);
    	poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "00:00");
    	poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "00:00");
    	poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 69);
    	poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 1);

    	toRet.add(poiValues);
    	

    	poiValues = new ContentValues();
    	poiValues.put(PoiEntry.COLUMN_POI_NAME, "carcheles");
    	poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.644594).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.638578).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 825.0);
        
        poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, Color.DKGRAY);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, "");
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Esto es donde vive Blas...El día que me cabree, en esa dirección vive");
    	poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "www.aBlasLeGustaLaFiesta.es");
    	poiValues.put(PoiEntry.COLUMN_POI_PRICE, 123);
    	poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "22:00");
    	poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "08:00");
    	poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 34);
    	poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 10);
    	
    	
    	toRet.add(poiValues);

        
        
        return toRet;
	}
}
