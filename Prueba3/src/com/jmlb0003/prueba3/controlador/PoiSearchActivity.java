package com.jmlb0003.prueba3.controlador;



import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.jmlb0003.prueba3.R;
import com.jmlb0003.prueba3.modelo.data.PoiContract;

public class PoiSearchActivity extends ActionBarActivity {
	
	private TextView mTextView;
	private ListView mListView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_search);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTextView = (TextView) findViewById(R.id.poi_search_text);
        mListView = (ListView) findViewById(android.R.id.list);

        Intent intent = getIntent();
    	if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handles a search query
            String query = intent.getStringExtra(SearchManager.QUERY);
            showResults(query);
        }
    }

	
	/**
     * Searches the dictionary and displays results for the given query.
     * @param query The search query
     */
    private void showResults(String query) {
    	Cursor cursor = getContentResolver().query(
        		PoiContract.PoiEntry.buildPoiByNameUri(query),
        		null,
        		null,
        		null, 
        		null);

        if (cursor == null || !cursor.moveToFirst()) {
            // There are no results
            mTextView.setText(getString(R.string.no_search_results, new Object[] {query}));
        } else {
            int count = cursor.getCount();
            String countString = getResources().getQuantityString(R.plurals.search_results,
                                    count, new Object[] {count, query});
            mTextView.setText(countString);

            // Specify the columns we want to display in the result
            String[] from = new String[] { PoiContract.PoiEntry.COLUMN_POI_NAME };
            // Specify the corresponding layout elements where we want the columns to go
            int[] to = new int[] { R.id.result_poi_name };
            
            // Create a simple cursor adapter for the definitions and apply them to the ListView
			SimpleCursorAdapter words = new SimpleCursorAdapter(this,
                                          R.layout.poi_result, cursor, from, to,
                                          SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
            mListView.setAdapter(words);

            // Define the on-click listener for the list items
            mListView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // Build the Intent used to open WordActivity with a specific word Uri
                    Intent detailIntent = new Intent(getApplicationContext(), PoiDetailsActivity.class);
                    Uri data = Uri.withAppendedPath(PoiContract.PoiEntry.CONTENT_URI,
                                                    String.valueOf(id));
                    detailIntent.setData(data);
                    detailIntent.setAction(Intent.ACTION_SEARCH);
                    startActivity(detailIntent);
                }
            });
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
                //Search action
                return true;
            case R.id.action_description:
            	showDescription();
            	return true;
            case R.id.action_about:
            	showAbout();
            	return true;
            
            default:
	            return false;
        }
    }
    
    
    
    /**
     * Método para mostrar una ventana de diálogo con la descripción del funcionamiento de
     * las búsquedas en la app.
     */
    private void showDescription() {
 	   AlertDialog.Builder ventanaAlerta = new AlertDialog.Builder(this);

        ventanaAlerta.setTitle(getString(R.string.search_description_dialog_title));
        ventanaAlerta.setMessage(getString(R.string.search_description));


        //Si se pulsa el botón de cancelar, cerrar la ventana de diálogo
        ventanaAlerta.setNeutralButton(getString(R.string.close), 
     		   											new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
         	   dialog.cancel();
            }
        });
        
        ventanaAlerta.setIcon(android.R.drawable.ic_dialog_alert);

        //Mostrar la ventana
        ventanaAlerta.show();
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
     	   						Intent.ACTION_VIEW, Uri.parse(getString(R.string.my_website))));
     	   				
     	   				dialog.cancel();
     	   			}
        });
        
        ventanaAlerta.setIcon(android.R.drawable.ic_dialog_info);

        //Mostrar la ventana
        ventanaAlerta.show();
    }
    
}
