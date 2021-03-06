package com.jmlb0003.prueba3.modelo.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.jmlb0003.prueba3.R;

import android.app.SearchManager;
import android.content.ContentUris;
import android.graphics.Color;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;



/**
 * En esta clase se definen las tablas y los nombres de las columnas para la BD de PIs
 *  
 * @author Jose
 */
public class PoiContract {
	
	/**
	 * El 'Content authority' es el nombre del dominio del content provider. Se recomienda 
	 * utilizar el nombre del paquete de la app buscando la unicidad en cualquier dispositivo
	 */
    public static final String CONTENT_AUTHORITY = "com.jmlb0003.prueba3";
    
  
    /**
     * Esta constante se usa para crear la base de todas las URIs del content provider
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);



    //Posibles rutas dentro del provider, que llevan a las tablas que contiene. 
    public static final String PATH_POIS = "poi";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_LOCATION_POI = "location_poi";
    
    public static final String POIS_BY_NAME = "by-name";
    public static final String POIS_BY_COORDS = "by-coords";


    /**
     * Formato que se usar� para las fechas que sea aceptado por la BD
     */
    public static final String DATE_FORMAT = "ddMMyyyy";
    
    
    /**
     * M�todo que convierte un objeto fecha en un string v�lido para almacenar dicha fecha en la BD.
     * @param date Fecha de entrada
     * @return una representaci�n de tipo String v�lida para la BD
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, java.util.Locale.getDefault());
        return sdf.format(date);
    }
    
    
    /**
     * M�todo que convierte una fecha de tipo String en un objeto de tipo Date
     * @param dateText String de entrada con la fecha
     * @return Objeto de tipo Date
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT, java.util.Locale.getDefault());
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }
    



    /**
     *  Clase interna que define los contenidos de la tabla posiciones 
     */
    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        
        public static final String TABLE_NAME = "location";


        /** Coordenada de latitud del PI en grados decimales, almacenada como float **/
        public static final String COLUMN_LOCATION_LATITUDE = "location_latitude";
        /** Coordenada de longitud del PI en grados decimales, almacenada como float **/
        public static final String COLUMN_LOCATION_LONGITUDE = "location_longitude";
   
        /** Valor del radio con el que se realiza la petici�n de descarga de PIs **/
        public static final String COLUMN_RADIUS = "radius";

        /** Fecha de descarga de los PIs asociados a esta posici�n. Se almacena como dato de 
         * tipo texto **/
        public static final String COLUMN_DATETEXT = "date";

        

