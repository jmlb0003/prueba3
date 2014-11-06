package com.jmlb0003.prueba3.modelo.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;



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


    /**
     * Posibles rutas dentro del provider, que llevan a las tablas que contiene.
     */
    public static final String PATH_POIS = "poi";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_LOCATION_POI = "location_poi";


    /**
     * Formato que se usará para las fechas que sea aceptado por la BD
     */
    public static final String DATE_FORMAT = "ddMMyyyy";
    
    
    /**
     * Método que convierte un objeto fecha en un string válido para almacenar dicha fecha en la BD.
     * @param date Fecha de entrada
     * @return una representación de tipo String válida para la BD
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, java.util.Locale.getDefault());
        return sdf.format(date);
    }
    
    
    /**
     * Método que convierte una fecha de tipo String en un objeto de tipo Date
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
   
        /** Valor del radio con el que se realiza la petición de descarga de PIs **/
        public static final String COLUMN_RADIUS = "radius";

        /** Fecha de descarga de los PIs asociados a esta posición. Se almacena como dato de 
         * tipo texto **/
        public static final String COLUMN_DATETEXT = "date";

        

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    
    
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

        

        public static Uri buildLocationPoiUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
    
    
    
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
        public static final String COLUMN_USUARIO_KEY = "usuario_ID_usuario";        

        /** Nombre del PI **/
        public static final String COLUMN_POI_NAME = "poi_name";
        
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
        
        /** Edad Máxima de acceso al PI, almacenada como tinyint **/
        public static final String COLUMN_POI_MAX_AGE = "poi_max_age";        
        /** Edad Mínima de acceso al PI, almacenada como tinyint **/
        public static final String COLUMN_POI_MIN_AGE = "poi_min_age";
        
        /** Altitud PI en metros, almacenada como DECIMAL (5,1) **/
        public static final String COLUMN_POI_ALTITUDE = "poi_altitude";        
        /** Coordenada de latitud del PI en grados decimales, almacenada como float**/
        public static final String COLUMN_POI_LATITUDE = "poi_latitude";
        /** Coordenada de longitud del PI en grados decimales, almacenada como float**/
        public static final String COLUMN_POI_LONGITUDE = "poi_longitude";
          
        


        public static Uri buildPoiUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
//
//        public static Uri buildPoiLocation(String locationSetting) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
//        }
//
//        public static Uri buildPoiLocationWithDate(String locationSetting, String date) {
//            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
//        }
//
//        public static String getLocationSettingFromUri(Uri uri) {
//            return uri.getPathSegments().get(1);
//        }
//
//        public static String getDateFromUri(Uri uri) {
//            return uri.getPathSegments().get(2);
//        }

    }// Fin de PoiEntry
    /*************************************/

}
