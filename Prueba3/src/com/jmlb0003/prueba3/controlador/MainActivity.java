package com.jmlb0003.prueba3.controlador;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
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
import com.jmlb0003.prueba3.modelo.DetallesPoi;
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.modelo.data.PoiContract;
import com.jmlb0003.prueba3.modelo.sync.LocalDataProvider;
import com.jmlb0003.prueba3.modelo.sync.LocalPoiLoaderTask;
import com.jmlb0003.prueba3.modelo.sync.NetworkDataProvider;
import com.jmlb0003.prueba3.modelo.sync.PoiDownloaderTask;
import com.jmlb0003.prueba3.modelo.sync.WikipediaDataProvider;
import com.jmlb0003.prueba3.utilidades.LocationUtility;

/**
 * //TODO Orden de los imports CODE GUIDELINES
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
	 * //TODO:Nomenclatura de variables CODE GUIDELINES
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
	private static final String STATE_SELECTED_POI_ID = "selected_poi_id";
	private static final String LOG_TAG = "mainActivity";
	
	   
    
    /*******************CONSTANTES PARA LOCALIZACION**********************************/
    /**Tiempo m�nimo en milisegundos entre lecturas de los sensores de posici�n (60 segundos)**/
    private static final int MIN_TIME = 60*1000;
    
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
	private static final Map<String,LocalDataProvider> LOCAL_POI_SOURCES = new ConcurrentHashMap<String,LocalDataProvider>();
	
	
    /**********************VARIABLES LOCALIZACION********************************/
    //Sensor de posici�n del dispositivo (GPS,WIFI)
    private static LocationManager sLocationMgr = null;
    //Variable que almacena la posici�n
    private Location mLocation = null;
    //Variable donde se almacena la �ltima posici�n que se utiliz� para actualizar los datos
    private Location mLastUpdateDataLocation = null;
    //Variable que controla si se ha encontrado alguna ubicaci�n v�lida
    private boolean hayLocation;
    //Variable con el radio de b�squeda de PI del radar
    private float mRadarSearch;
    
    /**Variable que almacena un Poi pulsado**/
    private Poi mTouchedPoi = null;
    
    /**Indica si se han inicializado los recursos de donde se obtienen los PIs**/
    private boolean isDataSourcesInit = false;
    
    /**Indica si la app est� inicializada**/
    private boolean isCreated = false;
    
    
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
		setPoiIcons();
		aplicarValoresDeAjustes();
		
		searchManager();
		
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

        	updateData(mLocation);
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

    
    
    @Override
    protected void onDestroy() {
    	Log.d("MainActivity","EV onDestroy");
    	onPoiUnselected(mTouchedPoi);
    	super.onDestroy();
    }// Fin de onDestroy
    
    
	@SuppressWarnings("deprecation")
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(LOG_TAG,"Onrestore");
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
		
		if(savedInstanceState.containsKey(STATE_SELECTED_POI_ID)) {
			Log.d(LOG_TAG,"Se guarda la pesta�a:"+getSupportActionBar().getSelectedNavigationIndex());
			mTouchedPoi = ARDataSource.getPoi(savedInstanceState.getLong(STATE_SELECTED_POI_ID));
		}
	}
	
	

	@SuppressWarnings("deprecation")
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		Log.d(LOG_TAG,"OnSaveInstance");
		Log.d(LOG_TAG,"Se guarda la pesta�a:"+getSupportActionBar().getSelectedNavigationIndex());
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
		if(mTouchedPoi != null) {
			Log.d(LOG_TAG,"Se guarda la pesta�a:"+getSupportActionBar().getSelectedNavigationIndex());
			outState.getLong(STATE_SELECTED_POI_ID,mTouchedPoi.getID());
		}
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		
		return true;
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			/*
	        case R.id.action_help:
	            //helpAction();
	            return super.onOptionsItemSelected(item);*/
			case R.id.action_search:
				// search action
	            return onSearchRequested();
	            
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
		if (LocationUtility.isBetterLocation(newLocation, ARDataSource.getCurrentLocation())) {
    		Log.d(LOG_TAG,"la posicion nueva es mejor: La:"+newLocation.getLatitude()+" lo:"+newLocation.getLongitude()+" Precision:"+newLocation.getAccuracy()+"\nEl anterior tenia precision: "+ARDataSource.getCurrentLocation().getAccuracy());    		
    		float locationsDistance = newLocation.distanceTo(mLastUpdateDataLocation);
    		ARDataSource.setCurrentLocation(newLocation);
    		mLocation = newLocation;

	        mFragmentModoCamara.calculateMagneticNorthCompensation();
	        //TODO: Esto est� para que se actualice el mapa si cambia la posici�n...habr�a que probar si funciona o no...
	        mFragmentModoMapa.setUpMapIfNeeded();
	        //////////////////////////////////////////////////////////

	        if ( (locationsDistance > MAX_DISTANCE) && (hayLocation) ) {
	        	Log.d(LOG_TAG,"UPDATEDATA: Hay que llamar a updateData");
		        //Se actualizan/descargan los PIs seg�n la posici�n obtenida
		        updateData(newLocation);
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
	 * M�todo que gestiona las b�squedas que se realicen desde esta activity
	 */
	private void searchManager() {
		Intent intent = getIntent();
		Intent destinyIntent = null;

        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        	// handles a click on a search suggestion; launches activity to show word
        	destinyIntent = new Intent(this, DetallesPoiActivity.class);
            
        	Bundle b = intent.getExtras();
        	if (b.containsKey(SearchManager.USER_QUERY)) {
        		Log.d(LOG_TAG,"el userQuery es:"+b.get(SearchManager.USER_QUERY).toString());
        		Log.d(LOG_TAG," y getData es:"+intent.getDataString());
        		destinyIntent.setData(PoiContract.PoiEntry.
        				buildPoiByNameUri(b.get(SearchManager.USER_QUERY).toString()));
        	}
            
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
        	destinyIntent = new Intent(getApplicationContext(), PoiSearchActivity.class);
        	destinyIntent.putExtra(SearchManager.QUERY, intent.getStringExtra(SearchManager.QUERY));
        }
        
        if(destinyIntent != null) {
        	destinyIntent.setAction(Intent.ACTION_SEARCH);
        	startActivity(destinyIntent);
        	//Esta actividad debe eliminarse de la pila porque ya ha cumplido su cometido
            finish();
        }        
	}


	
   /**
    * M�todo que actualiza el conjunto de PIs disponibles en memoria seg�n la �ltima localizaci�n
    * v�lida del dispositivo.
    * @param loc Posici�n v�lida del dispositivo
    */
   private void updateData(final Location loc) {
	   if (isDataSourcesInit) {
		   
		   Cursor poisByLocation = getContentResolver().query(
				   PoiContract.PoiEntry
				   .buildLocationUriWithCoords(Double.toString(loc.getLatitude()), Double.toString(loc.getLongitude())),
				   null, 
				   null, 
				   null, 
				   null);
		   
		   if (poisByLocation != null && poisByLocation.moveToFirst()) {
			   ARDataSource.addPoisFromCursor(poisByLocation);
		   }else{
			   NetworkInfo ni = ((ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE))
					   	.getActiveNetworkInfo();			   
			   
			   if (ni != null && ni.isConnected()) {
				   new PoiDownloaderTask(this, NETWORK_POI_SOURCES).execute();
				   new LocalPoiLoaderTask(this, LOCAL_POI_SOURCES).execute();
			   }else{
				   mostrarNetworkAlert();
			   }
		   }
		   poisByLocation.close();
		   
		   mLastUpdateDataLocation = loc;
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
		

		now.setToNow();
		mLocation = LocationUtility
				.getBestLastKnownLocation(sLocationMgr, now.toMillis(true) - THIRTY_MINUTES);
				
		try {
       	
			if (sLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				isGPSEnabled = true;
				sLocationMgr.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						MIN_TIME, MIN_DISTANCE, this);
			}
			
			if (sLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
				isNetworkEnabled = true;
				sLocationMgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
						MIN_TIME, MIN_DISTANCE, this);
			}    	   
    	   
			if (!isGPSEnabled && !isNetworkEnabled) {
				mostrarLocationSettingsAlert();
			}
    	   
			if ( mLocation == null ) {
				hayLocation = false;
				//Si no hay localizaci�n ninguna, por defecto se pone la de la biblioteca de la UJA
				Log.e(LOG_TAG,"GetLocation_No hay ubicaci�n, se pone la de la biblioteca");
				
				setLocationProviders(5*1000);
				
				//Ubicaci�n de la biblioteca de la UJA
				mLocation.setLatitude(37.789);
				mLocation.setLongitude(-3.779);
				mLocation.setAltitude(700);
//				mLocation.setLatitude(0);
//				mLocation.setLongitude(0);
//				mLocation.setAltitude(0);
//				mLocation.setAccuracy(0);				
			}
			//Inicializamos la variable para cuando se compruebe la distancia en onLocationChanged
			mLastUpdateDataLocation = mLocation;
    	   
		} catch (Exception ex) {
			if (sLocationMgr != null) {
				sLocationMgr.removeUpdates(this);
			}
    	   
			ex.printStackTrace();
		}
   }
   
   
   private void setLocationProviders(long toMinTime) {
	   if (toMinTime == MIN_TIME) {
		   hayLocation = true;
	   }
	   
	   List<String> providers = sLocationMgr.getAllProviders();
	   for (String provider: providers) {
		   sLocationMgr.requestLocationUpdates(provider, toMinTime, MIN_DISTANCE, this);
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
	 * Aqu� se crean y almacenan los iconos de los PIs que se utilizar�n en la app. Si hay alguna
	 * distinci�n entre los iconos de los PIs, aqu� han de guardarse todos los que hagan falta.
	 */
	private void setPoiIcons() {
		ARDataSource.sPoiIcons.put(DetallesPoi.DETALLESPI_ICON, 
				BitmapFactory.decodeResource(getResources(), R.drawable.icono_pi));
		ARDataSource.sPoiIcons.put(DetallesPoi.DETALLESPI_SELECTED_ICON, 
				BitmapFactory.decodeResource(getResources(), R.drawable.icono_pi_seleccionado));
	}
	
	
   /**
	 * M�todo para cargar los distintos proveedores de datos disponibles. En este m�todo no se
	 * descargan datos, solamente se preparan los proveedores. 
	 */
	private void setPoiProviders() {
		//Se a�aden recursos a la colecci�n SOURCES para descargar PIs
		NetworkDataProvider wikipedia = new WikipediaDataProvider();
		NETWORK_POI_SOURCES.put("wiki",wikipedia);
		
		LocalDataProvider local = new LocalDataProvider();
		LOCAL_POI_SOURCES.put("local",local);
		
		isDataSourcesInit = true;
	}




	@Override
	public void onPoiTouched(Poi poiTouched) {
		//Si hay uno seleccionado, se deselecciona
		if (ARDataSource.hasSelectededPoi()) {
			if (ARDataSource.SelectedPoi != poiTouched) {
				onPoiUnselected(ARDataSource.SelectedPoi);				
			}
		}
		
		poiTouched.setTouched(true);
		ARDataSource.SelectedPoi = poiTouched;
		//Si no hay un PI seleccionado, se pone seleccionado y fin
		if (mTouchedPoi == null) {
			mTouchedPoi = poiTouched;
		}else{

			if (mTouchedPoi.isSelected() && mTouchedPoi == poiTouched) {
				startActivity(new Intent(this,DetallesPoiActivity.class));
			}else{
				mTouchedPoi = poiTouched;
			}
			
		}
	}   
   
	@Override
	public void onPoiUnselected(Poi poiUnselected) {
		if (ARDataSource.hasSelectededPoi()) {
			if (ARDataSource.SelectedPoi == poiUnselected) {
				ARDataSource.SelectedPoi.setTouched(false);
				mTouchedPoi = null;
				ARDataSource.SelectedPoi = null;
			}
		}		
	}


}
