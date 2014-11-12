package com.jmlb0003.prueba3.controlador;



import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.jmlb0003.prueba3.modelo.WikipediaDataProvider;
import com.jmlb0003.prueba3.modelo.data.PoiContract;
import com.jmlb0003.prueba3.modelo.data.PoiContract.LocationEntry;
import com.jmlb0003.prueba3.modelo.data.PoiContract.LocationPoiEntry;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;



/**
 * Clase encargada de la gestión de las descargas y almacenamiento de PIs de forma asíncrona.
 * Antes de ejecutar la tarea, se debería haber revisado si hay una descarga anterior para la
 * posición actual (o cercana a esta).
 * @author Jose
 *
 */
public class PoiDownloaderTask extends AsyncTask<Void, Void, Void> {
	
	
	/**Variable que almacena los proveedores online de Puntos de Interés**/
	private final Map<String,NetworkDataProvider> mSources = new HashMap<String,NetworkDataProvider>(); 
    private final Context mContext;
	


    public PoiDownloaderTask(Context context) {
        mContext = context;
        
        mSources.put("wikipedia", new WikipediaDataProvider(mContext.getResources()));
    }



    /**
     * Método para guardar la posición actual en la Base de datos. A esta posición se
     * asociarán internamente los PIs que se inserten.
     * @return ID de la posición insertada en la Base de Datos.
     */
    private long insertCurrentLocation() {
		ContentValues locationValues = new ContentValues();
		locationValues.put(
				LocationEntry.COLUMN_LOCATION_LATITUDE, ARDataSource.getCurrentLocation().getLatitude());
		locationValues.put(
				LocationEntry.COLUMN_LOCATION_LONGITUDE, ARDataSource.getCurrentLocation().getLongitude());
		locationValues.put(
				LocationEntry.COLUMN_RADIUS, ARDataSource.getRadius());
		locationValues.put(
				LocationEntry.COLUMN_DATETEXT, PoiContract.getDbDateString(new Date()));
		
		//Buscar si hay un location cercano, si no insertarlo
		Uri idLocation = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI,
				locationValues);
		
		
		return ContentUris.parseId(idLocation);
    }
    
    
	@Override
	protected Void doInBackground(Void... params) {
		long idLocation = insertCurrentLocation();
		//TODO: Aquí estaría bien que se pusiera un loading o algo de que se están descargando e insertando cosas
		//TODO: En vez de esperar a que se descargue y se inserte todo, se pueden ir añadiendo a una lista de puntos directamente pa ir cogiendo de ahi???
		
		//Recorremos cada proveedor de PIs para descargar y parsear los datos
        for (NetworkDataProvider source: mSources.values()) {
        	ArrayList<ContentValues> poiData = new ArrayList<>();
        	poiData = source.fetchData();
        	
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
        
        return null;
	}

	

}