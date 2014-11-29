package com.jmlb0003.prueba3.modelo.sync;


import java.math.BigDecimal;
import java.util.ArrayList;

import android.content.ContentValues;
import android.util.Log;

import com.jmlb0003.prueba3.modelo.data.PoiContract;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;


/**
 * Clase encargada de proporcionar los PIs que están almacenados en el dispositivo
 * @author Jose
 *
 */
public class LocalDataProvider extends DataProvider {
	private static final String LOG_TAG = "LocalDataProvider";


	@Override
	public ArrayList<ContentValues> fetchData() {
		Log.d(LOG_TAG,"entrando en FetchData local");
		
		ArrayList<ContentValues> toRet = new ArrayList<>();
		ContentValues poiValues = new ContentValues();
		

		poiValues.put(PoiEntry.COLUMN_POI_NAME, "Casa");
    	poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.6759861).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.5661972).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 763.0);
        
    	poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, PoiContract.PoiEntry.LOCAL_COLOR);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, PoiContract.PoiEntry.LOCAL_DEFAULT_IMAGE);
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Este es mi casa. Vivo en Cambil...Y esta es la descripción más larga que voy a poner");
        poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "joselopez.hol.es");	                
        poiValues.put(PoiEntry.COLUMN_POI_PRICE, 10);
        poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "00:00");
        poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "23:00");
        poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 25);
        poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 1);
        
        toRet.add(poiValues);

        Log.d(LOG_TAG,"1");
        
        poiValues = new ContentValues();
        poiValues.put(PoiEntry.COLUMN_POI_NAME, "cortijo");
        poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.692997).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.565028).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 912.0);
        
        poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, PoiContract.PoiEntry.LOCAL_COLOR);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, PoiContract.PoiEntry.LOCAL_DEFAULT_IMAGE);
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Esto es donde está mi cortijo");
    	poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "fiestas de la loma.com");
    	poiValues.put(PoiEntry.COLUMN_POI_PRICE, 10);
    	poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "00:00");
    	poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "00:00");
    	poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 69);
    	poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 1);

    	toRet.add(poiValues);
    	Log.d(LOG_TAG,"2");

    	poiValues = new ContentValues();
    	poiValues.put(PoiEntry.COLUMN_POI_NAME, "carcheles");
    	poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.644594).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.638578).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 825.0);
        
        poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, PoiContract.PoiEntry.LOCAL_COLOR);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, PoiContract.PoiEntry.LOCAL_DEFAULT_IMAGE);
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Esto es donde vive Blas...El día que me cabree, en esa dirección vive");
    	poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "www.aBlasLeGustaLaFiesta.es");
    	poiValues.put(PoiEntry.COLUMN_POI_PRICE, 123);
    	poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "22:00");
    	poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "08:00");
    	poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 34);
    	poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 10);
    	
    	
    	toRet.add(poiValues);

    	Log.d(LOG_TAG,"3");
    	
    	
    	poiValues = new ContentValues();
    	poiValues.put(PoiEntry.COLUMN_POI_NAME, "Mágina");
    	poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, 
    			new BigDecimal(37.725685).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE,
        		new BigDecimal(-3.467162).setScale(6,BigDecimal.ROUND_FLOOR).doubleValue());
        poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 2139.0);
        
        poiValues.put(PoiEntry.COLUMN_POI_USER_ID, PoiContract.PoiEntry.LOCAL_PROVIDER);
    	poiValues.put(PoiEntry.COLUMN_POI_COLOR, PoiContract.PoiEntry.LOCAL_COLOR);
    	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, PoiContract.PoiEntry.LOCAL_DEFAULT_IMAGE);
    	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Pico de Mágina");
    	poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, "");
    	poiValues.put(PoiEntry.COLUMN_POI_PRICE, 123);
    	poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, "22:00");
    	poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, "08:00");
    	poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 34);
    	poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 10);
    	
    	
    	toRet.add(poiValues);

    	Log.d(LOG_TAG,"4 y fin");
        
        return toRet;
	}
}
