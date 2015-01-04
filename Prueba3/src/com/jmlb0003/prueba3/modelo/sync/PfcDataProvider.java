package com.jmlb0003.prueba3.modelo.sync;


import java.math.BigDecimal;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import com.jmlb0003.prueba3.modelo.data.PoiContract;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;



/**
 * Clase que representa al servidor propio como proveedor de recursos descargables para
 * crear PIs en la app.
 * @author Jose
 *
 */
public class PfcDataProvider extends NetworkDataProvider {
	private static final String BASE_URL = "http://192.168.1.104:8080/pfc_api/";
//	private static final String BASE_URL = "http://192.168.0.201:8080/pfc_api/";
	private static final String LOG_TAG = "PFCProvider";
	

    
    /**
     * Método que crea la URL personalizada para el proveedor creado como servidor propio
     */
    @Override
	protected String createRequestURL(double lat, double lon, double alt, float radius, 
				String locale, String username) {

    	radius = (radius>6)?6.0f:radius;	//Radio máximo de 6

		return BASE_URL + "pois_by_location/" +
				"?lat=" + lat +
				"&lon=" + lon +
				"&dist=" + radius;
//http://192.168.1.104:8080/pfc_api/pois_by_location/?lat=37.6269863&lon=-3.538161&dist=6
	}

	
	
	/**
	 * Método para interpretar el JSON que se obtiene del proveedor para convertirlo en 
	 * una lista de PIs. Si hay algún fallo en el proceso, se devolverá null.
	 * @param poisInJson Cadena que contiene el objeto en formato JSON
	 * @return Lista de objetos ContentValues con los pares clave-valor que contienen todos los 
	 * 		atributos los PIs obtenidos del proveedor
	 */
	@Override
	protected ArrayList<ContentValues> getDataFromJSON(JSONObject poisInJson) {
		// Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // Estos son los nombres de las variables dentro del JSON
        final String PFC_ROOT = "api_pfc";
        final String PFC_NAME = "Nombre";
        final String PFC_DESC = "Descripcion";
        final String PFC_WEB = "Sitio_web";
        final String PFC_LATITUDE = "Latitud";
        final String PFC_LONGITUDE = "Longitud";
        final String PFC_ALTITUDE = "Altitud";
        final String PFC_IMAGE = "Multimedia";


        try {
            if (!poisInJson.has(PFC_ROOT)) {
            	return null;
            }

            JSONArray poisArray = poisInJson.getJSONArray(PFC_ROOT);
            if (poisArray == null) {
            	return null;
            }
            
            // Insert the new weather information into the database
            ArrayList<ContentValues> cVVector = new ArrayList<ContentValues>(poisArray.length());

            int top = Math.min(MAX_RESOURCES_NUMBER, poisArray.length());
			for (int i = 0; i < top; i++) {
				// Variables del PI para almacenar en la BD 
//				int id;
				int color;
				String name;
				long userID;
				String image;
				String description;
				String webSite;
				double lat,lon,alt;
				float price;
				String openTime, closeTime;
				int maxAge, minAge;


				JSONObject jo = poisArray.getJSONObject(i);

		        if (jo != null && jo.has(PFC_NAME) && jo.has(PFC_LATITUDE) && 
		        		jo.has(PFC_LONGITUDE) && jo.has(PFC_ALTITUDE) ) {
		        	name = jo.getString(PFC_NAME);
		        	userID = PoiContract.PoiEntry.UJA_PROVIDER;
		        	color = PoiContract.PoiEntry.UJA_COLOR;
		        	description = jo.getString(PFC_DESC);
		        	webSite = jo.getString(PFC_WEB);
		        	lat = new BigDecimal(jo.getDouble(PFC_LATITUDE))
		        				.setScale(6,BigDecimal.ROUND_FLOOR).doubleValue();
		        	lon = new BigDecimal(jo.getDouble(PFC_LONGITUDE))
		        				.setScale(6,BigDecimal.ROUND_FLOOR).doubleValue();
		        	alt = jo.getDouble(PFC_ALTITUDE);
		        	price = 0;
		        	openTime = "00:00";
		        	closeTime = "00:00";
		        	maxAge = 0;
		        	minAge = 0;

		        	if (jo.has(PFC_IMAGE)) {
		        		image = jo.getString(PFC_IMAGE);
		        	}else{
		        		image = PoiContract.PoiEntry.UJA_DEFAULT_IMAGE;
		        	}

		        	ContentValues poiValues = new ContentValues();

		        	poiValues.put(PoiEntry.COLUMN_POI_NAME, name);
		        	poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, lat);
	                poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE, lon);
	                poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, alt);
	                
		        	poiValues.put(PoiEntry.COLUMN_POI_USER_ID, userID);		        	
		        	poiValues.put(PoiEntry.COLUMN_POI_COLOR, color);
		        	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, image);
		        	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, description);
	                poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, webSite);	                
	                poiValues.put(PoiEntry.COLUMN_POI_PRICE, price);
	                poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, openTime);
	                poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, closeTime);
	                poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, maxAge);
	                poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, minAge);

	                cVVector.add(poiValues);
		        }else{
		        	continue;
		        }
			}

			return cVVector;
			
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
		return null;
	}
    
    
   
    
}