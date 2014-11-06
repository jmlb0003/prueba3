package com.jmlb0003.prueba3.modelo;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncInfo;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.controlador.ARDataSource;
import com.jmlb0003.prueba3.controlador.NetworkDataProvider;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;



/**
 * Clase que representa a Wikipedia como proveedor de recursos descargables
 * @author Jose
 *
 */
public class WikipediaDataProvider extends NetworkDataProvider {
	private static final String BASE_URL = "http://api.geonames.org/findNearbyWikipediaJSON";

	private static Bitmap sIcon = null;
	private static Bitmap sSelectedIcon = null;
	private static Bitmap sWikipediaIcon = null;
	
	private final Context mContext;
	
	
	/**
	 * Constructor de la clase para poder descargar recursos geolocalizados de Wikipedia
	 * @param res
	 */
	public WikipediaDataProvider(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		
		mContext = context;

		createIcon();
	}

    private void createIcon() {
        sIcon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icono_pi);
        sSelectedIcon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icono_pi_seleccionado);
        sWikipediaIcon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.wikipedia);
    }

    
    /**
     * Método que crea la URL personalizada para el proveedor Wikipedia
     */
	public String createRequestURL() {
		double lat = ARDataSource.getCurrentLocation().getLatitude();
		double lon = ARDataSource.getCurrentLocation().getLongitude();
		float radius = (ARDataSource.getRadius() > 20)?20.0f:ARDataSource.getRadius();
		String locale = "es";
		String username = "jmlb0003";
		
		return BASE_URL+
				"?lat=" + lat +
				"&lng=" + lon +
				//La opción gratuita de esta API no permite consultas con radius mayor que 20
				"&radius=" + radius +
				"&maxRows=20" +
				"&lang=" + locale+
				"&username=" + username;
//http://api.geonames.org/findNearbyWikipediaJSON?lat=37.6759861&lng=-3.5661972&radius=15&maxRows=15&lang=es&username=jmlb0003
	}

	
	/**
	 * Método para interpretar el JSON que se obtiene del proveedor wikipedia para convertirlo en 
	 * una lista de PIs. Si hay algún fallo en el proceso, se devolverá null.
	 * @param wikipediaPoisStrg Cadena que contiene el objeto en formato JSON
	 * @return Lista de objetos ContentValues con los pares clave-valor que contienen todos los 
	 * 		atributos los PIs obtenidos del proveedor
	 */
	private List<ContentValues> processJSONObject(String wikipediaPoisStrg) {

		// Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // Estos son los nombres de las variables dentro del JSON
        final String WIKI_ROOT = "geonames";
        final String WIKI_NAME = "title";
        final String WIKI_DESC = "summary";
        final String WIKI_WEB = "wikipediaUrl";
        final String WIKI_LATITUDE = "lat";
        final String WIKI_LONGITUDE = "lng";
        final String WIKI_ALTITUDE = "elevation";
        

        try {
            JSONObject poisInJson = new JSONObject(wikipediaPoisStrg);
            if (!poisInJson.has(WIKI_ROOT)) {
            	return null;
            }
            
            JSONArray poisArray = poisInJson.getJSONArray(WIKI_ROOT);
            if (poisArray == null) {
            	return null;
            }
            
            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(poisArray.length());

            int top = Math.min(MAX_RESOURCES_NUMBER, poisArray.length());
			for (int i = 0; i < top; i++) {
				// Variables del PI para almacenar en la BD 
//				int id;
				int color;
				String name;
//				int userID;
				String image;
				String description;
				String webSite;
				double lat,lon,alt;
				float price;
				int openTime, closeTime;
				int maxAge, minAge;
				
				
				JSONObject jo = poisArray.getJSONObject(i);

		        if (jo != null && jo.has("title") && jo.has("lat") && jo.has("lng") && jo.has("elevation") ) {
//		        	id = jo.getInt(WIKI_ID);
		        	name = jo.getString(WIKI_NAME);
//		        	color = jo.getInt(WIKI_COLOR);
		        	color = Color.WHITE;
//		        	image = jo.getString(WIKI_IMAGE);
		        	//Adaptar a la BD que llevaría el String...
		        	image = "android.resource://com.jmlb0003.prueba3/drawable/wikipedia.png";
		        	description = jo.getString(WIKI_DESC);
		        	webSite = jo.getString(WIKI_WEB);
		        	lat = jo.getDouble(WIKI_LATITUDE);
		        	lon = jo.getDouble(WIKI_LONGITUDE);
		        	alt = jo.getDouble(WIKI_ALTITUDE);
		        	price = 0;
		        	openTime = 0;
		        	closeTime = 0;
		        	maxAge = 0;
		        	minAge = 0;

		        	ContentValues poiValues = new ContentValues();

		        	poiValues.put(PoiEntry.COLUMN_POI_NAME, name);
		        	poiValues.put(PoiEntry.COLUMN_POI_COLOR, color);
		        	poiValues.put(PoiEntry.COLUMN_POI_IMAGE, image);
		        	poiValues.put(PoiEntry.COLUMN_POI_DESCRIPTION, description);
	                poiValues.put(PoiEntry.COLUMN_POI_WEBSITE, webSite);
	                poiValues.put(PoiEntry.COLUMN_POI_LATITUDE, lat);
	                poiValues.put(PoiEntry.COLUMN_POI_LONGITUDE, lon);
	                poiValues.put(PoiEntry.COLUMN_POI_ALTITUDE, alt);
	                poiValues.put(PoiEntry.COLUMN_POI_PRICE, price);
	                poiValues.put(PoiEntry.COLUMN_POI_OPEN_HOURS, openTime);
	                poiValues.put(PoiEntry.COLUMN_POI_CLOSE_HOURS, closeTime);
	                poiValues.put(PoiEntry.COLUMN_POI_MAX_AGE, maxAge);
	                poiValues.put(PoiEntry.COLUMN_POI_MIN_AGE, minAge);


	                cVVector.add(poiValues);
		        }else{
		        	continue;
		        }
				
				
			}
			
			return cVVector;
			
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
		return null;
	}
	
	

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		Log.d(LOG_TAG, "Starting sync");

		
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Contendrá todos los PIs en formato JSON para la URL que solicitemos
        String wikipediaPoisStrg = null;


        try {          

            URL url = new URL(createRequestURL());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            wikipediaPoisStrg = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        List<ContentValues> processedPois = processJSONObject(wikipediaPoisStrg);
        
        if ( processedPois != null && processedPois.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[processedPois.size()];
            processedPois.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(PoiEntry.CONTENT_URI, cvArray);
        }
        Log.d(LOG_TAG, "FetchWeatherTask Complete. " + processedPois.size() + " Inserted");
        
        return;		
	}
	
	
	
	/**
     * Método para inicializar el provider
     * @param context
     */
	@Override
	protected void initialize(Context context) {
        getSyncAccount(context);
    }
    

	
	/**
     * Método para lanzar la consulta al proveedor.
     * @param context An app context
     */
	@Override
    public void syncImmediately(Context context) {
        Bundle bundle = new Bundle();

        //La petición se coloca al principio de la cola de peticiones de sincronización
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        //Se fuerza a que se realice la sincronización
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        //Inicia la operación de sincronización
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
    
    
    
    /**
     * Método para obtener una cuenta provisional para el SyncAdapter si existe o crear una nueva
     * si aún no existe.
     * @param context Contexto en el que se utiliza la cuenta
     * @return Devuelve una cuenta provisional con la que se realiza la sincronización
     */
     private Account getSyncAccount(Context context) {

         AccountManager accountManager =
                 (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

         //Crear el tipo de cuenta y una cuenta por defecto
         Account newAccount = new Account(context.getString(R.string.app_name), 
        		 context.getString(R.string.sync_account_type));

             // Si no hay contraseña, es porque la cuenta no existe y por tanto, se crea una nueva
             if (null == accountManager.getPassword(newAccount)) {
                 // Añadir la cuenta y el tipo de cuenta sin contraseña ni datos de usuario
                 if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                     return null;
                 }

                 onAccountCreated(newAccount, context);
             }
         return newAccount;
     }
     
     
     /**
      * Método con las instrucciones a ejecutar cuando se crea por primera vez una cuenta para
      * la descarga de los recursos.
      * @param newAccount
      * @param context
      */
     private void onAccountCreated(Account newAccount, Context context) {
         //Realizar una primera sincronización al crear la cuenta
         
    	 syncImmediately(context);
     }
   
    
}