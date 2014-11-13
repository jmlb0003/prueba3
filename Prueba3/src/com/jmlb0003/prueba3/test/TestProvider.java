package com.jmlb0003.prueba3.test;

import java.util.ArrayList;

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


    
    
    private ArrayList<ContentValues> fetchData() {    	
    	ArrayList<ContentValues> toRet = new ArrayList<>();
    	    	
    	toRet.add(TestDb.createPoiCasaValues("TestCasa"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa2"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa3"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa4"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa5"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa6"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa7"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa8"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa9"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa10"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa11"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa12"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa13"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa14"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa15"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa16"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa17"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa18"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa19"));
    	toRet.add(TestDb.createPoiCasaValues("TestCasa20"));
    	
    	return toRet;
    }
    
    private ArrayList<ContentValues> fetchData2() {    	
    	ArrayList<ContentValues> toRet = new ArrayList<>();
    	    	
    	toRet.add(TestDb.createPoiCasaValues("TestCas"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas2"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas3"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas4"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas5"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas6"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas7"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas8"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas9"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas10"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas11"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas12"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas13"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas14"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas15"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas16"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas17"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas18"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas19"));
    	toRet.add(TestDb.createPoiCasaValues("TestCas20"));
    	
    	return toRet;
    }
    
    private ArrayList<ContentValues> fetchData3() {    	
    	ArrayList<ContentValues> toRet = new ArrayList<>();
    	    	
    	toRet.add(TestDb.createPoiCasaValues("TestCsa"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa2"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa3"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa4"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa5"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa6"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa7"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa8"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa9"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa10"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa11"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa12"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa13"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa14"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa15"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa16"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa17"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa18"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa19"));
    	toRet.add(TestDb.createPoiCasaValues("TestCsa20"));
    	
    	return toRet;
    }
    
    private void createEntry(double lat,double lon) {
    	long idLocation = createUserPosition(lat,lon);	//arco

		ArrayList<ContentValues> poiData = new ArrayList<>();
    	poiData = fetchData();

    	//Si se han obtenido datos, se insertan en la BD a través del Content Provider
    	if (poiData.size() > 0) {
    		//Hacemos una trampa para añadir el idLocation al final de poiData
    		ContentValues idLocationValue = new ContentValues();
    		idLocationValue.put(LocationEntry._ID, idLocation);        		
    		poiData.add(idLocationValue);

            ContentValues[] poisToInsert = new ContentValues[poiData.size()];
            poiData.toArray(poisToInsert);
            
            mContext.getContentResolver().bulkInsert(PoiEntry.CONTENT_URI, poisToInsert);
           
        }
    }
    
    
    private void createEntry2(double lat,double lon) {
    	long idLocation = createUserPosition(lat,lon);	//arco

		ArrayList<ContentValues> poiData = new ArrayList<>();
    	poiData = fetchData2();

    	//Si se han obtenido datos, se insertan en la BD a través del Content Provider
    	if (poiData.size() > 0) {
    		//Hacemos una trampa para añadir el idLocation al final de poiData
    		ContentValues idLocationValue = new ContentValues();
    		idLocationValue.put(LocationEntry._ID, idLocation);        		
    		poiData.add(idLocationValue);

            ContentValues[] poisToInsert = new ContentValues[poiData.size()];
            poiData.toArray(poisToInsert);
            
            mContext.getContentResolver().bulkInsert(PoiEntry.CONTENT_URI, poisToInsert);
           
        }
    }
    
    private void createEntry3(double lat,double lon) {
    	long idLocation = createUserPosition(lat,lon);	//arco

		ArrayList<ContentValues> poiData = new ArrayList<>();
    	poiData = fetchData3();

    	//Si se han obtenido datos, se insertan en la BD a través del Content Provider
    	if (poiData.size() > 0) {
    		//Hacemos una trampa para añadir el idLocation al final de poiData
    		ContentValues idLocationValue = new ContentValues();
    		idLocationValue.put(LocationEntry._ID, idLocation);        		
    		poiData.add(idLocationValue);

            ContentValues[] poisToInsert = new ContentValues[poiData.size()];
            poiData.toArray(poisToInsert);
            
            mContext.getContentResolver().bulkInsert(PoiEntry.CONTENT_URI, poisToInsert);
           
        }
    }
    
    public void testbulkInsert(){
    	long tInicio,tFin;
        tInicio = System.currentTimeMillis();
        
        createEntry(37.685209,-3.581173);//arco
        
        createEntry2(37.663903, -3.548809);//Salao
        
        createEntry3(37.682192, -3.543474);	//Bornos/////////////
        
        createEntry(37.728501,-3.479748);	//Sitio1
        
        createEntry2(37.740619,-3.645887);	//Pegalajar
        
        createEntry3(37.786779,-3.610379);	//Mancha Real
        
        createEntry(37.674105, -3.569006);	//Casa manu//////////////
        
        createEntry2(37.679128,  -3.559011);	//locationVegueta
        
        createEntry3(37.688069,  -3.562788);	//locationLoma////////////////

        tFin = System.currentTimeMillis();         
        Log.d(LOG_TAG, "El tiempo que ha tardado es:"+(tFin-tInicio));
    	
    }
    

    public void notestInsertReadProvider() {

    	//Primero probamos con la tabla poi
        ContentValues poiTestValues = TestDb.createPoiCasaValues("TestCasa");

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
    
    
    public void notestInsertReadLocations() {
	
    	createUserPosition(37.677568, -3.562658);	//locationBarranco
    	createUserPosition(37.679128,  -3.559011);	//locationVegueta
    	createUserPosition(37.688069,  -3.562788);	//locationLoma
    	createUserPosition(37.674105, -3.569006);	//Casa manu
    	createUserPosition(37.786779,-3.610379);	//Mancha Real
    	createUserPosition(37.740619,-3.645887);	//Pegalajar
    	createUserPosition(37.728501,-3.479748);	//Sitio1
    	createUserPosition(37.682192, -3.543474);	//Bornos
    	createUserPosition(37.663903, -3.548809);	//Salao
    	createUserPosition(37.685209,-3.581173);	//arco
  

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
    
    
    private long insertarLocation(ContentValues values) {
    	Uri locationUri = mContext.getContentResolver()
                .insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);
        Log.d(LOG_TAG,"locationUri, con rowID:"+locationRowId+" vale: "+locationUri);
        
        
        // Verify we got a row back.
        assertTrue(locationRowId != -1);        
        assertTrue(locationUri != null);
        
        return locationRowId;    	
    }
    
    private long createUserPosition(double lat, double lon) {
    	ContentValues testValues = new ContentValues();
    	testValues.put(LocationEntry.COLUMN_RADIUS,40);
    	testValues.put(LocationEntry.COLUMN_DATETEXT,"20112014");
    	testValues.put(LocationEntry.COLUMN_LOCATION_LATITUDE, lat);
    	testValues.put(LocationEntry.COLUMN_LOCATION_LONGITUDE, lon);
    	
    	
    	return insertarLocation(testValues);
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
