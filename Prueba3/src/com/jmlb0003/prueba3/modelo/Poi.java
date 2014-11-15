package com.jmlb0003.prueba3.modelo;


import java.text.DecimalFormat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.utilidades.CameraModel;
import com.jmlb0003.prueba3.utilidades.PosicionPI;
import com.jmlb0003.prueba3.utilidades.Utilities;
import com.jmlb0003.prueba3.utilidades.Vector;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintableBox;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintableBoxedText;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintableGps;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintableIcon;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintableObject;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintablePosition;
import com.jmlb0003.prueba3.vista.Radar;


/**
 * Clase que se utiliza para controlar todo lo relacionado con los PIs. Los datos y operaciones 
 * de cada PI están encapsulados en una instancia de la clase Poi.
 * @author Jose
 *
 */
public class Poi implements Comparable<Poi> {
	
	/****Constante para formatear el texto de la distancia hasta el marcador******/
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("@#");
    
    /**Vectores para ubicar el icono de cada PI**/
    private static final Vector SYMBOL_VECTOR = new Vector(0, 0, 0);
    /**Vectores para ubicar el texto de cada PI**/
    private static final Vector TEXT_VECTOR = new Vector(0, 1, 0);
    
    /**Valor en píxeles del radio que se utiliza como icono en los PIs que no tienen un icono asignado**/
    private static final int SYMBOL_RADIUS = 24;
    
    /**Modelo de cámara que permitirá proyectar el PI en pantalla**/
    private volatile static CameraModel sCam = null;
    
    /**VARIABLES PARA PONER OPACAS ALGUNAS ZONAS DEL PI**/
    private static boolean sDebugTouchZone = false;
    private static PaintableBox sTouchBox = null;
    private static PaintablePosition sTouchPosition = null;

    private static boolean sDebugCollisionZone = false;
    private static PaintableBox sCollisionBox = null;
    private static PaintablePosition sCollisionPosition = null;
    

    /**Variables para ubicar y dibujar el símbolo y el texto de cada PI**/
    private final Vector mScreenPositionVector = new Vector();
    private final Vector mTmpSymbolVector = new Vector();
    private final Vector mTmpVector = new Vector();
    private final Vector mTmpTextVector = new Vector();
    private final float[] mDistanceArray = new float[1];
    private final float[] mLocationArray = new float[3];
    private final float[] mScreenPositionArray = new float[3];

    /*****Posición inicial del PI en el eje Y********/
    private float mInitialY = 0.0f;
    
    /**Icono del PI**/
    private Bitmap mBitmap;
    /**Icono del PI cuando está seleccionado**/
    private Bitmap mSelectedBitmap;
    /**Icono del PI que depende del estado actual (si está o no seleccionado)**/
    private Bitmap mCurrentBitmap;
    
    /**Variables para dibujar algunos de los componentes visuales del marcador**/
    private volatile PaintableBoxedText mTextBox = null;
    private volatile PaintablePosition mTextContainer = null;
    /**Variables con las que se ubicará el PI en pantalla**/
    protected final float[] symbolArray = new float[3];
    protected final float[] textArray = new float[3];
    
    /**VARIABLES QUE ALMACENAN LOS COMPONENTES DIBUJABLES DEL PI**/
    /**Variable con la que se pinta el icono del PI**/
    protected volatile PaintableObject gpsSymbol = null;
    /**Contenedor del gpsSymbol del PI**/
    protected volatile PaintablePosition symbolContainer = null;


    /** Indica si el PI debe aparecer en el radar o no**/
    protected volatile boolean isOnRadar = false;
    /**Indica si el PI está dentro del campo de visión o no**/
    protected volatile boolean isInView = false;
    /**Indica si el PI ha sido pulsado**/
    protected volatile boolean isSelected = false;
    /**Indica si el PI ha sido recolocado al estar en colisión con otro PI**/
    protected volatile boolean isAdjusted = false;
    
    /**VARIABLES PARA CONTROLAR LA POSICIÓN EN LA QUE SE DIBUJA EL PI EN PANTALLA
     * X define si está más arriba o más abajo
     * Y define si está más a la derecha o a la izquierda
     * Z no se usa
     **/
    /**Variable para controlar la posición del símbolo del PI en pantalla respecto 
     * del modelo de cámara**/
    protected final Vector symbolXyzRelativeToCameraView = new Vector();
    /**Variable para controlar la posición del texto del PI en pantalla respecto 
     * del modelo de cámara**/
    protected final Vector textXyzRelativeToCameraView = new Vector();
    /**Variable para controlar la posición del usuario respecto de la posición del 
     * PI */
    protected final Vector locationXyzRelativeToPhysicalLocation = new Vector();
    
