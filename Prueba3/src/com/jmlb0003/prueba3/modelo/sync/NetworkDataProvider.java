package com.jmlb0003.prueba3.modelo.sync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.util.Log;

import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.modelo.sync.Excepciones.NDPConectionException;


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
    /**Idiomas soportados por la app**/
    protected static final ArrayList<String> SUPPORTED_LANGUAGES = new ArrayList<>();
    
    
    NetworkDataProvider() {
		SUPPORTED_LANGUAGES.add("es");
		SUPPORTED_LANGUAGES.add("en");
		SUPPORTED_LANGUAGES.add("fr");
		SUPPORTED_LANGUAGES.add("de");
		SUPPORTED_LANGUAGES.add("it");
		SUPPORTED_LANGUAGES.add("nl");
		SUPPORTED_LANGUAGES.add("pl");
		SUPPORTED_LANGUAGES.add("pt");
	}

   
    
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
     * @throws NDPConectionException Se lanza esta excepción si ocurre algún problema durante la 
     * 	conexión y la obtención de los datos del proveedor.
     */
    private static InputStream getHttpGETInputStream(String urlStr) throws NDPConectionException {
        if (urlStr == null) {
        	return null;
        }

        InputStream is = null;
        URLConnection conn = null;

        try {
            URL url = new URL(urlStr);
            conn = url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECT_TIMEOUT);

            try {
				is = conn.getInputStream();
			} catch (IOException ex) {
				throw new NDPConectionException("Imposible extraer datos del servidor");
			}

            return is;
            
        } catch (MalformedURLException ex) {
        	throw new NDPConectionException("URL no válida");
        } catch (Exception ex) {
            try {
                is.close();
            } catch (Exception e) {
                // Ignore
            }
            try {
                if (conn instanceof HttpURLConnection) {
                	((HttpURLConnection) conn).disconnect();
                }
            } catch (Exception e) {
                // Ignore
            }
            throw new NDPConectionException("Imposible crear un inputStream con la url: '" + 
            		urlStr + "'");
        }
    }
    
    

    /**
     * Método para convertir un objeto InputStream en una cadena de caracteres para su posterior 
     * interpretación
     * @param is	Objeto InputStream que se va a convertir en cadena de caracteres
     * @return Cadena de caracteres con el contenido del objeto InputStream
     */
    private String getHttpInputString(InputStream is) {
        if (is == null) {
        	return null;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8 * 1024);
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            
        } catch (IOException e) {
        	Log.e(LOG_TAG, "Error interpretando el stream");
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
     * @throws NullPointerException Se lanza una excepción si falla la conexión con el servidor
     * @throws NDPConectionException 
     */
    public ArrayList<ContentValues> fetchData() 
    		throws NullPointerException, NDPConectionException {
    	
    	String locale = (SUPPORTED_LANGUAGES.contains(Locale.getDefault().getLanguage())) ?
    			Locale.getDefault().getLanguage() : "en";
    	

    	String url = createRequestURL(ARDataSource.getCurrentLocation().getLatitude(), 
    			ARDataSource.getCurrentLocation().getLongitude(), 
    			ARDataSource.getCurrentLocation().getAltitude(), 
    			ARDataSource.MAX_RADIUS, locale, "jmlb0003");
    	
    	
        if (url == null) {	//Error, no hay URL
        	throw new NullPointerException();
        	
        } else {
	        InputStream stream = null;
	        stream = getHttpGETInputStream(url);
	        if (stream == null) {	//Error, no hay stream
	        	throw new NullPointerException();
	        	
	        } else {
		        Log.d(LOG_TAG,"downTask network 2 con stream:\n"+stream);
		        String string = null;
		        string = getHttpInputString(stream);
		        if (string == null) {	//Error, no hay cadena en el stream
		        	throw new NullPointerException();
		        	
		        } else {		
			        JSONObject json = null;
			        try {
			            json = new JSONObject(string);
			        } catch (JSONException ex) {
			            ex.printStackTrace();
			        }Log.d(LOG_TAG,"downTask network 4");
			        if (json == null) {		//Error, no hay json en el stream
			        	throw new NullPointerException();
			        } else {
				        Log.d(LOG_TAG,"downTask network 5");
				        return getDataFromJSON(json);
			        }
		        }
	        }
        }
    }
    
    
    
}
