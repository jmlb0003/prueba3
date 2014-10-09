package com.jmlb0003.prueba3.controlador;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.LocalDataProvider;
import com.jmlb0003.prueba3.modelo.Marker;
import com.jmlb0003.prueba3.modelo.NetworkDataProvider;
import com.jmlb0003.prueba3.modelo.WikipediaDataProvider;

/*
 * TODO Orden de los imports CODE GUIDELINES
 * Android imports
 * Imports from third parties (com, junit, net, org)
 * java and javax
 */

/**
 * 
 * @author Jose
 *
 */
public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener,
				LocationListener/*, OnSharedPreferenceChangeListener*/ {
	
	/**
	 * Nomenclatura de variables CODE GUIDELINES
	 * Non-public, non-static field names start with m.
	 * Static field names start with s.
	 * Public static final fields (constants) are ALL_CAPS_WITH_UNDERSCORES.
	 * Other fields start with a lower case letter.
	 */
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final String CLASS_TAG = "mainActivity";
	
	/*******************CONSTANTES URLs**********************************/
	//private static final String TAG = "MainActivity";	//Para el log
    private static final String USERNAME = "jmlb0003";	//Usuario para json de wikipedia
    //TODO: Hay que investigar cómo parametrizar esta constante...
    private static final String LOCALE = "es";		//Para el idioma de los resultados de wikipedia
    
    
    /*******************CONSTANTES PARA LOCALIZACION**********************************/
    //Tiempo mínimo en milisegundos entre lecturas de los sensores de posición
    private static final int MIN_TIME = 30*1000;
    //Variable para el descartar lecturas de ubicaciones en el método isBetterLocation
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    //Distancia mínima en metros entre lecturas de los sensores de posición
    private static final int MIN_DISTANCE = 50;
    
    
    /***************CONSTANTES DESCARGAS DE PIs DE LAS DISTINTAS APIs***********************/
    //Estas variables permiten descargar los recursos en tareas asíncronas sin bloquear la interfaz
    private static final BlockingQueue<Runnable> QUEUE = new ArrayBlockingQueue<Runnable>(1);
    private static final ThreadPoolExecutor DOWNLOADS_SERVICE = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, QUEUE);
	private static final Map<String,NetworkDataProvider> SOURCES = new ConcurrentHashMap<String,NetworkDataProvider>(); 
	
	
    /**********************VARIABLES LOCALIZACION********************************/
    //Sensor de posición del dispositivo (GPS,WIFI)
    private static LocationManager sLocationMgr = null;
    //Variable que almacena la posición
    private Location mLocation = null;
    //Variable con el radio de búsqueda de PI del radar
    private float mRadarSearch;
    
    /***Variable con la densidad de píxeles por pulgada***/
    private float mDp;
    
    
	private FragmentModoCamara mFragmentModoCamara;
//	private FragmentModoMapa mFragmentModoMapa;
	private FragmentManager mFragmentManager;



	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Se aplican los ajustes por defecto
//		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		//Registrar el observador para que se notifiquen los cambios en las preferencias
		//PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);


		//Se indica a MainActivity que la vista que tiene que usar para meter todo el contenido que
		//sigue ahora es activity_main
		setContentView(R.layout.activity_main);
		
        
        //Se obtiene el servicio para controlar las actualizaciones de ubicación del dispositivo
        sLocationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
		
		mFragmentModoCamara = new FragmentModoCamara();
//		mFragmentModoMapa = new FragmentModoMapa();
		
		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section_modoCamara),
								getString(R.string.title_section_modoMapa) }), this);
		
		
		
		mFragmentManager = getSupportFragmentManager();
		Log.i(CLASS_TAG, "PantallaPrincipal:callbacks hechos y hola mundo");
		mFragmentManager.beginTransaction().add(R.id.container, mFragmentModoCamara).commit();
		Log.i(CLASS_TAG, "PantallaPrincipal:añadido el fragment de la camara");
