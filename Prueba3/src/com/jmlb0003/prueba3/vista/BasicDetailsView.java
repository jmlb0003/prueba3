package com.jmlb0003.prueba3.vista;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.Poi;
import com.jmlb0003.prueba3.modelo.sync.LoadPoisImagesTask;


/**
 * Clase utilizada para mostrar los detalles básicos de un PI que ha sido pulsado.
 * @author Jose
 * @see http://developer.android.com/training/custom-views/create-view.html
 */
public class BasicDetailsView extends ScrollView {
	private static final String LOG_TAG = "BasicDetails";
	private RelativeLayout mContainer = null;
	private ProgressBar mProgressBar;
	private Context mContext;

	public BasicDetailsView(Context context) {
		super(context);
		
		mContext = context;
		LayoutInflater inflater = 
				(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		inflater.inflate(R.layout.layout_detalles_basicos_pi, this, true);		
		    
		mContainer = (RelativeLayout) getChildAt(0);
	}
	
	public BasicDetailsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
		LayoutInflater inflater = 
				(LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.layout_detalles_basicos_pi, this, true);
		    
		mContainer = (RelativeLayout) getChildAt(0);
		
	}

	
	
	public void initView(Poi m) {
		
		if (mContainer != null) {
			setImage(m);
			setName(m);
			setDistance(m);
			setDescription(m);
			
		}else {
			Log.d(LOG_TAG,"No hay container...");
		}
		

	}
	
	
	private void setImage(Poi p) {
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		ImageView img = (ImageView) mContainer.findViewById(R.id.pi_image_container);
		
		if (mProgressBar == null || img == null) {
			return;
		}


		new LoadPoisImagesTask(p.getImage(), mProgressBar, img, mContext).execute();

		img.setContentDescription("Fotografía de "+p.getName());
		
//		ImageView img = (ImageView) mContainer.findViewById(R.id.pi_image_container);
//		Bitmap bm = BitmapFactory.decodeResource(mContext.getResources(), p.getImage());
//		if (img != null && bm != null) {
//			img.setImageBitmap(bm);
//			img.setContentDescription("Fotografía de "+p.getName());			
//		}else{
//			Log.d("BASICDETAILS","No hay IMG container... O no hay imagen para ponerle...");
//			
//			if (bm == null){
//				Log.d("BASICDETAILS","No hay imagen para ponerle...");
//			}
//		}
	}
	
	
	private void setName(Poi m) {
		TextView name = (TextView) mContainer.findViewById(R.id.pi_name);
		if (name != null) {
			name.setText(m.getName());
		}else{
			Log.d(LOG_TAG,"No hay name container...");
		}
	}
	
	private void setDistance(Poi m) {
		TextView distance = (TextView) mContainer.findViewById(R.id.pi_distance);
		if (distance != null) {			
			distance.setText(m.getTextDistance());
		}else{
			Log.d(LOG_TAG,"No hay distance container...");
		}
	}
	
	
	private void setDescription(Poi m) {
		TextView description = (TextView) mContainer.findViewById(R.id.pi_description);
		if (description != null) {
			description.setText(m.getDescription());
		}else{
			Log.d(LOG_TAG,"No hay description container...");
		}
	}
	
}
