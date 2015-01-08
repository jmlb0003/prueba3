package com.jmlb0003.prueba3.utilidades;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jmlb0003.prueba3.R;


/**
 * Clase para implementar el slider que permitirá establecer el radio de búsqueda del radar 
 * en kilómetros
 * @author Jose
 *
 */
public class SeekBarPreference extends Preference implements OnSeekBarChangeListener {
	private static final String ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android";
	
	private static int mDefaultValue = 15;
	private static int mMax    = 150;
	final float scale = getContext().getResources().getDisplayMetrics().density;
	private TextView mIndicator;
	private int mProgress;
	private SeekBar mSeekBar;
	private boolean mTrackingTouch;




	/**
	 * Constructor de la preferencia formada por un SeekBar acompañado de su valor en km
	 * @param context
	 */
	public SeekBarPreference(Context context) {
		this(context,null);
	}
	
	
	/**
	 * Constructor de la preferencia formada por un SeekBar acompañado de su valor en km
	 * @param context
	 * @param attrs
	 */
	public SeekBarPreference(Context context, AttributeSet attrs) {		
		super(context, attrs);
		initPreference(context, attrs);
	}
	
	
	/**
	 * Constructor de la preferencia formada por un SeekBar acompañado de su valor en km
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPreference(context, attrs);
	}
	

	/**
	 * Método para crear la vista de la preferencia
	 * @param parent
	 * @return
	 */
	@Override
	protected View onCreateView(ViewGroup parent) {
	    LinearLayout preferenciaLayout = new LinearLayout(getContext());
    	preferenciaLayout.setOrientation(LinearLayout.VERTICAL);
    	preferenciaLayout.setPadding(Math.round(16/scale), Math.round(6/scale), 
											Math.round(8/scale), Math.round(6/scale));

	    	
	    LinearLayout miSeekBarLayout = new LinearLayout(getContext());	    

	    
	    FrameLayout.LayoutParams sbarParams = new FrameLayout.LayoutParams(
	    											Math.round(scale * 200),
										    		FrameLayout.LayoutParams.WRAP_CONTENT);
	    

	    TextView titleText = new TextView(getContext());
	    titleText.setId(0);
	    titleText.setText(getTitle());
	    titleText.setTextSize(18);
	    
	    TextView summaryText = new TextView(getContext());
	    summaryText.setText(getSummary());
	    summaryText.setTextAppearance(getContext(), android.R.style.TextAppearance_Small);


	    mSeekBar.setId(R.id.mySeekBarPreference);
	    mSeekBar.setEnabled(true);
	    mSeekBar.setLayoutParams(sbarParams);
	    
	    
	    mIndicator = new TextView(getContext());
	    mIndicator.setTextSize(12);
	    mIndicator.setTypeface(Typeface.MONOSPACE, Typeface.ITALIC);
	    mIndicator.setPadding(Math.round(20*scale), Math.round(3*scale), 0, 0);
	    mIndicator.setText("" + mSeekBar.getProgress()+" km");
	    mSeekBar.setProgress(mProgress);
	    	    	    
	    miSeekBarLayout.addView(mSeekBar);
	    miSeekBarLayout.addView(mIndicator);
	    preferenciaLayout.addView(titleText);
	    preferenciaLayout.addView(summaryText);
	    preferenciaLayout.addView(miSeekBarLayout);



	    return preferenciaLayout;
	}// Fin de onCreateView
	
	
	
    

	@Override
	protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
	    setProgress(restoreValue ? getPersistedInt(mProgress) : (Integer) defaultValue);
	}
	
	
	
	@Override
	protected Object onGetDefaultValue(TypedArray ta, int index) {
	    int dValue = ta.getInt(index, mDefaultValue);
	    
	    if(dValue > mMax) {
	    	dValue = mMax;
	    }else{
	    	if (dValue < 0) {
	    		dValue = 0;
	    	}
	    }
	    
	    return dValue;
	}
	
    
    
    
    
    /**
     * Cuando se activa o desactiva la preferencia se aplica también a la seekbar
     */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mSeekBar.setEnabled(enabled);
	}
	
	
	@Override
	public void onDependencyChanged(Preference dependency, boolean disableDependent) {
		super.onDependencyChanged(dependency, disableDependent);
		
		//Disable movement of seek bar when dependency is false
		if (mSeekBar != null) {
			mSeekBar.setEnabled(!disableDependent);
		}
	}


	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		
		if (!mTrackingTouch) {			
			if (progress != mProgress) {
				if (!callChangeListener(progress)) {
	            	seekBar.setProgress(mProgress);
	            	return;
	            }
				
				setProgress(progress);
			}
		}
		mIndicator.setText("" + progress + " km");
	}
	
	
    
	
	@Override
	public void onStartTrackingTouch(SeekBar bar) {
		mTrackingTouch = true;
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mTrackingTouch = false;

		if (seekBar.getProgress() != mProgress) {
			setProgress(seekBar.getProgress());
        }
	}
	
	
	
	@Override
    protected Parcelable onSaveInstanceState() {
        /*
         * Suppose a client uses this preference type without persisting. We
         * must save the instance state so it is able to, for example, survive
         * orientation changes.
         */

        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }

        // Save the instance state
        final SavedState myState = new SavedState(superState);
        myState.progress = mProgress;
        myState.max = mMax;
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }

        // Restore the instance state
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        mProgress = myState.progress;
        mMax = myState.max;
        notifyChanged();
    }

    
    
    
    
    /**
     * SavedState, a subclass of {@link BaseSavedState}, will store the state
     * of MyPreference, a subclass of Preference.
     * <p>
     * It is important to always call through to super methods.
     */
    private static class SavedState extends BaseSavedState {
        int progress;
        int max;

        public SavedState(Parcel source) {
            super(source);

            // Restore the click counter
            progress = source.readInt();
            max = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);

            // Save the click counter
            dest.writeInt(progress);
            dest.writeInt(max);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
    
    
    
    
    private void initPreference(Context context, AttributeSet attrs) {
		setValuesFromXml(attrs);
		mSeekBar = new SeekBar(context, attrs);
		mSeekBar.setMax(mMax);
		mSeekBar.setOnSeekBarChangeListener(this);
	}
	
	private void setValuesFromXml(AttributeSet attrs) {
		mMax = attrs.getAttributeIntValue(ANDROID_NAMESPACE, "max", mMax);
		mDefaultValue = attrs.getAttributeIntValue(ANDROID_NAMESPACE, "defaultValue", mDefaultValue);
	}
	
    
    
    /**
     * Método para guardar el progreso en las preferencias y actualizar la variable interna 
     * donde lo almacenamos
     * @param progress
     */
    private void setProgress(int progress) {
        if (progress > mMax) {
            progress = mMax;
        }
        if (progress < 0) {
            progress = 0;
        }

        if (progress != mProgress) {
            persistInt(progress);
            mProgress = progress;
        }
    }
    
}
