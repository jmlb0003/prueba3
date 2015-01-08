package com.jmlb0003.prueba3.controlador;

import com.jmlb0003.prueba3.modelo.Poi;

public interface OnPoiTouchedListener {
	
	/**
	 * Implementa las acciones que debe realizar el observador cuando se pulse un PI
	 * @param poiTouched	PI pulsado
	 */
    public void onPoiTouched(Poi poiTouched);
    
    public void onPoiUnselected(Poi poiUnselected);
}
