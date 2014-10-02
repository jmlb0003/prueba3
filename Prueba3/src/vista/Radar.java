package vista;


import android.graphics.Canvas;
import android.graphics.Color;


import utilidades.CameraModel;
import utilidades.PitchAzimuthCalculator;
import utilidades.ScreenPositionUtility;
import utilidades.Paintables.PaintableCircle;
import utilidades.Paintables.PaintableLine;
import utilidades.Paintables.PaintablePosition;
import utilidades.Paintables.PaintableRadarPoints;
import utilidades.Paintables.PaintableText;
import controlador.ARDataSource;


/**
 * Esta clase sirve para manejar todos los eventos y operaciones relacionados con el radar que se 
 * dibuja en pantalla.
 * @author Jose
 *
 */
public class Radar {
    
    private static final int LINE_COLOR = Color.argb(150,0,220,220);
    private static final int RADAR_COLOR = Color.argb(100, 0, 0, 200);
    private static final int TEXT_COLOR = Color.rgb(255,255,255);
    private static final int TEXT_SIZE = 10;

    private static ScreenPositionUtility leftRadarLine = null;
    private static ScreenPositionUtility rightRadarLine = null;
    private static PaintablePosition leftLineContainer = null;
    private static PaintablePosition rightLineContainer = null;
    private static PaintablePosition circleContainer = null;
    
    private static PaintableRadarPoints radarPoints = null;
    private static PaintablePosition pointsContainer = null;
    
    private static PaintableText paintableText = null;
    private static PaintablePosition paintedContainer = null;
    
    /********Variables con valores en píxeles**********/
    private static float sPad_X = 10;
    private static float sPad_Y = 20;
    private static float sPixelsDensity = 1.0f;	//Esta es la densidad de píxeles de la pantalla
    private static float sRadarRadius = 40;

    
    
