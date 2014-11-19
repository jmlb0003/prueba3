package com.jmlb0003.prueba3.modelo.sync;


import java.util.Collection;

import android.content.ContentValues;

/**
 * Clase que representa un proveedor de PIs.
 * @author Jose
 *
 */
public abstract class DataProvider {
	
	
	/**
	 * M�todo para obtener los datos de los PIs del proveedor.
	 * @return Colecci�n de objetos ContentValues que contiene todos los datos necesarios
	 * para crear cada poi
	 */
	public abstract Collection<ContentValues> fetchData();

}
