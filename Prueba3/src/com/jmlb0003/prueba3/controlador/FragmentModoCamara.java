package com.jmlb0003.prueba3.controlador;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.utilidades.LowPassFilter;
import com.jmlb0003.prueba3.utilidades.Matrix;
import com.jmlb0003.prueba3.vista.AugmentedView;
import com.jmlb0003.prueba3.vista.BasicDetailsView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Clase que representa el Fragment que contiene y controla los componentes del modo Realidad
 * Aumentada en la app.
 * @author Jose
 *
 */
public class FragmentModoCamara extends Fragment implements SensorEventListener, OnTouchListener {
	private static final String LOG_TAG = "FragmentModoCamara";
		
	/***************CONSTANTES ORIENTACION*******************************************/
	//Flag para controlar si los sensores se pueden leer o no
    private static final AtomicBoolean COMPUTING = new AtomicBoolean(false);
    //Matriz de rotaci�n final
    private static final float ROTATION_MATRIX[] = new float[9];
    //Array con los datos de los aceler�metros
    private static final float GRAVITY_ORIENTATION[] = new float[3];
    //Array con los datos de los sensores magn�ticos
    private static final float MAGNETIC_ORIENTATION[] = new float[3];
    
    /***************CONSTANTES ORIENTACION*******************************************/
    //Matriz que almacena la orientaci�n/ubicaci�n actual del dispositivo
    private static final Matrix WORLD_COORDINATES = new Matrix();
    //Matrices para compensar las diferencias entre las posiciones respecto del polo magn�tico 
    //y el polo geogr�fico
    private static final Matrix MAGNETIC_COMPENSATED_COORDINATES = new Matrix();
    private static final Matrix MAGNETIC_NORTH_COMPENSATION = new Matrix();
    //Matriz para almacenar la matriz de rotaci�n si se rota 90 grados en el eje X 
    private static final Matrix X_AXIS_ROTATION = new Matrix();
    
    
    
    /***************VARIABLES SENSORES*******************************************/
    //Almacena la situaci�n del campo magn�tico en la ubicaci�n y tiempo actuales
    private static GeomagneticField sGMF = null;
    //Almacena los valores necesarios para aplicar el filtro de paso bajo en las lecturas de 
    //los sensores
    private static float sSmooth[] = new float[3];
    //Proveedor de los datos de los sensores
    private static SensorManager sSensorMgr = null;
    //Aceler�metro del dispositivo
    private static Sensor sSensorGrav = null;
    //Sensor magn�tico del dispositivo
    private static Sensor sSensorMag = null;
      
    
    /***************VARIABLES PRIVADAS*******************************************/
    private AugmentedView mAugmentedView;
    private Activity mActivity;
    private BasicDetailsView mBasicDetails;
    
    ////Variables para agrupar puntos solapados
    private float mDx;
    private int mPoiIndex;
    private List<Poi> mGroupedPois = new ArrayList<>();
    ///////////////////////////////////////////////////

	/** Interfaz para indicar a MainActivity que se ha pulsado un PI **/
    private OnPoiTouchedListener mCallback;
    
    /***************FUNCIONES*******************************************/
    
