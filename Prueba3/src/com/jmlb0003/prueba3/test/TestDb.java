package com.jmlb0003.prueba3.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.test.AndroidTestCase;
import android.util.Log;

import com.jmlb0003.prueba3.modelo.data.PoiContract.LocationEntry;
import com.jmlb0003.prueba3.modelo.data.PoiContract.LocationPoiEntry;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;
import com.jmlb0003.prueba3.modelo.data.PoiDbHelper;




public class TestDb extends AndroidTestCase {
	
	public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
//    	Log.d(LOG_TAG,"Creando bd para tests");
        mContext.deleteDatabase(PoiDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new PoiDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        
        db.close();
    }
    
    
    
    public void notestInsertReadDb() {

    	PoiDbHelper dbHelper = new PoiDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testPoiValues = createPoiCasaValues("TestCasa");
        ContentValues testLocationValues = createUserPosition();

        
        //Se inserta una fila de cada tabla:
        long poiRowId, locationRowId, locationPoiRowId;
        poiRowId = db.insert(PoiEntry.TABLE_NAME, null, testPoiValues);
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, testLocationValues);
        ContentValues testlocationPoiValues = new ContentValues();
        testlocationPoiValues.put(LocationPoiEntry.COLUMN_ID_LOCATION, locationRowId);
        testlocationPoiValues.put(LocationPoiEntry.COLUMN_ID_POI, poiRowId);
        
        locationPoiRowId = db.insert(LocationPoiEntry.TABLE_NAME, null, testlocationPoiValues);

        //Se verifica que se ha insertado dicha fila
        assertTrue(poiRowId != -1);
        assertTrue(locationRowId != -1);
        assertTrue(locationPoiRowId != -1);
        Log.d(LOG_TAG, "New row id en tabla poi: " + poiRowId);
        Log.d(LOG_TAG, "New row id en tabla location: " + locationRowId);
        Log.d(LOG_TAG, "New row id en tabla location_poi: " + locationPoiRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.
        
        //Intentamos extraer ahora la fila insertada mediante una consulta
        Cursor cursor = db.query(
                PoiEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        Cursor cursorLocation = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );
        Cursor cursorLocationPoi = db.query(
                LocationPoiEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        //Se valida que la fila está correcta
        validateCursor(cursor, testPoiValues/*, poiRowId*/);
        validateCursor(cursorLocation, testLocationValues/*, locationRowId*/);
        validateCursor(cursorLocationPoi, testlocationPoiValues/*, locationPoiRowId*/);
        
        /******************Insertar otro poi*******************/
        testPoiValues = createPoiCasaValues("TestCasa2");
        
        poiRowId = db.insert(PoiEntry.TABLE_NAME, null, testPoiValues);

        //Se verifica que se ha insertado dicha fila
        assertTrue(poiRowId != -1);
        Log.d(LOG_TAG, "New row id: " + poiRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        //Intentamos extraer ahora la fila insertada mediante una consulta
        cursor = db.query(
                PoiEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        //Se valida que la fila está correcta
        validateCursor(cursor, testPoiValues/*, poiRowId*/);
        
        


        dbHelper.close();
    }
    
    
    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToLast());

//        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
//        for (Map.Entry<String, Object> entry : valueSet) {
//            String columnName = entry.getKey();
//            int idx = valueCursor.getColumnIndex(columnName);
//            assertFalse(idx == -1);
//            String expectedValue = entry.getValue().toString();
//            
//            if ( (columnName.contains("latitude")) || (columnName.contains("longitude"))) {
//            	double val = Double.parseDouble(entry.getValue().toString());
////            	assertEquals(val, valueCursor.getDouble(idx));
////            	Log.d(LOG_TAG,columnName + "1.2 expectedValue:"+val+" y contiene:"+valueCursor.getDouble(idx));
//            }else{
////            	Log.d(LOG_TAG,columnName + "1.1 expectedValue:"+expectedValue+" y contiene:"+valueCursor.getString(idx));
////            	assertEquals(expectedValue, valueCursor.getString(idx));
//            }
//        }

        valueCursor.close();
    }
    
    
    static ContentValues createPoiCasaValues(String name) {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(PoiEntry.COLUMN_USUARIO_KEY, 1);
        testValues.put(PoiEntry.COLUMN_POI_NAME, name);
        testValues.put(PoiEntry.COLUMN_POI_COLOR, Color.RED);
        testValues.put(PoiEntry.COLUMN_POI_IMAGE, "testDB");
        testValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, "Esta es la descripción para el test del poi Casa");
        testValues.put(PoiEntry.COLUMN_POI_ALTITUDE, 760);
        testValues.put(PoiEntry.COLUMN_POI_LATITUDE, 37.6759861);
        testValues.put(PoiEntry.COLUMN_POI_LONGITUDE, -3.5661972);
        testValues.put(PoiEntry.COLUMN_POI_WEBSITE, "joselopez.hol.es");
        testValues.put(PoiEntry.COLUMN_POI_PRICE, 0);
        testValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, 0);
        testValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, 0);
        testValues.put(PoiEntry.COLUMN_POI_MAX_AGE, 0);
        testValues.put(PoiEntry.COLUMN_POI_MIN_AGE, 0);
        
        return testValues;
    }
    
    
    static ContentValues createUserPosition() {
    	ContentValues testValues = new ContentValues();
    	testValues.put(LocationEntry.COLUMN_RADIUS,40);
    	testValues.put(LocationEntry.COLUMN_DATETEXT,"20112014");
    	testValues.put(LocationEntry.COLUMN_LOCATION_LATITUDE,37.6757738);
    	testValues.put(LocationEntry.COLUMN_LOCATION_LONGITUDE,-3.5661434);
    	
    	return testValues;
    }
    

}
