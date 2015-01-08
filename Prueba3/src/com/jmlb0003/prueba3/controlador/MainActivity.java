package com.jmlb0003.prueba3.controlador;


import android.annotation.SuppressLint;
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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.jmlb0003.prueba3.modelo.sync.PfcDataProvider;
import com.jmlb0003.prueba3.modelo.sync.PoiDownloaderTask;
import com.jmlb0003.prueba3.modelo.sync.WikipediaDataProvider;
import com.jmlb0003.prueba3.utilidades.LocationUtility;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * //TODO Orden de los imports CODE GUIDELINES
 * Android imports
 * Imports from third parties (com, junit, net, org)
 * java and javax
 */

/**
 * Actividad principal de la app. Es el punto de entrada.
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
    /**Tiempo mínimo en milisegundos entre lecturas de los sensores de posición (60 segundos)**/
    private static final int MIN_TIME = 60*1000;
    
    /**Distancia mínima en metros entre lecturas de los sensores de posición (50 metros)**/
    private static final int MIN_DISTANCE = 50;
    /**Distancia máxima en metros entre dos posiciones para que se actualicen/descarguen PIs (1700 metros)**/
    private static final int MAX_DISTANCE = 1700;
    /**Constante con el tiempo en milisegundos para descartar una lectura de posición anterior**/
    private static final int THIRTY_MINUTES = 30*60*1000;
    
    
    /***************CONSTANTES DESCARGAS DE PIs DE LAS DISTINTAS APIs***********************/
    /**Variable que almacena los proveedores online de Puntos de Interés**/
	private static final Map<String,NetworkDataProvider> NETWORK_POI_SOURCES = new ConcurrentHashMap<String,NetworkDataProvider>();
	private static final Map<String,LocalDataProvider> LOCAL_POI_SOURCES = new ConcurrentHashMap<String,LocalDataProvider>();
	
	
    /**********************VARIABLES LOCALIZACION********************************/
    //Sensor de posición del dispositivo (GPS,WIFI)
    private static LocationManager sLocationMgr = null;
    //Variable que almacena la posición
    private Location mLocation = null;
    /**Variable donde se almacena la última posición que se utilizó para actualizar los datos**/
    private Location mLastUpdateDataLocation = null;
    //Variable que controla si se ha encontrado alguna ubicación válida
    private boolean hayLocation;
    
    /**Variable que almacena un Poi pulsado**/
    private Poi mTouchedPoi = null;
    
    /**Indica si se han inicializado los recursos de donde se obtienen los PIs**/
    private boolean isDataSourcesInit = false;
    
    /**Indica si la app está inicializada**/
    private boolean isCreated = false;
    
    private boolean isShowedAlert = false;
    
    
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
		
		//TODO: A ver como se pone el icono en la barra de accionessssss
		/**************************
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 */
        
        //Se obtiene el servicio para controlar las actualizaciones de ubicación del dispositivo
        sLocationMgr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        hayLocation = false;
		
		mFragmentModoCamara = new FragmentModoCamara();
		mFragmentModoMapa = new FragmentModoMapa();
		
