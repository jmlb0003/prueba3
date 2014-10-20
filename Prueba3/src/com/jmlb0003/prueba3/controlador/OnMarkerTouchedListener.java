package com.jmlb0003.prueba3.controlador;

import com.jmlb0003.prueba3.modelo.Marker;

public interface OnMarkerTouchedListener {
    public void onMarkerSelected(Marker markerTouched);
    
    public void onMarkerUnselected(Marker markerUnselected);
}
