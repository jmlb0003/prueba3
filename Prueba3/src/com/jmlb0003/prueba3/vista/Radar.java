package com.jmlb0003.prueba3.vista;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.utilidades.CameraModel;
import com.jmlb0003.prueba3.utilidades.PitchAzimuthCalculator;
import com.jmlb0003.prueba3.utilidades.ScreenPositionUtility;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintableCircle;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintableLine;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintablePosition;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintableRadarPoints;
import com.jmlb0003.prueba3.utilidades.Paintables.PaintableText;



/**
 * Esta clase sirve para manejar todos los eventos y operaciones relacionados con el radar
 * que se dibuja en pantalla.
 * @author Jose
 *
 */
public class Radar {
    
    private static final int LINE_COLOR = Color.argb(150,0,220,220);
    private static final int TEXT_COLOR = Color.rgb(255,255,255);
    private static final int TEXT_SIZE = 10;

    private static ScreenPositionUtility sLeftRadarLine = null;
    private static ScreenPositionUtility sRightRadarLine = null;
    private static PaintablePosition sLeftLineContainer = null;
    private static PaintablePosition sRightLineContainer = null;
    private static PaintablePosition sCircleContainer = null;
    
    private static PaintableRadarPoints sRadarPoints = null;
    private static PaintablePosition sPointsContainer = null;
    
    private static PaintableText sPaintableText = null;
    private static PaintablePosition sPaintedContainer = null;
    
    /********Variables con valores en p�xeles**********/
    private static float sPad_X = 10;
    private static float sPad_Y = 20;
    private static float sPixelsDensity = 0.0f;	//Esta es la densidad de p�xeles de la pantalla
    private static float sRadarRadius = 40;
    private static int sTextSize = TEXT_SIZE;
    
    /********Color del Radar*************************/
    private static int sRadarColor;
    
    
    private Context mContext;

    
    /**
     * Constructor de las instancias de la clase Radar.
     */
    public Radar(Context c) {
    	mContext = c;
    	
    	/**
    	 * De esta forma nos podemos asegurar de que la primera vez se realizan los c�lculos 
    	 * para adaptar las variables en p�xeles a unidades proporcionales a la densidad de 
    	 * p�xeles. Cuando se abre la app despu�s de la primera vez, las variables est�ticas est�n
    	 * ya iniciadas. Por tanto, estas variables solamente se calcular�n la primera vez
    	 * porque si no, se descuadran los puntos del radar.
    	 */
    	if (mContext.getResources().getDisplayMetrics().density != sPixelsDensity) {
    		sPixelsDensity = mContext.getResources().getDisplayMetrics().density;

            if (sLeftRadarLine == null) {
            	sLeftRadarLine = new ScreenPositionUtility();
            }
            if (sRightRadarLine == null) {
            	sRightRadarLine = new ScreenPositionUtility();
            }
            
            sRadarRadius = 40 * sPixelsDensity;
            sPad_X = 10 * sPixelsDensity;
            sPad_Y = 20 * sPixelsDensity;
            sTextSize = Math.round(TEXT_SIZE * sPixelsDensity);
        }
    	
    	sRadarColor = mContext.getResources().getColor(R.color.primary_transparent);
    }

    
    /**
     * M�todo para dibujar el radar en pantalla. El proceso es obtener pitch y azimuth y despu�s
     * llamar a los m�todos necesarios para dibujar los elementos del radar en el canvas.
     * @param canvas Canvas donde se dibujar� el radar
     */
    public void draw(Canvas canvas) {
    	if (canvas == null) {
    		return;
    	}

    	PitchAzimuthCalculator.calcPitchBearing(ARDataSource.getRotationMatrix());
    	ARDataSource.setAzimuth(PitchAzimuthCalculator.getAzimuth());
        
        drawRadarCircle(canvas);
        drawRadarPoints(canvas);
        drawRadarLines(canvas);
        drawRadarText(canvas);
    }
    
    
    /**
     * M�todo para obtener el radio del c�rculo que representa el radar en pantalla.
     * @return Valor en unidades DP del radio del radar
     */
    public static float getRadius() {
    	return sRadarRadius;
    }    
    
    
    /**
     * Dibuja el c�rculo del radar
     * @param canvas Canvas sobre el que se dibuja
     */
    private void drawRadarCircle(Canvas canvas) {
    	if (canvas == null) {
    		return;
    	}
    	
        if (sCircleContainer == null) {
            PaintableCircle paintableCircle = new PaintableCircle(sRadarColor,sRadarRadius,true);
            sCircleContainer = new PaintablePosition(paintableCircle,sPad_X+sRadarRadius,sPad_Y+sRadarRadius,0,1);
        }
        sCircleContainer.paint(canvas);
    }
    
