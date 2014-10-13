package com.jmlb0003.prueba3.vista;

import com.jmlb0003.prueba3.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;


/**
 * Clase utilizada para mostrar los detalles básicos de un PI que ha sido pulsado.
 * En principio se usará tanto en modo cámara como en modo mapa
 * @author Jose
 * @see http://developer.android.com/training/custom-views/create-view.html
 */
public class BasicDetailsView extends View {

//	public BasicDetailView(Context context) {
//		super(context);
//		initPreference(context, attrs);
//	}
	
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
	}

	public BasicDetailsView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
}
