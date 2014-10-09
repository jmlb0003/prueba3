package com.jmlb0003.prueba3.utilidades;

import android.location.Location;


/**
 * Clase de apoyo para calcular la posición y orientación del dispositivo en el 
 * mundo tridimensional.
 * @author Jose
 *
 */
public class PhysicalLocationUtility {
	private static float[] sX = new float[1];
	private static double sY = 0.0d;
	private static float[] sZ = new float[1];
	
	private double mLatitude = 0.0;
	private double mLongitude = 0.0;
	private double mAltitude = 0.0;

	
	
	/**
	 * Constructor por defecto de la clase
	 */
	public PhysicalLocationUtility() { }

	/**
	 * Constructor de la clase
	 * @param pl Valores a asignar a la nueva instancia que se creará
	 */
	public PhysicalLocationUtility(PhysicalLocationUtility pl) {
		if (pl == null) {
			throw new NullPointerException();
		}
		
		set(pl.mLatitude, pl.mLongitude, pl.mAltitude);
	}

	
	public void set(double latitude, double longitude, double altitude) {
        mLatitude = latitude;
        mLongitude = longitude;
        mAltitude = altitude;
	}
	

	public void setLatitude(double latitude) {
		mLatitude = latitude;
	}

	
	public double getLatitude() {
		return mLatitude;
	}

	
	public void setLongitude(double longitude) {
		mLongitude = longitude;
	}

	
	public double getLongitude() {
		return mLongitude;
	}

	
	public void setAltitude(double altitude) {
		mAltitude = altitude;
	}

	
	public double getAltitude() {
		return mAltitude;
	}

	
	/**
	 * Método para convertir las coordenadas de una posición en valores de un vector
	 * @param org Variable que contiene la posición del usuario en coordenadas gps
	 * @param gp Variable que contiene la posición del PI en coordenadas gps
	 * @param v Vector donde se almacena el resultado
	 */
	public static synchronized void convLocationToVector(Location org, PhysicalLocationUtility gp, Vector v) {
		if (org == null || gp == null || v == null) {
			throw new NullPointerException("Location, PhysicalLocationUtility, and Vector cannot be NULL.");
		}

		Location.distanceBetween(	org.getLatitude(), org.getLongitude(), 
									gp.getLatitude(), org.getLongitude(), 
									sZ);

		Location.distanceBetween(	org.getLatitude(), org.getLongitude(), 
									org.getLatitude(), gp.getLongitude(), 
									sX);
		sY = gp.getAltitude() - org.getAltitude();
		if (org.getLatitude() < gp.getLatitude()) {
			sZ[0] *= -1;
		}
		if (org.getLongitude() > gp.getLongitude()) {
			sX[0] *= -1;
		}
		
		v.set(sX[0], (float) sY, sZ[0]);
	}

	@Override
	public String toString() {
		return "(lat=" + mLatitude + ", lng=" + mLongitude + ", alt=" + mAltitude + ")";
	}
}
