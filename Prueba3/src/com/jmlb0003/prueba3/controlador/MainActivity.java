package com.jmlb0003.prueba3.controlador;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
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
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.modelo.data.PoiContract;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;
import com.jmlb0003.prueba3.modelo.sync.NetworkDataProvider;
import com.jmlb0003.prueba3.modelo.sync.PoiDownloaderTask;
import com.jmlb0003.prueba3.modelo.sync.WikipediaDataProvider;

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
				LocationListener,OnPoiTouchedListener, LoaderCallbacks<Cursor> {
	
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
	private static final String LOG_TAG = "mainActivity";
	
	   
    
    /*******************CONSTANTES PARA LOCALIZACION**********************************/
    /**Tiempo m�nimo en milisegundos entre lecturas de los sensores de posici�n (60 segundos)**/
    private static final int MIN_TIME = 60*1000;
    /**Constante para el descartar lecturas de ubicaciones en el m�todo isBetterLocation**/
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    /**Distancia m�nima en metros entre lecturas de los sensores de posici�n (50 metros)**/
    private static final int MIN_DISTANCE = 50;
    /**Distancia m�xima en metros entre dos posiciones para que se actualicen/descarguen PIs (1700 metros)**/
    private static final int MAX_DISTANCE = 1700;
    /**Constante con el tiempo en milisegundos para descartar una lectura de posici�n anterior**/
    //TODO:Esta modificado para que no incordie con las posiciones. Volver a dejarlo en 30 minutos
    private static final int THIRTY_MINUTES = 30*60*1000    *2*24;
    
    
    /***************CONSTANTES DESCARGAS DE PIs DE LAS DISTINTAS APIs***********************/
    /**Variable que almacena los proveedores online de Puntos de Inter�s**/
	private static final Map<String,NetworkDataProvider> NETWORK_POI_SOURCES = new ConcurrentHashMap<String,NetworkDataProvider>();
	
	
    /**********************VARIABLES LOCALIZACION********************************/
    //Sensor de posici�n del dispositivo (GPS,WIFI)
    private static LocationManager sLocationMgr = null;
    //Variable que almacena la posici�n
    private Location mLocation = null;
    //Variable que controla si se ha encontrado alguna ubicaci�n v�lida
    private boolean hayLocation;
    //Variable con el radio de b�squeda de PI del radar
    private float mRadarSearch;
    
    /**Variable que almacena un Poi pulsado**/
    private Poi mTouchedPoi = null;
    
    /**Indica si se han inicializado los recursos de donde se obtienen los PIs**/
    private boolean isDataSourcesInit = false;
    
    /**Indica si el usuario quiere trabajar sin conexi�n a Internet**/
    private boolean stopRememberNoNetwork = false;
    /**Indica si la app est� inicializada**/
    private boolean isCreated = false;
    
    /************* CONSTANTES PARA EL LOADER ******************/
    /** Identificador del Loader. Actualmente solo hay uno **/
    private static final int POI_LOADER = 0;

    /** Datos necesarios del PI de entre los disponibles en la BD **/
    private static final String[] POI_COLUMNS = {
            PoiEntry.TABLE_NAME + "." + PoiEntry._ID,
            PoiEntry.COLUMN_POI_NAME,
            PoiEntry.COLUMN_POI_COLOR,
            PoiEntry.COLUMN_POI_IMAGE,
            PoiEntry.COLUMN_POI_DESCRIPTION,
            PoiEntry.COLUMN_POI_WEBSITE,
            PoiEntry.COLUMN_POI_PRICE,
            PoiEntry.COLUMN_POI_OPEN_HOURS,
            PoiEntry.COLUMN_POI_CLOSE_HOURS,
            PoiEntry.COLUMN_POI_MAX_AGE,
            PoiEntry.COLUMN_POI_MIN_AGE,
            PoiEntry.COLUMN_POI_LATITUDE,
            PoiEntry.COLUMN_POI_LONGITUDE,
            PoiEntry.COLUMN_POI_ALTITUDE
    };
    
    
    
	private FragmentModoCamara mFragmentModoCamara;
	private FragmentModoMapa mFragmentModoMapa;
	private FragmentManager mFragmentManager;



	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("MainActivity","EV onCreate");
		
		//Se indica a MainActivity que la vista que tiene que usar para meter todo el contenido que
		//sigue ahora es activity_main
		setContentView(R.layout.activity_main);
		
//		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//        }
		
        
        //Se obtiene el servicio para controlar las actualizaciones de ubicaci�n del dispositivo
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

		//No se pueden reemplazar los fragments a�adidos por XML, por lo que hay que a�adirlos 
		//de forma din�mica. 
		mFragmentManager.beginTransaction().add(R.id.container, mFragmentModoCamara).commit();
		Log.d(LOG_TAG, "PantallaPrincipal:a�adido el fragment de la camara");

		
		setPoiProviders();
		aplicarValoresDeAjustes();
		
		
		/**********************************************************************************/
//		getSupportLoaderManager().initLoader(POI_LOADER, null, this);
		
	}// Fin de onCreate()
	
	
	
	
	/**
	 * Se llama cada vez que la actividad est� preparada para interactuar con el usuario.
	 * Se encarga de iniciar la lectura de la posici�n del dispositivo.
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
        	isCreated = true;

        	updateData(mLocation.getLatitude(), mLocation.getLongitude(), mLocation.getAltitude());
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
        isCreated = false;
        try {
        	sLocationMgr.removeUpdates(this);
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
    }// Fin de onPause

    
    
	@SuppressWarnings("deprecation")
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}
	
	

	@SuppressWarnings("deprecation")
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
				ft.replace(R.id.container, mFragmentModoCamara);
				//ft.addToBackStack(null);		//Quitar esto porque da fallos y no es necesario
				
				ft.commit();				
				
				break;
			case 1:
				ft.replace(R.id.container, mFragmentModoMapa,"tagMapa");
				//ft.addToBackStack(null);	//Quitar esto porque da fallos y no es necesario				
				
				ft.commit();
				
				break;
	
			default:
				break;
		}
	
		return true;
	}


	/**
	 * M�todo que se ejecuta cada vez que se obtiene una lectura de la localizaci�n v�lida
	 */
	@Override
	public void onLocationChanged(Location newLocation) {
		
		//Si hayLocation es false y la posici�n es distinta de la default, es una posici�n
		//v�lida y se vuelve a los intervalos de tiempo normales
		if (!hayLocation && ((newLocation.getLatitude() != 0) || (newLocation.getLongitude() != 0)) ) {
			Log.d("MAINACTIVITY","Se restablece el intervalo de los providers");
			setLocationProviders(MIN_TIME);
		}else{
			if (!hayLocation) {
				Log.d("MAINACTIVITY","haylocation:"+hayLocation+" latitude y longitude:"+newLocation.getLatitude()+" "+newLocation.getLongitude() );
				mostrarLocationAlert();
			}
		}
		
		// Solo se tiene en cuenta la nueva ubicaci�n si es mejor que la anterior 
		// (m�s precisa y reciente)
		if (isBetterLocation(newLocation, ARDataSource.getCurrentLocation())) {
    		Log.d(LOG_TAG,"la posicion nueva es mejor: La:"+newLocation.getLatitude()+" lo:"+newLocation.getLongitude()+" Precision:"+newLocation.getAccuracy()+"\nEl anterior tenia precision: "+ARDataSource.getCurrentLocation().getAccuracy());    		
    		float locationsDistance = newLocation.distanceTo(ARDataSource.getCurrentLocation());
    		ARDataSource.setCurrentLocation(newLocation);
    		mLocation = newLocation;

	        mFragmentModoCamara.calculateMagneticNorthCompensation();
	        //TODO: Esto est� para que se actualice el mapa si cambia la posici�n...habr�a que probar si funciona o no...
	        mFragmentModoMapa.setUpMapIfNeeded();


	        if ( (locationsDistance > MAX_DISTANCE) && (hayLocation) ) {
	        	Log.d(LOG_TAG,"UPDATEDATA: Hay que llamar a updateData");
		        //Se actualizan/descargan los PIs seg�n la posici�n obtenida
		        updateData(newLocation.getLatitude(),
		        			newLocation.getLongitude(),
		        			newLocation.getAltitude());
	        }
		}		
		
	}


	@Override
	public void onProviderDisabled(String arg0) {
		//Funci�n de onLocationListener - No se utiliza
		if (!sLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
				!sLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			
			mostrarLocationSettingsAlert();
		}
		
	}


	@Override
	public void onProviderEnabled(String arg0) {
		//Funci�n de onLocationListener - No se utiliza		
	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		//Funci�n de onLocationListener - No se utiliza		
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

       //Comprobar si la nueva ubicaci�n es m�s o menos reciente que la anterior
       long timeDelta = location.getTime() - currentBestLocation.getTime();
       boolean isNewer = timeDelta > 0;

        // Si hace m�s de dos minutos de la �ltima ubicaci�n ->nueva es mejor
       if (timeDelta > TWO_MINUTES) {
           return true;
           //Si la nueva ubicaci�n es antigua -> nueva es peor
       } else if (timeDelta < -TWO_MINUTES) {
           return false;
       }


       int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
       boolean isLessAccurate = accuracyDelta > 0;
       boolean isMoreAccurate = accuracyDelta < 0;
       boolean isSignificantlyLessAccurate = accuracyDelta > 200;

       
       boolean isFromSameProvider = isSameProvider(location.getProvider(),
               currentBestLocation.getProvider());

       // Determinar la mejor localizaci�n seg�n las variables calculadas
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
    * M�todo que actualiza el conjunto de PIs disponibles en memoria seg�n la �ltima localizaci�n
    * v�lida del dispositivo.
    * @param lat Latitud de la �ltima posici�n v�lida del dispositivo
    * @param lon Longitud de la �ltima posici�n v�lida del dispositivo
    * @param alt Altitud de la �ltima posici�n v�lida del dispositivo
    */
   private void updateData(final double lat, final double lon, final double alt) {
	   if (isDataSourcesInit) {
		   
		   Cursor poisByLocation = getContentResolver().query(
				   PoiContract.PoiEntry
				   .buildLocationUriWithCoords(Double.toString(lat), Double.toString(lon)),
				   null, 
				   null, 
				   null, 
				   null);
		   
		   if (poisByLocation != null && poisByLocation.moveToFirst()) {
			   Log.d(LOG_TAG,"poisfrom LOCATION");
			   ARDataSource.addPoisFromCursor(poisByLocation);
		   }else{
			   //TODO: Esto solo deberia llamarse una vez que tenemos una posici�n muy exacta...porque si no, se insertan mal los puntos
			   //http://developer.android.com/training/location/retrieve-current.html
			   NetworkInfo ni = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))
					   	.getActiveNetworkInfo();
			   
			   
			   if (ni != null && ni.isConnected()) {
				   Log.d(LOG_TAG,"1poisfrom TASK");
				   new PoiDownloaderTask(this, NETWORK_POI_SOURCES).execute();
			   }else{
				   mostrarNetworkAlert();
			   }
		   }
		   poisByLocation.close();
	   }
   }
   
 
   
   /**
    * M�todo con el que se lee cada ajuste que pueda haber aplicado el usuario para que se 
    * refleje en la aplicaci�n
    */
   private void aplicarValoresDeAjustes() {
	   //Se construyen las preferencias con los valores por defecto de la definici�n del XML si
	   //no hay preferencias anteriores guardadas. Si las hay, se salta este paso.
	   PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
	   //Obtenemos la distancia hasta la que se buscan PI en los ajustes
	   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	   mRadarSearch = prefs.getInt(getString(R.string.pref_seekBar_distance_key),0);
	   ARDataSource.updateRadarDistance(mRadarSearch);
	   
	   
	   
	   /***********Recorriendo todas las preferencias almacenadas******************
	   Map<String,?> keys = prefs.getAll();
	   Log.d("map values!","En prefs hay: "+ keys.size()+ " valores");
	   for(Map.Entry<String,?> entry : keys.entrySet()) {
		   Log.d("map values!",entry.getKey() + ": " +  entry.getValue().toString());
	   }
	   ************************************/
	   
   }
   
   
   
   /**
    * M�todo para gestionar el registro en los proveedores de localizaci�n y obtener una posici�n
    * v�lida. Si no hay forma de obtener una posici�n, se fija la posici�n por defecto que es la 
    * biblioteca de la UJA.
    */
   private void getLocation() {
		boolean isGPSEnabled = false;
		boolean isNetworkEnabled = false;
		Time now = new Time();
		
		
		//TODO: En este enlace est� la forma de obtener la posici�n y poner una pantalla de buscando mientras tanto
		//http://stackoverflow.com/questions/11752961/how-to-show-a-progress-spinner-in-android-when-doinbackground-is-being-execut
		
		
		now.setToNow();
		
		
		try {
			Location gps = null;
			Location network = null;
       	
			//Obtenemos la �ltima posici�n almacenada de los proveedores y comprobamos
			//si es v�lida
			if (sLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				isGPSEnabled = true;
				gps = sLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				sLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						MIN_TIME, MIN_DISTANCE, this);
				
				//Si la localizaci�n es de hace m�s de 30 minutos se descarta
				if ( gps != null && (now.toMillis(true) - gps.getTime()) > THIRTY_MINUTES ) {
					gps = null;
				}
				
				
			}
			if (sLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				isNetworkEnabled = true;
				network = sLocationMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				sLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						MIN_TIME, MIN_DISTANCE, this);
				
				//Si la localizaci�n es de hace m�s de 30 minutos se descarta
				if ( network != null && (now.toMillis(true) - network.getTime()) > THIRTY_MINUTES ) {
					network = null;
				}
			}
    	   
    	   
			if (!isGPSEnabled && !isNetworkEnabled) {
				mostrarLocationSettingsAlert();
			}
    	   
			//Si hay posici�n de los dos proveedores nos quedamos con la mejor
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
						//Si no hay localizaci�n ninguna, por defecto se pone la de la biblioteca de la UJA
						Log.e("MainActivity","No hay ubicaci�n, se pone la de la biblioteca");
						
						mLocation = sLocationMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						setLocationProviders(5*1000);
						
						//Ubicaci�n de la biblioteca de la UJA
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
			
			//Si no se encuentra la posici�n, se vuelve a llamar con intervalos de tiempo de 5 
			//segundos para encontrarla m�s r�pido
