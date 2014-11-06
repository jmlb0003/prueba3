package com.jmlb0003.prueba3.controlador;

import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;
import android.util.Log;


/**
 * Clase abstracta que forma parte del paquete Controlador de la app. Se encarga de 
 * sincronizar/descargar los datos para almacenarlos en la base de datos interna seg�n 
 * la posici�n actual.
 * @author Jose
 *
 */
public abstract class NetworkDataProvider extends AbstractThreadedSyncAdapter {

	public final String LOG_TAG = "NetworkDataProvider";
	
	/** N�mero m�ximo de resultados que se descargar�n del proveedor**/
    protected static final int MAX_RESOURCES_NUMBER = 1000;
    /** Tiempo m�ximo en milisegundos para lectura de los datos de este proveedor**/
    protected static final int READ_TIMEOUT = 10000;
    /** Tiempo m�ximo de conexi�n al proveedor de recursos**/
    protected static final int CONNECT_TIMEOUT = 10000;

    
	public NetworkDataProvider(Context context, boolean autoInitialize) {
		super(context, autoInitialize);

		Log.d(LOG_TAG, "Creating SyncAdapter");
	}
	
	
	/**
	 * M�todo en el que se implementa el protocolo de descarga para cada proveedor de PIs
	 * @param context Contexto en que se utilizar� el proveedor de PIs
	 */
	protected abstract void initialize(Context context);
	
	
	/**
	 * M�todo con el que se lanza inmediatamente una consulta para descargar datos del proveedor.
	 * @param context Contexto en que se utilizar� el proveedor de PIs
	 */
	protected abstract void syncImmediately(Context context);
	
	
	/**
	 * M�todo para inicializar los proveedores de descarga de PIs. Aparte de inicializar, se 
	 * hace una primera petici�n para descargar datos aunque despu�s se pueden solicitar m�s
	 * descargas mediante la funci�n requestSync().
	 * @param context Contexto en que se utilizar� el proveedor de PIs
	 */
	public void initializeSyncAdapter(Context context) {
		initialize(context);
	}
	
	
	public void  requestSync(Context context) {
		syncImmediately(context);
	}
}
