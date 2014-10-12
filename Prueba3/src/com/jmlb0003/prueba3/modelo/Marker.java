package com.jmlb0003.prueba3.modelo;


import java.text.DecimalFormat;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;

import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.utilidades.CameraModel;
import com.jmlb0003.prueba3.utilidades.PhysicalLocationUtility;
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
 * de cada PI están encapsulados en una instancia de la clase Marker.
 * @author Jose
 *
 */
public class Marker implements Comparable<Marker> {
	
	/****Constante para formatear el texto de la distancia hasta el marcador******/
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("@#");
    
    /**Vectores para ubicar el símbolo y el texto de cada marker**/
    private static final Vector SYMBOL_VECTOR = new Vector(0, 0, 0);
    private static final Vector TEXT_VECTOR = new Vector(0, 1, 0);
    
    /**Modelo de cámara que permitirá proyectar el marker en pantalla**/
    private volatile static CameraModel sCam = null;
    
    /**VARIABLES PARA PONER OPACAS ALGUNAS ZONAS DEL MARKER**/
    private static boolean sDebugTouchZone = true;
    private static PaintableBox sTouchBox = null;
    private static PaintablePosition sTouchPosition = null;

    private static boolean sDebugCollisionZone = true;
    private static PaintableBox sCollisionBox = null;
    private static PaintablePosition sCollisionPosition = null;
    

    /**Variables para ubicar y dibujar el símbolo y el texto de cada marker**/
    private final Vector mScreenPositionVector = new Vector();
    private final Vector mTmpSymbolVector = new Vector();
    private final Vector mTmpVector = new Vector();
    private final Vector mTmpTextVector = new Vector();
    private final float[] mDistanceArray = new float[1];
    private final float[] mLocationArray = new float[3];
    private final float[] mScreenPositionArray = new float[3];

    /*****Posición inicial del marker en el eje Y********/
    private float mInitialY = 0.0f;
    
    /**Icono del marker**/
    private Bitmap mBitmap;
    
    /**Variables para dibujar algunos de los componentes visuales del marcador**/
    private volatile PaintableBoxedText mTextBox = null;
    private volatile PaintablePosition mTextContainer = null;
    /**Variables con las que se ubicará el marker en pantalla**/
    protected final float[] symbolArray = new float[3];
    protected final float[] textArray = new float[3];
    
    /**VARIABLES QUE ALMACENAN LOS COMPONENTES DIBUJABLES DEL MARKER**/
    /**Símbolo del marker**/
    protected volatile PaintableObject gpsSymbol = null;
    /**Contenedor del Símbolo del marker**/
    protected volatile PaintablePosition symbolContainer = null;
    /**Nombre del marker**/
    protected String name = null;
    /**Ubicación del marker en el mundo real **/
    protected volatile PhysicalLocationUtility markerPhysicalLocation = new PhysicalLocationUtility();
    /**Distancia en metros hasta el marker desde la posición del usuario **/
    protected volatile double distance = 0.0;
    /** Indica si el marker debe aparecer en el radar o no**/
    protected volatile boolean isOnRadar = false;
    /**Indica si el marker está dentro del campo de visión o no**/
    protected volatile boolean isInView = false;
    
    /**VARIABLES PARA CONTROLAR LA POSICIÓN EN LA QUE SE DIBUJA EL MARKER EN PANTALLA
     * X define si está más arriba o más abajo
     * Y define si está más a la derecha o a la izquierda
     * Z no se usa
     **/
    /**Variable para controlar la posición del símbolo del marker en pantalla respecto 
     * del modelo de cámara**/
    protected final Vector symbolXyzRelativeToCameraView = new Vector();
    /**Variable para controlar la posición del texto del marker en pantalla respecto 
     * del modelo de cámara**/
    protected final Vector textXyzRelativeToCameraView = new Vector();
    /**Variable para controlar la posición del usuario respecto de la posición del 
     * marker */
    protected final Vector locationXyzRelativeToPhysicalLocation = new Vector();
    