    /**
     * Este es el primer evento del ciclo de vida del fragment
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
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
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	Log.d(LOG_TAG,"EV onCreate");

    	List<Sensor> sensors;

		
		//Se inicializa la matriz de rotaci�n
		X_AXIS_ROTATION.set( 	
        		1f, 		0f, 									0f, 
        		0f, (float) Math.cos(Math.toRadians(-90)), (float) -Math.sin(Math.toRadians(-90)),
        		0f, (float) Math.sin(Math.toRadians(-90)), (float) Math.cos(Math.toRadians(-90))
        );
    	

		mActivity = getActivity();
		
		//Se obtienen el aceler�metro y la br�jula del dispositivo para poder leerlos despu�s
    	sSensorMgr = (SensorManager) mActivity.getSystemService(Context.SENSOR_SERVICE);     

        sensors = sSensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);            
        if (sensors.size() > 0) {
        	sSensorGrav = sensors.get(0);
        }

        sensors = sSensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);            
        if (sensors.size() > 0) {
        	sSensorMag = sensors.get(0);
        }
    }// Fin de onCreate()
    
    
    
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(LOG_TAG,"EV onCreateView");
		//Para obtener el layout donde se construye la vista en modo Realidad Aumentada:
		// 1- Obtener la vista principal del fragment
		// 2- Obtener el layout donde se colocan las vistas de camara e informaci�n de aumento
		View rootView = inflater.inflate(R.layout.fragment_modo_camara, container, false);
		RelativeLayout preview = (RelativeLayout) rootView.findViewById(R.id.layoutParaCamara);
		
		mActivity = getActivity();
        
		//Se crea el view con la informaci�n de aumento de realidad y sus par�metros de layout
		mAugmentedView = (AugmentedView) preview.findViewById(R.id.augmented_view_id);
        mAugmentedView.setOnTouchListener(this);
        
        mBasicDetails = (BasicDetailsView) preview.findViewById(R.id.basic_details_id);
        
        //Se establece que el alto conserve el ratio 1:1 (aproximado) para seguir las l�neas de dise�o
        //https://www.google.com/design/spec/layout/metrics-and-keylines.html#metrics-and-keylines-ratio-keylines
        float d = getResources().getDisplayMetrics().density;
        int h = getResources().getDisplayMetrics().heightPixels;
        int w = getResources().getDisplayMetrics().widthPixels;
        int f = (Math.round(h-w-(80/d)));
        
        if (f < 200) {
        	f = 250;
        }
        mBasicDetails.getLayoutParams().height = f;
        

	    return rootView;
	}// Fin de onCreateView
	
	
	/**
	 * Se llama cada vez que el fragment est� en pantalla para interactuar con el usuario.
	 * Se encarga de iniciar las lecturas del aceler�metro y de la br�jula
	 */
	@Override
	public void onResume() {
		super.onResume();
		Log.d(LOG_TAG,"EV onResume");

        sSensorMgr.registerListener(this, sSensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
        sSensorMgr.registerListener(this, sSensorMag, SensorManager.SENSOR_DELAY_NORMAL);
        
        if (ARDataSource.hasSelectededPoi()) {
        	mBasicDetails.setVisibility(View.VISIBLE);
    		mBasicDetails.initView(ARDataSource.SelectedPoi);
        }
        
	}// Fin de onResume()
	
	
	
	/**
	 * Se llama cada vez que el fragment deja de estar en primer plano.
	 * Se encarga de parar las lecturas de los sensores del dispositivo.
	 */
    public void onPause() {
        super.onPause();
        Log.d(LOG_TAG,"EV onpause");
        sSensorMgr.unregisterListener(this);
    }// Fin de onPause


    @Override
    public void onDestroy() {
    	super.onDestroy();
    	Log.d(LOG_TAG,"EV onDestroy");
    }// Fin de onDestroy

    /**
	 * Si hay alg�n cambio en la precisi�n de las lecturas de los sensores se llama a este
	 * m�todo. Concretamente se est� controlando los posibles fallos y que la br�jula no tenga
	 * STATUS_UNRELIABLE porque en ese caso no se podr�an recoger datos.
	 */
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		if (sensor == null) {
			return;
		}
		
        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD 
        		&& accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
        	Log.e(LOG_TAG, "Compass data unreliable");
        }
		
	}// Fin de onAccuracyChanged



	
	/**
	 * Se llama cada vez que se obtiene una nueva lectura de los sensores registrados.
	 * Aqu� se calcula la matriz de rotaci�n necesaria para calcular la posici�n en pantalla de 
	 * los Poi que ser�n mostrados.
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		/**
    	 * Se espera que sea false. Si lo es, se le asigna true. Eso hace la funcion compareAndSet.
    	 * Devuelve false si el valor no era el esperado (en este caso se esperaba false)
    	 */
    	if (!COMPUTING.compareAndSet(false, true)) {
    		return;
    	}

    	// Se recogen los valores de los sensores y se les aplica un filtro para eliminar ruido
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        	sSmooth = LowPassFilter.filter(0.5f, 1.0f, event.values, GRAVITY_ORIENTATION);
            GRAVITY_ORIENTATION[0] = sSmooth[0];
            GRAVITY_ORIENTATION[1] = sSmooth[1];
            GRAVITY_ORIENTATION[2] = sSmooth[2];
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
        	sSmooth = LowPassFilter.filter(2.0f, 4.0f, event.values, MAGNETIC_ORIENTATION);
            MAGNETIC_ORIENTATION[0] = sSmooth[0];
            MAGNETIC_ORIENTATION[1] = sSmooth[1];
            MAGNETIC_ORIENTATION[2] = sSmooth[2];
        }

        /**
         * Originalmente, se usaba TEMP en lugar de ROTATION_MATRIX para luego transformar los ejes
         * con la funcion remapCoordinateSystem al tener modo landscape. Como ya no se usa,
         * directamente cogemos la matriz de rotaci�n para usarla sin remapearla.
         * @see http://developer.android.com/reference/android/hardware/SensorManager.html#getRotationMatrix(float[], float[], float[], float[])
         */
        SensorManager.getRotationMatrix(ROTATION_MATRIX, null, GRAVITY_ORIENTATION, MAGNETIC_ORIENTATION);

        /**
         * @see http://developer.android.com/reference/android/hardware/SensorManager.html#remapCoordinateSystem(float[], int, int, float[])
         */
        // Hasta aqu�, se han hecho las operaciones necesarias para obtener la rotaci�n del
        // dispositivo (en modo apaisado)
        //ORIGINAL
