package com.jmlb0003.prueba3.controlador;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.DetallesPoi;
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.vista.BasicDetailsView;


/**
 * Clase que representa el Fragment que contiene y controla los componentes del modo mapa
 * en la app.
 * @author Jose
 *
 */
public class FragmentModoMapa extends Fragment implements OnMarkerClickListener, 
				OnMapClickListener {
	
	/**Constante con el tag representativo de la clase para el logcat**/
	private static final String LOG_TAG = "FragmentModoMapa";


	private static GoogleMap mMap = null; // Might be null if Google Play services APK is not available.
	private SupportMapFragment mMapFragment;
	private Marker mSelectedMarker = null;
	
	private FragmentActivity mActivity;
	private BasicDetailsView mBasicDetails;
	
	private OnPoiTouchedListener mCallback;

		
		
	/**
     * Este es el primer evento del ciclo de vida del fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(LOG_TAG,"EV onAttach");
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnPoiTouchedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPoiTouchedListener");
        }
    }
	    
	    
    @SuppressLint("ClickableViewAccessibility")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		Log.d(LOG_TAG,"EV oncreateView");
		if (container == null) {
			return null;
		}

		View rootView = inflater.inflate(R.layout.fragment_modo_mapa, container, false);
		
		mActivity = getActivity();
		//Se busca el fragment que va a contener el layout fragment_modo_mapa 
		Fragment f = mActivity.getSupportFragmentManager().findFragmentByTag("tagMapa");
		
		//Dentro de dicho layout, está el supportMapFragment y el basicDetailsView
		mMapFragment = (SupportMapFragment) f.getChildFragmentManager().findFragmentById(R.id.mapa_container_id);

		//Por último se obtiene el BasicDetailsView de rootView
		mBasicDetails = (BasicDetailsView) rootView.findViewById(R.id.basic_details2_id);

		
		//Se establece que el alto conserve el ratio 1:1 (aproximado) para seguir las líneas de diseño
        //https://www.google.com/design/spec/layout/metrics-and-keylines.html#metrics-and-keylines-ratio-keylines
        float d = getResources().getDisplayMetrics().density;
        int h = getResources().getDisplayMetrics().heightPixels;
        int w = getResources().getDisplayMetrics().widthPixels;
        Log.d("BasicDetailsEn MAPA","Al final esta es la altura:"+Math.round((h-(80/d)) -w));
        mBasicDetails.getLayoutParams().height = (Math.round(h-w-(80/d)));			
		
		
		Log.d(LOG_TAG, "onCreateView terminado");
		return rootView;
		
	}// Fin de onCreateView

	
	
    @Override
	public void onResume() {
        super.onResume();
        Log.d(LOG_TAG,"EV onResume");
        
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity)) {
	        case 2: //out date
	            try {
	                GooglePlayServicesUtil.getErrorDialog(2, mActivity, 0).show();
	            } catch (Exception e) {
	                e.printStackTrace();
	            }	            
	            
	            break;
        }
        
        setUpMapIfNeeded();
    }
    
    
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(LOG_TAG,"EV onDestroy"); 	
    }
    

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    public void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
    	Log.d(LOG_TAG, "SetupIfNeeded:Entra");
        if (mMap == null && mMapFragment != null) {
            // Try to obtain the map from the SupportMapFragment.
        	mMap = mMapFragment.getMap();

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }else if (mMap != null){
        	mMap.clear();
        	setUpMap();
        }
        Log.d(LOG_TAG, "Setup:termina");
    }
		    
    
    
		    
		    
    /**
     * The mapfragment's id must be removed from the FragmentManager or else if the same 
     * it is passed on the next time then app will crash 
     */
    @Override
    public void onDestroyView() {
    	Log.d(LOG_TAG, "EV ondestroyview:entra");
        super.onDestroyView();
        
        if (mMap != null) {

        	if (mMapFragment != null && mMapFragment.isResumed()) {
    	    	mActivity.getSupportFragmentManager().beginTransaction()
    			.remove(mMapFragment)
    			.commit();
    	    }
            mMap = null;
        }
        Log.d(LOG_TAG, "ondestroyview: sale");
    }

    /**
     * Método donde se añaden al mapa los markers, listeners, etc. 
     * <p>
     * Solo debe ser llamado una vez que se asegure que {@link #mMap} no es null.
     */
    private void setUpMap() {
    	Log.d(LOG_TAG,"Setup Map: Añadiendo marcadores, etc...");
    	// Hide the zoom controls as the button panel will cover it.
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);	//Al pulsar un marker, se muestran iconos
        mMap.setBuildingsEnabled(false);
        
        if (isVisible()) {
	        // Add lots of markers to the map.
	        addMarkersToMap();
	        
	        
	        // Set listeners for marker events.  See the bottom of this class for their behavior.
	        mMap.setOnMarkerClickListener(this);
	        mMap.setOnMapClickListener(this);
	        
	        
	        double lat,lon;
	        if (ARDataSource.hasSelectededPoi()) {
	        	lat = ARDataSource.SelectedPoi.getLatitude();
	        	lon = ARDataSource.SelectedPoi.getLongitude();	        	
	        }else{
	        	lat = ARDataSource.getCurrentLocation().getLatitude();
	        	lon = ARDataSource.getCurrentLocation().getLongitude();
	        }
	        mMap.animateCamera(
	        		CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),
    				calculateMapZoomLevel(ARDataSource.getRadius()*1000)));
        }
    } 
		    
   
    
    /**
     * Método para añadir al mapa los PIs que se encuentran dentro del rango de búsqueda actual
     */
    private void addMarkersToMap() {

    	Log.d(LOG_TAG,"añandiendo Pois al mapa");
    	
    	for (Poi poi : ARDataSource.getPois()) {
    		if (poi.getIcon() != null) {
    			
    			if (poi.isSelected()) {
    				Log.d(LOG_TAG,"SELECCIONADO!!!El poi "+poi.getName()+" tiene icono");

    				mSelectedMarker = mMap.addMarker(new MarkerOptions()
										.position(new LatLng(poi.getLatitude(), poi.getLongitude()))
										.title( poi.getName() )
										.icon(BitmapDescriptorFactory.fromBitmap(poi.getIcon())) );
    			}else{
    				mMap.addMarker(new MarkerOptions()
					.position( new LatLng(poi.getLatitude(), poi.getLongitude()) )
					.title( poi.getName() )
					.icon(BitmapDescriptorFactory.fromBitmap(poi.getIcon()) )  );
    			}
    		}else{
    			mMap.addMarker(new MarkerOptions()
							.position( new LatLng(poi.getLatitude(), poi.getLongitude()) )
							.title( poi.getName() )
							.icon( BitmapDescriptorFactory.fromBitmap(ARDataSource.sPoiIcons
									.get(DetallesPoi.DETALLESPI_ICON)) )  );
    		}
	    		
	    }
    	
    	if (ARDataSource.hasSelectededPoi()) {
    		mBasicDetails.setVisibility(View.VISIBLE);
    		mBasicDetails.initView(ARDataSource.SelectedPoi);
    	}

    	mMap.addMarker(new MarkerOptions().position(new LatLng(ARDataSource.getCurrentLocation().getLatitude(),ARDataSource.getCurrentLocation().getLongitude()))
    			.title(getString(R.string.you_are_here))
    			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))  );
    	
    }

	
	
	@Override
	public void onMapClick(LatLng point) {
		Log.d(LOG_TAG,"EV onMapClick");
		
		if (ARDataSource.hasSelectededPoi()) {
    		deseleccionarMarker();
		}
		
	}
	

	@Override
	public boolean onMarkerClick(final Marker marker) {
		if (marker != null) {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final long duration = 1000;

            final Interpolator interpolator = new BounceInterpolator();

            handler.post(new Runnable() {
                @Override
                public void run() {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = Math.max(1 - interpolator
                            .getInterpolation((float) elapsed / duration), 0);
                    marker.setAnchor(0.5f, 1.0f + 2 * t);

                    if (t > 0.0) {
                        // Post again 16ms later.
                        handler.postDelayed(this, 16);
                    }
                }
            });


            if ( !marker.getTitle().equals(getString(R.string.you_are_here)) ) {
            	poiTouched(ARDataSource.getPoiByName(marker.getTitle()), marker);
            }else{
            	if (ARDataSource.hasSelectededPoi()) {
            		deseleccionarMarker();
            	}
            }
            
        }
        
        
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
	}
	
	
	/**
	 * Método con las acciones a realizar cuando se pulsa un PI
	 * @param poi PI que ha sido pulsado
	 * @param m Marker que ha sido pulsado
	 */
	private void poiTouched(Poi poi, Marker m) {
		if (poi == null) {
			return;			
		}
		
		mCallback.onPoiTouched(poi);
		
		m.setIcon(BitmapDescriptorFactory.fromBitmap(
				ARDataSource.sPoiIcons.get(DetallesPoi.DETALLESPI_SELECTED_ICON)));
		
		if (mSelectedMarker != null && mSelectedMarker != m) {
			mSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
					ARDataSource.sPoiIcons.get(DetallesPoi.DETALLESPI_ICON)));
		}
		mSelectedMarker = m;
		
		mBasicDetails.setVisibility(View.VISIBLE);
		mBasicDetails.initView(poi);
	}
	
	
	private void deseleccionarMarker() {
		mCallback.onPoiUnselected(ARDataSource.SelectedPoi);
		mSelectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
				ARDataSource.sPoiIcons.get(DetallesPoi.DETALLESPI_ICON)));
		mSelectedMarker = null;
		
		mBasicDetails.setVisibility(View.INVISIBLE);
	}
	
	
	/**
	 * Método que calcula el nivel de zoom necesario para que los markers que se van a mostrar 
	 * quepan en la pantalla. Se utiliza el alcance actual del radar como distancia a mostrar en
	 * pantalla.
	 * @param maxDistance Distancia que se va a mostrar en pantalla
	 * @return Valor entero del zoom que se aplica a Google Maps para que quepan los markers en
	 * pantalla.
	 */
	private int calculateMapZoomLevel(float maxDistance) {
        //@see: http://stackoverflow.com/questions/5939983/how-does-this-google-maps-zoom-level-calculation-work
        return (int) (16 - Math.log(maxDistance/500) / Math.log(2));
	}


}
