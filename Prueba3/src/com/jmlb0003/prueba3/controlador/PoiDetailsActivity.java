package com.jmlb0003.prueba3.controlador;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
public class PoiDetailsActivity extends ActionBarActivity {
	private static final String LOG_TAG = "PoiDetailsActivity";
	
	private Poi mShowedPoi = null;
	private RelativeLayout mContainer = null;
	private ProgressBar mProgressBar = null;
	
	private String mWeb = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi_details);
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
	        	mShowedPoi = ARDataSource.getPoi(cursor.getLong(cursor.getColumnIndex(PoiEntry._ID)));
	        	if (mShowedPoi == null) {
	        		ARDataSource.addPoisFromCursor(cursor);
	        		mShowedPoi = ARDataSource
	        				.getPoi(cursor.getLong(cursor.getColumnIndex(PoiEntry._ID)));
	        	}
	        }
	        cursor.close();
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
			mShowedPoi = ARDataSource.SelectedPoi;
		}
		
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayUseLogoEnabled(false);
		setTitle(mShowedPoi.getName());
		
		mContainer = (RelativeLayout) findViewById(R.id.pi_details_id);
		
		if (mContainer != null) {
			setImage();
			setName();
			setDistance();
			setDescription();
			setSeeMoreButton();
			setTimetable();
			setPrice();
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
	        
	        case R.id.action_settings:
	        	// Settings action
	        	startActivity(new Intent(this,SettingsActivity.class));
	            return true;*/
			
			case R.id.action_about:
				//aboutAction
				showAbout();
				return true;
            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	private void setImage() {
		
		mProgressBar = (ProgressBar) findViewById(R.id.pi_details_progressBar);
		ImageView img = (ImageView) mContainer.findViewById(R.id.pi_details_image_container);
		
		if (mProgressBar == null || img == null) {
			return;
		}
		
		new LoadPoisImagesTask(mShowedPoi, mProgressBar, img, this).execute();

		img.setContentDescription("Fotografía de " + mShowedPoi.getName());
		
	}
	
	
	private void setName() {
		TextView name = (TextView) mContainer.findViewById(R.id.pi_details_pi_name);
		if (name != null) {
			name.setText(mShowedPoi.getName());
		}else{
			Log.d("DetallesActivity","No hay name container...");
		}
	}
	
	private void setDistance() {
		TextView distance = (TextView) mContainer.findViewById(R.id.pi_details_distance);
		if (distance != null) {			
			distance.setText(mShowedPoi.getTextDistance());
		}else{
			Log.d("DetallesActivity","No hay distance container...");
		}
	}
	
	
	private void setDescription() {
		TextView description = (TextView) mContainer.findViewById(R.id.pi_details_description);
		if (description != null) {
			description.setText(mShowedPoi.getDescription());
		}else{
			Log.d("DetallesActivity","No hay description container...");
		}
	}
	
	private void setSeeMoreButton() {
		mWeb = mShowedPoi.getWebSite();
		if (mWeb != null && !mWeb.isEmpty() && !mWeb.equals("null")) {
			if (!mWeb.startsWith("http://") && !mWeb.startsWith("https://")) {
				mWeb = "http://" + mWeb;
			}
			Button smButton = (Button) mContainer.findViewById(R.id.pi_details_see_more_button);
			smButton.setVisibility(View.VISIBLE);
		}
	}
	
	private void setTimetable() {
		TextView timetable = (TextView) mContainer.findViewById(R.id.pi_details_timetable);
		if (timetable != null) {
			String op = mShowedPoi.getOpenHours();
			String cl = mShowedPoi.getCloseHours();
			String txt;
			
			if (op.equals(cl)) {
				txt = getString(R.string.always_open);
			}else{
				txt = mShowedPoi.isOpen()?
						getString(R.string.pi_open_now):getString(R.string.pi_close_now);
			}
			
			timetable.setText(txt);
		}
	}
	
	
	private void setPrice() {
		TextView price = (TextView) mContainer.findViewById(R.id.pi_details_price);
		if (price != null) {
			float p = mShowedPoi.getPrice();
			String txt;
			
			if (p <= 0) {
				txt = getString(R.string.pi_free);
			}else{
				txt = getResources().getString(R.string.pi_euro_price, new Object[]{p});
			}
			
			price.setText(txt);
		}
	}
	
	
	/**
	 * Método para mostrar una ventana de diálogo con la descripción del funcionamiento de
	 * las búsquedas en la app.
	 */
	private void showAbout() {
		AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);

		ventanaAlerta.setTitle(getString(R.string.title_about));
		ventanaAlerta.setMessage(getString(R.string.message_about));


		//Si se pulsa el botón de cancelar, cerrar la ventana de diálogo
		ventanaAlerta.setNeutralButton(getString(R.string.close), 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		ventanaAlerta.setPositiveButton(getString(R.string.developer_webSite),
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(
						Intent.ACTION_VIEW, 
						Uri.parse(getString(R.string.my_website))));
    	   				
    	   				dialog.cancel();
    		}
		});
       
		ventanaAlerta.setIcon(android.R.drawable.ic_dialog_info);

		//Mostrar la ventana
		ventanaAlerta.show();
	}
	
	public void seeMore(View v) {
		if (mWeb != null) {		
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mWeb)));
		}
	}
}
