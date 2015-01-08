package com.jmlb0003.prueba3.modelo.data;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;




/**
 * Clase que implementa el Content Provider con el que se manejarán los datos almacenados
 * en la BD de la aplicación.
 * @author Jose
 *
 */
public class PoiProvider extends ContentProvider {

	private static final String LOG_TAG = "PoiProvider";
    
//    TODO: En este enlace hay una posible solución a las consultas SQLite para las distancias
//    http://goodenoughpractices.blogspot.com.es/2011/08/query-by-proximity-in-android.html
//	  TODO: Aquí hay unas pruebas sobre las consultas con fórmula de Haversine 'mejorada'
//	  http://www.notaires.fr/sites/default/files/geo_searchjkkjkj_0.pdf
	
	// The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PoiDbHelper mOpenHelper;

    /** Distancia en metros que representa la máxima distancia para considerar cercanos dos puntos **/
    public static final float MAX_DISTANCE = 0.025f;// un poco menos de 2000m // 2000;
    
    private static final int POIS = 100;
    private static final int POI_BY_ID = 101;
    private static final int POIS_BY_NAME = 102;
    private static final int POIS_BY_COORDS = 103;
    
    private static final int LOCATIONS = 200;
    private static final int LOCATION_BY_ID = 201;
    
    private static final int LOCATION_POI = 300;
    private static final int LOCATION_POI_BY_ID = 301;
    
    
    /*************** CONSTANTES PARA DEFINIR LAS SELECCIONES (WHERE) PERMITIDAS *****************/
    
    /**
     * Constante para calcular la distancia entre dos posiciones dadas las coordenadas GPS mediante
     * la fórmula de Haversine. Al utilizar la función arcocoseno, no es soportada por SQLite.
     
    private static final String sHaversineDistance = 
			"(6371 * acos( " + 
			"cos((? * PI() / 180)) " + 
			"* cos((" + PoiContract.LocationEntry.COLUMN_LOCATION_LATITUDE + " * PI() / 180)) " +
			"* cos((" + PoiContract.LocationEntry.COLUMN_LOCATION_LONGITUDE + " * PI() / 180) " +
			"- (? * PI() / 180)) + sin((? * PI() / 180)) " + 
			"* sin((" + PoiContract.LocationEntry.COLUMN_LOCATION_LATITUDE + " * PI() / 180)) ) )";
			
			*/
    
    /**
     * Constante para calcular una aproximación de la distancia entre dos posiciones dadas las
     * coordenadas GPS.
     * @see http://goodenoughpractices.blogspot.com.es/2011/08/query-by-proximity-in-android.html
     */
    public static final String sManhattanDistance = 
    		"(abs( " + PoiContract.LocationEntry.COLUMN_LOCATION_LATITUDE + " - ( ? )) " +
        			"+ abs( " + PoiContract.LocationEntry.COLUMN_LOCATION_LONGITUDE + " - ( ? )))";
    
    /** 
     * Constante para las consultas del ID de los PIs dadas como variables su nombre, latitud 
     * y longitud. Será:
     * WHERE name = ? AND latitude = ? AND longitude = ?
     */
    private static final String sIdPoiSearchSelection =
    		PoiContract.PoiEntry.COLUMN_POI_NAME + " = ? AND " +
			PoiContract.PoiEntry.COLUMN_POI_LATITUDE + " = ? AND " +
			PoiContract.PoiEntry.COLUMN_POI_LONGITUDE + " = ?";    
    
    /** 
     * Constante para las consultas del ID de las entradas de la tabla location_poi dadas como 
     * variables el id_location y el id_poi. Será:
     * WHERE id_location = ? AND id_poi = ?
     */
    private static final String sIdLocationPoiSearchSelection =
			PoiContract.LocationPoiEntry.COLUMN_ID_LOCATION + " = ? AND " +
			PoiContract.LocationPoiEntry.COLUMN_ID_POI + " = ?";

 
    
    /**************** CONSTANTES PARA DEFINIR CONSULTAS **********************/

    /** 
     * Maneja las consultas a las tablas de la base de datos mediante una operación JOIN definida.
     * Equivale (más o menos) a la cláusula FROM de la consulta. 
     */
    private static final SQLiteQueryBuilder sPoisByLocationQueryBuilder;


