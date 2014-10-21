package com.jmlb0003.prueba3.controlador;

import com.jmlb0003.prueba3.modelo.Poi;

public interface OnPoiTouchedListener {
    public void onPoiSelected(Poi poiTouched);
    
    public void onPoiUnselected(Poi poiUnselected);
}
