package com.jmlb0003.prueba3.controlador;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.jmlb0003.prueba3.R;





public class FragmentModoMapa extends Fragment {
	
	// TODO: Rename parameter arguments, choose names that match
		// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

		private static View v;
		private static GoogleMap mMap = null; // Might be null if Google Play services APK is not available.
		private FragmentActivity activity;
		
		private OnMarkerTouchedListener mCallback;
	/*
		private OnFragmentInteractionListener mListener;

		/**
		 * Use this factory method to create a new instance of this fragment using
		 * the provided parameters.
		 * 
		 * @param param1
		 *            Parameter 1.
		 * @param param2
		 *            Parameter 2.
		 * @return A new instance of fragment FragmentModoMapa.
		 *
		// TODO: Rename and change types and number of parameters
		public static FragmentModoMapa newInstance(String param1, String param2) {
			FragmentModoMapa fragment = new FragmentModoMapa();
			Bundle args = new Bundle();
			
			fragment.setArguments(args);
			return fragment;
		}

		public FragmentModoMapa() {
			// Required empty public constructor
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			if (getArguments() != null) {
				
			}
		}*/
		
		/**
	     * Este es el primer evento del ciclo de vida del fragment
	     */
	    @Override
	    public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        
	        // This makes sure that the container activity has implemented
	        // the callback interface. If not, it throws an exception
	        try {
	            mCallback = (OnMarkerTouchedListener) activity;
	        } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement OnMarkerTouchedListener");
	        }
	    }
	    
	    

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// Inflate the layout for this fragment
			Log.i("FragmentModoMapa", "Creando el createView del mapa");
			if (container == null) {
				Log.i("FragmentModoMapa", "Createview mapa: el container es null");
				return null;
			}
			/*return inflater.inflate(R.layout.fragment_fragment_modo_mapa, container, false);
			
			   // TODO Auto-generated method stub
			Log.i("INICIO", "Se saca lo del inflate");
		    View view = inflater.inflate(R.layout.fragment_fragment_modo_mapa, container, false);
		    Log.i("INICIO", "Sacado");
		    setUpMapIfNeeded();
		    
		    Log.i("INICIO", "Hecho el oncreateview MAPA");
		    return view;*/
			activity = getActivity();
			v = (RelativeLayout) inflater.inflate(R.layout.fragment_modo_mapa, container, false);
			
			setUpMapIfNeeded();
			
			Log.i("FragmentModoMapa", "onCreateView terminado");
			return v;
		}
	/*
		    @Override
			public void onResume() {
		        super.onResume();
		        setUpMapIfNeeded();
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
		    private void setUpMapIfNeeded() {
		        // Do a null check to confirm that we have not already instantiated the map.
		    	Log.i("INICIO", "SetupIfNeeded:Entra");
		        if (mMap == null) {
		        	Log.i("INICIO", "Setup:mMap=null");
		            // Try to obtain the map from the SupportMapFragment.
		        	mMap = ((SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mapa_container_id)).getMap();
		        	Log.i("INICIO", "Setup:mMap=iniciado");
		            // Check if we were successful in obtaining the map.
		           /* if (mMap != null) {
		                setUpMap();
		            }*/	            
		        }
		        Log.i("INICIO", "Setup:termina");
		    }
		    
		    /**
		     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
		     * just add a marker near Africa.
		     * <p>
		     * This should only be called once and when we are sure that {@link #mMap} is not null.
		    
		    private void setUpMap() {
		    	Log.i("INICIO", "Setup2:a poner el marker");
		        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
		    } */
		    
		    
		    @Override
		    public void onViewCreated(View view, Bundle savedInstanceState) {
		        // TODO Auto-generated method stub
		    	Log.i("INICIO", "onviewCreated: entra");
		        if (mMap == null) {
		            // Try to obtain the map from the SupportMapFragment.
		            mMap = ((SupportMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.mapa_container_id)).getMap();
		            // Check if we were successful in obtaining the map.	         
		        }
		        
		        /*   if (mMap != null) {
	        			setUpMap();
	        		 }*/
		        Log.i("INICIO", "onviewCreated: sale");
		    }
		    
		    
		    /**** The mapfragment's id must be removed from the FragmentManager
		     **** or else if the same it is passed on the next time then 
		     **** app will crash ****/
		    @Override
		    public void onDestroyView() {
		    	Log.i("FragmentModoMapa", "ondestroyview:entra");
		        super.onDestroyView();
		        
		        if (mMap != null) {
		        	Fragment f = activity.getSupportFragmentManager().findFragmentById(R.id.mapa_container_id);

		        	if (f != null && f.isResumed()) {
		    	    	activity.getSupportFragmentManager()
		    			.beginTransaction()
		    			.remove(f)
		    			.commit();
		    	    }
		            mMap = null;
		        }
		        Log.i("FragmentModoMapa", "ondestroyview: sale");
		    }
		    
		    
		    /**
		     * Función que te pone tu posicion en el mapa con animacion
		     
		    private void zoomToMyLocation() {
		    	GeoPoint myLocationGeoPoint = myLocationOverlay.getMyLocation();
		    	if(myLocationGeoPoint != null) {
			    	mapView.getController().animateTo(myLocationGeoPoint);
			    	mapView.getController().setZoom(10);
		    	}
		    }*/
}




