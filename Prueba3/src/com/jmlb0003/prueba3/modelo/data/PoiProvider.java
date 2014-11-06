package com.jmlb0003.prueba3.modelo.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;




/**
 * Clase que implementa el Content Provider con el que se manejarán los datos almacenados
 * en la BD de la aplicación.
 * @author Jose
 *
 */
public class PoiProvider extends ContentProvider {

    
//    TODO: En este enlace hay una posible solución a las consultas SQLite para las distancias
//    http://goodenoughpractices.blogspot.com.es/2011/08/query-by-proximity-in-android.html
//	  TODO: Aquí hay unas pruebas sobre las consultas con fórmula de Haversine 'mejorada'
//	http://www.notaires.fr/sites/default/files/geo_searchjkkjkj_0.pdf
	
	// The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private PoiDbHelper mOpenHelper;

    private static final int POIS = 100;
    private static final int POIS_WITH_CURRENT_POSITION = 101;
//    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
//    private static final int LOCATION = 300;
//    private static final int LOCATION_ID = 301;

    private static final SQLiteQueryBuilder sPoisByLocationSettingQueryBuilder;

    static{
    	sPoisByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
    	sPoisByLocationSettingQueryBuilder.setTables(
                PoiContract.PoiEntry.TABLE_NAME + " INNER JOIN " +
                		PoiContract.LocationEntry.TABLE_NAME +
                        " ON " + 
                		PoiContract.PoiEntry.TABLE_NAME + 
                			"." + PoiContract.PoiEntry._ID +
                			
                        " = " + PoiContract.LocationPoiEntry.TABLE_NAME + 
                        	"." + PoiContract.LocationPoiEntry.COLUMN_ID_POI +
                        	
                        " AND " + PoiContract.LocationEntry.TABLE_NAME + 
                        	"." + PoiContract.LocationEntry._ID +
                        	
                        " = " + PoiContract.LocationPoiEntry.TABLE_NAME + 
                        	"." + PoiContract.LocationPoiEntry.COLUMN_ID_LOCATION
    	);
    }

    /** Constante para añadir a las consultas variables de latitud y longitud **/
    private static final String sLocationSelection =
            PoiContract.PoiEntry.TABLE_NAME +
                    "." + PoiContract.PoiEntry.COLUMN_POI_LATITUDE + " = ? " +
		    PoiContract.PoiEntry.TABLE_NAME +
		    		"." + PoiContract.PoiEntry.COLUMN_POI_LONGITUDE + " = ? ";
    
//    private static final String sLocationSettingWithStartDateSelection =
//            WeatherContract.LocationEntry.TABLE_NAME+
//                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
//                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " >= ? ";
//
//    private static final String sLocationSettingAndDaySelection =
//            WeatherContract.LocationEntry.TABLE_NAME +
//                    "." + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
//                    WeatherContract.WeatherEntry.COLUMN_DATETEXT + " = ? ";
    
    
    /**
     * Método para obtener un cursor de datos del content provider a partir de una URI que apunta
     * al content. Concretamente, con esta función se intenta obtener todos los PIs disponibles.
     * @param uri URI donde están los datos que se quieren obtener y donde se va a realizar la 
     * 				consulta.
     * @param projection Lista de columnas que se incluirán en el cursor. Si vale null, se 
     * 				incluyen todas las columnas.
     * @param sortOrder Criterio para ordenar las columnas. Si es null, el provider las ordena 
     * 				según su propio criterio.
     * @return Cursor con el resultado de la consulta o null si algo falla.
     */
    private Cursor getAllPois(Uri uri, String[] projection, String sortOrder) {
        /**
         * Valores de los parámetros ? de selection. Serán reemplazados según el orden de aparición. 
         */
        String[] selectionArgs = null;
        /**
         * Comprende la cláusula WHERE de la consulta (excluyendo el WHERE). Si vale null, se 
         * devuelven todas las columnas.
         */
        String selection = null;

        return sPoisByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,	//Contenido del SELECT
                selection,	//Contenido del WHERE (puede incluir parámetros con ?)
                selectionArgs,	//Valores de parámetros ? de selection
                null,	//Contenido del GROUP BY
                null,	//Contenido del HAVING
                sortOrder	//Contenido del ORDER BY
        );
    }
    
    
    
    
    
    /**
     * Método para crear un UriMatcher asociando códigos con las distintas rutas posibles.
     * @return Urimatcher creado
     */
    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PoiContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, PoiContract.PATH_POIS, POIS);
        matcher.addURI(authority, PoiContract.PATH_POIS + "/*", POIS_WITH_CURRENT_POSITION);
