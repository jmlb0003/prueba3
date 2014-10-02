package utilidades;


/**
 * Clase para dar soporte al cálculo del pitch y el azimuth de los PIs a partir de la 
 * matriz de rotación.
 * @author Jose
 *
 */
public class PitchAzimuthCalculator {
    private static final Vector LOOKING = new Vector();
    private static final float[] LOOKING_ARRAY = new float[3];

    private static volatile float sAzimuth = 0;
    private static volatile float sPitch = 0;

    
    
    private PitchAzimuthCalculator() {};

    public static synchronized float getAzimuth() {
        return PitchAzimuthCalculator.sAzimuth;
    }
    public static synchronized float getPitch() {
        return PitchAzimuthCalculator.sPitch;
    }

    public static synchronized void calcPitchBearing(Matrix rotationM) {
        if (rotationM == null) {
        	return;
        }

        LOOKING.set(0, 0, 0);
        rotationM.transpose();
        LOOKING.set(1, 0, 0);
        LOOKING.prod(rotationM);
        LOOKING.get(LOOKING_ARRAY);
        PitchAzimuthCalculator.sAzimuth = ((Utilities.getAngle(0, 0, LOOKING_ARRAY[0], LOOKING_ARRAY[2])  + 360 ) % 360);

        rotationM.transpose();
        LOOKING.set(0, 1, 0);
        LOOKING.prod(rotationM);
        LOOKING.get(LOOKING_ARRAY);
        PitchAzimuthCalculator.sPitch = -Utilities.getAngle(0, 0, LOOKING_ARRAY[1], LOOKING_ARRAY[2]);
    }
}
