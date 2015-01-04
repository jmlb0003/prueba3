package com.jmlb0003.prueba3.utilidades;

import java.util.List;

import android.location.Location;
import android.location.LocationManager;

public abstract class LocationUtility {
	
	
	/**Constante para el descartar lecturas de ubicaciones en el método isBetterLocation**/
    private static final int TWO_MINUTES = 1000 * 60 * 2;
	
	/** 
	 * Determines whether one Location reading is better than another Location fix
     * @param location  The new Location that you want to evaluate
     * @param anotherLocation  Another Location fix, to which you want to compare the new one
     * @see http://developer.android.com/guide/topics/location/strategies.html#BestEstimate
     */
	public static boolean isBetterLocation(Location location, Location anotherLocation) {
		
		if (anotherLocation == null) {
			return true;
		}else if (anotherLocation.getAltitude() <= 0){
			return true;
		}

		//Comprobar si la nueva ubicación es más o menos reciente que la anterior
		long timeDelta = location.getTime() - anotherLocation.getTime();
		boolean isNewer = timeDelta > 0;
//		Log.d(log,"1.1: "+location.getLatitude() + ","+location.getLongitude()+" AC:"+location.getAccuracy()+" aLT:"+location.getAltitude()+"\n");
//		Log.d(log,"1.2: "+anotherLocation.getLatitude() + ","+anotherLocation.getLongitude()+" AC:"+anotherLocation.getAccuracy()+" aLT:"+anotherLocation.getAltitude()+"\n");
        // Si hace más de dos minutos de la última ubicación ->nueva es mejor
		if (timeDelta > TWO_MINUTES) {
//			Log.d(log,"2");
			return true;
			//Si la nueva ubicación es antigua -> nueva es peor
		} else if (timeDelta < -TWO_MINUTES) {
//			Log.d(log,"3");
			return false;
		}


		int accuracyDelta = (int) (location.getAccuracy() - anotherLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

       
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				anotherLocation.getProvider());
//		Log.d(log,"4");
		// Determinar la mejor localización según las variables calculadas
		if (isMoreAccurate) {
//			Log.d(log,"5");
			return true;
		} else if (isNewer && !isLessAccurate) {
//			Log.d(log,"6");
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
//			Log.d(log,"7");
			return true;
		}
//		Log.d(log,"8");
		return false;
	}
	
	
	
	/** Checks whether two providers are the same */
	private static boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
	  	}
		       
		return provider1.equals(provider2);
	}
	
	
	
	public static Location getBestLastKnownLocation(LocationManager lm, long minTime) {
	   	Location bestLocation = null;
		float bestAccuracy = Float.MAX_VALUE;
	   	long bestTime = Long.MIN_VALUE;
	     
		// Iterate through all the providers on the system, keeping
		// note of the most accurate result within the acceptable time limit.
		// If no result is found within maxTime, return the newest Location.
		List<String> matchingProviders = lm.getAllProviders();
		for (String provider: matchingProviders) {
			Location location = lm.getLastKnownLocation(provider);
	 
			if (location != null && location.hasAltitude()) {
				float accuracy = location.getAccuracy();
				long time = location.getTime();
	     
				if ( (time > minTime) && (accuracy < bestAccuracy) ) {	//Tiempo y precisión mejores
					bestLocation = location;
					bestAccuracy = accuracy;
					bestTime = time;
				}else if ( (time < minTime) && (bestAccuracy == Float.MAX_VALUE) && (time > bestTime) ) {	
					//Tiempo peor que el mínimo, pero no hay otro mejor -> se acepta la posición 
					bestLocation = location;
					bestTime = time;
				}
			}
		}
		
		
		if (bestLocation == null) {
			bestAccuracy = Float.MAX_VALUE;
		   	bestTime = Long.MIN_VALUE;
			for (String provider: matchingProviders) {
				Location location = lm.getLastKnownLocation(provider);
		 
				if (location != null) {
					float accuracy = location.getAccuracy();
					long time = location.getTime();
		     
					if ( (time > minTime) && (accuracy < bestAccuracy) ) {	//Tiempo y precisión mejores
						bestLocation = location;
						bestAccuracy = accuracy;
						bestTime = time;
					}else if ( (time < minTime) && (bestAccuracy == Float.MAX_VALUE) && (time > bestTime) ) {	
						//Tiempo peor que el mínimo, pero no hay otro mejor -> se acepta la posición 
						bestLocation = location;
						bestTime = time;
					}
				}
			}
		}
		
		
		return bestLocation;
	}

}