    /**Color por defecto del marker**/
    protected int color = Color.WHITE;

    

    
    /**
     * Constructor de la clase Marker sin icono.
     * @param name
     * @param latitude
     * @param longitude
     * @param altitude
     * @param color
     */
	public Marker(String name, double latitude, double longitude, double altitude, int color) {
		set(name, latitude, longitude, altitude, color);
		mBitmap = null;
	}
	
	
    /**
     * Constructor de la clase Marker con icono.
     * @param name Nombre del Marker
     * @param latitude	Latitud del Marker
     * @param longitude Longitud del Marker
     * @param altitude Altitud del Marker
     * @param color Color que tendrá el marker en el radar y en las vistas de realidad aumentada y mapa
     * @param toBitmap Icono que se aplicará al Marker
     */
	public Marker(String name, double latitude, double longitude, double altitude, int color, 
							Bitmap toBitmap) {
		set(name, latitude, longitude, altitude, color);
		mBitmap = toBitmap;
	}

	public synchronized void set(String toName, double toLatitude, double toLongitude, 
																double toAltitude, int toColor) {
		if (toName == null) {
			throw new NullPointerException();
		}

		name = toName;
		markerPhysicalLocation.set(toLatitude,toLongitude,toAltitude);
		color = toColor;
		isOnRadar = false;
		isInView = false;
		symbolXyzRelativeToCameraView.set(0, 0, 0);
		textXyzRelativeToCameraView.set(0, 0, 0);
		locationXyzRelativeToPhysicalLocation.set(0, 0, 0);
		mInitialY = 0.0f;
	}

	public synchronized String getName(){
		return name;
	}

    public synchronized int getColor() {
    	return color;
    }

