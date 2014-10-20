package com.jmlb0003.prueba3.vista;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.Marker;


/**
 * Clase utilizada para mostrar los detalles básicos de un PI que ha sido pulsado.
 * @author Jose
 * @see http://developer.android.com/training/custom-views/create-view.html
 */
public class BasicDetailsView extends ScrollView {
	
	private RelativeLayout mContainer = null;

	public BasicDetailsView(Context context) {
		super(context);
		
		LayoutInflater inflater = 
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		inflater.inflate(R.layout.layout_detalles_basicos_pi, this, true);		
		    
		mContainer = (RelativeLayout) getChildAt(0);
	}
	
	public BasicDetailsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		
		LayoutInflater inflater = 
				(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(R.layout.layout_detalles_basicos_pi, this, true);
		    
		mContainer = (RelativeLayout) getChildAt(0);
		
	}

	
	
	public void initView(Marker m) {
		
		if (mContainer != null) {
			setImage(m);
			setName(m);
			setDistance(m);
			setDescription(m);
			
		}else {
			Log.d("BASICDETAILS","No hay container...");
		}
		

	}
	
	
	private void setImage(Marker m) {
		ImageView img = (ImageView) mContainer.findViewById(R.id.pi_image_container);
		if (img != null) {
			img.setImageBitmap(m.getImage());
			setContentDescription("Fotografía de "+m.getName());
		}else{
			Log.d("BASICDETAILS","No hay IMG container...");
		}
	}
	
	
	private void setName(Marker m) {
		TextView name = (TextView) mContainer.findViewById(R.id.pi_name);
		if (name != null) {
			name.setText(m.getName());
		}else{
			Log.d("BASICDETAILS","No hay name container...");
		}
	}
	
	private void setDistance(Marker m) {
		TextView distance = (TextView) mContainer.findViewById(R.id.pi_distance);
		if (distance != null) {			
			distance.setText(m.getTextDistance());
		}else{
			Log.d("BASICDETAILS","No hay distance container...");
		}
	}
	
	
	private void setDescription(Marker m) {
		TextView description = (TextView) mContainer.findViewById(R.id.pi_description);
		if (description != null) {
			description.setText(m.getDescription());
		}else{
			Log.d("BASICDETAILS","No hay description container...");
		}
	}
	
}