    /**DETALLES DEL PI**/
    /**Nombre del PI**/
    private String mName = null;
    /**Ubicación del PI en el mundo real **/
    private volatile PosicionPI mPoiPhysicalLocation = new PosicionPI();
    /**Distancia en metros hasta el PI desde la posición del usuario **/
    private volatile double mDistance = 0.0;


    /**Variable que contiene otros atributos del PI**/    
    private DetallesPI mDetails = new DetallesPI(null);
    
    /**
     * Constructor de la clase Poi sin icono.
     * @param name
     * @param latitude
     * @param longitude
     * @param altitude
     * @param otrosDetalles
     */
	public Poi(String name, double latitude, double longitude, double altitude, 
																DetallesPI otrosDetalles) {
		set(name, latitude, longitude, altitude, otrosDetalles);
		mBitmap = null;
		mSelectedBitmap = null;
		mCurrentBitmap = mBitmap;
	}
	
	
    /**
     * Constructor de la clase Poi con icono.
     * @param name Nombre del Poi
     * @param latitude	Latitud del Poi
     * @param longitude Longitud del Poi
     * @param altitude Altitud del Poi
     * @param otrosDetalles Contiene otros atributos del PI. Ver la clase DetallesPI.
     * @param toBitmap Icono que se aplicará al Poi
     * @param toSelectedBitmap Icono que se aplicará al PI cuando sea seleccionado.
     */
	public Poi(String name, double latitude, double longitude, double altitude, 
					DetallesPI otrosDetalles, Bitmap toBitmap, Bitmap toSelectedBitmap) {
		set(name, latitude, longitude, altitude, otrosDetalles);
		mBitmap = toBitmap;
		mSelectedBitmap = toSelectedBitmap;
		mCurrentBitmap = mBitmap;
	}

	
	/**
	 * Inicializa los atributos comunes de todos los PIs.
	 * @param toName Nombre del PI
	 * @param toLatitude Coordenada de latitud del PI en grados decimales
	 * @param toLongitude Coordenada de longitud del PI en grados decimales
	 * @param toAltitude Altitud del PI en metros
	 * @param detalles Contiene otros atributos de los PI. Ver la clase DetallesPI.
	 */
	public synchronized void set(String toName, double toLatitude, double toLongitude, 
													double toAltitude, DetallesPI detalles) {
		if (toName == null) {
			throw new NullPointerException();
		}

		mName = toName;
		mPoiPhysicalLocation.set(toLatitude,toLongitude,toAltitude);		
		isOnRadar = false;
		isInView = false;
		symbolXyzRelativeToCameraView.set(0, 0, 0);
		textXyzRelativeToCameraView.set(0, 0, 0);
		locationXyzRelativeToPhysicalLocation.set(0, 0, 0);
		mInitialY = 0.0f;
		
		mDetails = detalles;
	}

	public synchronized String getName(){
		return mName;
	}
	
	public synchronized long getID(){
		if (mDetails.getDetalle(DetallesPI.DETALLESPI_ID_POI) != null) {
			return (long)mDetails.getDetalle(DetallesPI.DETALLESPI_ID_POI);
		}
		return -1;
	}
	
	public synchronized long getIDUsuario(){
		if (mDetails.getDetalle(DetallesPI.DETALLESPI_USER_ID) != null) {
			return (long)mDetails.getDetalle(DetallesPI.DETALLESPI_USER_ID);
		}
		return -1;
	}

    public synchronized int getColor() {
    	return (int)mDetails.getDetalle(DetallesPI.DETALLESPI_COLOR);
    }

    public synchronized double getDistance() {
        return mDistance;
    }
    
    public synchronized String getTextDistance() {
        return (String) mDetails.getDetalle(DetallesPI.DETALLESPI_DISTANCE);
    }
    
    public synchronized int getImage() {
        return (int)mDetails.getDetalle(DetallesPI.DETALLESPI_IMAGE);
    }
    
