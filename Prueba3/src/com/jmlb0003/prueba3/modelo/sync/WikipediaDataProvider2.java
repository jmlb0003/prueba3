
package com.jmlb0003.prueba3.modelo.sync;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.DetallesPoi;
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.modelo.data.PoiContract;



/**
 * Clase que representa a Wikipedia como proveedor de recursos descargables
 * @author Jose
 *
 */
public class WikipediaDataProvider2 extends NetworkDataProvider2 {
	//TODO:El enlace de wikipedia está mal a caso hecho
	private static final String BASE_URL = "http://api.geonames.org/findNearbyWikipediaJSON";

	private static Bitmap sIcon = null;
	private static Bitmap sSelectedIcon = null;
	private static Bitmap sWikipediaIcon = null; 
	
	
	/**
	 * Constructor de la clase para poder descargar recursos geolocalizados de Wikipedia
	 * @param res
	 */
	public WikipediaDataProvider2(Resources res) {        
	    if (res == null) {
	    	throw new NullPointerException();
	    }
        
        createIcon(res);
    }

    private void createIcon(Resources res) {
        if (res == null) {
        	throw new NullPointerException();
        }
        
        
        sIcon = BitmapFactory.decodeResource(res, R.drawable.icono_pi);
        sSelectedIcon = BitmapFactory.decodeResource(res, R.drawable.icono_pi_seleccionado);
        sWikipediaIcon = BitmapFactory.decodeResource(res, R.drawable.wikipedia);
    }

    
    /**
     * Método que crea la URL personalizada para el proveedor Wikipedia
     */
	@Override
	public String createRequestURL(double lat, double lon, double alt, float radius, String locale, String mUsername) {
		//La opción gratuita de esta API no permite consultas con radius mayor que 20
		radius = (radius>20)?20.0f:radius;
		
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
	 * Método para interpretar el JSON que se obtiene del proveedor wikipedia para convertirlo en una lista de PIs
	 */
	@Override
	public List<Poi> parse(JSONObject root) {
		if (root == null) {
			return null;
		}
		
		JSONObject jo = null;
		JSONArray dataArray = null;
    	List<Poi> pois = new ArrayList<Poi>();
		try {
			if(root.has("geonames")) {
				dataArray = root.getJSONArray("geonames");
			}
			
			if (dataArray == null) 
				return pois;
			
			int top = Math.min(MAX_RESOURCES_NUMBER, dataArray.length());
			for (int i = 0; i < top; i++) {		
				jo = dataArray.getJSONObject(i);
				Poi ma = processJSONObject(jo);
				if(ma!=null) {
					pois.add(ma);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return pois;
	}
	
	
	private Poi processJSONObject(JSONObject jo) {

		if (jo == null) {
			return null;
		}
		
        Poi ma = null;
        Map<String, Object> datos;
        
        if ( jo.has("title") && jo.has("lat") && jo.has("lng") && jo.has("elevation") ) {
        	try {
        		datos = new HashMap<String, Object>();
        		datos.put("ID", -1);
        		datos.put("id_usuario", PoiContract.PoiEntry.WIKIPEDIA_PROVIDER);
            	datos.put("color", Color.WHITE);
            	datos.put("imagen", sWikipediaIcon);
            	datos.put("descripcion", jo.get("summary"));
            	datos.put("sitio_web", jo.get("wikipediaUrl"));
            	datos.put("precio", 0);
            	datos.put("horario_apertura", "00:00");
            	datos.put("horario_cierre", "00:00");
            	datos.put("edad_maxima", 0);
            	datos.put("edad_minima", 0);
            	
        		ma = new Poi(jo.getString("title"), jo.getDouble("lat"), jo.getDouble("lng"),
        							jo.getDouble("elevation"), new DetallesPoi(datos), sIcon, sSelectedIcon);
        		Log.d("wikipediaDatasource","Creado un Poi de wikipedia");
        	} catch (JSONException e) {
        		e.printStackTrace();
        	}
        }
        
        
        return ma;
	}
}