    static{
    	sPoisByLocationQueryBuilder = new SQLiteQueryBuilder();
    	sPoisByLocationQueryBuilder.setTables(
                PoiContract.PoiEntry.TABLE_NAME + " INNER JOIN " + 
    			PoiContract.LocationPoiEntry.TABLE_NAME + " ON " +
        		
        		PoiContract.PoiEntry.TABLE_NAME + "." + 
        		PoiContract.PoiEntry._ID + 
        		" = " +                			
                PoiContract.LocationPoiEntry.TABLE_NAME + "." + 
        		PoiContract.LocationPoiEntry.COLUMN_ID_POI + 
                
                	" INNER JOIN " + 
                PoiContract.LocationEntry.TABLE_NAME + " ON " +
                	
                PoiContract.LocationPoiEntry.TABLE_NAME + 
                "." + PoiContract.LocationEntry._ID +                        	
                " = " + 
                PoiContract.LocationPoiEntry.TABLE_NAME +
                "." + PoiContract.LocationPoiEntry.COLUMN_ID_LOCATION
    	);
    }


    
    /************* FUNCIONES DONDE SE CREAN LAS QUERYS CONTRA EL CONTENT PROVIDER *****************/
    
    /**
     * Método para generar una consulta con la que se obtienen los IDs de las posiciones que están
     * dentro de un radio dado por el coeficiente MAX_DISTANCE. El radio es aproximado y está 
     * basado en la fórmula de la distancia de Manhattan o geometría taxicab. 
     * @see Ver http://goodenoughpractices.blogspot.com.es/2011/08/query-by-proximity-in-android.html
     * @return Cadena con la consulta SQL que incluye parámetros ? para asignar el valor de la
     * latitud y la longitud actual.
     */
    private String getIdsNearLocationsQuery() {      	
    	String[] columns = {PoiContract.LocationEntry._ID};    	
    	String where = MAX_DISTANCE + " > " + sManhattanDistance;
        
        return SQLiteQueryBuilder.buildQueryString(
                true, // include distinct
                PoiContract.LocationEntry.TABLE_NAME,
                columns,
                where,
                null, // group by
                null, // having
                null, // order by
                null);	// limit
    }


    
    /**
     * Método que lanza una consulta sobre la Base de Datos para obtener los IDs de los PIs
     * asociados a una entrada de la tabla Location.
     * @param locationSubquery Subconsulta con la que se pueden obtener las localizaciones a las
     * 							que se asocian los PIs.
     * @return Cadena con la consulta SQL
     */
    private String getPoisByLocationIdQuery(String locationSubquery) {
    	String[] columns = {PoiContract.LocationPoiEntry.COLUMN_ID_POI};
    	
    	String where = PoiContract.LocationPoiEntry.COLUMN_ID_LOCATION + " IN (" + 
    			locationSubquery + ")";
    	
    	
    	return SQLiteQueryBuilder.buildQueryString(
        		true, 
        		PoiContract.LocationPoiEntry.TABLE_NAME,
        		columns, 
        		where, 
                null, // group by
                null, // having
                null, // order by
                null);	// limit
    }
    
    
    