    /**
     * Constructor de las instancias de la clase Radar. Para dibujar los elementos contenidos 
     * en el radar (puntos, líneas, texto, etc.) es necesario adjuntar un valor de densidad de 
     * píxeles de pantalla para mantener la homogeneidad de la interfaz.
     * @param PixelDensity Valor de la densidad de píxeles de la pantalla del dispositivo. Las 
     * medidas en píxeles usadas para dibujar componentes visuales se multiplican por este valor. 
     * Para obtenerlo desde el contexto de una vista se puede usar:
     *  	context.getResources().getDisplayMetrics().density
     * También se pueden usar las siguientes instrucciones desde una actividad:
     *  	DisplayMetrics dm = new DisplayMetrics();
     *  	getWindowManager().getDefaultDisplay().getMetrics(dm);
     *  	float density = dm.density;
     */
    public Radar(float pixelDensity) {
        if (leftRadarLine == null) {
        	leftRadarLine = new ScreenPositionUtility();
        }
        if (rightRadarLine == null) {
        	rightRadarLine = new ScreenPositionUtility();
        }
        
        sPixelsDensity = pixelDensity;
        sRadarRadius *= sPixelsDensity;
        sPad_X *= sPixelsDensity;
        sPad_Y *= sPixelsDensity;
    }

    
    /**
     * Método para dibujar el radar en pantalla. El proceso es obtener pitch y azimuth y después
     * llamar a los métodos necesarios para dibujar los elementos del radar en el canvas.
     * @param canvas Canvas donde se dibujará el radar
     */
    public void draw(Canvas canvas) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}

    	PitchAzimuthCalculator.calcPitchBearing(ARDataSource.getRotationMatrix());
    	ARDataSource.setAzimuth(PitchAzimuthCalculator.getAzimuth());
        ARDataSource.setPitch(PitchAzimuthCalculator.getPitch());
        
        drawRadarCircle(canvas);
        drawRadarPoints(canvas);
        drawRadarLines(canvas);
        drawRadarText(canvas);
    }
    
    
    /**
     * Método para obtener el radio del círculo que representa el radar en pantalla.
     * @return Valor en unidades DP del radio del radar
     */
    public static float getRadius() {
    	return sRadarRadius;
    }
    
    
    /**
     * Método para obtener la densidad de píxeles por pulgada de la pantalla
     * @return Valor de la densidad de píxeles por pulgada de la pantalla
     */
    public static float getPixelsDensity() {
    	return sPixelsDensity;
    }
    
    
    
    /**
     * Dibuja el círculo del radar
     * @param canvas Canvas sobre el que se dibuja
     */
    private void drawRadarCircle(Canvas canvas) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        if (circleContainer == null) {
            PaintableCircle paintableCircle = new PaintableCircle(RADAR_COLOR,sRadarRadius,true);
            circleContainer = new PaintablePosition(paintableCircle,sPad_X+sRadarRadius,sPad_Y+sRadarRadius,0,1);
        }
        circleContainer.paint(canvas);
    }
    
    private void drawRadarPoints(Canvas canvas) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        if (radarPoints == null) {
        	radarPoints = new PaintableRadarPoints();
        }
        
        if (pointsContainer == null) {
        	pointsContainer = new PaintablePosition( radarPoints,
        												sPad_X,
        												sPad_Y,
        												-ARDataSource.getAzimuth(),
        												1);
        }else {
        	pointsContainer.set(radarPoints, 
				        			sPad_X,
									sPad_Y,
									-ARDataSource.getAzimuth(),
									1);
        }
        
        pointsContainer.paint(canvas);
    }
    
    private void drawRadarLines(Canvas canvas) {
    	if (canvas == null) {
    		throw new NullPointerException();
    	}
    	
        if (leftLineContainer == null) {
            leftRadarLine.set(0, -sRadarRadius);
            leftRadarLine.rotate(-CameraModel.DEFAULT_VIEW_ANGLE / 2);
            leftRadarLine.add(sPad_X+sRadarRadius, sPad_Y+sRadarRadius);

            float leftX = leftRadarLine.getX()-(sPad_X+sRadarRadius);
            float leftY = leftRadarLine.getY()-(sPad_Y+sRadarRadius);
            PaintableLine leftLine = new PaintableLine(LINE_COLOR, leftX, leftY);
            leftLineContainer = new PaintablePosition(  leftLine, 
            		sPad_X+sRadarRadius, 
            		sPad_Y+sRadarRadius, 
                                                        0, 
                                                        1);
        }
        leftLineContainer.paint(canvas);
        
        if (rightLineContainer == null) {
            rightRadarLine.set(0, -sRadarRadius);
            rightRadarLine.rotate(CameraModel.DEFAULT_VIEW_ANGLE / 2);
            rightRadarLine.add(sPad_X+sRadarRadius, sPad_Y+sRadarRadius);
            
            float rightX = rightRadarLine.getX()-(sPad_X+sRadarRadius);
            float rightY = rightRadarLine.getY()-(sPad_Y+sRadarRadius);
            PaintableLine rightLine = new PaintableLine(LINE_COLOR, rightX, rightY);
            rightLineContainer = new PaintablePosition( rightLine, 
            		sPad_X+sRadarRadius, 
            		sPad_Y+sRadarRadius, 
                                                        0, 
                                                        1);
        }
        
        rightLineContainer.paint(canvas);
    }

    private void drawRadarText(Canvas canvas) {
    	if (canvas==null) throw new NullPointerException();
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
                    (sPad_Y - 5), 
                    true
                 );
        
        radarText(  canvas, 
                    formatDist(ARDataSource.getRadius() * 1000), 
                    (sPad_X + sRadarRadius), 
                    (sPad_Y + sRadarRadius*2 -10), 
                    false
                 );
    }
    
    private void radarText(Canvas canvas, String txt, float x, float y, boolean bg) {
    	if (canvas== null || txt == null) {
    		throw new NullPointerException();
    	}
    	
        if (paintableText == null) {
        	paintableText = new PaintableText(txt,TEXT_COLOR,TEXT_SIZE,bg);
        }else {
        	paintableText.set(txt,TEXT_COLOR,TEXT_SIZE,bg);
        }
        
        if (paintedContainer == null) {
        	paintedContainer = new PaintablePosition(paintableText,x,y,0,1);
        }else {
        	paintedContainer.set(paintableText,x,y,0,1);
        }
        
        paintedContainer.paint(canvas);
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