//        SensorManager.remapCoordinateSystem(TEMP, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, ROTATION_MATRIX);

        WORLD_COORDINATES.set(	ROTATION_MATRIX[0], ROTATION_MATRIX[1], ROTATION_MATRIX[2], 
        						ROTATION_MATRIX[3], ROTATION_MATRIX[4], ROTATION_MATRIX[5], 
    							ROTATION_MATRIX[6], ROTATION_MATRIX[7], ROTATION_MATRIX[8]);

        MAGNETIC_COMPENSATED_COORDINATES.toIdentity();

        synchronized (MAGNETIC_NORTH_COMPENSATION) {
        	MAGNETIC_COMPENSATED_COORDINATES.prod(MAGNETIC_NORTH_COMPENSATION);
        }

        MAGNETIC_COMPENSATED_COORDINATES.prod(WORLD_COORDINATES);

        MAGNETIC_COMPENSATED_COORDINATES.invert(); 

        // Ahora se calcula la matriz de rotaci�n con la que se calcular�n las coordenadas de 
        // pantalla donde se dibujan los PIs. Por �ltimo, se pone computing a falso.
        ARDataSource.setRotationMatrix(MAGNETIC_COMPENSATED_COORDINATES);

        COMPUTING.set(false);
        
        //Si hay movimiento en los sensores, se llama a postInvalidate que realiza las
        //operaciones necesarias para que la pantalla se redibuje
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER || 
        		event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mAugmentedView.postInvalidate();
        }
	}
	
	
	/**
	 * M�todo para calcular la matriz de compensaci�n entre los polos norte magn�tico y geogr�fico
	 */
	public void calculateMagneticNorthCompensation() {
		double angleY;
	   
	   
		sGMF = new GeomagneticField( (float) ARDataSource.getCurrentLocation().getLatitude(), 
                                    (float) ARDataSource.getCurrentLocation().getLongitude(),
                                    (float) ARDataSource.getCurrentLocation().getAltitude(), 
                                    System.currentTimeMillis());
	   
		angleY = Math.toRadians(-sGMF.getDeclination());
       
       
		//Se calcula el valor del campo magn�tico en sGMF y despu�s se obtiene su valor 
		//del �ngulo en Y que es la diferencia en radianes entre el polo norte magn�tico
		//y geogr�fico que se mueve cada a�o
		synchronized (MAGNETIC_NORTH_COMPENSATION) {
			MAGNETIC_NORTH_COMPENSATION.toIdentity();

			MAGNETIC_NORTH_COMPENSATION.set( (float) Math.cos(angleY), 0f, (float) Math.sin(angleY), 
                                         0f, 1f, 0f, 
                                         (float) -Math.sin(angleY), 0f, (float) Math.cos(angleY));

			MAGNETIC_NORTH_COMPENSATION.prod(X_AXIS_ROTATION);
		}
		
	}
	
	
	
	/********************************************************************************/
	/***********************OPERACIONES CON LOS PoiS******************************/
	/********************************************************************************/
	
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		Log.d(LOG_TAG,"EV onTouc");
		view.performClick();

        // Listening for the down and up touch events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            	for (Poi poi : ARDataSource.getPois()) {
        	        if (poi.handleClick(event.getX(), event.getY())) {
        	        	//Si se pincha en un PI que est� en un grupo
        	        	if (poi.isAdjusted()) {
        	        		mDx = event.getX();
        	        		//Si es la primera vez, obtener el PI para obtener el grupo despues
        	        		if (mGroupedPois.isEmpty()) {
        	        			mPoiIndex = 0;
        	        			mGroupedPois = mAugmentedView.getPoiGroup(poi);
        	        		}
        	        	}        	        	        	            
        	            return true;
        	        }
        	    }
            	mDx = -1;
        		mPoiIndex = -1;
        		mGroupedPois.clear();
        		//Si se levanta el dedo fuera de un PI y no est� mostr�ndose alguno de los grupos...
            	if (ARDataSource.hasSelectededPoi() && mPoiIndex < 0) {
            		mCallback.onPoiUnselected(ARDataSource.SelectedPoi);
            		mBasicDetails.setVisibility(View.INVISIBLE);
            	}
            	
                return true;
                
            
            case MotionEvent.ACTION_MOVE:            	

            	if ( (mDx > 0) && (Math.abs(event.getX() - mDx) > 100) ){
            		if ((event.getX() - mDx) < 0) {
            			moveToLeft();
                	}
                	if ((event.getX() - mDx) > 0) {
                		moveToRight();
                	}
                	mDx = -1;
            	}
            	
            	
            	return true;

            case MotionEvent.ACTION_UP:
            	//Cuando el evento no es de la vista de detalles b�sicos se comprueban los PIs
            	for (Poi poi : ARDataSource.getPois()) {
        	        if (poi.handleClick(event.getX(), event.getY())) {
        	        	poiTouched(poi);
        	            
        	            return true;
        	        }
        	    }
            	
            	//Si se levanta el dedo fuera de un PI y no est� mostr�ndose alguno de los grupos...
            	if (ARDataSource.hasSelectededPoi() && mPoiIndex < 0) {
            		mCallback.onPoiUnselected(ARDataSource.SelectedPoi);

            		mBasicDetails.setVisibility(View.INVISIBLE);
            	}
            	
            	return false;            	
        }
        return false;
	}
	
	
	
	
	private void moveToLeft() {
		if (mPoiIndex < 0) {
			return;
		}else{			
			if (mPoiIndex == 0) {
				mPoiIndex = mGroupedPois.size()-1;
			}else{
				mPoiIndex--;
			}
		}
		Log.d(LOG_TAG,"mPoiIndex:"+mPoiIndex+" Size:"+mGroupedPois.size());
		poiTouched(mGroupedPois.get(mPoiIndex));
	}
	
	
	private void moveToRight() {
		if(mPoiIndex < 0) {
			return;
		}else{
			if (mPoiIndex == mGroupedPois.size()-1) {
				mPoiIndex = 0;
			}else{
				mPoiIndex++;
			}
		}
		Log.d(LOG_TAG,"mPoiIndex:"+mPoiIndex+" Size:"+mGroupedPois.size());
		poiTouched(mGroupedPois.get(mPoiIndex));
	}
	
	
	
	/**
	 * M�todo con las acciones a realizar cuando se pulsa un PI
	 * @param poi PI que ha sido pulsado
	 */
	private void poiTouched(Poi poi) {
		if (poi == null) {
			return;
		}

		if (!poi.isAdjusted() || !poi.isSelected()) {
			mCallback.onPoiTouched(poi);
		}
		
		mBasicDetails.setVisibility(View.VISIBLE);
		mBasicDetails.initView(poi);		
	}
}