//        matcher.addURI(authority, PoiContract.PATH_WEATHER + "/*/*", WEATHER_WITH_LOCATION_AND_DATE);
//
//        matcher.addURI(authority, PoiContract.PATH_LOCATION, LOCATION);
//        matcher.addURI(authority, PoiContract.PATH_LOCATION + "/#", LOCATION_ID);

        return matcher;
    }
    
    
    
    @Override
    public boolean onCreate() {
        mOpenHelper = new PoiDbHelper(getContext());
        return true;
    }


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        //Dada una URI, se determina el tipo de consulta para realizarla de forma adecuada
        //Debe haber una por cada tipo de URI
        switch (sUriMatcher.match(uri)) {
            // "poi/*"
            case POIS_WITH_CURRENT_POSITION: {
                retCursor = getAllPois(uri, projection, sortOrder);
                break;
            }
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
//            // "location/*"
//            case LOCATION_ID: {
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        WeatherContract.LocationEntry.TABLE_NAME,
//                        projection,
//                        WeatherContract.LocationEntry._ID + " = '" + ContentUris.parseId(uri) + "'",
//                        null,
//                        null,
//                        null,
//                        sortOrder
//                );
//                break;
//            }
//            // "location"
//            case LOCATION: {
//                retCursor = mOpenHelper.getReadableDatabase().query(
//                        WeatherContract.LocationEntry.TABLE_NAME,
//                        projection,
//                        selection,
//                        selectionArgs,
//                        null,
//                        null,
//                        sortOrder
//                );
//                break;
//            }

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
            case POIS_WITH_CURRENT_POSITION:
                return PoiContract.PoiEntry.CONTENT_TYPE;
            case POIS:
                return PoiContract.PoiEntry.CONTENT_TYPE;
//            case LOCATION:
//                return PoiContract.LocationEntry.CONTENT_TYPE;
//            case LOCATION_ID:
//                return PoiContract.LocationEntry.CONTENT_ITEM_TYPE;
          
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
	}


	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case POIS: {
                long _id = db.insert(PoiContract.PoiEntry.TABLE_NAME, null, values);
                if ( _id > 0 ) {
                	returnUri = PoiContract.PoiEntry.buildPoiUri(_id);
                } else {
                	throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                
                break;
            }
//            case LOCATION: {
//                long _id = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, values);
//                if ( _id > 0 )
//                    returnUri = WeatherContract.LocationEntry.buildLocationUri(_id);
//                else
//                    throw new android.database.SQLException("Failed to insert row into " + uri);
//                break;
//            }
            
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


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
//            case LOCATION:
//                rowsDeleted = db.delete(
//                        WeatherContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
//                break;
                
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
//            case LOCATION:
//                rowsUpdated = db.update(WeatherContract.LocationEntry.TABLE_NAME, values, selection,
//                        selectionArgs);
//                break;
                
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
	 * 			provider. No puede ser null.
	 * @return Devuelve un entero con el número de valores que se han insertado.
	 */
	@Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        
        switch (match) {
            case POIS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PoiContract.PoiEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
                
            default:
                return super.bulkInsert(uri, values);
        }
    }
 
}
