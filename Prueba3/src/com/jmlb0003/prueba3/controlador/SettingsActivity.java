package com.jmlb0003.prueba3.controlador;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.jmlb0003.prueba3.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
		
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d("SettingsActivity","Va a leer el xml de los ajustes");
		addPreferencesFromResource(R.xml.activity_general_prefs);
	}

}
