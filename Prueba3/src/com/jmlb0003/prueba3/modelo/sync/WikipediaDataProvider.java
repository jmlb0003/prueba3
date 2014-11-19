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
 * Clase que representa a Wikipedia como proveedor de recursos descargables para crear PIs en la
 * app.
 * @author Jose
 *
 */
public class WikipediaDataProvider extends NetworkDataProvider {
	private static final String BASE_URL = "http://api.geonames.org/findNearbyWikipediaJSON";
	private static final String LOG_TAG = "WikipediaProvider";
	

    
    /**
     * Método que crea la URL personalizada para el proveedor Wikipedia
     */
    @Override
	protected String createRequestURL(double lat, double lon, double alt, float radius, 
				String locale, String username) {
    	//La opción gratuita de esta API no permite consultas con radius mayor que 20
    	radius = (radius>20)?20.0f:radius;
    	//TODO: Como se podrían parametrizar los idiomas... (locale)
		return BASE_URL+
				"?lat=" + lat +
				"&lng=" + lon +
				//La opción gratuita de esta API no permite consultas con radius mayor que 20
				"&radius=" + radius +
				"&maxRows=500" +
				"&lang=" + locale+
				"&username=" + username;
//http://api.geonames.org/findNearbyWikipediaJSON?lat=37.6759861&lng=-3.5661972&radius=15&maxRows=15&lang=es&username=jmlb0003
	}

	
	
	/**
	 * Método para interpretar el JSON que se obtiene del proveedor wikipedia para convertirlo en 
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
        final String WIKI_ROOT = "geonames";
        final String WIKI_NAME = "title";
        final String WIKI_DESC = "summary";
        final String WIKI_WEB = "wikipediaUrl";
        final String WIKI_LATITUDE = "lat";
        final String WIKI_LONGITUDE = "lng";
        final String WIKI_ALTITUDE = "elevation";
        final String WIKI_IMAGE = "thumbnailImg";
        
        
        try {
            if (!poisInJson.has(WIKI_ROOT)) {
            	return null;
            }

            JSONArray poisArray = poisInJson.getJSONArray(WIKI_ROOT);
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
				int openTime, closeTime;
				int maxAge, minAge;
				
				
				JSONObject jo = poisArray.getJSONObject(i);

		        if (jo != null && jo.has(WIKI_NAME) && jo.has(WIKI_LATITUDE) && 
		        		jo.has(WIKI_LONGITUDE) && jo.has(WIKI_ALTITUDE) ) {
		        	name = jo.getString(WIKI_NAME);
		        	userID = PoiContract.PoiEntry.WIKIPEDIA_PROVIDER;
		        	color = PoiContract.PoiEntry.WIKIPEDIA_COLOR;
		        	description = jo.getString(WIKI_DESC);
		        	webSite = jo.getString(WIKI_WEB);
		        	lat = new BigDecimal(jo.getDouble(WIKI_LATITUDE))
		        				.setScale(6,BigDecimal.ROUND_FLOOR).doubleValue();
		        	lon = new BigDecimal(jo.getDouble(WIKI_LONGITUDE))
		        				.setScale(6,BigDecimal.ROUND_FLOOR).doubleValue();
		        	alt = jo.getDouble(WIKI_ALTITUDE);
		        	price = 0;
		        	openTime = 0;
		        	closeTime = 0;
		        	maxAge = 0;
		        	minAge = 0;
		        	
		        	if (jo.has(WIKI_IMAGE)) {
		        		image = jo.getString(WIKI_IMAGE);
		        	}else{
		        		image = PoiContract.PoiEntry.WIKIPEDIA_DEFAULT_IMAGE;
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