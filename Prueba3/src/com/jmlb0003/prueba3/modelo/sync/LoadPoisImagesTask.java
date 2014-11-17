package com.jmlb0003.prueba3.modelo.sync;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;



import com.jmlb0003.prueba3.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * Esta clase se encarga de descargar y/o asignar imágenes a un PI que se ha seleccionado para que
 * se muestren los detalles. 
 * //TODO:Las imágenes se guardarán en la memoria del teléfono...
 * @author Jose
 *
 */
public class LoadPoisImagesTask extends AsyncTask<Void, Integer, Void> {
	
	private static final String LOG_TAG = "LoadPoisImagesTask";

	private ProgressBar mProgressBar;
	private String mUrl;
	private Context mContext;
	private int mProgress;
	private ImageView mImageView;
	private Bitmap mBmp;
	
	
	public LoadPoisImagesTask(String url, ProgressBar pb, ImageView img, Context c) {

		mUrl = url;
		mProgressBar = pb;
		mContext = c;
		mImageView = img;
	}
	
	public interface ImageLoaderListener {
		void onImageDownloaded(Bitmap bmp);
	}
	
	
	@Override
	protected void onPreExecute() {
		Log.d(LOG_TAG,"Image onPreexecute");
	    mProgress = 0;
	    mProgressBar.setVisibility(View.VISIBLE);
	    Toast.makeText(mContext, R.string.downloading, Toast.LENGTH_SHORT).show();

	    super.onPreExecute();
	}
	
	
	
	
	@Override
	protected Void doInBackground(Void... params) {
		Log.d(LOG_TAG,"Image doInBackground");
		
		mBmp = getBitmapFromURL(mUrl);

	    while (mProgress < 100) {
	    	
	    	mProgress += 1;

	        publishProgress(mProgress);

	        /*--- an image download usually happens very fast so you would not notice 
	         * how the ProgressBar jumps from 0 to 100 percent. You can use the method below 
	         * to visually "slow down" the download and see the progress bein updated ---*/

	        SystemClock.sleep(200);

	    }
		
		return null;
	}
	
	
	
	@Override
	protected void onProgressUpdate(Integer... values) {

	    mProgressBar.setProgress(values[0]);
	    
	    Log.d(LOG_TAG,"Image onProgressUpdate con valor:"+values[0]);
	    
	    super.onProgressUpdate(values);
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.d(LOG_TAG,"Image onPostWxecute!!!");
		
		if (mBmp != null) {
			Log.d(LOG_TAG,"Image poniendo la imagen y todo");
			mImageView.setImageBitmap(mBmp);
		    mProgressBar.setVisibility(View.INVISIBLE);
		    mImageView.setVisibility(View.VISIBLE);
		}

	    super.onPostExecute(result);
	}

	
	public Bitmap getBitmapFromURL(String link) {
		Log.d(LOG_TAG,"Image getBitmapFromURL");
	    try {
	        URL url = new URL(link);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();

	        return BitmapFactory.decodeStream(input);

	    } catch (IOException e) {
	        e.printStackTrace();
	        Log.e(LOG_TAG, e.getMessage().toString());
	        return null;
	    }
	}


}
