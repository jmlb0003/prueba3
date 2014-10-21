package com.jmlb0003.prueba3.controlador;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.Poi;

public class DetallesPIActivity extends ActionBarActivity {
	private Poi mShowedPoi = null;
	private RelativeLayout mContainer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalles_pi);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		if (ARDataSource.hasSelectededPoi()) {
			mShowedPoi = ARDataSource.SelectedPoi;
			
			setTitle(mShowedPoi.getName());
			
			mContainer = (RelativeLayout) findViewById(R.id.pi_details_id);
			
			if (mContainer != null) {
				setImage();
				setName();
				setDistance();
				setDescription();
				
			}
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	            return super.onOptionsItemSelected(item);*/
	        case R.id.action_settings:
	        	// Settings action
	        	startActivity(new Intent(this,SettingsActivity.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	
	
	private void setImage() {
		ImageView img = (ImageView) mContainer.findViewById(R.id.pi_image_container2);
		if (img != null) {
			img.setImageBitmap(mShowedPoi.getImage());
		}else{
			Log.d("DetallesActivity","No hay IMG container...");
		}
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
