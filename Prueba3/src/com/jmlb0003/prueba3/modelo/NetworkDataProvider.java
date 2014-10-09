package com.jmlb0003.prueba3.modelo;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/**
 * Clase abstracta con los componentes básicos para crear un recurso descargable desde la red.
 * @author Jose
 *
 */
public abstract class NetworkDataProvider {
	/**Número máximo de resultados que se descargarán del proveedor**/
    protected static final int MAX_RESOURCES_NUMBER = 1000;
    /**Tiempo máximo en milisegundos para lectura de los datos de este proveedor**/
    protected static final int READ_TIMEOUT = 10000;
    /**Tiempo máximo de conexión al proveedor de recursos**/
    protected static final int CONNECT_TIMEOUT = 10000;
    /**Lista de markers descargados hasta el momento de este proveedor**/
    protected List<Marker> markersCache = null;
    
    /**
     * Método para crear la URL con la que se descargarán los recursos del proveedor
     * @param lat	Latitud de la posición actual del usuario
     * @param lon	Longitud de la posición actual del usuario
     * @param alt	Altitud de la posición actual del usuario
     * @param radius Distancia máxima de los resultados obtenidos del proveedor
     * @param locale Indica el idioma deseado para la descarga de los recursos del proveedor
     * @param mUsername	Usuario con el que se descargarán los recursos del proveedor
     * @return	Cadena de caracteres que contiene la url con la que se descargarán los datos
     */
    public abstract String createRequestURL(double lat, double lon, double alt,
                                            float radius, String locale, String mUsername);

    /**
     * Método para interpretar los datos descargados del proveedor y convertirlos en markers
     * @param root Objeto JSON que se va a interpretar
     * @return Lista de markers creados a partir del JSON inicial
     */
    public abstract List<Marker> parse(JSONObject root);

    
    /**
     * Devuelve la lista actual de markers de este proveedor
     */
    public List<Marker> getMarkers() {
        return markersCache;
    }
    
    
    /**
     * Método para obtener los recursos del proveedor a partir de la URL personalizada
     * @param urlStr  URL con la que se van a obtener los recursos del proveedor
     * @return	Objeto InputStream que contiene el resultado del proveedor con la URL
     */
    protected static InputStream getHttpGETInputStream(String urlStr) {
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
        	Log.d("NetWorkDataProvider","Error al intentar crear un inputStream con la url:"+urlStr);
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
    protected String getHttpInputString(InputStream is) {
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
     * Método para convertir en markers los recursos descargados del proveedor a partir de la URL
     * personalizada que se pasa como parámetro
     * @param url URL personalizada con la que se descargarán los datos del proveedor
     * @return Lista de markers obtenidos del proveedor a partir de la URL personalizada
     */
    public List<Marker> parse(String url) {
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
        
        return parse(json);
    }
}