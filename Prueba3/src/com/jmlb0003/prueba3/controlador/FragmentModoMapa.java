package com.jmlb0003.prueba3.controlador;


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
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.vista.BasicDetailsView;





public class FragmentModoMapa extends Fragment implements OnMarkerClickListener, OnMapClickListener,
				OnInfoWindowClickListener {
	
	/**Constante con el tag representativo de la clase para el logcat**/
	private static final String LOG_TAG = "FragmentModoMapa";


	private static GoogleMap mMap = null; // Might be null if Google Play services APK is not available.
	private SupportMapFragment mMapFragment;
	
	private FragmentActivity mActivity;
	private BasicDetailsView mBasicDetails;
	
	private OnPoiTouchedListener mCallback;
	
	
	
	/***************Clase representante de un cuadro con información del PI***********************/
	/** Demonstrates customizing the info window and/or its contents. 
    class CustomInfoWindowAdapter implements InfoWindowAdapter {
        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = mActivity.getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = mActivity.getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
//            if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_window) {
//                // This means that getInfoContents will be called.
//                return null;
//            }
//            render(marker, mWindow);
//            return mWindow;
        	return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
//            if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
//                // This means that the default info contents will be used.
//                return null;
//            }
            render(marker, mContents);
            return mContents;
        }

        private void render(Marker marker, View view) {
            int badge;
            // Use the equals() method on a Poi to check for equals.  Do not use ==.
            if (marker.equals(mBrisbane)) {
                badge = R.drawable.badge_qld;
            } else if (marker.equals(mAdelaide)) {
                badge = R.drawable.badge_sa;
            } else if (marker.equals(mSydney)) {
                badge = R.drawable.badge_nsw;
            } else if (marker.equals(mMelbourne)) {
                badge = R.drawable.badge_victoria;
            } else if (marker.equals(mPerth)) {
                badge = R.drawable.badge_wa;
            } else {
                // Passing 0 to setImageResource will clear the image view.
                badge = 0;
            }
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getSnippet();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }
    }
	****************Fin de la clase CustomInfoWindowAdapter*******************************/

		
		
		
		
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
    	if (ARDataSource.hasSelectededPoi()) {
    		deseleccionarPoi(ARDataSource.SelectedPoi);
    	}    	
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
        	Log.d(LOG_TAG, "Setup:mMap=null");
            // Try to obtain the map from the SupportMapFragment.
        	mMap = mMapFragment.getMap();
        	Log.d(LOG_TAG, "Setup:mMap=iniciado");


            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
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
        mMap.getUiSettings().setZoomControlsEnabled(false);
        
        if (isVisible()) {
	        // Add lots of markers to the map.
	        addMarkersToMap();
	        
	        
	//        // Setting an info window adapter allows us to change the both the contents and look of the
	//        // info window.
	//        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
	        
	        // Set listeners for marker events.  See the bottom of this class for their behavior.
	        mMap.setOnMarkerClickListener(this);
	        mMap.setOnMapClickListener(this);
	        mMap.setOnInfoWindowClickListener(this);
	        
	        LatLng currentPosition = new LatLng(ARDataSource.getCurrentLocation().getLatitude(),
	        								ARDataSource.getCurrentLocation().getLongitude());
	        
	        
	        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 
	        								calculateMapZoomLevel(ARDataSource.getRadius()*1000)));
        }
    } 
		    
   
    
    /**
     * Método para añadir al mapa los PIs que se encuentran dentro del rango de búsqueda actual
     */
    private void addMarkersToMap() {

    	Log.d(LOG_TAG,"añandiendo Poi al mapa");
    	
    	for (Poi poi : ARDataSource.getPois()) {
    		if (poi.getIcon() != null) {
   
    			mMap.addMarker(new MarkerOptions()
	    					.position( new LatLng(poi.getLatitude(), poi.getLongitude()) )
	    					.title( poi.getName() )
	    					.icon(BitmapDescriptorFactory.fromBitmap(poi.getIcon()) )  );
	    			
    		}else{
    			mMap.addMarker(new MarkerOptions()
							.position( new LatLng(poi.getLatitude(), poi.getLongitude()) )
							.title( poi.getName() )
							.icon( BitmapDescriptorFactory.fromResource(R.drawable.icono_pi) )  );
    		}
	    		
	    }
    	
    	
    	/////////////////////////////////////////
    	mMap.addMarker(new MarkerOptions().position(new LatLng(ARDataSource.getCurrentLocation().getLatitude(),ARDataSource.getCurrentLocation().getLongitude()))
    			.title(getString(R.string.you_are_here))
    			.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))  );
    	
    }
    
    



	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(mActivity, "Click Info Window", Toast.LENGTH_SHORT).show();
	}

	
	
	@Override
	public void onMapClick(LatLng point) {
		Log.d(LOG_TAG,"EV onMapClick");
		
		if (ARDataSource.hasSelectededPoi()) {
			deseleccionarPoi(ARDataSource.SelectedPoi);
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
            	poiTouched(ARDataSource.getPoiByName(marker.getTitle()));
            }
            
        }
        
        
        // We return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
	}
	
	
	/**
	 * Método con las acciones a realizar cuando se pulsa un PI
	 * @param poi
	 */
	private void poiTouched(Poi poi) {		
		if (poi == null) {
			return;			
		}
		
		/**
		 * Se ha pulsado Poi.
		 * 1º si hay ya uno pulsado:
		 * 		1.1 Si es distinto, se cambia el seleccionado y se abre o se cambian los detalles básicos
		 * 		1.2 Si es igual, se abre la ventana de detalles
		 * 2º si no hay ninguno pulsado:
		 * 		2.1 Se pone como seleccionado y se abre la vista de detalles básicos
		 */		
		if (ARDataSource.hasSelectededPoi()) {

			if (ARDataSource.SelectedPoi != poi) {
				//Se deselecciona el Poi actual y se selecciona el nuevo
				deseleccionarPoi(ARDataSource.SelectedPoi);
				
			}
		}
		
		
		seleccionarPoi(poi);
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

	
	
	/**
	 * Método para marcar como seleccionado un Poi de los que actualmente se encuentran en pantalla
	 * @param poi PI que se va a marcar como seleccionado
	 */
	private void seleccionarPoi(Poi poi) {
		if (poi == null) {
			return;			
		}
		
		Log.d(LOG_TAG,"Seleccionar poi:"+poi.getName());
		poi.setTouched(true);
		ARDataSource.SelectedPoi = poi;
		mCallback.onPoiSelected(poi);
		
		mBasicDetails.setVisibility(View.VISIBLE);
		mBasicDetails.initView(poi);
	}
	


	
	
	/**
	 * Método para desmarcar como seleccionado un Poi previamente seleccionado
	 * @param poi Marker que se va a deseleccionar
	 */
	private void deseleccionarPoi(Poi poi) {
		if (poi == null) {
			return;			
		}
		
		Log.d(LOG_TAG,"Deseleccionar poi:"+poi.getName());
		if (poi != null) {
			poi.setTouched(false);
			mCallback.onPoiUnselected(poi);
			ARDataSource.SelectedPoi = null;
		}
		
		mBasicDetails.setVisibility(View.INVISIBLE);
	}



}