//		mFragmentManager.beginTransaction().add(R.id.container, mFragmentModoMapa).commit();
		Log.i(CLASS_TAG, "PantallaPrincipal:Añadido el fragment del mapa_:FIN");
		
		
		aplicarValoresDeAjustes();
		
		//Ahora se cargan en memoria los PIs disponibles en el dispositivo
		LocalDataProvider localData = new LocalDataProvider(this.getResources());
        ARDataSource.addMarkers(localData.getMarkers());
        //Se añaden recursos a la colección SOURCES para descargar PIs
        NetworkDataProvider wikipedia = new WikipediaDataProvider(this.getResources());
        SOURCES.put("wiki",wikipedia);
  
        
        //Obtenemos la densidad de píxeles de la pantalla para dibujar los componentes
        //convirtiendo px en dp
        DisplayMetrics dm = new DisplayMetrics();        
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDp = dm.density;
        
		
	}// Fin de onCreate()
	
	
	
	
	/**
	 * Se llama cada vez que la actividad está preparada para interactuar con el usuario.
	 * Se encarga de iniciar la lectura de la posición del dispositivo.
	 */
	@Override
    public void onResume() {
        super.onResume();        
        
        aplicarValoresDeAjustes();
  
        try {            
            
            sLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
            		MIN_TIME, MIN_DISTANCE, this);
            sLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
            		MIN_TIME, MIN_DISTANCE, this);

            try {
            	
            	Location gps = null;
            	Location network = null;
            	
            	if (sLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            		gps = sLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            	}
            	if (sLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            		network = sLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            	}
            	
            	
            	if ( (gps != null) && (isBetterLocation(gps, network)) ) {
            		mLocation = gps;
            	}else {
            		if (network != null) {
            			mLocation = network;
            		}else {
            			//Si no hay localización ninguna, por defecto se pone la de la biblioteca de la UJA
            			//TODO: aqui hay que hacer algo...meter la ultima ubicacion, o por lo menos un mensaje de que no hay ubicaciones
            			mLocation.setLatitude(37.789);
            			mLocation.setLongitude(-3.779);
                		mLocation.setAltitude(700);
                		
            		}
            	}
            	
            	onLocationChanged(mLocation);            	

            	
            	
            } catch (Exception ex) {
            	ex.printStackTrace();
            }
        } catch (Exception ex1) {
            try {
                if (sLocationMgr != null) {
                	sLocationMgr.removeUpdates(this);
                }
            } catch (Exception ex2) {
            	ex2.printStackTrace();
            }
        }

    }// Fin de onResume()
	
	
	/**
	 * Se llama cada vez que la actividad deja de estar en primer plano.
	 * Se encarga de parar las lecturas de los sensores del dispositivo.
	 */
    public void onPause() {
        super.onPause();

        try {
        	sLocationMgr.removeUpdates(this);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }// Fin de onPause

    
    
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			/*case R.id.action_search:
	            // search action
	            return super.onOptionsItemSelected(item);
	        case R.id.action_help:
	            //helpAction();
	            return super.onOptionsItemSelected(item);*/
	        case R.id.action_settings:
	        	// Settings action
	        	Log.d("MainActivity","Va a lanzar los ajustes");
	        	startActivity(new Intent(this,SettingsActivity.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		
		if (position == 0) {
			ft.show(mFragmentModoCamara);
			ft.replace(R.id.container, mFragmentModoCamara);
			/**
			 * Con esto se pone una especie de animacion al cambiar de fragment pero tambien
			 * se ve un parpadeo con el color de la pantalla anterior...
			 * */
			//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);	
			//ft.addToBackStack(null);		//Quitar esto porque da fallos y no es necesario
			
		}else{
			if (position ==1) {
//				ft.show(mFragmentModoMapa);
//				ft.replace(R.id.container, mFragmentModoMapa);
				//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				//ft.addToBackStack(null);	//Quitar esto porque da fallos y no es necesario
				
			}else{
//				if (mFragmentModoMapa.isVisible()){
//					ft.hide(mFragmentModoMapa);
//				}
				if(mFragmentModoCamara.isVisible()) {
					ft.hide(mFragmentModoCamara);
				}
				
			}
		}
		
		ft.commit();
		
		return true;
	}


	/**
	 * Método que se ejecuta cada vez que se obtiene una lectura de la localización válida
	 */
	@Override
	public void onLocationChanged(Location newLocation) {
		
		// Solo se tiene en cuenta la nueva ubicación si es mejor que la anterior 
		// (más precisa y reciente)
		if (isBetterLocation(newLocation, ARDataSource.getCurrentLocation())) {
    		Log.i("SensorsActivity","la posicion nueva es mejor: La:"+newLocation.getLatitude()+" lo:"+newLocation.getLongitude()+" Precision:"+newLocation.getAccuracy()+"\nEl anterior tenia precision: "+ARDataSource.getCurrentLocation().getAccuracy());
    		ARDataSource.setCurrentLocation(newLocation);

	        mFragmentModoCamara.calcularMagneticNorthCompensation();
	        
	        //Se actualizan/descargan los PIs según la posición obtenida
	        updateData(newLocation.getLatitude(),newLocation.getLongitude(),newLocation.getAltitude());
		}else{
    		Log.d("SensorsActivity","la posicion anterior es mejor: La:"+ARDataSource.getCurrentLocation().getLatitude()+" lo:"+ARDataSource.getCurrentLocation().getLongitude()+" Precision:"+ARDataSource.getCurrentLocation().getAccuracy()+"\nLa nueva tenia precision: "+newLocation.getAccuracy());
    	}
		
	}


	@Override
	public void onProviderDisabled(String arg0) {
		//No se utiliza
		
	}


	@Override
	public void onProviderEnabled(String arg0) {
		//No se utiliza
		
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		//No se utiliza		
	}


	


	
	
	
	/** 
	 * Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     * @see http://developer.android.com/guide/topics/location/strategies.html#BestEstimate
     */
	private boolean isBetterLocation(Location location, Location currentBestLocation) {
       if (currentBestLocation == null) {
           return true;
       }

       //Comprobar si la nueva ubicación es más o menos reciente que la anterior
       long timeDelta = location.getTime() - currentBestLocation.getTime();
       boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
       boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
       boolean isNewer = timeDelta > 0;

       // Si hace más de dos minutos de la última ubicación, usar la nueva por si el usuario 
       // se ha movido
       if (isSignificantlyNewer) {
           return true;           
       } else if (isSignificantlyOlder) {
           return false;
       }

       // Comprobar las precisiones de las ubicaciones
       int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
       boolean isLessAccurate = accuracyDelta > 0;
       boolean isMoreAccurate = accuracyDelta < 0;
       boolean isSignificantlyLessAccurate = accuracyDelta > 200;

       
       boolean isFromSameProvider = isSameProvider(location.getProvider(),
               currentBestLocation.getProvider());

       // Determinar la mejor localización según las variables calculadas
       if (isMoreAccurate) {
           return true;
       } else if (isNewer && !isLessAccurate) {
           return true;
       } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
           return true;
       }
       return false;
   }

   /** Checks whether two providers are the same */
   private boolean isSameProvider(String provider1, String provider2) {
       if (provider1 == null) {
    	   return provider2 == null;
       }
       
       return provider1.equals(provider2);
   }
   
   
   /**
    * Método que actualiza el conjunto de PIs disponibles en memoria según la última localización
    * válida del dispositivo.
    * @param lat Latitud de la última posición válida del dispositivo
    * @param lon Longitud de la última posición válida del dispositivo
    * @param alt Altitud de la última posición válida del dispositivo
    */
   private void updateData(final double lat, final double lon, final double alt) {
       try {
    	   DOWNLOADS_SERVICE.execute(
    			   new Runnable() {
    				   public void run() {
    					   for (NetworkDataProvider source : SOURCES.values()) {
    						   download(source, lat, lon, alt);
    					   }
    						   
    				   }
    			   }
           );
       } catch (RejectedExecutionException rej) {
           Log.w("FragmentModoCamara", "Not running new download Runnable, queue is full.");
       } catch (Exception e) {
           Log.e("FragmentModoCamara", "Exception running download Runnable.",e);
       }
   }
   
   
   private static boolean download(NetworkDataProvider source, double lat, double lon, double alt) {
		if (source == null) {
			return false;
		}
		
		String url = null;
		try {
			url = source.createRequestURL(lat, lon, alt, ARDataSource.getRadius(), LOCALE, USERNAME);    	
		} catch (NullPointerException e) {
			return false;
		}
   	
		List<Marker> markers = null;
		try {
			markers = source.parse(url);
		} catch (NullPointerException e) {
			return false;
		}
		
		ARDataSource.addMarkers(markers);
		
		
		return true;
   }
   
   
   /**
    * Método con el que se lee cada ajuste que pueda haber aplicado el usuario para que se 
    * refleje en la aplicación
    */
   private void aplicarValoresDeAjustes() {
	   //Se construyen las preferencias con los valores por defecto de la definición del XML si
	   //no hay preferencias anteriores guardadas. Si las hay, se salta este paso.
	   PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
	   //Obtenemos la distancia hasta la que se buscan PI en los ajustes
	   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	   mRadarSearch = prefs.getInt(getString(R.string.pref_seekBar_distance_key),0);
	   ARDataSource.updateDataWithMaxDistance(mRadarSearch);
	   
	   
	   
	   /***********Recorriendo todas las preferencias almacenadas******************/
	   Map<String,?> keys = prefs.getAll();
	   Log.d("map values!","En prefs hay: "+ keys.size()+ " valores");
	   for(Map.Entry<String,?> entry : keys.entrySet()) {
		   Log.d("map values!",entry.getKey() + ": " +  entry.getValue().toString());
	   }
	   /************************************/
	   
   }




//@Override
//public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
//	
//	if ( key.equals(getString(R.string.pref_seekBar_distance_key)) ) {
//		Log.d("MainActivity","En el main, ha cambiado la barra"+sharedPreferences.getInt(key, 123456));
//	}
//}
   
   

//    @Override
//	protected void updateDataOnZoom() {
//	    super.updateDataOnZoom();
//        Location last = ARData.getCurrentLocation();
//        updateData(last.getLatitude(),last.getLongitude(),last.getAltitude());
//        Log.d("mainActivity","La posicion actual es: LAT:"+last.getLatitude()+" - LON:"+last.getLongitude()+" - ALT:"+last.getAltitude());
//	}
    
   
   


}
