package com.jmlb0003.prueba3.controlador;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.jmlb0003.prueba3.R;

public class FilterActivity extends PreferenceActivity {
	
	
	@SuppressWarnings("deprecation")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        addPreferencesFromResource(R.layout.activity_filter_prefs);
    }

}