    public synchronized String getDescription() {
        return (String)mDetails.getDetalle(DetallesPI.DETALLESPI_DESCRIPTION);
    }
    
    public synchronized String getWebSite() {
        return (String)mDetails.getDetalle(DetallesPI.DETALLESPI_WEBSITE);
    }
    
    public synchronized float getPrice() {
        return (float)mDetails.getDetalle(DetallesPI.DETALLESPI_PRICE);
    }
    
    public synchronized String getOpenHours() {
        return (String)mDetails.getDetalle(DetallesPI.DETALLESPI_OPEN_HOURS);
    }
    
    public synchronized String getCloseHours() {
        return (String)mDetails.getDetalle(DetallesPI.DETALLESPI_CLOSE_HOURS);
    }
    
    public synchronized int getMaxYears() {
        return (int)mDetails.getDetalle(DetallesPI.DETALLESPI_MAX_AGE);
    }
    
    public synchronized int getMinYears() {
        return (int)mDetails.getDetalle(DetallesPI.DETALLESPI_MIN_AGE);
    }
    
    public synchronized Bitmap getIcon() {
        return mCurrentBitmap;
    }

    public synchronized float getInitialY() {
        return mInitialY;
    }

    public synchronized boolean isOnRadar() {
        return isOnRadar;
    }

    public synchronized boolean isInView() {
        return isInView;
    }

    
    public synchronized void setTouched(boolean clicked) {
    	isSelected = clicked;
    	
    	mCurrentBitmap = (isSelected)?mSelectedBitmap:mBitmap;

    }
    
    public synchronized boolean isSelected() {
    	return isSelected;
    }
    
    
    /**
     * Calcula la posición central del PI (icono y texto) a partir de las coordenadas del texto 
     * y el símbolo relativas a la cámara. Esta posición se traduce en unas coordenadas en píxeles
     * donde se dibuja el PI en pantalla. 
     * @return Vector con las coordenadas donde se ubicará el PI.
     */
    public synchronized Vector getScreenPosition() {
        symbolXyzRelativeToCameraView.get(symbolArray);
        textXyzRelativeToCameraView.get(textArray);
        
        float x = (symbolArray[0] + textArray[0])/2;
        float y = (symbolArray[1] + textArray[1])/2;
        float z = (symbolArray[2] + textArray[2])/2;

        if (mTextBox != null) {
        	y += (mTextBox.getHeight()/2);
        }

        mScreenPositionVector.set(x, y, z);
        
        
        return mScreenPositionVector;
    }

    public synchronized Vector getLocation() {
        return locationXyzRelativeToPhysicalLocation;
    }
    
    public synchronized double getLatitude() {
        return mPoiPhysicalLocation.getLatitude();
    }
    
    public synchronized double getLongitude() {
        return mPoiPhysicalLocation.getLongitude();
    }
    
    public synchronized double getAltitude() {
        return mPoiPhysicalLocation.getAltitude();
    }

    public synchronized float getHeight() {
        if (symbolContainer == null || mTextContainer == null) {
        	return 0f;
        }
        
        return symbolContainer.getHeight() + mTextContainer.getHeight();
    }
    
