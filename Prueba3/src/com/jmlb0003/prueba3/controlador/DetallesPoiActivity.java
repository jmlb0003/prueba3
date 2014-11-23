package com.jmlb0003.prueba3.controlador;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;
import com.jmlb0003.prueba3.modelo.sync.LoadPoisImagesTask;


/**
 * Clase que controla la vista que muestra los detalles de un PI.
 * @author Jose
 */
public class DetallesPoiActivity extends ActionBarActivity {
	private static final String LOG_TAG = "DetallesPoiActivity";
	
	private Poi mShowedPoi = null;
	private RelativeLayout mContainer = null;
	private ProgressBar mProgressBar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalles_pi);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Log.d(LOG_TAG,"a ver si hay poi o no con intent:"+getIntent().getAction());
		if (getIntent().getAction() != null && 
				getIntent().getAction().equals(Intent.ACTION_SEARCH) && 
				getIntent().getData() != null) {
			Log.d(LOG_TAG,"1");
	        Cursor cursor = getContentResolver().query(
	        		//PoiContract.PoiEntry.CONTENT_URI,
	        		getIntent().getData(),	//Aquí va la URI del PI concreto que se mostrará
	        		null,
	        		null,
	        		null, 
	        		null);
	        Log.d(LOG_TAG,"2");
	        if (cursor == null || !cursor.moveToFirst()) {
	        	Log.d(LOG_TAG,"3");
	            finish();
	        } else {
	        	Log.d(LOG_TAG,"4");
	        	//Se añade el PI a la lista en memoria y después se asigna a mShowedPoi
	        	Log.d(LOG_TAG, "El ID del poi que va a mshowed es:"+cursor.getLong(cursor.getColumnIndex(PoiEntry._ID)));
	        	mShowedPoi = ARDataSource.getPoi(cursor.getLong(cursor.getColumnIndex(PoiEntry._ID)));
	        	
	        	/********************************************/
	        	if (mShowedPoi == null) {
	        		Log.d(LOG_TAG,"ES NULL mshowed...");
	        	}else{
	        		Log.d(LOG_TAG,"Mshowed Name: "+mShowedPoi.getName());
	        		Log.d(LOG_TAG,"Mshowed id: "+mShowedPoi.getID());
	        		Log.d(LOG_TAG,"Mshowed Distance: "+mShowedPoi.getDistance());
	        	}
	        }
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (mShowedPoi == null && !ARDataSource.hasSelectededPoi()) {
			finish();
			return;
		}
		
		if (mShowedPoi == null && ARDataSource.hasSelectededPoi()) {
			Log.d(LOG_TAG,"5");
			mShowedPoi = ARDataSource.SelectedPoi;
		}
		
		if (mShowedPoi == null) {
			Log.d(LOG_TAG,"mshowed es null");
		}else{
			if (mShowedPoi.getName() == null) {
				Log.d(LOG_TAG,"y el getname tambien es null");
			}
		}
		
		if (ARDataSource.hasSelectededPoi()){
			Log.d(LOG_TAG,"Hay un poiSeleccionado");
		}
		
		setTitle(mShowedPoi.getName());
		
		mContainer = (RelativeLayout) findViewById(R.id.pi_details_id);
		
		if (mContainer != null) {
			setImage();
			setName();
			setDistance();
			setDescription();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.detalles_poi_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			/*case R.id.action_search:
	            // search action
	            return super.onOptionsItemSelected(item);
	        case R.id.action_help:
	            //helpAction();
	            return super.onOptionsItemSelected(item);
	        case R.id.action_settings:
	        	// Settings action
	        	startActivity(new Intent(this,SettingsActivity.class));
	            return true;*/
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	
	
	private void setImage() {
		
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar2);
		ImageView img = (ImageView) mContainer.findViewById(R.id.pi_image_container2);
		
		if (mProgressBar == null || img == null) {
			return;
		}
		
		new LoadPoisImagesTask(mShowedPoi, mProgressBar, img, this).execute();

		img.setContentDescription("Fotografía de "+mShowedPoi.getName());
		
	}
	
	
	private void setName() {
		TextView name = (TextView) mContainer.findViewById(R.id.pi_name2);
		if (name != null) {
			name.setText(mShowedPoi.getName());
		}else{
			Log.d("DetallesActivity","No hay name container...");
		}
	}
	
	private void setDistance() {
		TextView distance = (TextView) mContainer.findViewById(R.id.pi_distance2);
		if (distance != null) {			
			distance.setText(mShowedPoi.getTextDistance());
		}else{
			Log.d("DetallesActivity","No hay distance container...");
		}
	}
	
	
	private void setDescription() {
		TextView description = (TextView) mContainer.findViewById(R.id.pi_description2);
		if (description != null) {
			description.setText(mShowedPoi.getDescription());
		}else{
			Log.d("DetallesActivity","No hay description container...");
		}
	}
}
