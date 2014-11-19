package com.jmlb0003.prueba3.modelo.sync;


import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.modelo.data.PoiContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Esta clase se encarga de descargar y/o asignar imágenes a un PI que se ha seleccionado para que
 * se muestren los detalles.
 * @author Jose
 *
 */
public class LoadPoisImagesTask extends AsyncTask<Void, Integer, Void> {
	
	private static final String LOG_TAG = "LoadPoisImagesTask";
	private static final String IMAGE_DIR = "poi_images";
	
	private ProgressBar mProgressBar;
	private String mUrl;
	private Poi mPoi;
	private String mImageName;
	private Context mContext;
	private ImageView mImageView;
	private Bitmap mBmp;
	
	
	public LoadPoisImagesTask(Poi p, ProgressBar pb, ImageView img, Context c) {
		if (p == null || pb == null || img == null || c == null) {
			return;
		}
		
		mPoi = p;
		mUrl = mPoi.getImage();
		mImageName = mPoi.getID()+".png";
		
		mProgressBar = pb;
		mContext = c;
		mImageView = img;
	}
	
	
	@Override
	protected void onPreExecute() {
	    mImageView.setVisibility(View.INVISIBLE);
	    mProgressBar.setVisibility(View.VISIBLE);

	    super.onPreExecute();
	}
	
	
	
	
	@Override
	protected Void doInBackground(Void... params) {
		mBmp = getBitmap(mUrl);

		return null;
	}


	@Override
	protected void onPostExecute(Void result) {
		Log.d(LOG_TAG,"Image onPostWxecute!!!");
		
		if (mBmp != null) {
			mImageView.setImageBitmap(mBmp);
		    mProgressBar.setVisibility(View.INVISIBLE);
		    mImageView.setVisibility(View.VISIBLE);
		}

	    super.onPostExecute(result);
	}

	
	/**
	 * Método encargado de obtener la imagen del PI. Dicha imagen se busca primero en la memoria
	 * interna dedicada a la app y si no se encuentra, se descarga de la URL que se pasa como 
	 * parámetro.
	 * @param imageUrl	URL donde se encuentra la imagen para descargar
	 * @return
	 */
	private Bitmap getBitmap(String imageUrl) {
		Bitmap toRetBitmap = null;

		//Se busca la imagen en memoria, y si no está: se descarga, se almacena, y se retorna
		switch (imageUrl) {
		
			case PoiContract.PoiEntry.WIKIPEDIA_DEFAULT_IMAGE:
				toRetBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.wikipedia);

				break;
				
			case PoiContract.PoiEntry.LOCAL_DEFAULT_IMAGE:
				toRetBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.local_data);
				
				break;
//			case PoiContract.PoiEntry.GOOGLE_PLACES_DEFAULT


				
			default:
				try {

					File f = new File(mContext.getDir(IMAGE_DIR, Context.MODE_PRIVATE), mImageName);

					if (f != null && f.exists()) {
						FileInputStream fi = new FileInputStream(f);
						toRetBitmap = BitmapFactory.decodeStream(fi);
					}

				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
					return null;
				}
				
				break;
		}

		if (toRetBitmap != null) {
			return toRetBitmap;
		}

		
		//Comprobamos si hay conexión a Internet, y si no, se pone una imagen predeterminada
		NetworkInfo ni = 
				((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
			   	.getActiveNetworkInfo();

		if (ni == null || !ni.isConnected()) {
			return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.disconnected);
		}

		
	    try {
	    	
	        URL url = new URL(imageUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        toRetBitmap = BitmapFactory.decodeStream(input);
	        
	        if (toRetBitmap != null) {
		        int w = toRetBitmap.getWidth();
		        int h = toRetBitmap.getHeight();
		        int top = (w > h)?w:h;
		        float dpi = top / mContext.getResources().getDisplayMetrics().density;
		        int scale = 1;		        
		        
		        //Queremos que la imagen se almacene con su dimensión mayor a 200dp aprox.
		        if (dpi > 200) {
		        	while ( (dpi / scale) > 200 ) {
			        	scale++;
			        }
		        	scale--;
		        }
		        
		        toRetBitmap = Bitmap.createScaledBitmap(toRetBitmap, 
		        						Math.round(w/scale), Math.round(h/scale), false);		        		
		        		
	        	saveToInternalSorage(toRetBitmap);
			}
	        
	        return toRetBitmap;

	    } catch (IOException e) {
	        e.printStackTrace();
	        Log.e(LOG_TAG, e.getMessage().toString());
	        return null;
	    }
	}


	/**
	 * Método encargado de almacenar en la memoria interna del dispositivo una copia comprimida
	 * de la imagen del poi. El nombre del archivo será el ID del poi con extensión png.
	 * @param bitmapImage Bitmap que se va a almacenar en memoria
	 */
	private void saveToInternalSorage(Bitmap bitmapImage){		
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir(IMAGE_DIR, Context.MODE_PRIVATE);
        File mypath = new File(directory, mImageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);

            bitmapImage.compress(Bitmap.CompressFormat.PNG, 0, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	
	

}