    public synchronized float getWidth() {
        if (symbolContainer == null || mTextContainer == null) {
        	return 0f;
        }
        
        float w1 = mTextContainer.getWidth();
        float w2 = symbolContainer.getWidth();
        
        
        return (w1>w2)?w1:w2;
    }
    
    
    /**
     * Método con el que se actualizan los componentes de la interfaz.
     * @param canvas Canvas en el que se dibuja el PI
     * @param addX Valor de la coordenada X que se va a sumar al actual en la actualización
     * @param addY Valor de la coordenada Y que se va a sumar al actual en la actualización
     */
    public synchronized void update(Canvas canvas, float addX, float addY) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
    	if (sCam == null) {
    		sCam = new CameraModel(canvas.getWidth(), canvas.getHeight(), true);
    	}    	
    	sCam.set(canvas.getWidth(), canvas.getHeight(), false);
        sCam.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE);
        
        
        populateMatrices(sCam, addX, addY);
        
        updateRadar();
        updateView();
    }

    
    /**
     * Método para actualizar las matrices de los objetos del PI (símbolo y texto). Además se
     * añade la variación en X e Y.
     * @param casCamodelo de cámara
     * @param addX Variación en X
     * @param addY Variación en Y
     */
	private synchronized void populateMatrices(CameraModel cam, float addX, float addY) {
		if (cam == null) {
			throw new NullPointerException();
		}
		
		mTmpSymbolVector.set(SYMBOL_VECTOR);
		mTmpSymbolVector.add(locationXyzRelativeToPhysicalLocation);        
        mTmpSymbolVector.prod(ARDataSource.getRotationMatrix());
		
		mTmpTextVector.set(TEXT_VECTOR);
		mTmpTextVector.add(locationXyzRelativeToPhysicalLocation);
		mTmpTextVector.prod(ARDataSource.getRotationMatrix());

		//Se calcula la nueva matriz de proyección del símbolo
		cam.projectPoint(mTmpSymbolVector, mTmpVector, addX, addY);
		//Se asigna el nuevo valor
		symbolXyzRelativeToCameraView.set(mTmpVector);
		//Se calcula la nueva matriz de proyección del texto
		cam.projectPoint(mTmpTextVector, mTmpVector, addX, addY);
		//Se asigna el nuevo valor
		textXyzRelativeToCameraView.set(mTmpVector);
	}

	/**
	 * Método para actualizar la posición del PI dentro del radar
	 */
	private synchronized void updateRadar() {
		isOnRadar = false;

		float range = ARDataSource.getRadius() * 1000;
		float scale = range / Radar.getRadius();
		locationXyzRelativeToPhysicalLocation.get(mLocationArray);
        float x = mLocationArray[0] / scale;
        float y = mLocationArray[2] / scale; // z==y Switched on purpose 
        symbolXyzRelativeToCameraView.get(symbolArray);
        
		if ((symbolArray[2] < -1f) && ((x*x+y*y)<(Radar.getRadius()*Radar.getRadius()))) {
			isOnRadar = true;
		}
	}

	
	/**
	 * Método para calcular si la nueva posición en pantalla del PI está dentro del 
	 * campo de visión o no
	 */
    private synchronized void updateView() {
        isInView = false;

        symbolXyzRelativeToCameraView.get(symbolArray);
        float x1 = symbolArray[0] + (getWidth()/2);
        float y1 = symbolArray[1] + (getHeight()/2);
        float x2 = symbolArray[0] - (getWidth()/2);
        float y2 = symbolArray[1] - (getHeight()/2);
        
        if ( (x1 >= -1  &&  x2 <= sCam.getWidth()) &&  
        		(y1 >= -1 && y2 <= sCam.getHeight())  ) {
            isInView = true;
        }
    }

    
    /**
     * Método para calcular la posición relativa del PI respecto de una posición dada
     * @param location Nueva posición del usuario
     */
    public synchronized void calcRelativePosition(Location location) {
		if (location == null) {
			throw new NullPointerException();
		}
		
		//Se actualiza la distancia hasta desde el usuario el PI
	    updateDistance(location);
	    
	    //Si el PI no tiene una altitud correcta se le asigna la actual del usuario
		if (mPoiPhysicalLocation.getAltitude() == 0.0) {
			mPoiPhysicalLocation.setAltitude(location.getAltitude());
		}
		
		//Se calcula la posición relativa del PI desde la posición del usuario
		PosicionPI.convLocationToVector(location, mPoiPhysicalLocation, 
											locationXyzRelativeToPhysicalLocation);
		mInitialY = locationXyzRelativeToPhysicalLocation.getY();
		
		//Se actualiza el radar con la nueva posición
		updateRadar();
    }
    
    
    /**
     * Método que actualiza la distancia entre el usuario y el PI
     * @param location Posición actual del usuario
     */
    private synchronized void updateDistance(Location location) {
        if (location == null) {
        	throw new NullPointerException();
        }

        Location.distanceBetween(mPoiPhysicalLocation.getLatitude(), 
        		mPoiPhysicalLocation.getLongitude(), location.getLatitude(), 
        		location.getLongitude(), mDistanceArray);
        
        mDistance = mDistanceArray[0];
    }

    
    /**
     * Manejador de las pulsaciones sobre el PI
     * @param x Valor X de las coordenadas del evento de pulsación
     * @param y Valor Y de las coordenadas del evento de pulsación
     * @return Devuelve verdadero si el marcador se ha pulsado o falso si no se ha pulsado
     */
    public synchronized boolean handleClick(float x, float y) {
    	if (!isOnRadar || !isInView) {
    		return false;
    	}
    	
    	return isPointOnPoi(x,y,this);
    }
    
    
    /**
     * Método para cambiar el valor del flag que indica si se ha ajustado la posición del PI por 
     * una colisión.
     * @param v Parámetro para indicar si la posición en pantalla del PI se ha corregido para
     * 		arreglar la colisión con otro PI
     */
    public void setAdjusted(boolean v) {
    	isAdjusted = v;
    }
    
    /**
     * Método para saber si se ha ajustado la posición del PI por una colisión.
     * @return Verdadero si se ajustó la posición del PI o falso en caso contrario.
     */
    public boolean isAdjusted() {
    	return isAdjusted;
    }

    
    /**
     * Método para comprobar si el PI está solapado con otro PI
     * @param otherPoi PI susceptible de estar solapado con el actual
     * @return Verdadero si el PI actual se solapa con el otro o falso en caso contrario
     */
    public synchronized boolean isPoiOnPoi(Poi otherPoi) {
        return isPoiOnPoi(otherPoi,true);
    }

    
    /**
     * Método para comprobar si el PI está solapado con otro PI comprobando las 
     * cuatro esquinas
     * @param otherPoi Poi susceptible de estar solapado con el actual
     * @param reflect Variable que controla las rellamadas de la función. Si se indica true, se
     * 			comprueban las esquinas (other con this) y después se volverá a llamar para 
     * 			comparar las esquinas a la inversa (this con other); si se indica false
     * 			termina el proceso
     * @return
     */
    private synchronized boolean isPoiOnPoi(Poi otherPoi, boolean reflect) {
        otherPoi.getScreenPosition().get(mScreenPositionArray);
        float mdlX = mScreenPositionArray[0];	//Coordenada central X del Poi
        float mdlY = mScreenPositionArray[1];  //Coordenada central Y del Poi
//        Log.d("POI","POI: "+otherPoi.getName()+" mdlX:"+mdlX+"  mdlY:"+mdlY);
        boolean middleOfPoi = isPointOnPoi(mdlX, mdlY, this);
        if (middleOfPoi) {
        	return true;
        }

        float halfWidth = otherPoi.getWidth()/2;	//Anchura, antes era el punto central
        float halfHeight = otherPoi.getHeight()/2;	//Altura, antes era el punto central
//        Log.d("POI","halfWidht: "+halfWidth+" halfHeight:"+halfHeight+"  Con getWi "+otherPoi.getWidth()+" y getHe:"+otherPoi.getHeight());
        if (halfWidth == 0f || halfHeight == 0f) {
//        	Log.d("POI","halfWidht o el otro son false ");
        	return false;
        }
        float x1 = mdlX - halfWidth;
        float y1 = mdlY - halfHeight;
        boolean upperLeftOfPoi = isPointOnPoi(x1,y1,this);
//        Log.d("POI","Las coordenadas upperLeftPoi son X:"+x1+"  Y:"+y1);
        if (upperLeftOfPoi) {
        	return true;
        }

        float x2 = mdlX + halfWidth;
        float y2 = y1;
        boolean upperRightOfPoi = isPointOnPoi(x2,y2,this);
//        Log.d("POI","Las coordenadas upperRightPoi son X:"+x2+"  Y:"+y2);
        if (upperRightOfPoi) {
        	return true;
        }

        float x3 = x1;
        float y3 = mdlY + halfHeight;
        boolean lowerLeftOfPoi = isPointOnPoi(x3,y3,this);
//        Log.d("POI","Las coordenadas lowerleftPoi son X:"+x3+"  Y:"+y3);
        if (lowerLeftOfPoi) {
        	return true;
        }

        float x4 = x2;
        float y4 = y3;
        boolean lowerRightOfPoi = isPointOnPoi(x4,y4,this);
//        Log.d("POI","Las coordenadas lowerRightPoi son X:"+x4+"  Y:"+y4);
        if (lowerRightOfPoi) {
        	return true;
        }

        return (reflect)?otherPoi.isPoiOnPoi(this,false):false;
    }

    
    /**
     * Comprueba si el punto dados por las coordenadas X e Y está dentro del PI
     * @param x Valor de la coordenada X del punto a comprobar
     * @param y Valor de la coordenada Y del punto a comprobar
     * @param poi Poi sobre el que se hace la comprobación
     * @return Verdadero si el punto está dentro del PI o falso en caso contrario
     */
	private synchronized boolean isPointOnPoi(float x, float y, Poi poi) {
	    poi.getScreenPosition().get(mScreenPositionArray);
        float myX = mScreenPositionArray[0];
        float myY = mScreenPositionArray[1];
        float adjWidth = poi.getWidth()/2;
        float adjHeight = poi.getHeight()/2;

        float x1 = myX-adjWidth;
        float y1 = myY-adjHeight;
        float x2 = myX+adjWidth;
        float y2 = myY+adjHeight;

        if (x >= x1 && x <= x2 && y >= y1 && y <= y2) {
        	return true;
        }
        
        return false;
	}

	
	/**
	 * Si el PI está dentro del campo de visión se dibuja el PI (el símbolo y el texto)
	 * @param canvas Canvas donde se dibujará el PI
	 */
    public synchronized void draw(Canvas canvas) {
        if (canvas == null) {
        	throw new NullPointerException();
        }

        if (!isOnRadar || !isInView) {
        	return;
        }
        
        if (sDebugTouchZone) {
        	drawTouchZone(canvas);
        }
        if (sDebugCollisionZone) {
        	drawCollisionZone(canvas);
        }
        
        
        drawIcon(canvas);
        drawText(canvas);
    }

    protected synchronized void drawCollisionZone(Canvas canvas) {
        if (canvas == null) {
        	throw new NullPointerException();
        }
        
        getScreenPosition().get(mScreenPositionArray);
        float x = mScreenPositionArray[0];
        float y = mScreenPositionArray[1];        

        float width = getWidth();
        float height = getHeight();
        float halfWidth = width/2;
        float halfHeight = height/2;

        float x1 = x - halfWidth;
        float y1 = y - halfHeight;

        float x2 = x + halfWidth;
        float y2 = y1;

        float x3 = x1;
        float y3 = y + halfHeight;

        float x4 = x2;
        float y4 = y3;

        Log.w("collisionBox", "ul (x="+x1+" y="+y1+")");
        Log.w("collisionBox", "ur (x="+x2+" y="+y2+")");
        Log.w("collisionBox", "ll (x="+x3+" y="+y3+")");
        Log.w("collisionBox", "lr (x="+x4+" y="+y4+")");
        
        if (sCollisionBox == null) {
        	sCollisionBox = new PaintableBox(width,height,Color.WHITE,Color.RED);
        }else{
        	sCollisionBox.set(width,height);
        }

        float currentAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1])+90;
        
        if (sCollisionPosition == null) {
        	sCollisionPosition = new PaintablePosition(sCollisionBox, x1, y1, currentAngle, 1);
        }else{
        	sCollisionPosition.set(sCollisionBox, x1, y1, currentAngle, 1);
        }
        sCollisionPosition.paint(canvas);
    }

    protected synchronized void drawTouchZone(Canvas canvas) {
        if (canvas == null) {
        	throw new NullPointerException();
        }
        
        if (gpsSymbol == null) {
        	return;
        }
        
        symbolXyzRelativeToCameraView.get(symbolArray);
        textXyzRelativeToCameraView.get(textArray);        
        float x1 = symbolArray[0];
        float y1 = symbolArray[1];
        float x2 = textArray[0];
        float y2 = textArray[1];
        float width = getWidth();
        float height = getHeight();
        float adjX = (x1 + x2)/2;
        float adjY = (y1 + y2)/2;
        float currentAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1])+90;
        adjX -= (width/2);
        adjY -= (gpsSymbol.getHeight()/2);
        
        Log.w("touchBox", "ul (x="+(adjX)+" y="+(adjY)+")");
        Log.w("touchBox", "ur (x="+(adjX+width)+" y="+(adjY)+")");
        Log.w("touchBox", "ll (x="+(adjX)+" y="+(adjY+height)+")");
        Log.w("touchBox", "lr (x="+(adjX+width)+" y="+(adjY+height)+")");
        
        if (sTouchBox == null) {
        	sTouchBox = new PaintableBox(width,height,Color.WHITE,Color.GREEN);
        }else{
        	sTouchBox.set(width,height);
        }

        if (sTouchPosition == null) {
        	sTouchPosition = new PaintablePosition(sTouchBox, adjX, adjY, currentAngle, 1);
        }else{
        	sTouchPosition.set(sTouchBox, adjX, adjY, currentAngle, 1);
        }
        
        sTouchPosition.paint(canvas);
    }
    
    protected synchronized void drawIcon(Canvas canvas) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}

    	float scaleDistance = 1.0f;
    	if (isSelected)
    		scaleDistance = 1.4f;
    	else if (mDistance > 5000)
    		scaleDistance = 0.7f;
    	else if (mDistance > 10000)
    		scaleDistance = 0.5f;
    	else if (mDistance > 30000)
    		scaleDistance = 0.4f;
    		   	
    	

        if (gpsSymbol == null && mCurrentBitmap == null) {
        	gpsSymbol = 
        			new PaintableGps((SYMBOL_RADIUS*ARDataSource.PixelsDensity*scaleDistance), 
        					(SYMBOL_RADIUS*ARDataSource.PixelsDensity*scaleDistance), true, getColor());
        }else{
        	if (mCurrentBitmap != null) {
        		gpsSymbol = new PaintableIcon(mCurrentBitmap,scaleDistance);
        	}
        }

        textXyzRelativeToCameraView.get(textArray);
        symbolXyzRelativeToCameraView.get(symbolArray);

        float currentAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], 
        										textArray[0], textArray[1]);
        float angle = currentAngle + 90;

        if (symbolContainer == null) {
        	symbolContainer = new PaintablePosition(gpsSymbol, symbolArray[0], symbolArray[1], angle, 1);
        }else{
        	symbolContainer.set(gpsSymbol, symbolArray[0], symbolArray[1], angle, 1);
        }

        symbolContainer.paint(canvas);
    }

    protected synchronized void drawText(Canvas canvas) {
		if (canvas == null) {
			throw new NullPointerException();
		}
		
	    String textStr = null;
	    if (mDistance < 1000.0) {
	        textStr = mName;// + " ("+ DECIMAL_FORMAT.format(mDistance) + "m)";
	        mDetails.actualizaDistancia(DECIMAL_FORMAT.format(mDistance) + " m");
	    } else {
	        double d = mDistance/1000.0;
	        textStr = mName;// + " (" + DECIMAL_FORMAT.format(d) + "km)";
	        mDetails.actualizaDistancia(DECIMAL_FORMAT.format(d) + " km");
	    }


	    textXyzRelativeToCameraView.get(textArray);
	    symbolXyzRelativeToCameraView.get(symbolArray);

	    
	    float textSize = 16 * ARDataSource.PixelsDensity;
	    
	    if (mTextBox == null) {
	    	mTextBox = new PaintableBoxedText(textStr, textSize, 300);
	    }else{
	    	mTextBox.set(textStr, textSize, 300);
	    }

	    float currentAngle = Utilities.getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]);
        float angle = currentAngle + 90;

	    float x = textArray[0] - (mTextBox.getWidth() / 2);
	    float y = textArray[1] + (textSize * 2);

	    if (mTextContainer == null) {
	    	mTextContainer = new PaintablePosition(mTextBox, x, y, angle, 1);
	    }else{
	    	mTextContainer.set(mTextBox, x, y, angle, 1);
	    }
	    
	    mTextContainer.paint(canvas);
	}

    
    /**
     * Compara el PI con otro PI teniendo en cuenta la distancia hasta el usuario.
     * @param another PI con el que se compara esta instancia
     * @returns Un entero negativo si este PI tiene una distancia menor que another; un entero
     * 		positivo si este PI tiene una distancia mayor que another; 0 si están a la misma distancia.
     */
    public synchronized int compareTo(Poi another) {
        if (another == null) {
        	throw new NullPointerException();
        }

        return (int)Math.round(mDistance-another.getDistance());
    }


    
    @Override
    public synchronized boolean equals(Object poi) {
        if(poi == null || mName == null) {
        	throw new NullPointerException();
        }
        
        if (((Poi)poi).getID() != getID()) {
        	return false;
        }
        
        if (!mName.equals(((Poi)poi).getName())) {
        	return false;
        }
        
        if (((Poi)poi).getLatitude() != getLatitude()) {
        	return false;
        }
        
        if (((Poi)poi).getLongitude() != getLongitude()) {
        	return false;
        }
        
        return true;
    }
    
    
}
