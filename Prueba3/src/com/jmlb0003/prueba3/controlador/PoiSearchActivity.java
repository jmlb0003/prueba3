package com.jmlb0003.prueba3.controlador;



import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.data.PoiContract;

public class PoiSearchActivity extends ActionBarActivity {
	private static final String LOG_TAG = "PoiSearchActivity";
	
	private TextView mTextView;
	private ListView mListView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_search);

        mTextView = (TextView) findViewById(R.id.poi_search_text);
        mListView = (ListView) findViewById(android.R.id.list);

        Intent intent = getIntent();
        Log.d(LOG_TAG,"Busqueda 1");
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
        	Log.d(LOG_TAG,"Busqueda 2");
            // handles a click on a search suggestion; launches activity to show word
            Intent detailIntent = new Intent(this, DetallesPoiActivity.class);
            Bundle b = intent.getExtras();
        	if (b.containsKey(SearchManager.USER_QUERY)) {
        		detailIntent.setData(PoiContract.PoiEntry.
        				buildPoiByNameUri(b.get(SearchManager.USER_QUERY).toString()));
        	}
            Log.d(LOG_TAG,"Busqueda 3");
            startActivity(detailIntent);
            Log.d(LOG_TAG,"Busqueda 4");
            finish();
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
        	Log.d(LOG_TAG,"Busqueda 5");
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(LOG_TAG,"Busqueda 6");
            showResults(query);
            Log.d(LOG_TAG,"Busqueda 7");
            
            //TODO: Si se abre otra ventana de busqueda, hay que cerrar la anterior...
            /***************************************************
             * 
             * Comprobar lo de pinchar en las sugerencias que no funciona.
             * Si se pincha en la sugerencia se va al PI, al darle patras vuelve a los resultados
             * 
             * si se termina de buscar, sale una nueva pantalla de busqueda, la anterior deberia cerrarse
             */
        }
    }
	
	
	
	/**
     * Searches the dictionary and displays results for the given query.
     * @param query The search query
     */
    private void showResults(String query) {
    	Log.d(LOG_TAG,"ShowResults 1 con query:"+query);
    	
        Cursor cursor = getContentResolver().query(
        		PoiContract.PoiEntry.buildPoiByNameUri(query),
        		null,
        		null,
        		null, 
        		null);

        if (cursor == null || !cursor.moveToFirst()) {
        	Log.d(LOG_TAG,"ShowResults 2");
            // There are no results
        	mTextView.setVisibility(View.VISIBLE);
            mTextView.setText(getString(R.string.no_search_results, new Object[] {query}));
        } else {
        	Log.d(LOG_TAG,"ShowResults 3 con getCount:"+cursor.getCount());
            mTextView.setVisibility(View.INVISIBLE);

            // Specify the columns we want to display in the result
            String[] from = new String[] { PoiContract.PoiEntry.COLUMN_POI_NAME };
            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.result_poi_name };
            Log.d(LOG_TAG,"ShowResults 4");
            // Create a simple cursor adapter for the definitions and apply them to the ListView
            @SuppressWarnings("deprecation")
			SimpleCursorAdapter words = new SimpleCursorAdapter(this,
                                          R.layout.poi_result, cursor, from, to);
            mListView.setAdapter(words);
            Log.d(LOG_TAG,"ShowResults 5");
            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Build the Intent used to open WordActivity with a specific word Uri
                    Intent detailIntent = new Intent(getApplicationContext(), DetallesPoiActivity.class);
                    Uri data = Uri.withAppendedPath(PoiContract.PoiEntry.CONTENT_URI,
                                                    String.valueOf(id));
                    detailIntent.setData(data);
                    detailIntent.setAction(Intent.ACTION_SEARCH);
                    startActivity(detailIntent);
                    
                    //No finish...asi si le das a flecha atras se va a la pantalla busqueda. Si se va con la flecha home se va a la pantalla principal
//                    finish();
                }
            });
            Log.d(LOG_TAG,"ShowResults 6");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                onSearchRequested();
                finish();
                return true;
            default:
                return false;
        }
    }
}
