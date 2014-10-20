package com.jmlb0003.prueba3.modelo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import com.jmlb0003.prueba3.R;


/**
 * Clase encargada de proporcionar los markers que están almacenados en el dispositivo
 * @author Jose
 *
 */
public class LocalDataProvider {
    private List<Marker> cachedMarkers = new ArrayList<Marker>();
    private static Bitmap sIcon = null;
    private static Bitmap sSelectedIcon = null;
    
    public LocalDataProvider(Resources res) {
        if (res == null) {
        	throw new NullPointerException();
        }
        
        createIcons(res);
    }
    
    
    protected void createIcons(Resources res) {
        if (res == null) {
        	throw new NullPointerException();
        }
        
        sIcon = BitmapFactory.decodeResource(res, R.drawable.icono_pi);
        sSelectedIcon = BitmapFactory.decodeResource(res, R.drawable.icono_pi_seleccionado);
    }
    
    public List<Marker> getMarkers() {
    	Map<String, Object> datos = new HashMap<String, Object>();
    	datos.put("ID", 0);
    	datos.put("color", Color.DKGRAY);
    	datos.put("imagen", sIcon);
    	datos.put("descripcion", "Este es el PI alternativo que está situado en Nueva York");
    	datos.put("sitio_web", "www.ninguno.com");
    	datos.put("precio", 0);
    	datos.put("horario_apertura", "00:00");
    	datos.put("horario_cierre", "01:00");
    	datos.put("edad_maxima", 43);
    	datos.put("edad_minima", 3);
    	    	
    	Marker atl = new Marker("ATL", 39.931269, -75.051261, 0, new DetallesPI(datos), sIcon, sSelectedIcon);
        cachedMarkers.add(atl);
        
        datos.put("ID", 1);
        Log.d("LocalDataProvider","Se vaa a dar el color:"+(Color.YELLOW));
    	datos.put("color", Color.YELLOW);
    	Log.d("LocalDataProvider","1Se vaa a dar el color:"+(datos.get("color")));
    	Log.d("LocalDataProvider","2Se vaa a dar el color:"+((int)datos.get("color")));
    	datos.put("imagen", sIcon);
    	datos.put("descripcion", "Este es mi casa. Vivo en Cambil...Y esta es la descripción más larga que voy a poner");
    	datos.put("sitio_web", "joselopez.hol.es");
    	datos.put("precio", 10);
    	datos.put("horario_apertura", "00:00");
    	datos.put("horario_cierre", "23:00");
    	datos.put("edad_maxima", 25);
    	datos.put("edad_minima", 1);

        Marker home = new Marker("Casa", 37.6759861, -3.5661972, 763.0, new DetallesPI(datos));
        cachedMarkers.add(home);

        
        datos = new HashMap<String, Object>();
        datos.put("ID", 2);
    	datos.put("color", Color.DKGRAY);
    	datos.put("imagen", sIcon);
    	datos.put("descripcion", "Este es el pico de Mágina. Tiene 2300 metros o así y es donde compruebo si apuntan bien los pinchicos");
    	datos.put("sitio_web", "joselopez.hol.es");
    	datos.put("precio", 10);
    	datos.put("horario_apertura", "00:00");
    	datos.put("horario_cierre", "00:00");
    	datos.put("edad_maxima", 69);
    	datos.put("edad_minima", 1);
    	
        Marker picoMagina = new Marker("Magina", 37.725048, -3.466663, 2132.0, new DetallesPI(datos), sIcon, sSelectedIcon);
        cachedMarkers.add(picoMagina);
        
        
        datos = new HashMap<String, Object>();
        datos.put("ID", 3);
    	datos.put("color", Color.DKGRAY);
    	datos.put("imagen", sSelectedIcon);
    	datos.put("descripcion", "Esto es donde está mi cortijo");
    	datos.put("sitio_web", "fiestas de la loma.com");
    	datos.put("precio", 10);
    	datos.put("horario_apertura", "00:00");
    	datos.put("horario_cierre", "00:00");
    	datos.put("edad_maxima", 69);
    	datos.put("edad_minima", 1);
    	
        Marker cortijo = new Marker("cortijo", 37.692997,-3.565028, 912.0, new DetallesPI(datos), sIcon, sSelectedIcon);
        cachedMarkers.add(cortijo);
        
        datos = new HashMap<String, Object>();
        datos.put("ID", 4);
    	datos.put("color", Color.DKGRAY);
    	datos.put("imagen", sSelectedIcon);
    	datos.put("descripcion", "Esto es donde vive Blas...El día que me cabree, en esa dirección vive");
    	datos.put("sitio_web", "www.aBlasLeGustaLaFiesta.es");
    	datos.put("precio", 123);
    	datos.put("horario_apertura", "22:00");
    	datos.put("horario_cierre", "08:00");
    	datos.put("edad_maxima", 34);
    	datos.put("edad_minima", 10);
    	
        Marker carcheles = new Marker("carcheles", 37.644594, -3.638578, 825.0, new DetallesPI(datos), sIcon, sSelectedIcon);
        cachedMarkers.add(carcheles);
        
        
        return cachedMarkers;
    }
}
