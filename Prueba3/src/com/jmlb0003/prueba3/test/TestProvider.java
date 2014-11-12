package com.jmlb0003.prueba3.test;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.jmlb0003.prueba3.modelo.data.PoiContract;
import com.jmlb0003.prueba3.modelo.data.PoiContract.LocationEntry;
import com.jmlb0003.prueba3.modelo.data.PoiContract.LocationPoiEntry;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;

public class TestProvider extends AndroidTestCase {
	
	public static final String LOG_TAG = TestProvider.class.getSimpleName();
	
	
	//Primero se eliminan los datos del Content Provider
    public void deleteAllRecords() {
    	Log.d(LOG_TAG,"Borrando las tablas");
        mContext.getContentResolver().delete(
                LocationPoiEntry.CONTENT_URI,
                null,
                null
        );
        
        mContext.getContentResolver().delete(
                PoiEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );


        Log.d(LOG_TAG,"Comprobando que se han borrado las tablas");
        //Ahora se comprueba que está vacío
        Cursor cursor = mContext.getContentResolver().query(
        		PoiEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
        
        cursor = mContext.getContentResolver().query(
                LocationPoiEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }
    
    


    /**
     * Método para reiniciar el Content Provider en un test
     */
    public void setUp() {
//        deleteAllRecords();
    }
    
    
    
    public void testbulkInsert(){
    	/**********************************
    	 * 
    	 * Comprobar cómo se insertan los puntos en el bulkInsert...
    	 * 
    	 * 
    	 * Comprobar la diferencia de tiempo de insertar con consultas o sin consultas(errores al repetirse entradas)
    	 * 
    	 * 
    	 * 
    	 */
    }
    

    public void testInsertReadProvider() {

    	//Primero probamos con la tabla poi
        ContentValues poiTestValues = TestDb.createPoiCasaValues();

        Uri poiUri = mContext.getContentResolver()
        		.insert(PoiEntry.CONTENT_URI, poiTestValues);
        long poiRowId = ContentUris.parseId(poiUri);
        Log.d(LOG_TAG,"poiUri, con rowID:"+poiRowId+" vale: "+poiUri);

        // Verify we got a row back.
        assertTrue(poiRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        Log.d(LOG_TAG,"Cursor 1, con query:"+PoiEntry.CONTENT_URI+"-Lo demás es null");
        // A cursor is your primary interface to the query results.
        Cursor poiCursor = mContext.getContentResolver().query(
                PoiEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        
        TestDb.validateCursor(poiCursor, poiTestValues);

        Log.d(LOG_TAG,"Cursor 1.2, con query:"+PoiEntry.buildPoiUri(poiRowId)+"-Lo demás es null");
        // Now see if we can successfully query if we include the row id
        poiCursor = mContext.getContentResolver().query(
                PoiEntry.buildPoiUri(poiRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(poiCursor, poiTestValues);

        /********************************/
        //Ahora probamos con la tabla location
        ContentValues locationTestValues = TestDb.createUserPosition();

        Uri locationUri = mContext.getContentResolver()
                .insert(LocationEntry.CONTENT_URI, locationTestValues);
        long locationRowId = ContentUris.parseId(locationUri);
        Log.d(LOG_TAG,"locationUri, con rowID:"+locationRowId+" vale: "+locationUri);
        
        
        // Verify we got a row back.
        assertTrue(locationRowId != -1);        
        assertTrue(locationUri != null);

        Log.d(LOG_TAG,"Cursor 2, con query:"+LocationEntry.CONTENT_URI+"-Lo demás es null");
        // A cursor is your primary interface to the query results.
        Cursor locationCursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(locationCursor, locationTestValues);
        Log.d(LOG_TAG,"Cursor 2.2, con query:"+LocationEntry.buildLocationUri(locationRowId)+"-Lo demás es null");
        // Now see if we can successfully query if we include the row id
        locationCursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(locationCursor, locationTestValues);
        
        /********************************/
        //Ahora probamos con la tabla locationPoi

    	
        ContentValues locationPoiTestValues = new ContentValues();
        locationPoiTestValues.put(LocationPoiEntry.COLUMN_ID_LOCATION, locationRowId);
        locationPoiTestValues.put(LocationPoiEntry.COLUMN_ID_POI, poiRowId);        


        Uri locationPoiUri = mContext.getContentResolver()
        		.insert(LocationPoiEntry.CONTENT_URI, locationPoiTestValues);
        long locationPoiRowId = ContentUris.parseId(locationPoiUri);
        Log.d(LOG_TAG,"locationPoiUri, con rowID:"+locationPoiRowId+" vale: "+locationPoiUri);
        
        
        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        assertTrue(locationPoiUri != null);

        Log.d(LOG_TAG,"Cursor 3, con query:"+LocationPoiEntry.CONTENT_URI+"-Lo demás es null");
        // A cursor is your primary interface to the query results.
        Cursor locationPoiCursor = mContext.getContentResolver().query(
                LocationPoiEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(locationPoiCursor, locationPoiTestValues);


        Log.d(LOG_TAG,"Cursor 3.2, con query:"+LocationPoiEntry.buildLocationPoiUri(locationPoiRowId)+"-Lo demás es null");
        // Now see if we can successfully query if we include the row id
        locationPoiCursor = mContext.getContentResolver().query(
                LocationPoiEntry.buildLocationPoiUri(locationPoiRowId),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(locationPoiCursor, locationPoiTestValues);
        /*********************************/
        // Añadimos todos los datos en poiTestValues para comprobar que el Join funciona
//        addAllContentValues(poiTestValues, locationPoiTestValues);
//        addAllContentValues(poiTestValues, locationTestValues);


        String[] projection = {
        		PoiEntry.TABLE_NAME + "." + PoiEntry._ID,
        		PoiEntry.COLUMN_POI_NAME,
        		PoiEntry.COLUMN_POI_LATITUDE,
        		PoiEntry.COLUMN_POI_LONGITUDE
        };
        // Get the joined Weather and Location data with a start date
        poiCursor = mContext.getContentResolver().query(
                PoiEntry.buildLocationUriWithCoords("37.6757738", "-3.5661434"),
                projection, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        Log.d(LOG_TAG,"CONSULTAAA con "+poiCursor.getCount()+" filas");
    	for (int i=0; i<poiCursor.getCount();i++) {
    		poiCursor.moveToPosition(i);
    		
    		for (int j=0; j<poiCursor.getColumnCount();j++) {
    			Log.d(LOG_TAG,"Columna "+i+"-"+j+":"+poiCursor.getString(j));
    		}
    	}
      
    	Log.d(LOG_TAG,"fin del recorrido");
        
        poiCursor.close();
    }
    
    
    public void testInsertReadLocations() {
	
    	ContentValues locationBarranco = createUserPosition2(37.677568, -3.562658);
    	ContentValues locationVegueta = createUserPosition2(37.679128,  -3.559011);
    	ContentValues locationLoma = createUserPosition2(37.688069,  -3.562788);
    	ContentValues casaManu = createUserPosition2(37.674105, -3.569006);
    	ContentValues manchaReal = createUserPosition2(37.786779,-3.610379);
    	ContentValues pegalajar = createUserPosition2(37.740619,-3.645887);
    	ContentValues sitio1 = createUserPosition2(37.728501,-3.479748);
    	ContentValues bornos = createUserPosition2(37.682192, -3.543474);
    	ContentValues salao = createUserPosition2( 37.663903, -3.548809);
    	ContentValues arco = createUserPosition2(37.685209,-3.581173);
  
    	insertarLocation(locationBarranco);
    	insertarLocation(locationVegueta);
    	insertarLocation(locationLoma);
    	insertarLocation(casaManu);
    	insertarLocation(manchaReal);
    	insertarLocation(pegalajar);
    	insertarLocation(sitio1);
    	insertarLocation(bornos);
    	insertarLocation(salao);
    	insertarLocation(arco);


    	String[] projection = {
        		PoiEntry.TABLE_NAME + "." + PoiEntry._ID,
        		PoiEntry.COLUMN_POI_NAME,
        		PoiEntry.COLUMN_POI_LATITUDE,
        		PoiEntry.COLUMN_POI_LONGITUDE
        };
    	Cursor locationCursor = mContext.getContentResolver().query(
                PoiEntry.buildLocationUriWithCoords("37.6757738", "-3.5661434"),
                projection, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
    	
    	//Comprobar las entradas obtenidas
    	for (int i=0; i<locationCursor.getCount();i++) {
        	locationCursor.moveToPosition(i);
    		long id;
    		
    		id = locationCursor.getLong(locationCursor.getColumnIndex(PoiContract.LocationEntry._ID));
    		Log.d(LOG_TAG,i + "-ID: " + id);
    		Log.d(LOG_TAG,"\n *************** ");
    		
    	}
      
    	Log.d(LOG_TAG,"fin del recorrido");
    	
    }
    
    
    private void insertarLocation(ContentValues values) {
    	Uri locationUri = mContext.getContentResolver()
                .insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);
        Log.d(LOG_TAG,"locationUri, con rowID:"+locationRowId+" vale: "+locationUri);
        
        
        // Verify we got a row back.
        assertTrue(locationRowId != -1);        
        assertTrue(locationUri != null);
    	
    }
    
    private ContentValues createUserPosition2(double lat, double lon) {
    	ContentValues testValues = new ContentValues();
    	testValues.put(LocationEntry.COLUMN_RADIUS,40);
    	testValues.put(LocationEntry.COLUMN_DATETEXT,"20112014");
    	testValues.put(LocationEntry.COLUMN_LOCATION_LATITUDE, lat);
    	testValues.put(LocationEntry.COLUMN_LOCATION_LONGITUDE, lon);
    	
    	return testValues;
    }
    
    

    
    
    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }
    

    //TODO: Pruebas de velocidad de insertar puntos...a ver la diferencia del bulkinsert consultando antes de insertar para eliminar errores, o intentando insertar, y si no, consultar
}
