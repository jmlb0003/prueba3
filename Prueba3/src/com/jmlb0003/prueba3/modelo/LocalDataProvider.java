package com.jmlb0003.prueba3.modelo;


import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.jmlb0003.prueba3.R;


/**
 * Clase encargada de proporcionar los markers que están almacenados en el dispositivo
 * @author Jose
 *
 */
public class LocalDataProvider {
    private List<Marker> cachedMarkers = new ArrayList<Marker>();
    private static Bitmap icon = null;
    
    public LocalDataProvider(Resources res) {
        if (res == null) {
        	throw new NullPointerException();
        }
        
        createIcon(res);
    }
    
    
    protected void createIcon(Resources res) {
        if (res == null) {
        	throw new NullPointerException();
        }
        
        icon = BitmapFactory.decodeResource(res, R.drawable.ic_launcher);
    }
    
    public List<Marker> getMarkers() {
    	Marker atl = new Marker("ATL", 39.931269, -75.051261, 0, Color.DKGRAY, icon);
        cachedMarkers.add(atl);

        Marker home = new Marker("Casa", 37.6759861, -3.5661972, 763.0, Color.YELLOW);
        cachedMarkers.add(home);

        Marker picoMagina = new Marker("Magina", 37.725048, -3.466663, 2132.0, Color.DKGRAY, icon);
        cachedMarkers.add(picoMagina);
        
        Marker cortijo = new Marker("cortijo", 37.692997,-3.565028, 912.0, Color.DKGRAY, icon);
        cachedMarkers.add(cortijo);
        
        
        Marker carcheles = new Marker("carcheles", 37.644594, -3.638578, 825.0, Color.DKGRAY, icon);
        cachedMarkers.add(carcheles);
        
        
        return cachedMarkers;
    }
}