    /**
     * Método para obtener un cursor de datos del content provider a partir de una URI que apunta
     * al content. Concretamente, con esta función se intenta obtener todos los PIs disponibles
     * dada una posición por sus coordenadas de latitud y longitud.
     * @param uri URI donde están los datos que se quieren obtener y donde se va a realizar la 
     * 				consulta.
     * @param projection Lista de columnas que se incluirán en el cursor. Si vale null, se 
     * 				incluyen todas las columnas.
     * @param sortOrder Criterio para ordenar las columnas. Si es null, el provider las ordena 
     * 				según su propio criterio.
     * @return Cursor con el resultado de la consulta o null si algo falla.
     */
    private Cursor getAllPoisByLocation(Uri uri, String[] projection, String sortOrder) {
        //1 - Query para IDs de location cercanos a las coordenadas dadas en la URI
        String locationSubquery = getIdsNearLocationsQuery();
        //2 - Query para IDs de los pois que pertenecen a los location de la 1
        String idPoisSubquery = getPoisByLocationIdQuery(locationSubquery);   

        //3 - Query para datos de los pois cuyos IDs son los de 2
        String poisByLocationQuery = SQLiteQueryBuilder.buildQueryString(
        		true, 
        		PoiContract.PoiEntry.TABLE_NAME,
        		projection, 
        		PoiContract.PoiEntry._ID + " IN (" + idPoisSubquery + ")", 
                null, // group by
                null, // having
                sortOrder, // order by
                null);	// limit
        
        String[] selectionArgs = {PoiContract.LocationEntry.getLocationLatitudeFromUri(uri), 
        		PoiContract.LocationEntry.getLocationLongitudeFromUri(uri)};
  
        
        return mOpenHelper.getReadableDatabase().rawQuery(poisByLocationQuery, selectionArgs);
    }
    
    
    
    
    /**
     * Método para crear un UriMatcher asociando códigos con las distintas rutas posibles. Por 
     * cada tipo de URI que maneje el Content Provider se asignará un código para que sea más 
     * fácil de manejar la petición después. De esta forma no es necesario usar complejos patrones
     * para distinguir unas URIs de otras.
     * @return Urimatcher creado
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PoiContract.CONTENT_AUTHORITY;


        // PIs en general  ---> content://com.jmlb0003.prueba3/poi
        matcher.addURI(authority, PoiContract.PATH_POIS, POIS);
        // PIs por ID  ---> content://com.jmlb0003.prueba3/poi/12312
        matcher.addURI(authority, PoiContract.PATH_POIS + "/#", POI_BY_ID);
        // PIs por nombre  ---> content://com.jmlb0003.prueba3/poi/by-name/asds
        matcher.addURI(authority, PoiContract.PATH_POIS + "/" + PoiContract.POIS_BY_NAME + "/*",
        														POIS_BY_NAME);
        // Location por Coordenadas --> content://com.jmlb0003.prueba3/poi/by-coords?location_sadasd
        matcher.addURI(authority, PoiContract.PATH_POIS + "/" + PoiContract.POIS_BY_COORDS, 
        														POIS_BY_COORDS);
//        matcher.addURI(authority, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        

        // Locations en general --> content://com.jmlb0003.prueba3/location
        matcher.addURI(authority, PoiContract.PATH_LOCATION, LOCATIONS);
        // Location por ID --> content://com.jmlb0003.prueba3/location/123
        matcher.addURI(authority, PoiContract.PATH_LOCATION + "/#", LOCATION_BY_ID);
        

        // Locations en general --> content://com.jmlb0003.prueba3/location_poi
        matcher.addURI(authority, PoiContract.PATH_LOCATION_POI, LOCATION_POI);
        // Location por ID --> content://com.jmlb0003.prueba3/location_poi/123
        matcher.addURI(authority, PoiContract.PATH_LOCATION_POI + "/#", LOCATION_POI_BY_ID);
        
        
        return matcher;
    }
    
    
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new PoiDbHelper(getContext());
        return true;
    }


    /**
     * En este método se determina, dada una URI, el tipo de petición que se está realizando
     * al Content Provider. Para cada tipo de petición se realizan las llamadas necesarias para
     * atenderla y se devuelve un Cursor con los datos resultantes de lanzar la petición.
     */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        //Dada una URI, se determina el tipo de consulta para realizarla de forma adecuada
        //Debe haber una por cada tipo de URI

        Log.d(LOG_TAG,"En Query!!. La uri es:"+uri+"-");
        Log.d(LOG_TAG,"El uri matcher es:"+sUriMatcher.match(uri));
       
       
        