//			if (!hayLocation) {
//				getLocation(5*1000);
//				mostrarLocationAlert();
//				
//				
//			}else{
//				//Si ya se ha encontrado la posici�n, se vuelve al intervalo original para 
//				// ahorrar bater�a realizando menos lecturas
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
    * M�todo para avisar de que no se puede obtener ubicaci�n en el dispositivo. Se mostrar�
    * un cuadro de di�logo que permite activar las funciones de localizaci�n.
    */
   private void mostrarLocationSettingsAlert(){
       AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);

       ventanaAlerta.setTitle(getString(R.string.alert_location_title));
       ventanaAlerta.setMessage(getString(R.string.alert_location_message));

       //Si se pulsa el bot�n de ajustes, abrir los ajustes de localizaci�n
       ventanaAlerta.setPositiveButton(getString(R.string.alert_location_positive_button),
    		   											new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
               Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
               startActivity(intent);
           }
       });

       //Si se pulsa el bot�n de cancelar, cerrar la ventana de di�logo
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
    * M�todo para avisar de que no hay conexi�n a Internet para descargar datos.
    */
   private void mostrarNetworkAlert () {
	   Log.d(LOG_TAG,"MostrarNetworkAlert: ha entrado ahora y iscreated es "+isCreated);
	   if (!isCreated) {
		   return;
	   }
	   
	   AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);

       ventanaAlerta.setTitle(getString(R.string.alert_network_title));
       ventanaAlerta.setMessage(getString(R.string.alert_network_message));

       //Si se pulsa el bot�n de ajustes, abrir los ajustes de localizaci�n
       ventanaAlerta.setPositiveButton(getString(R.string.alert_network_positive_button),
    		   											new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
               Intent intent = new Intent(Settings.ACTION_SETTINGS);
               startActivity(intent);
           }
       });

       //Si se pulsa el bot�n de cancelar, cerrar la ventana de di�logo
       ventanaAlerta.setNegativeButton(getString(R.string.alert_network_negative_button), 
    		   											new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
        	   Toast toast = Toast.makeText(getApplicationContext(), 
        			   				getString(R.string.alert_network_negative_message), 
        			   				Toast.LENGTH_LONG);
        	   
        	   dialog.cancel();
        	   toast.show();
        	   stopRememberNoNetwork = true;
           }
       });

       //Mostrar la ventana
       ventanaAlerta.show();
   }
   
   
   
   /**
    * M�todo para avisar de que se est� buscando una ubicaci�n v�lida.
    */
   private void mostrarLocationAlert () {
	   AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);

       ventanaAlerta.setTitle(getString(R.string.alert_location2_title));
       ventanaAlerta.setMessage(getString(R.string.alert_location2_message));


       //Si se pulsa el bot�n de cancelar, cerrar la ventana de di�logo
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
	 * M�todo para cargar los distintos proveedores de datos disponibles. En este m�todo no se
	 * descargan datos, solamente se preparan los proveedores. 
	 */
	private void setPoiProviders() {
		//Ahora se cargan en memoria los PIs disponibles en el dispositivo
//		LocalDataProvider localData = new LocalDataProvider(getResources());
//		ARDataSource.addPois(localData.getPois());
		
		//Se a�aden recursos a la colecci�n SOURCES para descargar PIs
		NetworkDataProvider wikipedia = new WikipediaDataProvider();
		NETWORK_POI_SOURCES.put("wiki",wikipedia);
		
		NetworkDataProvider local = new WikipediaDataProvider();
		NETWORK_POI_SOURCES.put("local",local);
		
		isDataSourcesInit = true;
	}




	@Override
	public void onPoiSelected(Poi poiTouched) {
		//Si no hay un PI seleccionado, se pone seleccionado y fin
		if (mTouchedPoi == null) {
			mTouchedPoi = poiTouched;
		}else{

			if (mTouchedPoi.isSelected() && mTouchedPoi == poiTouched) {
				//TODO: Est� quitado del manifest lo de parentActivity...
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




	/**
	 * El Loader se usa para obtener los datos del Content Provider, que a su vez hace de 
	 * intermediario entre el Loader y la Base de Datos. A continuaci�n se implementan los 
	 * m�todos necesarios para manejarlo y poder obtener los PIs.
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
//		// This is called when a new Loader needs to be created.  This
//        // fragment only uses one loader, so we don't care about checking the id.
//
//
//        // Sort order:  Resultados ordenados por distancia (formato ORDER BY de SQL).
//        //TODO: Aqu� f�rmula de la distancia....
////        String sortOrder = PoiEntry.COLUMN_DISTANCE + " ASC";
//
//
//        // Now create and return a CursorLoader that will take care of
//        // creating a Cursor for the data being displayed.
//        return new CursorLoader(
//        		this,	//Context
//                PoiEntry.CONTENT_URI,	//URI
//                POI_COLUMNS,	//Proyecci�n (SELECT)
//                null,	//Selecci�n (WHERE)
//                null,	//Argumentos de la selecci�n (Par�metros ? del WHERE)
////                sortOrder	//(SORTED BY)
//                null
//        );
		return null;
	}




	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//		Log.d(CLASS_TAG,"onLoadFinished:1");
//		if (data.getCount() == 0) {
//			Log.d(CLASS_TAG,"onLoadFinished:2");
//			NetworkDataProvider nd = new WikipediaDataProvider(this, true);
//			Log.d(CLASS_TAG,"onLoadFinished:2.1");
//            nd.syncImmediately(this);
//            Log.d(CLASS_TAG,"onLoadFinished:2.2");
//        }
//		Log.d(CLASS_TAG,"onLoadFinished:3 con Count:"+data.getCount());
//
//
//		for (int i=0; i<data.getColumnCount(); i++) {
//			Log.d(CLASS_TAG,"onLoadFinished:4 columna "+i+" "+data.getColumnName(i));
//		}
	}




	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}


}
