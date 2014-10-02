package utilidades;


/**
 * Clase de apoyo que permite calcular las proyecciones de los PIs en la pantalla y el ángulo 
 * de visión de la cámara 
 * @author Jose
 *
 */
public class CameraModel {
    private static final float[] TMP1 = new float[3];
    private static final float[] TMP2 = new float[3];

	public static final float DEFAULT_VIEW_ANGLE = (float) Math.toRadians(45);
	

	private int mWidth = 0; 
	private int mHeight = 0;
	private float mDistance = 0F;
	

	public CameraModel(int width, int height, boolean init) {
		set(width, height, init);
	}

	public void set(int width, int height, boolean init) {
		mWidth = width;
		mHeight = height;
	}

	public int getWidth() {
	    return mWidth;
	}

    public int getHeight() {
        return mHeight;
    }

	public void setViewAngle(float viewAngle) {
		mDistance = (mWidth / 2) / (float) Math.tan(viewAngle / 2);
	}
	
	public void projectPoint(Vector orgPoint, Vector prjPoint, float addX, float addY) {
	    orgPoint.get(TMP1);
	    TMP2[0]=(mDistance * TMP1[0] / -TMP1[2]);
	    TMP2[1]=(mDistance * TMP1[1] / -TMP1[2]);
	    TMP2[2]=(TMP1[2]);
	    TMP2[0]=(TMP2[0] + addX + mWidth / 2);
	    TMP2[1]=(-TMP2[1] + addY + mHeight / 2);
	    prjPoint.set(TMP2);
	}
}