//		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getSupportActionBar();

		
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setIcon(R.drawable.ic_launcher);
		
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
		Log.d(LOG_TAG, "PantallaPrincipal:añadido el fragment de la camara");

		
		setPoiProviders();
		setPoiIcons();
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
        
        getLocation();
        
        if (mLocation != null) {
        	onLocationChanged(mLocation);        	
        	isCreated = true;

        	updateData(mLocation);
        } else {
        	isShowedAlert = false;
        	mostrarLocationAlert();
        }
        aplicarValoresDeAjustes();
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
			Log.d(LOG_TAG,"Se guarda la pestaña:"+getSupportActionBar().getSelectedNavigationIndex());
			mTouchedPoi = ARDataSource.getPoi(savedInstanceState.getLong(STATE_SELECTED_POI_ID));
		}
	}
	
	

	@SuppressWarnings("deprecation")
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		Log.d(LOG_TAG,"OnSaveInstance");
		Log.d(LOG_TAG,"Se guarda la pestaña:"+getSupportActionBar().getSelectedNavigationIndex());
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
		if(mTouchedPoi != null) {
			Log.d(LOG_TAG,"Se guarda la pestaña:"+getSupportActionBar().getSelectedNavigationIndex());
			outState.getLong(STATE_SELECTED_POI_ID,mTouchedPoi.getID());
		}
	}
	
	

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		
		// Associate searchable configuration with the SearchView
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
	        searchView.setIconifiedByDefault(false);
	    }
		
		return super.onCreateOptionsMenu(menu);
	}

	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
			case R.id.action_filter:
				// filter action
				startActivity(new Intent(this, FilterActivity.class));
	            return true;
	            
			case R.id.action_search:
				// search action	(funciona sola mediante SearchView)
	            return true;
	            
	        case R.id.action_settings:
	        	// Settings action
	        	startActivity( new Intent(this,SettingsActivity.class) );
	            return true;
	        
	        case R.id.action_help:
	            //helpAction();
	            showAbout();
	            return true;
	            
	        default:
	            return false;
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
	 * Método que se ejecuta cada vez que se obtiene una lectura de la localización válida
	 */
	@Override
	public void onLocationChanged(Location newLocation) {
		Log.d("MAINACTIVITY","haylocation:"+hayLocation+" latitude y longitude:"+newLocation.getLatitude()+" "+newLocation.getLongitude()+"-"+newLocation.getAltitude());
		//Si hayLocation es false y la posición es distinta de la default, es una posición
		//válida y se vuelve a los intervalos de tiempo normales
		if (!hayLocation && 
				((newLocation.getLatitude() != 0) || (newLocation.getLongitude() != 0)) &&
				newLocation.getAltitude() > 0) {
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
		if (LocationUtility.isBetterLocation(newLocation, ARDataSource.getCurrentLocation())) {
    		Log.d(LOG_TAG,"la posicion nueva es mejor: La:"+newLocation.getLatitude()+" lo:"+newLocation.getLongitude()+" Precision:"+newLocation.getAccuracy()+"\nEl anterior tenia precision: "+ARDataSource.getCurrentLocation().getAccuracy());
    		Log.d(LOG_TAG,"Latitude:"+mLocation.getLatitude()+" longitude:"+mLocation.getLongitude()+
        			" altitude:"+mLocation.getAltitude()+" Precision:"+mLocation.getAccuracy());
    		ARDataSource.setCurrentLocation(newLocation);
    		mLocation = newLocation;

	        mFragmentModoCamara.calculateMagneticNorthCompensation();
	        mFragmentModoMapa.setUpMapIfNeeded();
	    

	        if ( ( mLastUpdateDataLocation != null && 
	        		newLocation.distanceTo(mLastUpdateDataLocation) > MAX_DISTANCE) 
	        		&& (hayLocation) ) {
	        	Log.d(LOG_TAG,"UPDATEDATA: Hay que llamar a updateData");
		        //Se actualizan/descargan los PIs según la posición obtenida
		        updateData(newLocation);
	        }
		}
	}


	@Override
	public void onProviderDisabled(String arg0) {
		//Función de onLocationListener - No se utiliza
		if (!sLocationMgr.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
				!sLocationMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			
			mostrarLocationSettingsAlert();
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
    * Método que actualiza el conjunto de PIs disponibles en memoria según la última localización
    * válida del dispositivo.
    * @param loc Posición válida del dispositivo
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
    * Método con el que se lee cada ajuste que pueda haber aplicado el usuario para que se 
    * refleje en la aplicación
    */
   private void aplicarValoresDeAjustes() {	   
	   PreferenceManager.setDefaultValues(this, R.xml.activity_general_prefs, false);
	   //Obtenemos la distancia hasta la que se buscan PI en los ajustes
	   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
	   ARDataSource.updateRadarDistance(prefs.getInt(getString(R.string.pref_seekBar_distance_key),0));
	   
	   //Aplicar filtros:
	   //Por horario
	   ARDataSource.setFilterByHours(prefs.getBoolean(getString(R.string.ft_open_key), false));
	   //Filtros por proveedor (wikipedia)
	   if (!prefs.getBoolean(getString(R.string.ft_wiki_key), true)) {
		   ARDataSource.setFilterByProvider(PoiContract.PoiEntry.WIKIPEDIA_PROVIDER);
	   }
	   //Filtros por proveedor (local)
	   if (!prefs.getBoolean(getString(R.string.ft_local_key), true)) {
		   ARDataSource.setFilterByProvider(PoiContract.PoiEntry.LOCAL_PROVIDER);
	   }
	   //Filtros por proveedor (pfc server)
	   if (!prefs.getBoolean(getString(R.string.ft_pfc_server_key), true)) {
		   ARDataSource.setFilterByProvider(PoiContract.PoiEntry.UJA_PROVIDER);
	   }
	   
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
			
			now.setToNow();
			mLocation = LocationUtility
					.getBestLastKnownLocation(sLocationMgr, now.toMillis(true) - THIRTY_MINUTES);
    	   
			if ( mLocation == null ) {
				hayLocation = false;
				
				setLocationProviders(1*1000);
				
				mLocation.setLatitude(0);
				mLocation.setLongitude(0);
				mLocation.setAltitude(0);
				mLocation.setAccuracy(Float.MAX_VALUE);				
			}
    	   
		} catch (Exception ex) {
			if (sLocationMgr != null) {
				sLocationMgr.removeUpdates(this);
			}
    	   
			ex.printStackTrace();
		}
   }
   
   
   private void setLocationProviders(long toMinTime) {
	   hayLocation = (toMinTime == MIN_TIME)?true:false;
	   
	   List<String> providers = sLocationMgr.getAllProviders();
	   for (String provider: providers) {
		   sLocationMgr.requestLocationUpdates(provider, toMinTime, MIN_DISTANCE, this);
	   }
   }
   
   
   
   /**
    * Método para avisar de que no se puede obtener ubicación en el dispositivo. Se mostrará
    * un cuadro de diálogo que permite activar las funciones de localización.
    */
   private void mostrarLocationSettingsAlert(){
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
       ventanaAlerta.setNegativeButton(getString(R.string.close), 
    		   											new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
        	   dialog.cancel();
        	   Toast.makeText(
        			   getApplicationContext(),
        			   getString(R.string.alert_location_negative_message),
        			   Toast.LENGTH_LONG)
        			   .show();
           }
       });

       //Mostrar la ventana
       ventanaAlerta.show();
   }
   
   
   /**
    * Método para avisar de que no hay conexión a Internet para descargar datos.
    */
   private void mostrarNetworkAlert () {
	   if (!isCreated) {
		   return;
	   }
	   
	   AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);

       ventanaAlerta.setTitle(getString(R.string.alert_network_title));
       ventanaAlerta.setMessage(getString(R.string.alert_network_message));

       //Si se pulsa el botón de ajustes, abrir los ajustes de localización
       ventanaAlerta.setPositiveButton(getString(R.string.alert_network_positive_button),
    		   											new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
               Intent intent = new Intent(Settings.ACTION_SETTINGS);
               startActivity(intent);
           }
       });

       //Si se pulsa el botón de cancelar, cerrar la ventana de diálogo
       ventanaAlerta.setNegativeButton(getString(R.string.close), 
    		   											new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
        	   dialog.cancel();
        	   Toast.makeText(
        			   getApplicationContext(),
        			   getString(R.string.alert_network_negative_message),
        			   Toast.LENGTH_LONG)
        			   .show();
           }
       });

       //Mostrar la ventana
       ventanaAlerta.show();
   }
   
   
   
   /**
    * Método para avisar de que se está buscando una ubicación válida.
    */
   private void mostrarLocationAlert () {
	   if (!isShowedAlert) {
		   AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);
	
	       ventanaAlerta.setTitle(getString(R.string.alert_location2_title));
	       ventanaAlerta.setMessage(getString(R.string.alert_location2_message));
	
	
	       //Si se pulsa el botón de cancelar, cerrar la ventana de diálogo
	       ventanaAlerta.setNegativeButton(getString(R.string.close), 
	    		   											new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int which) {
	        	   dialog.cancel();
	        	   Toast.makeText(
	        			   getApplicationContext(), 
	        			   getString(R.string.alert_location2_negative_message),
	        			   Toast.LENGTH_SHORT)
	        			   .show();
	           }
	       });
	
	       //Mostrar la ventana
	       ventanaAlerta.show();
	       isShowedAlert = true;
	   }
   }
   
   
   /**
    * Método para mostrar una ventana de diálogo con la descripción del funcionamiento de
    * las búsquedas en la app.
    */
   private void showAbout() {
	   AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);

       ventanaAlerta.setTitle(getString(R.string.title_about));
       ventanaAlerta.setMessage(getString(R.string.message_about));


       //Si se pulsa el botón de cancelar, cerrar la ventana de diálogo
       ventanaAlerta.setNeutralButton(getString(R.string.close), 
    		   new DialogInterface.OnClickListener() {
    	   			public void onClick(DialogInterface dialog, int which) {
    	   				dialog.cancel();
    	   			}
       });
       ventanaAlerta.setPositiveButton(getString(R.string.developer_webSite),
    		   new DialogInterface.OnClickListener() {
    	   			public void onClick(DialogInterface dialog, int which) {
    	   				startActivity(new Intent(
    	   						Intent.ACTION_VIEW, Uri.parse(getString(R.string.my_website))));
    	   				
    	   				dialog.cancel();
    	   			}
       });
       
       ventanaAlerta.setIcon(android.R.drawable.ic_dialog_info);

       //Mostrar la ventana
       ventanaAlerta.show();
   }
   
   
   /**
	 * Aquí se crean y almacenan los iconos de los PIs que se utilizarán en la app. Si hay alguna
	 * distinción entre los iconos de los PIs, aquí han de guardarse todos los que hagan falta.
	 */
	private void setPoiIcons() {
		ARDataSource.sPoiIcons.put(DetallesPoi.DETALLESPI_ICON, 
				BitmapFactory.decodeResource(getResources(), R.drawable.icono_pi));
		ARDataSource.sPoiIcons.put(DetallesPoi.DETALLESPI_SELECTED_ICON, 
				BitmapFactory.decodeResource(getResources(), R.drawable.icono_pi_seleccionado));
	}
	
	
   /**
	 * Método para cargar los distintos proveedores de datos disponibles. En este método no se
	 * descargan datos, solamente se preparan los proveedores. 
	 */
	private void setPoiProviders() {
		//Se añaden recursos a la colección SOURCES para descargar PIs
		NetworkDataProvider wikipedia = new WikipediaDataProvider();
		NETWORK_POI_SOURCES.put("wiki",wikipedia);
		
		NetworkDataProvider pfcServer = new PfcDataProvider();
		NETWORK_POI_SOURCES.put("pfcServer",pfcServer);
		
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
				startActivity(new Intent(this,PoiDetailsActivity.class));
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
	
	
	/**
	 * Método que gestiona las acciones a realizar cuando se pulsa la vista de detalles básicos
	 * @param v
	 */
	public void basicDetailsViewTouched(View v){
		onPoiTouched(ARDataSource.SelectedPoi);
	}


}
