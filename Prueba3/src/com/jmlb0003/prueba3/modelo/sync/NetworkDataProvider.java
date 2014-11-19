package com.jmlb0003.prueba3.modelo.sync;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.jmlb0003.prueba3.controlador.ARDataSource;

import android.content.ContentValues;
import android.util.Log;


/**
 * Clase que representa a un proveedor de PIs en Internet.
 * @author Jose
 *
 */
public abstract class NetworkDataProvider extends DataProvider {

	private static final String LOG_TAG = "NetworkDataProvider";
	
	/** Número máximo de resultados que se descargarán del proveedor**/
    protected static final int MAX_RESOURCES_NUMBER = 100;
    /** Tiempo máximo en milisegundos para lectura de los datos de este proveedor**/
    protected static final int READ_TIMEOUT = 10000;
    /** Tiempo máximo de conexión al proveedor de recursos**/
    protected static final int CONNECT_TIMEOUT = 10000;

   
    
	protected abstract String createRequestURL(double latitude, double longitude, double altitude,
			float radius, String locale, String username);
	
	
	/**
     * Método para interpretar los datos descargados del proveedor y convertirlos en PIs
     * @param root Objeto JSON que se va a interpretar
     * @return Lista de PIs creados a partir del JSON inicial
     */
    protected abstract ArrayList<ContentValues> getDataFromJSON(JSONObject root);
	
    
    
    /**
     * Método para obtener los recursos del proveedor a partir de la URL personalizada
     * @param urlStr  URL con la que se van a obtener los recursos del proveedor
     * @return	Objeto InputStream que contiene el resultado del proveedor con la URL
     */
    private static InputStream getHttpGETInputStream(String urlStr) {
        if (urlStr == null) {
        	throw new NullPointerException();
        }

        InputStream is = null;
        URLConnection conn = null;

        try {
            if (urlStr.startsWith("file://")) {
            	return new FileInputStream(urlStr.replace("file://", ""));
            }

            URL url = new URL(urlStr);
            conn = url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);

            is = conn.getInputStream();

            return is;
        } catch (Exception ex) {
        	Log.d(LOG_TAG,"Error al intentar crear un inputStream con la url:"+urlStr);
            try {
                is.close();
            } catch (Exception e) {
                // Ignore
            }
            try {
                if (conn instanceof HttpURLConnection)
                    ((HttpURLConnection) conn).disconnect();
            } catch (Exception e) {
                // Ignore
            }
            ex.printStackTrace();
        }

        return null;
    }
    
    

    /**
     * Método para convertir un objeto InputStream en una cadena de caracteres para su posterior 
     * interpretación
     * @param is	Objeto InputStream que se va a convertir en cadena de caracteres
     * @return Cadena de caracteres con el contenido del objeto InputStream
     */
    private String getHttpInputString(InputStream is) {
        if (is == null) {
        	throw new NullPointerException();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8 * 1024);
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
        return sb.toString();
    }
    
    
    
    /**
     * Método para convertir en PIs los recursos descargados del proveedor a partir de la URL
     * personalizada que se pasa como parámetro
     * @param url URL personalizada con la que se descargarán los datos del proveedor
     * @return Lista con los datos (de PIs) obtenidos del proveedor
     */
    public ArrayList<ContentValues> fetchData() {

    	String url = createRequestURL(ARDataSource.getCurrentLocation().getLatitude(), 
    			ARDataSource.getCurrentLocation().getLongitude(), 
    			ARDataSource.getCurrentLocation().getAltitude(), 
    			ARDataSource.MAX_RADIUS, "es", "jmlb0003");
    	
    	
        if (url == null) {
        	throw new NullPointerException();
        }

        InputStream stream = null;
        stream = getHttpGETInputStream(url);
        if (stream == null) {
        	throw new NullPointerException();
        }

        String string = null;
        string = getHttpInputString(stream);
        if (string == null) {
        	throw new NullPointerException();
        }

        JSONObject json = null;
        try {
            json = new JSONObject(string);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (json == null) {
        	throw new NullPointerException();
        }


        return getDataFromJSON(json);
    }
    
    
    
}
