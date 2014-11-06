package com.jmlb0003.prueba3.controlador;

import android.content.AbstractThreadedSyncAdapter;
import android.content.Context;
import android.util.Log;


/**
 * Clase abstracta que forma parte del paquete Controlador de la app. Se encarga de 
 * sincronizar/descargar los datos para almacenarlos en la base de datos interna según 
 * la posición actual.
 * @author Jose
 *
 */
public abstract class NetworkDataProvider extends AbstractThreadedSyncAdapter {

	public final String LOG_TAG = "NetworkDataProvider";
	
	/** Número máximo de resultados que se descargarán del proveedor**/
    protected static final int MAX_RESOURCES_NUMBER = 1000;
    /** Tiempo máximo en milisegundos para lectura de los datos de este proveedor**/
    protected static final int READ_TIMEOUT = 10000;
    /** Tiempo máximo de conexión al proveedor de recursos**/
    protected static final int CONNECT_TIMEOUT = 10000;

    
	public NetworkDataProvider(Context context, boolean autoInitialize) {
		super(context, autoInitialize);

		Log.d(LOG_TAG, "Creating SyncAdapter");
	}
	
	
	/**
	 * Método en el que se implementa el protocolo de descarga para cada proveedor de PIs
	 * @param context Contexto en que se utilizará el proveedor de PIs
	 */
	protected abstract void initialize(Context context);
	
	
	/**
	 * Método con el que se lanza inmediatamente una consulta para descargar datos del proveedor.
	 * @param context Contexto en que se utilizará el proveedor de PIs
	 */
	protected abstract void syncImmediately(Context context);
	
	
	/**
	 * Método para inicializar los proveedores de descarga de PIs. Aparte de inicializar, se 
	 * hace una primera petición para descargar datos aunque después se pueden solicitar más
	 * descargas mediante la función requestSync().
	 * @param context Contexto en que se utilizará el proveedor de PIs
	 */
	public void initializeSyncAdapter(Context context) {
		initialize(context);
	}
	
	
	public void  requestSync(Context context) {
		syncImmediately(context);
	}
}
