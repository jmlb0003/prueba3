package com.jmlb0003.prueba3.modelo;



import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jmlb0003.prueba3.R;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;



/**
 * Clase que representa a Wikipedia como proveedor de recursos descargables
 * @author Jose
 *
 */
public class WikipediaDataProvider extends NetworkDataProvider {
	private static final String BASE_URL = "http://api.geonames.org/findNearbyWikipediaJSON";

	private static Bitmap mIcon = null;
	
	
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
        if (res == null) {
        	throw new NullPointerException();
        }
        
        
        mIcon = BitmapFactory.decodeResource(res, R.drawable.wikipedia);
    }

    
    /**
     * Método que crea la URL personalizada para el proveedor Wikipedia
     */
	@Override
	public String createRequestURL(double lat, double lon, double alt, float radius, String locale, String mUsername) {
		
		return BASE_URL+
				"?lat=" + lat +
				"&lng=" + lon +
				"&radius="+ radius +
				"&maxRows=20" +
				"&lang=" + locale+
				"&username="+mUsername;
//http://api.geonames.org/findNearbyWikipediaJSON?lat=37.6759861&lng=-3.5661972&radius=15&maxRows=15&lang=es&username=jmlb0003
	}

	
	/**
	 * Método para interpretar el JSON que se obtiene del proveedor wikipedia para convertirlo en una lista de markers
	 */
	@Override
	public List<Marker> parse(JSONObject root) {
		if (root == null) {
			return null;
		}
		
		JSONObject jo = null;
		JSONArray dataArray = null;
    	List<Marker> markers = new ArrayList<Marker>();
		try {
			if(root.has("geonames")) {
				dataArray = root.getJSONArray("geonames");
			}
			
			if (dataArray == null) 
				return markers;
			
			int top = Math.min(MAX_RESOURCES_NUMBER, dataArray.length());
			for (int i = 0; i < top; i++) {		
				jo = dataArray.getJSONObject(i);
				Marker ma = processJSONObject(jo);
				if(ma!=null) {
					markers.add(ma);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return markers;
	}
	
	
	private Marker processJSONObject(JSONObject jo) {

		if (jo == null) {
			return null;
		}
		
        Marker ma = null;
        
        if ( jo.has("title") && jo.has("lat") && jo.has("lng") && jo.has("elevation") ) {
        	try {
        		ma = new Marker(jo.getString("title"), jo.getDouble("lat"), jo.getDouble("lng"),
        							jo.getDouble("elevation"), Color.WHITE,	mIcon);
        		
        	} catch (JSONException e) {
        		e.printStackTrace();
        	}
        }
        
        
        return ma;
	}
}