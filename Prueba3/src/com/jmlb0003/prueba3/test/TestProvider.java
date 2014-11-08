package com.jmlb0003.prueba3.test;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

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
    
    
    /**
     * Prueba de inserción y lectura del Content Provider
     */
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
        		PoiEntry._ID,
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
        
        
//        TestDb.validateCursor(poiCursor, poiTestValues);
//
//        // Get the joined Weather data for a specific date
//        poiCursor = mContext.getContentResolver().query(
//                WeatherEntry.buildWeatherLocationWithDate(TestDb.TEST_LOCATION, TestDb.TEST_DATE),
//                null,
//                null,
//                null,
//                null
//        );
//        TestDb.validateCursor(poiCursor, poiTestValues);
    }
    
    
    
    
    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }
    

}