        /**
         * M�todo que genera la URI de consulta de la tabla Location del Content Provider por ID.
         * @param id Id de la posici�n que se quiere consultar
         * @return URI para consultar una fila de Location en el Content Provider dada su ID.
         */
        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        
        /**
         * M�todo para extraer la coordenada de latitud en una URI de una entrada para
         * la tabla Location
         * @param uri URI de la que se extrae la latitud
         * @return String con la latitud (Debe convertirse a double despu�s si es necesario)
         */
        public static String getLocationLatitudeFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_LOCATION_LATITUDE);
        }
        
        
        /**
         * M�todo para extraer la coordenada de longitud en una URI de una entrada para
         * la tabla Location
         * @param uri URI de la que se extrae la longitud
         * @return String con la longitud (Debe convertirse a double despu�s si es necesario)
         */
        public static String getLocationLongitudeFromUri(Uri uri) {
        	Log.d("PoiProvider","En poicontract: "+uri.getPath());
            return uri.getQueryParameter(COLUMN_LOCATION_LONGITUDE);
        }
        
        
    }// Fin de LocationEntry
    
    
    /**
     *  Clase interna que define los contenidos de la tabla location_poi que relaciona las tuplas 
     *  de la tabla location y la tabla poi.
     */
    public static final class LocationPoiEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION_POI).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION_POI;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION_POI;

        
        public static final String TABLE_NAME = "location_poi";

        /** ID de la tabla location **/
        public static final String COLUMN_ID_LOCATION = "id_location";

        /** ID de la tabla poi **/
        public static final String COLUMN_ID_POI = "id_poi";

        
        /**
         * M�todo que genera la URI de consulta en la tabla Location_Poi del Content Provider 
         * por ID.
         * @param id Id de la fila que se quiere consultar
         * @return URI para consultar una fila de Location_Poi en el Content Provider dada su ID.
         */
        public static Uri buildLocationPoiUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }// Fin de LocationPoiEntry
    
    
    
    /**
     *  Clase interna que define los contenidos de la tabla pois 
     */
    public static final class PoiEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                		BASE_CONTENT_URI.buildUpon().appendPath(PATH_POIS).build();

        public static final String CONTENT_TYPE =
                	"vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_POIS;
        public static final String CONTENT_ITEM_TYPE =
                	"vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_POIS;

        
        public static final String TABLE_NAME = "poi";
        /** Columna con la foreign key de la tabla de usuario (por si hace falta). **/
        public static final String COLUMN_POI_USER_ID = "usuario_ID_usuario";        

        /** Nombre del PI **/
        public static final String COLUMN_POI_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;//"poi_name";
        //Para que funcionen las sugerencias de las b�squedas, es necesario ponerle ese nombre
        
        /** Color del PI en el Radar **/
        public static final String COLUMN_POI_COLOR = "poi_color";
        
        /** Ruta de la imagen del PI. Este atributo se identifica como multimedia en la API **/
        public static final String COLUMN_POI_IMAGE = "poi_image";
        
        /** Descripcion del PI **/
        public static final String COLUMN_POI_DESCRIPTION = "poi_description";
        
        /** Sitio web del PI **/
        public static final String COLUMN_POI_WEBSITE = "poi_website";
        
        /** Precio del PI **/
        public static final String COLUMN_POI_PRICE = "poi_price";
        
        /** Horario de apertura del PI, almacenada como time **/
        public static final String COLUMN_POI_OPEN_HOURS = "poi_open_hours";        
        /** Horario de cierre del PI, almacenada como time **/
        public static final String COLUMN_POI_CLOSE_HOURS = "poi_close_hours";
        
        /** Edad M�xima de acceso al PI, almacenada como tinyint **/
        public static final String COLUMN_POI_MAX_AGE = "poi_max_age";        
        /** Edad M�nima de acceso al PI, almacenada como tinyint **/
        public static final String COLUMN_POI_MIN_AGE = "poi_min_age";
        
        /** Altitud PI en metros, almacenada como DECIMAL (5,1) **/
        public static final String COLUMN_POI_ALTITUDE = "poi_altitude";        
        /** Coordenada de latitud del PI en grados decimales, almacenada como float**/
        public static final String COLUMN_POI_LATITUDE = "poi_latitude";
        /** Coordenada de longitud del PI en grados decimales, almacenada como float**/
        public static final String COLUMN_POI_LONGITUDE = "poi_longitude";
          
        /** Constantes para distinguir el tipo de proveedor de PIs **/
        public static final int WIKIPEDIA_PROVIDER = 1;
        public static final int WIKIPEDIA_COLOR = Color.WHITE;
        public static final String WIKIPEDIA_DEFAULT_IMAGE = "android.resource://" +
        		CONTENT_AUTHORITY + "/" + R.drawable.wikipedia;
        
        public static final int GOOGLE_PLACES_PROVIDER = 2;
        public static final int GOOGLE_COLOR = Color.RED;
        
        public static final int UJA_PROVIDER = 3;
        public static final int UJA_COLOR = Color.GREEN;
        public static final String UJA_DEFAULT_IMAGE = "android.resource://" +
				CONTENT_AUTHORITY + "/" + R.drawable.uja_data;
        
        public static final int LOCAL_PROVIDER = 4;
        public static final int LOCAL_COLOR = Color.YELLOW;
        public static final String LOCAL_DEFAULT_IMAGE = "android.resource://" +
				CONTENT_AUTHORITY + "/" + R.drawable.local_data;

        /**
         * M�todo que genera la URI de consulta del Content Provider por ID.
         * @param id Id del PI que se quiere consultar
         * @return URI para consultar en el Content Provider un PI dada su ID.
         */
        public static Uri buildPoiUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        
        
        /**
         * M�todo que genera la URI de consulta de PIs del Content Provider dadas las 
         * coordenadas GPS. Ser� de la forma: 
         * <pre>content://< paquete >/poi/by-coords?latitud=lat&longitud=lon</pre>
         * @param lat Coordenada de latitud
         * @param lon Coordenada de longitud
         * @return URI para consultar en el Content Provider los PIs que coinciden con
         * la posici�n dada.
         */
        public static Uri buildLocationUriWithCoords(String lat, String lon) {
        	
        	return CONTENT_URI.buildUpon().appendPath(POIS_BY_COORDS)
                    .appendQueryParameter(LocationEntry.COLUMN_LOCATION_LATITUDE, lat)
                    .appendQueryParameter(LocationEntry.COLUMN_LOCATION_LONGITUDE, lon)
                    .build();
        }
        
        

        /**
         * M�todo que genera la URI de consulta de PIs del Content Provider dadas un nombre (o una
         * parte). Ser� de la forma: 
         * <pre>content://< paquete >/poi/by-name/name</pre>
         * @param name	Cadena que debe contener el nombre de los PIs que se devuelvan con esta URI
         * @return URI para consultar en el Content Provider los PIs que contienen la cadena dada.
         */
        public static Uri buildPoiByNameUri(String name) {
            return CONTENT_URI.buildUpon().appendPath(POIS_BY_NAME)
            		.appendPath(name).build();
        }

    }// Fin de PoiEntry

}
