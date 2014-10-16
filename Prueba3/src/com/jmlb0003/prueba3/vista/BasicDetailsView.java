package com.jmlb0003.prueba3.vista;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.Marker;


/**
 * Clase utilizada para mostrar los detalles básicos de un PI que ha sido pulsado.
 * En principio se usará tanto en modo cámara como en modo mapa
 * @author Jose
 * @see http://developer.android.com/training/custom-views/create-view.html
 */
public class BasicDetailsView extends ScrollView {
	
	private FrameLayout mContainer;

	public BasicDetailsView(Context context) {
		super(context);
		
		//TODO: Esto no se puede hacer...hay que buscar una forma de crear la vista de detalles básicos...
//		mContainer = (FrameLayout) context.getResources().getLayout(R.layout.activity_main);
		
		if (mContainer != null) {
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			    inflater.inflate(R.layout.layout_detalles_basicos_pi, mContainer);
		}
		
//		initView(context);
	}
	
	public BasicDetailsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
//		TypedArray a = context.getTheme().obtainStyledAttributes(
//				attrs,
//		        R.styleable.BasicDetailsView,
//		        0, 0);
//
//		try {
//			mShowText = a.getBoolean(R.styleable.BasicDetailsView_showText, false);
//			mTextPos = a.getInteger(R.styleable.BasicDetailsView_labelPosition, 0);
//		} finally {
//			a.recycle();
//		}
		
//		initView(context);
	}

	public BasicDetailsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
//		initView(context);
	}
	
	
	public void initView(Marker m) {
		
		if (mContainer != null) {
//			setImage();
			setName(m);
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
	
}
