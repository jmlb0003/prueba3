package com.jmlb0003.prueba3.modelo.sync;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.modelo.data.PoiContract;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;



/**
 * Clase encargada de la gesti�n de las descargas y almacenamiento de PIs de forma as�ncrona.
 * Antes de ejecutar la tarea, se deber�a haber revisado si hay una descarga anterior para la
 * posici�n actual (o cercana a esta).
 * @author Jose
 *
 */
public class PoiDownloaderTask extends AsyncTask<Void, Integer, Void> {
	
	private static final String LOG_TAG = "poiDownloaderTask";
	
	/** Variable que almacena los proveedores online de Puntos de Inter�s **/
	private final Map<String,NetworkDataProvider> mSources = new HashMap<String,NetworkDataProvider>(); 
    private final Context mContext;
    
    private ProgressDialog mProgressDialog;
	


    /**
     * Constructor de la tarea de fondo que descarga de internet todos los recursos predeterminados
     * @param context
     * @param sources
     */
    public PoiDownloaderTask(Context context, Map<String,NetworkDataProvider> sources) {
        mContext = context;
        Log.d(LOG_TAG, "creando el asyncTask");
        Set<String> keySet = sources.keySet();
        for(String key: keySet) {
        	Log.d(LOG_TAG, "Metiendo el source:"+key);
        	mSources.put(key, sources.get(key));
        }        
    }
    
    
    @Override
    protected void onPreExecute() {

    	if (mProgressDialog != null) {
    		Log.d(LOG_TAG,"Ha habido que llamar al dimiss...");
        	mProgressDialog.dismiss();
        	mProgressDialog = null;        	
        }

    	mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(R.string.downloading);
        mProgressDialog.setMessage(mContext.getString(R.string.downloading_pois_description));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }
	
    

	@Override
	protected Void doInBackground(Void... params) {
		Log.d(LOG_TAG, "Ejecutando el asyncTask");
		long idLocation = insertCurrentLocation();
		ContentValues idLocationValue = new ContentValues();
		
		
		//Recorremos cada proveedor de PIs para descargar y parsear los datos
        for (NetworkDataProvider source: mSources.values()) {
        	ArrayList<ContentValues> poiData = new ArrayList<>();
        	poiData = source.fetchData();
        	
        	//Si se han obtenido datos, se insertan en la BD a trav�s del Content Provider
        	if (poiData.size() > 0) {
        		//Hacemos una trampa para a�adir el idLocation al final de poiData
        		idLocationValue.clear();
        		idLocationValue.put(PoiContract.LocationEntry._ID, idLocation);        		
        		poiData.add(idLocationValue);
        		
                ContentValues[] poisToInsert = new ContentValues[poiData.size()];
                poiData.toArray(poisToInsert);
                mContext.getContentResolver().bulkInsert(
                		PoiEntry.CONTENT_URI, poisToInsert);
                
                
                //Ahora cargamos en memoria los PIs de este proveedor
                cargarPIs();
            }
        }
        
        
        mProgressDialog.dismiss();
        mProgressDialog = null;
        return null;
	}


    protected void onPostExecute() {
    	mProgressDialog.dismiss();
    	mProgressDialog = null;
    }
	
	
	/**
     * M�todo para guardar la posici�n actual en la Base de datos. A esta posici�n se
     * asociar�n internamente los PIs que se inserten.
     * @return ID de la posici�n insertada en la Base de Datos.
     */
    private long insertCurrentLocation() {
		ContentValues locationValues = new ContentValues();
		
		locationValues.put(PoiContract.LocationEntry.COLUMN_LOCATION_LATITUDE, 
				ARDataSource.getCurrentLocation().getLatitude());
		locationValues.put(PoiContract.LocationEntry.COLUMN_LOCATION_LONGITUDE, 
				ARDataSource.getCurrentLocation().getLongitude());
		locationValues.put(PoiContract.LocationEntry.COLUMN_RADIUS,
				ARDataSource.getRadius());
		locationValues.put(PoiContract.LocationEntry.COLUMN_DATETEXT,
				PoiContract.getDbDateString(new Date()));


		Uri idLocation = mContext.getContentResolver().insert(
				PoiContract.LocationEntry.CONTENT_URI, locationValues);
		
		
		return ContentUris.parseId(idLocation);
    }
    

    
    private void cargarPIs() {
    	String lat = Double.toString(ARDataSource.getCurrentLocation().getLatitude());
    	String lon = Double.toString(ARDataSource.getCurrentLocation().getLongitude());
    	
    	
    	Cursor pois = mContext.getContentResolver().query(
    			PoiEntry.buildLocationUriWithCoords(lat, lon),
    			null, 
    			null, 
    			null, 
    			null);

    	ARDataSource.addPoisFromCursor(pois);
    	
    	pois.close();
    }

}