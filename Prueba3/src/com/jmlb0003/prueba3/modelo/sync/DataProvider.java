package com.jmlb0003.prueba3.modelo.sync;


import java.util.Collection;

import com.jmlb0003.prueba3.modelo.sync.Excepciones.NDPConectionException;

import android.content.ContentValues;

/**
 * Clase que representa un proveedor de PIs.
 * @author Jose
 *
 */
public abstract class DataProvider {
	
	
	/**
	 * Método para obtener los datos de los PIs del proveedor.
	 * @return Colección de objetos ContentValues que contiene todos los datos necesarios
	 * para crear cada poi
	 * @throws NDPConectionException Los proveedores de datos online pueden lanzar excepciones
	 * relacionadas con problemas de conexión.
	 * @throws NullPointerException Se lanza esta excepción si el resultado de fetchData es null
	 */
	public abstract Collection<ContentValues> fetchData() throws NullPointerException, NDPConectionException;

}
