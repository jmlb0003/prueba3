package com.jmlb0003.prueba3.controlador;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.LocalDataProvider;
import com.jmlb0003.prueba3.modelo.NetworkDataProvider;
import com.jmlb0003.prueba3.modelo.Poi;
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
				LocationListener,OnPoiTouchedListener {
	
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
    /**Tiempo mínimo en milisegundos entre lecturas de los sensores de posición (60 segundos)**/
    private static final int MIN_TIME = 60*1000;
    /**Constante para el descartar lecturas de ubicaciones en el método isBetterLocation**/
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    /**Distancia mínima en metros entre lecturas de los sensores de posición (50 metros)**/
    private static final int MIN_DISTANCE = 50;
    /**Constante con el tiempo en milisegundos para descartar una lectura de posición anterior**/
    //TODO:Esta modificado para que no incordie con las posiciones. Volver a dejarlo en 30 minutos
    private static final int THIRTY_MINUTES = 30*60*1000    *2*24;
    
    
    /***************CONSTANTES DESCARGAS DE PIs DE LAS DISTINTAS APIs***********************/
    //Estas variables permiten descargar los recursos en tareas asíncronas sin bloquear la interfaz
    private static final BlockingQueue<Runnable> QUEUE = new ArrayBlockingQueue<Runnable>(1);
    private static final ThreadPoolExecutor DOWNLOADS_SERVICE = new ThreadPoolExecutor(1, 1, 20, TimeUnit.SECONDS, QUEUE);
    /**Variable que almacena los proveedores online de Puntos de Interés**/
	private static final Map<String,NetworkDataProvider> SOURCES = new ConcurrentHashMap<String,NetworkDataProvider>(); 
	
	
    /**********************VARIABLES LOCALIZACION********************************/
    //Sensor de posición del dispositivo (GPS,WIFI)
    private static LocationManager sLocationMgr = null;
    //Variable que almacena la posición
    private Location mLocation = null;
    //Variable que controla si se ha encontrado alguna ubicación válida
    private boolean hayLocation;
    //Variable con el radio de búsqueda de PI del radar
    private float mRadarSearch;
    
    /**Variable que almacena un Poi pulsado**/
    private Poi mTouchedPoi = null;
    
    /**Indica si se han inicializado los recursos de donde se obtienen los PIs**/
    private boolean isDataSourcesInit = false;
    
    
    
	private FragmentModoCamara mFragmentModoCamara;
	private FragmentModoMapa mFragmentModoMapa;
	private FragmentManager mFragmentManager;



	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MainActivity","EV onCreate");
		if (mTouchedPoi != null) {
			Log.d("mainactivity","Y hay PoiSelected");
		}else{
			Log.d("mainactivity","Y NO hay PoiSelected");
		}


		//Se indica a MainActivity que la vista que tiene que usar para meter todo el contenido que
		//sigue ahora es activity_main
		setContentView(R.layout.activity_main);
		
//		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//        }
		
        
        //Se obtiene el servicio para controlar las actualizaciones de ubicación del dispositivo
        sLocationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        hayLocation = false;
		
		mFragmentModoCamara = new FragmentModoCamara();
		mFragmentModoMapa = new FragmentModoMapa();
		
//		// Set up the action bar to show a dropdown list.
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


		//No se pueden reemplazar los fragments añadidos por XML, por lo que hay que añadirlos 
		//de forma dinámica. 
		mFragmentManager.beginTransaction().add(R.id.container, mFragmentModoCamara).commit();
		Log.d(CLASS_TAG, "PantallaPrincipal:añadido el fragment de la camara");
		mFragmentManager.beginTransaction().add(R.id.container, mFragmentModoMapa).commit();
		Log.d(CLASS_TAG, "PantallaPrincipal:Añadido el fragment del mapa_:FIN");

		
		aplicarValoresDeAjustes();
		
	}// Fin de onCreate()
	
	
	
	
	/**
	 * Se llama cada vez que la actividad está preparada para interactuar con el usuario.
	 * Se encarga de iniciar la lectura de la posición del dispositivo.
	 */
	@Override
    public void onResume() {
        super.onResume();
        Log.d("MainActivity","EV onResume");
        
        //Con esto se mantiene la pantalla encendida mientras se use la app en esta activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        aplicarValoresDeAjustes();
  
        
        getLocation();
        
        if (mLocation != null) {
        	onLocationChanged(mLocation);
        	
        	cargarPIs();
        	
        	//TODO: Esto es provisional...hay que poner en condiciones lo de descargar/almacenar los datos
        	updateData(mLocation.getLatitude(),
        			mLocation.getLongitude(),
        			mLocation.getAltitude());
        }

    }// Fin de onResume()
	
	
	
	
	
	/**
	 * Se llama cada vez que la actividad deja de estar en primer plano.
	 * Se encarga de parar las lecturas de los sensores del dispositivo.
	 */
    public void onPause() {
        super.onPause();
        Log.d("MainActivity","EV onPause");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
	        	startActivity( new Intent(this,SettingsActivity.class) );
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
		
		switch (position) {
			case 0:
				ft.show(mFragmentModoCamara);
				ft.replace(R.id.container, mFragmentModoCamara);
//				  Con esto se pone una especie de animacion al cambiar de fragment pero tambien
//				  se ve un parpadeo con el color de la pantalla anterior...
				//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);	
				//ft.addToBackStack(null);		//Quitar esto porque da fallos y no es necesario
				
				ft.commit();
				
				break;
			case 1:
				ft.show(mFragmentModoMapa);
				ft.replace(R.id.container, mFragmentModoMapa);
				//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				//ft.addToBackStack(null);	//Quitar esto porque da fallos y no es necesario
				
				ft.commit();
				
				break;
	
			default:
				break;
		}
	
		return true;
	}


	/**
	 * Método que se ejecuta cada vez que se obtiene una lectura de la localización válida
	 */
	@Override
	public void onLocationChanged(Location newLocation) {
		
		//Si hayLocation es false y la posición es distinta de la default, es una posición
		//válida y se vuelve a los intervalos de tiempo normales
		if (!hayLocation && ((newLocation.getLatitude() != 0) || (newLocation.getLongitude() != 0)) ) {
			Log.d("MAINACTIVITY","Se restablece el intervalo de los providers");
			setLocationProviders(MIN_TIME);
		}else{
			if (!hayLocation) {
				Log.d("MAINACTIVITY","haylocation:"+hayLocation+" latitude y longitude:"+newLocation.getLatitude()+" "+newLocation.getLongitude() );
				mostrarLocationAlert();
			}
		}
		
		// Solo se tiene en cuenta la nueva ubicación si es mejor que la anterior 
		// (más precisa y reciente)
		if (isBetterLocation(newLocation, ARDataSource.getCurrentLocation())) {
    		Log.d(CLASS_TAG,"la posicion nueva es mejor: La:"+newLocation.getLatitude()+" lo:"+newLocation.getLongitude()+" Precision:"+newLocation.getAccuracy()+"\nEl anterior tenia precision: "+ARDataSource.getCurrentLocation().getAccuracy());
    		ARDataSource.setCurrentLocation(newLocation);

	        mFragmentModoCamara.calcularMagneticNorthCompensation();
	        
	        //Se actualizan/descargan los PIs según la posición obtenida
	        updateData(newLocation.getLatitude(),
	        			newLocation.getLongitude(),
	        			newLocation.getAltitude());
		}else{
    		Log.d(CLASS_TAG,"la posicion anterior es mejor: La:"+ARDataSource.getCurrentLocation().getLatitude()+" lo:"+ARDataSource.getCurrentLocation().getLongitude()+" Precision:"+ARDataSource.getCurrentLocation().getAccuracy()+"\nLa nueva tenia precision: "+newLocation.getAccuracy());
    	}
		
		
	}


	@Override
	public void onProviderDisabled(String arg0) {
		//Función de onLocationListener - No se utiliza
		if (!sLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
				!sLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			
			mostrarSettingsAlert();
		}
		
	}


	@Override
	public void onProviderEnabled(String arg0) {
		//Función de onLocationListener - No se utiliza
		
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		//Función de onLocationListener - No se utiliza		
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
	   if (isDataSourcesInit) {
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
   }
   
   
   private static boolean download(NetworkDataProvider source, double lat, double lon, double alt) {
		if (source == null) {
			return false;
		}
		
		String url = null;
		try {
			url = source.createRequestURL(lat, lon, alt, ARDataSource.getRadius(), LOCALE, USERNAME);
			Log.d("mainactivity","createrequestURL de:"+url);
		} catch (NullPointerException e) {
			return false;
		}
   	
		List<Poi> pois = null;
		try {
			pois = source.parse(url);
		} catch (NullPointerException e) {
			return false;
		}
		
		ARDataSource.addPois(pois);
		
		
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
   
   
   
   /**
    * Método para gestionar el registro en los proveedores de localización y obtener una posición
    * válida. Si no hay forma de obtener una posición, se fija la posición por defecto que es la 
    * biblioteca de la UJA.
    */
   private void getLocation() {
		boolean isGPSEnabled = false;
		boolean isNetworkEnabled = false;
		Time now = new Time();
		
		
		
		now.setToNow();
		
		
		try {
			Location gps = null;
			Location network = null;
       	
			//Obtenemos la última posición almacenada de los proveedores y comprobamos
			//si es válida
			if (sLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				isGPSEnabled = true;
				gps = sLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				sLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						MIN_TIME, MIN_DISTANCE, this);
				
				//Si la localización es de hace más de 30 minutos se descarta
				if ( gps != null && (now.toMillis(true) - gps.getTime()) > THIRTY_MINUTES ) {
					gps = null;
				}
				
				
			}
			if (sLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				isNetworkEnabled = true;
				network = sLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				sLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						MIN_TIME, MIN_DISTANCE, this);
				
				//Si la localización es de hace más de 30 minutos se descarta
				if ( network != null && (now.toMillis(true) - network.getTime()) > THIRTY_MINUTES ) {
					network = null;
				}
			}
    	   
    	   
			if (!isGPSEnabled && !isNetworkEnabled) {
				mostrarSettingsAlert();
			}
    	   
			//Si hay posición de los dos proveedores nos quedamos con la mejor
			if ( (gps != null) && (network != null) ) {
				if (isBetterLocation(gps, network)) {
					Log.d("MainActivity","LastKnownLocation: mejor la del GPS");
					mLocation = gps;
					
				}else {
					Log.d("MainActivity","LastKnownLocation: mejor la de network");
					mLocation = network;
				}
    		   
				//Si no, nos quedamos con la que haya
			}else {
				if (gps != null) {	
					Log.d("MainActivity","LastKnownLocation: solo hay de network");
					mLocation = gps;
				}else {
					if (network != null) {
						Log.d("MainActivity","LastKnownLocation: solo hay del GPS");
						mLocation = network;
						
					}else {
						hayLocation = false;
						//Si no hay localización ninguna, por defecto se pone la de la biblioteca de la UJA
						Log.e("MainActivity","No hay ubicación, se pone la de la biblioteca");
						
						mLocation = sLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						setLocationProviders(5*1000);
						
						//Ubicación de la biblioteca de la UJA
//						mLocation.setLatitude(37.789);
//						mLocation.setLongitude(-3.779);
//						mLocation.setAltitude(700);
						mLocation.setLatitude(0);
						mLocation.setLongitude(0);
						mLocation.setAltitude(0);
						mLocation.setAccuracy(0);
					}
				}
			}
			
			//Si no se encuentra la posición, se vuelve a llamar con intervalos de tiempo de 5 
			//segundos para encontrarla más rápido
//			if (!hayLocation) {
//				getLocation(5*1000);
//				mostrarLocationAlert();
//				
//				
//			}else{
//				//Si ya se ha encontrado la posición, se vuelve al intervalo original para 
//				// ahorrar batería realizando menos lecturas
//				if (isGPSEnabled) {
//					sLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//							MIN_TIME, MIN_DISTANCE, this);
//				}
//				if (isNetworkEnabled) {
//					sLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//							MIN_TIME, MIN_DISTANCE, this);
//				}
//			}
    	   
		} catch (Exception ex) {
			if (sLocationMgr != null) {
				sLocationMgr.removeUpdates(this);
			}
    	   
			ex.printStackTrace();
		}
   }
   
   
   private void setLocationProviders(long toMinTime) {
	   Log.d("MAINACTIVITY","haylocation:"+hayLocation+" y toMinTime:"+toMinTime);
	   if (toMinTime == MIN_TIME) {
		   hayLocation = true;
	   }
	 
	   if (sLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
		   sLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					toMinTime, MIN_DISTANCE, this);
		   Log.d("MAINACTIVITY","Se pone GPS con toMinTime:"+toMinTime);
	   }
	   
	   if (sLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			sLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					toMinTime, MIN_DISTANCE, this);
			Log.d("MAINACTIVITY","Se pone NETWORK con toMinTime:"+toMinTime);
	   }
   }
   
   
   
   /**
    * Método para avisar de que no se puede obtener ubicación en el dispositivo. Se mostrará
    * un cuadro de diálogo que permite activar las funciones de localización.
    */
   private void mostrarSettingsAlert(){
       AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);

       ventanaAlerta.setTitle(getString(R.string.alert_location_title));
       ventanaAlerta.setMessage(getString(R.string.alert_location_message));

       //Si se pulsa el botón de ajustes, abrir los ajustes de localización
       ventanaAlerta.setPositiveButton(getString(R.string.alert_location_positive_button),
    		   											new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
               Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               startActivity(intent);
           }
       });

       //Si se pulsa el botón de cancelar, cerrar la ventana de diálogo
       ventanaAlerta.setNegativeButton(getString(R.string.alert_location_negative_button), 
    		   											new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
        	   Toast toast = Toast.makeText(getApplicationContext(), 
        			   				getString(R.string.alert_location_negative_message), 
        			   				Toast.LENGTH_LONG);
        	   
        	   dialog.cancel();
        	   toast.show();
           }
       });

       //Mostrar la ventana
       ventanaAlerta.show();
   }
   
   
   
   /**
    * Método para avisar de que se está buscando una ubicación válida.
    */
   private void mostrarLocationAlert () {
	   AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);

       ventanaAlerta.setTitle(getString(R.string.alert_location2_title));
       ventanaAlerta.setMessage(getString(R.string.alert_location2_message));


       //Si se pulsa el botón de cancelar, cerrar la ventana de diálogo
       ventanaAlerta.setNegativeButton(getString(R.string.alert_location2_negative_button), 
    		   											new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
        	   Toast toast = Toast.makeText(getApplicationContext(), 
        			   				getString(R.string.alert_location2_negative_message), 
        			   				Toast.LENGTH_SHORT);
        	   
        	   dialog.cancel();
        	   toast.show();
           }
       });

       //Mostrar la ventana
       ventanaAlerta.show();
   }
   
   /**
	 * Método para cargar los distintos proveedores de datos disponibles. En este método no se
	 * descargan datos, solamente se preparan los proveedores. 
	 */
	private void cargarPIs() {
		//Ahora se cargan en memoria los PIs disponibles en el dispositivo
		LocalDataProvider localData = new LocalDataProvider(getResources());
		ARDataSource.addPois(localData.getPois());
		
		//Se añaden recursos a la colección SOURCES para descargar PIs
		NetworkDataProvider wikipedia = new WikipediaDataProvider(getResources());
		SOURCES.put("wiki",wikipedia);
		
		isDataSourcesInit = true;
	}




	@Override
	public void onPoiSelected(Poi poiTouched) {
		//Si no hay un PI seleccionado, se pone seleccionado y fin
		if (mTouchedPoi == null) {
			mTouchedPoi = poiTouched;
		}else{

			if (mTouchedPoi.isSelected() && mTouchedPoi == poiTouched) {
				startActivity(new Intent(this,DetallesPIActivity.class));
			}else{
				mTouchedPoi = poiTouched;
			}
			
		}
		
	}   
   
	@Override
	public void onPoiUnselected(Poi PoiUnselected) {
		mTouchedPoi = null;
	}


}