        switch (sUriMatcher.match(uri)) {
	     	// "poi"
	        case POIS: {
	            retCursor = mOpenHelper.getReadableDatabase().query(
	                    PoiContract.PoiEntry.TABLE_NAME,
	                    projection,
	                    selection,
	                    selectionArgs,
	                    null,
	                    null,
	                    sortOrder
	            );
	            break;
	        }
            // "poi/#"
            case POI_BY_ID: {
            	//Seleccionar la entrada cuyo ID sea el de la URI
            	selection = PoiContract.PoiEntry._ID + " = '" + ContentUris.parseId(uri) + "'";

            	retCursor = mOpenHelper.getReadableDatabase().query(
                        PoiContract.PoiEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            	
                break;
            }            
            // "/poi/by-name/*"
            case POIS_BY_NAME: {
            	//Seleccionar la entrada cuyo nombre sea de la forma %parametroURI%, es decir, que contenga la cadena de la URI
            	selection = PoiContract.PoiEntry.COLUMN_POI_NAME + " LIKE '%" + uri.getLastPathSegment() + "%'";
            	
            	retCursor = mOpenHelper.getReadableDatabase().query(
                        PoiContract.PoiEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            	
                break;
            }
            // "poi/by-coords?location_latitude=*&location_longitude=*"
            case POIS_BY_COORDS: {
            	retCursor = getAllPoisByLocation(uri, projection, sortOrder);
            	
            	break;
            }
            // "location"
            case LOCATIONS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PoiContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                
                break;
            }
            // "location/#"
            case LOCATION_BY_ID: {
            	selection = PoiContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'";
            	
            	retCursor = mOpenHelper.getReadableDatabase().query(
            			PoiContract.LocationEntry.TABLE_NAME,
            			projection,
            			selection,
            			null,
            			null,
            			null,
		              	sortOrder
            	);
            	
            	break;
            }
            
            
            //"location_poi"
            case LOCATION_POI: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        PoiContract.LocationPoiEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location_poi/#"
            case LOCATION_POI_BY_ID: {
            	selection = PoiContract.LocationPoiEntry._ID + " = '" + ContentUris.parseId(uri) + "'";
            	
            	retCursor = mOpenHelper.getReadableDatabase().query(
            			PoiContract.LocationPoiEntry.TABLE_NAME,
            			projection,
            			selection,
            			null,
            			null,
            			null,
		              	sortOrder
            	);
            	
            	break;
            }
            

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
	}


	@Override
	public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
        	case POIS:
        		return PoiContract.PoiEntry.CONTENT_TYPE;
            case POI_BY_ID:
                return PoiContract.PoiEntry.CONTENT_ITEM_TYPE;
            case POIS_BY_NAME:
                return PoiContract.PoiEntry.CONTENT_TYPE;
            case POIS_BY_COORDS:
                return PoiContract.LocationEntry.CONTENT_TYPE;
//            case SEARCH_SUGGEST:
//                return SearchManager.SUGGEST_MIME_TYPE;
            case LOCATIONS:
                return PoiContract.LocationEntry.CONTENT_TYPE;
            case LOCATION_BY_ID:
                return PoiContract.LocationEntry.CONTENT_ITEM_TYPE;
            case LOCATION_POI:
                return PoiContract.LocationPoiEntry.CONTENT_TYPE;
            case LOCATION_POI_BY_ID:
                return PoiContract.LocationPoiEntry.CONTENT_ITEM_TYPE;
          
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match) {
            case POIS: {
                //Comprobamos si el PI ya estaba insertado consultando por las columnas que 
                //indican la unicidad de cada PI (además del ID):
            	// NOMBRE, LATITUD y LONGITUD
            	
            	//Realizamos una consulta en la que solamente interesa la columna de ID
            	String[] columns = {PoiContract.PoiEntry._ID};
            	//Parámetros para los ? de el WHERE de sIdPoiSearchSelection
            	String[] selectionArgs = {
            			values.getAsString(PoiContract.PoiEntry.COLUMN_POI_NAME),
            			values.getAsString(PoiContract.PoiEntry.COLUMN_POI_LATITUDE),
            			values.getAsString(PoiContract.PoiEntry.COLUMN_POI_LONGITUDE)
            			};
            	//Consulta
            	Cursor c = db.query(
            			PoiContract.PoiEntry.TABLE_NAME,
            			columns, 
            			sIdPoiSearchSelection,
            			selectionArgs,
            			null,
            			null,
            			null
            	);
            	
            	if (c.moveToFirst()) {
            		//Si se ha encontrado el PI que se intentaba insertar, obtenemos el ID
            		_id = c.getLong(c.getColumnIndex(PoiContract.PoiEntry._ID));
            	}else{
            		_id = db.insert(PoiContract.PoiEntry.TABLE_NAME, null, values);
            	}
            	c.close();
            	
                if ( _id < 0 ) {
                	throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                
                //Finalmente, construimos la URI con el ID del PI en la tabla
                returnUri = PoiContract.PoiEntry.buildPoiUri(_id);

                break;
            }
            
            
            case LOCATIONS: {
            	//Comprobamos si hay alguna posición insertada 'cercana' a la que se intenta
                //insertar en la tabla location
            	
            	String[] columns = {PoiContract.LocationEntry._ID};

            	String[] selectionArgs = {
            			values.getAsString(PoiContract.LocationEntry.COLUMN_LOCATION_LATITUDE),
            			values.getAsString(PoiContract.LocationEntry.COLUMN_LOCATION_LONGITUDE)
            			};

            	Cursor c = db.query(
            			PoiContract.LocationEntry.TABLE_NAME,
            			columns,
            			MAX_DISTANCE + " > " + sManhattanDistance,	
            			selectionArgs,
            			null,
            			null,
            			null
            	);

            	if (c.moveToFirst()) {
            		//Si se ha encontrado alguna posición 'cercana', obtenemos el ID
            		_id = c.getLong(c.getColumnIndex(PoiContract.LocationEntry._ID));
            		Log.d(LOG_TAG,"Insertando location repetida...El id que da es:"+_id);
            	}else{
            		_id = db.insert(PoiContract.LocationEntry.TABLE_NAME, null, values);
            		Log.d(LOG_TAG,"Insertando location bien. El id que da es:"+_id);
            	}
            	c.close();
            	
                if ( _id < 0 ) {
                	throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                
                //Finalmente, construimos la URI con el ID de la posición en la tabla
                returnUri = PoiContract.LocationEntry.buildLocationUri(_id);

                break;
            }
            
            
            case LOCATION_POI: {
            	//Comprobamos si la entrada ya estaba insertada consultando por las columnas que
                //indican la unicidad de cada entrada de la tabla location_poi (además del ID):
            	// id_location e id_poi
            	
            	//Realizamos una consulta en la que solamente interesa la columna de ID
            	String[] columns = {PoiContract.LocationPoiEntry._ID};
            	//Parámetros para los ? de el WHERE de sIdLocationPoiSearchSelection
            	String[] selectionArgs = {
            			values.getAsString(PoiContract.LocationPoiEntry.COLUMN_ID_LOCATION),
            			values.getAsString(PoiContract.LocationPoiEntry.COLUMN_ID_POI)
            			};
            	//Consulta
            	Cursor c = db.query(
            			PoiContract.LocationPoiEntry.TABLE_NAME,
            			columns, 
            			sIdLocationPoiSearchSelection,
            			selectionArgs,
            			null,
            			null,
            			null
            	);
            	
            	if (c.moveToFirst()) {
            		//Si se ha encontrado la posición que se intentaba insertar, obtenemos el ID
            		_id = c.getLong(c.getColumnIndex(PoiContract.LocationPoiEntry._ID));
            	}else{
            		_id = db.insert(PoiContract.LocationPoiEntry.TABLE_NAME, null, values);
            	}
            	c.close();
            	
                if ( _id < 0 ) {
                	throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                
                //Finalmente, construimos la URI con el ID del PI en la tabla
                returnUri = PoiContract.LocationPoiEntry.buildLocationPoiUri(_id);
                
                break;
            }
            
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }// Fin de insert


	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        
        switch (match) {
            case POIS:
                rowsDeleted = db.delete(
                		PoiContract.PoiEntry.TABLE_NAME, selection, selectionArgs);
                break;
                
            case LOCATIONS:
                rowsDeleted = db.delete(
                		PoiContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
                
            case LOCATION_POI:
                rowsDeleted = db.delete(
                		PoiContract.LocationPoiEntry.TABLE_NAME, selection, selectionArgs);
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
	}


	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case POIS:
                rowsUpdated = db.update(PoiContract.PoiEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
                
            case LOCATIONS:
                rowsUpdated = db.update(PoiContract.LocationEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            
            case LOCATION_POI:
                rowsUpdated = db.update(PoiContract.LocationPoiEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
                
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        
        return rowsUpdated;
	}



	/**
	 * Método para insertar un conjunto de filas en una misma transacción.
	 * @param uri Representa a la URI dentro del content provider donde se realizará la inserción.
	 * @param values Es un array de pares nombre-de-columna/valor que se añadirán al content 
	 * 			provider. No puede ser null y MUY IMPORTANTE: el último elemento debe contener el
	 * 			ID de la posición a la que está asociado.
	 * @return Devuelve un entero con el número de valores que se han insertado.
	 */
	@Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowCount;
        
        switch (match) {
            case POIS:
                db.beginTransaction();
                rowCount = 0;

            	ArrayList<ContentValues> toLocationPoi = new ArrayList<>();
            	
                try {
                	//Sacar el último elemento de values, donde está idLocation
                	ArrayList<ContentValues> list = new ArrayList<>();
                	for (int i = 0; i < values.length; i++) {
                		list.add(values[i]);
                	}
                	ContentValues locationValue = list.remove((list.size()-1));
                	long idLocation = locationValue.getAsLong(PoiContract.LocationEntry._ID);
                	long _idPoi;
                	                	
                	
                    for (ContentValues value : list) {
                    	String[] columns = {PoiContract.PoiEntry._ID};
                    	//Parámetros para los ? de el WHERE de sIdPoiSearchSelection
                    	String[] selectionArgs = {
                    			value.getAsString(PoiContract.PoiEntry.COLUMN_POI_NAME),
                    			value.getAsString(PoiContract.PoiEntry.COLUMN_POI_LATITUDE),
                    			value.getAsString(PoiContract.PoiEntry.COLUMN_POI_LONGITUDE) };

                    	//Consulta
                    	Cursor c = db.query(
                    			PoiContract.PoiEntry.TABLE_NAME,
                    			columns, 
                    			sIdPoiSearchSelection,
                    			selectionArgs,
                    			null,
                    			null,
                    			null
                    	);

                    	if (c.moveToFirst()) {
                    		//Si se ha encontrado el PI que se intentaba insertar, obtenemos el ID
                    		_idPoi = c.getLong(c.getColumnIndex(PoiContract.PoiEntry._ID));
                    		Log.d(LOG_TAG,"1El poi "+ _idPoi + " ya estaba");
                    	}else{
                    		_idPoi = db.insert(PoiContract.PoiEntry.TABLE_NAME, null, value);
                    		Log.d(LOG_TAG,"2Insertado el poi "+ _idPoi);
                    		if (_idPoi != -1) {
                            	rowCount++;
                            }                    		  
                    	}
                    	c.close();

                    	ContentValues cv = new ContentValues();
                    	cv.put(PoiContract.LocationPoiEntry.COLUMN_ID_LOCATION, idLocation);
                    	cv.put(PoiContract.LocationPoiEntry.COLUMN_ID_POI, _idPoi);
                	
                    	toLocationPoi.add(cv);
                        
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                Log.d(LOG_TAG,"Insertados todos los pois, ahora los location_poi");
                //Si se llegaron a insertar PIs, se insertan las entradas en location_poi
                if (!toLocationPoi.isEmpty()) {
                	Log.d(LOG_TAG,"Creando el array");
                	ContentValues[] valuesToInsert = new ContentValues[toLocationPoi.size()];
                	toLocationPoi.toArray(valuesToInsert);
                    bulkInsert(PoiContract.LocationPoiEntry.CONTENT_URI, valuesToInsert);
                }
                
                return rowCount;
                
                
            case LOCATION_POI:
            	db.beginTransaction();
            	rowCount = 0;
            	try {
            		Log.d(LOG_TAG,"Empieza el bulkinsert de location_poi");
                    for (ContentValues value : values) {
                    	long _id;
                    	
                    	String[] columns = {PoiContract.LocationPoiEntry._ID};
                    	//Parámetros para los ? de el WHERE de sIdLocationPoiSearchSelection
                    	String[] selectionArgs = {
                    			value.getAsString(PoiContract.LocationPoiEntry.COLUMN_ID_LOCATION),
                    			value.getAsString(PoiContract.LocationPoiEntry.COLUMN_ID_POI)
                    			};
                    	//Consulta
                    	Cursor c = db.query(
                    			PoiContract.LocationPoiEntry.TABLE_NAME,
                    			columns, 
                    			sIdLocationPoiSearchSelection,
                    			selectionArgs,
                    			null,
                    			null,
                    			null
                    	);
                    	
                    	if (c.moveToFirst()) {
                    		//Si se ha encontrado la posición que se intentaba insertar, obtenemos el ID
                    		_id = c.getLong(c.getColumnIndex(PoiContract.LocationPoiEntry._ID));
                    		Log.d(LOG_TAG,"1El location_poi "+ _id + " ya estaba");
                    	}else{
                    		_id = db.insert(PoiContract.LocationPoiEntry.TABLE_NAME, null, value);
                    		Log.d(LOG_TAG,"2Insertado el location_poi "+ _id);
                    		if (_id != -1) {
                            	rowCount++;
                            }
                    	}
                    	c.close();
                        
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return rowCount;
                
            default:
                return super.bulkInsert(uri, values);
        }
    }
	
 
}
