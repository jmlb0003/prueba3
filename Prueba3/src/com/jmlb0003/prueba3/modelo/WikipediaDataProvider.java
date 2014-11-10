package com.jmlb0003.prueba3.modelo;



import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.controlador.NetworkDataProvider;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;



/**
 * Clase que representa a Wikipedia como proveedor de recursos descargables
 * @author Jose
 *
 */
public class WikipediaDataProvider extends NetworkDataProvider {
	private static final String BASE_URL = "http://api.geonames.org/findNearbyWikipediaJSON";

	private static Bitmap sIcon = null;
	private static Bitmap sSelectedIcon = null;
	private static Bitmap sWikipediaIcon = null;


	
	
	/**
	 * Constructor de la clase para poder descargar recursos geolocalizados de Wikipedia
	 * @param res
	 */
	public WikipediaDataProvider(Resources res) {
		if (res == null) {
	    	throw new NullPointerException();
	    }
        
        createIcon(res);
	}

    private void createIcon(Resources res) {
        sIcon = BitmapFactory.decodeResource(res, R.drawable.icono_pi);
        sSelectedIcon = BitmapFactory.decodeResource(res, R.drawable.icono_pi_seleccionado);
        sWikipediaIcon = BitmapFactory.decodeResource(res, R.drawable.wikipedia);
    }

    
    /**
     * Método que crea la URL personalizada para el proveedor Wikipedia
     */
    @Override
	protected String createRequestURL(double lat, double lon, double alt, float radius, 
				String locale, String username) {
		
		return BASE_URL+
				"?lat=" + lat +
				"&lng=" + lon +
				//La opción gratuita de esta API no permite consultas con radius mayor que 20
				"&radius=" + radius +
				"&maxRows=20" +
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
//				int userID;
				String image;
				String description;
				String webSite;
				double lat,lon,alt;
				float price;
				int openTime, closeTime;
				int maxAge, minAge;
				
				
				JSONObject jo = poisArray.getJSONObject(i);

		        if (jo != null && jo.has("title") && jo.has("lat") && jo.has("lng") && jo.has("elevation") ) {
//		        	id = jo.getInt(WIKI_ID);
		        	name = jo.getString(WIKI_NAME);
//		        	color = jo.getInt(WIKI_COLOR);
		        	color = Color.WHITE;
//		        	image = jo.getString(WIKI_IMAGE);
		        	//Adaptar a la BD que llevaría el String...
		        	image = "android.resource://com.jmlb0003.prueba3/drawable/wikipedia.png";
		        	description = jo.getString(WIKI_DESC);
		        	webSite = jo.getString(WIKI_WEB);
		        	lat = jo.getDouble(WIKI_LATITUDE);
		        	lon = jo.getDouble(WIKI_LONGITUDE);
		        	alt = jo.getDouble(WIKI_ALTITUDE);
		        	price = 0;
		        	openTime = 0;
		        	closeTime = 0;
		        	maxAge = 0;
		        	minAge = 0;

		        	ContentValues poiValues = new ContentValues();

		        	poiValues.put(PoiEntry.COLUMN_POI_NAME, name);
		        	poiValues.put(PoiEntry.COLUMN_POI_COLOR, color);
		        	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, image);
		        	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, description);
	                poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, webSite);
	                poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, lat);
	                poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE, lon);
	                poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, alt);
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