    private void drawRadarPoints(Canvas canvas) {
    	if (canvas == null) {
    		return;
    	}
    	
        if (sRadarPoints == null) {
        	sRadarPoints = new PaintableRadarPoints(sPixelsDensity);
        }
        
        if (sPointsContainer == null) {
        	sPointsContainer = new PaintablePosition( sRadarPoints,
        												sPad_X,
        												sPad_Y,
        												-ARDataSource.getAzimuth(),
        												1);
        }else {
        	sPointsContainer.set(sRadarPoints, 
				        			sPad_X,
									sPad_Y,
									-ARDataSource.getAzimuth(),
									1);
        }
        
        sPointsContainer.paint(canvas);
    }
    
    private void drawRadarLines(Canvas canvas) {
    	if (canvas == null) {
    		return;
    	}
    	
        if (sLeftLineContainer == null) {
            sLeftRadarLine.set(0, -sRadarRadius);
            sLeftRadarLine.rotate(-CameraModel.DEFAULT_VIEW_ANGLE / 2);
            sLeftRadarLine.add(sPad_X+sRadarRadius, sPad_Y+sRadarRadius);

            float leftX = sLeftRadarLine.getX()-(sPad_X+sRadarRadius);
            float leftY = sLeftRadarLine.getY()-(sPad_Y+sRadarRadius);
            PaintableLine leftLine = new PaintableLine(LINE_COLOR, leftX, leftY);
            sLeftLineContainer = new PaintablePosition(  
            		leftLine,
            		sPad_X+sRadarRadius, 
            		sPad_Y+sRadarRadius, 
            		0,
            		1);
        }
        sLeftLineContainer.paint(canvas);
        
        if (sRightLineContainer == null) {
            sRightRadarLine.set(0, -sRadarRadius);
            sRightRadarLine.rotate(CameraModel.DEFAULT_VIEW_ANGLE / 2);
            sRightRadarLine.add(sPad_X+sRadarRadius, sPad_Y+sRadarRadius);
            
            float rightX = sRightRadarLine.getX()-(sPad_X+sRadarRadius);
            float rightY = sRightRadarLine.getY()-(sPad_Y+sRadarRadius);
            PaintableLine rightLine = new PaintableLine(LINE_COLOR, rightX, rightY);
            sRightLineContainer = new PaintablePosition( 
            		rightLine, 
            		sPad_X+sRadarRadius, 
            		sPad_Y+sRadarRadius, 
            		0,
            		1);
        }
        
        sRightLineContainer.paint(canvas);
    }

    private void drawRadarText(Canvas canvas) {
    	if (canvas == null) {
    		return;
    	}
    	
        int range = (int) (ARDataSource.getAzimuth() / (360f / 16f)); 
        String  dirTxt = "";
        if (range == 15 || range == 0) dirTxt = "N"; 
        else if (range == 1 || range == 2) dirTxt = "NE"; 
        else if (range == 3 || range == 4) dirTxt = "E"; 
        else if (range == 5 || range == 6) dirTxt = "SE";
        else if (range == 7 || range == 8) dirTxt= "S"; 
        else if (range == 9 || range == 10) dirTxt = "SW"; 
        else if (range == 11 || range == 12) dirTxt = "W"; 
        else if (range == 13 || range == 14) dirTxt = "NW";
        int bearing = (int) ARDataSource.getAzimuth(); 
        radarText(  canvas, 
                    ""+bearing+((char)176)+" "+dirTxt, 
                    (sPad_X + sRadarRadius), 
                    (sPad_Y - sTextSize), 
                    true
                 );
        
        radarText(  canvas, 
                    formatDist(ARDataSource.getRadius() * 1000), 
                    (sPad_X + sRadarRadius), 
                    (sPad_Y + sRadarRadius*2 -sTextSize), 
                    false
                 );
    }
    
    private void radarText(Canvas canvas, String txt, float x, float y, boolean bg) {
    	if (canvas== null || txt == null) {
    		return;
    	}
    	
        if (sPaintableText == null) {
        	sPaintableText = new PaintableText(txt, TEXT_COLOR, sTextSize, bg);
        }else {
        	sPaintableText.set(txt, TEXT_COLOR, sTextSize, bg);
        }
        
        if (sPaintedContainer == null) {
        	sPaintedContainer = new PaintablePosition(sPaintableText,x,y,0,1);
        }else {
        	sPaintedContainer.set(sPaintableText,x,y,0,1);
        }
        
        sPaintedContainer.paint(canvas);
    }

    private static String formatDist(float meters) {
        if (meters < 1000) {
            return ((int) meters) + "m";
        } else if (meters < 10000) {
            return formatDec(meters / 1000f, 1) + "km";
        } else {
            return ((int) (meters / 1000f)) + "km";
        }
    }

    private static String formatDec(float val, int dec) {
        int factor = (int) Math.pow(10, dec);

        int front = (int) (val);
        int back = (int) Math.abs(val * (factor) ) % factor;

        return front + "." + back;
    }
}