    public synchronized double getDistance() {
        return distance;
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

    /**
     * Calcula la posición del marker en la pantalla a partir de las coordenadas del texto 
     * y el símbolo relativas a la cámara
     * @return Vector con las coordenadas donde se ubicará el marker.
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
     * @param canvas Canvas en el que se dibuja el marker
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
     * Método para actualizar las matrices de los objetos del marker (símbolo y texto). Además se
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
	 * Método para actualizar la posición del marker dentro del radar
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
	 * Método para calcular si la nueva posición en pantalla del marker está dentro del 
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
     * Método para calcular la posición relativa del marker respecto de una posición dada
     * @param location Nueva posición del usuario
     */
    public synchronized void calcRelativePosition(Location location) {
		if (location == null) {
			throw new NullPointerException();
		}
		
		//Se actualiza la distancia hasta desde el usuario el marker
	    updateDistance(location);
	    
	    //Si el marker no tiene una altitud correcta se le asigna la actual del usuario
		if (markerPhysicalLocation.getAltitude() == 0.0) {
			markerPhysicalLocation.setAltitude(location.getAltitude());
		}
		
		//Se calcula la posición relativa del marker desde la posición del usuario
		PhysicalLocationUtility.convLocationToVector(location, markerPhysicalLocation, 
											locationXyzRelativeToPhysicalLocation);
		mInitialY = locationXyzRelativeToPhysicalLocation.getY();
		
		//Se actualiza el radar con la nueva posición
		updateRadar();
    }
    
    
    /**
     * Método que actualiza la distancia entre el usuario y el marker
     * @param location Posición actual del usuario
     */
    private synchronized void updateDistance(Location location) {
        if (location == null) {
        	throw new NullPointerException();
        }

        Location.distanceBetween(markerPhysicalLocation.getLatitude(), 
        		markerPhysicalLocation.getLongitude(), location.getLatitude(), 
        		location.getLongitude(), mDistanceArray);
        
        distance = mDistanceArray[0];
    }

    
    /**
     * Manejador de las pulsaciones sobre el marker
     * @param x Valor X de las coordenadas del evento de pulsación
     * @param y Valor Y de las coordenadas del evento de pulsación
     * @return Devuelve verdadero si el marcador se ha pulsado o falso si no se ha pulsado
     */
    public synchronized boolean handleClick(float x, float y) {
    	if (!isOnRadar || !isInView) {
    		return false;
    	}
    	
    	return isPointOnMarker(x,y,this);
    }

    
    /**
     * Método para comprobar si el marker está solapado con otro marker
     * @param otherMarker Marker susceptible de estar solapado con el actual
     * @return Verdadero si el marker actual se solapa con el otro o falso en caso contrario
     */
    public synchronized boolean isMarkerOnMarker(Marker otherMarker) {
        return isMarkerOnMarker(otherMarker,true);
    }

    
    /**
     * Método para comprobar si el marker está solapado con otro marker comprobando las 
     * cuatro esquinas
     * @param otherMarker Marker susceptible de estar solapado con el actual
     * @param reflect Variable que controla las rellamadas de la función. Si se indica true, se
     * 			comprueban las esquinas (other con this) y después se volverá a llamar para 
     * 			comparar las esquinas a la inversa (this con other); si se indica false
     * 			termina el proceso
     * @return
     */
    private synchronized boolean isMarkerOnMarker(Marker otherMarker, boolean reflect) {
        otherMarker.getScreenPosition().get(mScreenPositionArray);
        float x = mScreenPositionArray[0];
        float y = mScreenPositionArray[1];        
        boolean middleOfMarker = isPointOnMarker(x,y,this);
        if (middleOfMarker) {
        	return true;
        }

        float halfWidth = otherMarker.getWidth()/2;
        float halfHeight = otherMarker.getHeight()/2;

        float x1 = x - halfWidth;
        float y1 = y - halfHeight;
        boolean upperLeftOfMarker = isPointOnMarker(x1,y1,this);
        if (upperLeftOfMarker) {
        	return true;
        }

        float x2 = x + halfWidth;
        float y2 = y1;
        boolean upperRightOfMarker = isPointOnMarker(x2,y2,this);
        if (upperRightOfMarker) {
        	return true;
        }

        float x3 = x1;
        float y3 = y + halfHeight;
        boolean lowerLeftOfMarker = isPointOnMarker(x3,y3,this);
        if (lowerLeftOfMarker) {
        	return true;
        }

        float x4 = x2;
        float y4 = y3;
        boolean lowerRightOfMarker = isPointOnMarker(x4,y4,this);
        if (lowerRightOfMarker) {
        	return true;
        }

        return (reflect)?otherMarker.isMarkerOnMarker(this,false):false;
    }

    
    /**
     * Comprueba si el punto dados por las coordenadas X e Y está dentro del marker
     * @param x Valor de la coordenada X del punto a comprobar
     * @param y Valor de la coordenada Y del punto a comprobar
     * @param marker Marker sobre el que se hace la comprobación
     * @return Verdadero si el punto está dentro del marker o falso en caso contrario
     */
	private synchronized boolean isPointOnMarker(float x, float y, Marker marker) {
	    marker.getScreenPosition().get(mScreenPositionArray);
        float myX = mScreenPositionArray[0];
        float myY = mScreenPositionArray[1];
        float adjWidth = marker.getWidth()/2;
        float adjHeight = marker.getHeight()/2;

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
	 * Si el marker está dentro del campo de visión se dibuja el marker (el símbolo y el texto)
	 * @param canvas Canvas donde se dibujará el marker
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
    	if (distance > 2000)
    		scaleDistance = 0.9f;
    	else if (distance > 10000)
    		scaleDistance = 0.6f;
    	else if (distance > 50000)
    		scaleDistance = 0.4f;
    		   	
    	

        if (gpsSymbol == null && mBitmap == null) {
        	gpsSymbol = 
        			new PaintableGps((24*ARDataSource.PixelsDensity*scaleDistance), 
        					(24*ARDataSource.PixelsDensity*scaleDistance), true, getColor());
        }else{
        	if (mBitmap != null) {
        		gpsSymbol = new PaintableIcon(mBitmap,scaleDistance);
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
	    if (distance < 1000.0) {
	        textStr = name + " ("+ DECIMAL_FORMAT.format(distance) + "m)";          
	    } else {
	        double d = distance/1000.0;
	        textStr = name + " (" + DECIMAL_FORMAT.format(d) + "km)";
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

    public synchronized int compareTo(Marker another) {
        if (another == null) {
        	throw new NullPointerException();
        }
        
        return name.compareTo(another.getName());
    }

    @Override
    public synchronized boolean equals(Object marker) {
        if(marker == null || name == null) {
        	throw new NullPointerException();
        }
        
        return name.equals(((Marker)marker).getName());
    }
}
