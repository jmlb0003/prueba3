package com.jmlb0003.prueba3.modelo.data;


import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.jmlb0003.prueba3.modelo.data.PoiContract.LocationEntry;
import com.jmlb0003.prueba3.modelo.data.PoiContract.LocationPoiEntry;
import com.jmlb0003.prueba3.modelo.data.PoiContract.PoiEntry;


/**
 * Esta clase gestiona la base de datos local con los PIs
 * @author Jose
 *
 */
public class PoiDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "poi.db";

    public PoiDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.setLocale(Locale.getDefault());
        }
    }
    

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    	/**
    	 * Crea la tabla de posiciones. La tabla de posiciones contiene un ID, las coordenads de 
    	 * posición (latitud y longitud), el radio de alcance de los PIs descargados desde esa 
    	 * posición y la fecha en que se descargaron.
    	 */
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                LocationEntry.COLUMN_LOCATION_LATITUDE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_LOCATION_LONGITUDE + " REAL NOT NULL, " +
                
                LocationEntry.COLUMN_RADIUS + " INTEGER NOT NULL, " +
                LocationEntry.COLUMN_DATETEXT + " TEXT NOT NULL, " +
                
                //No habrá dos posiciones con las mismas coordenadas (y se controlará en el 
                //código que las posiciones estén a más de 50 metros de distancia entre ellas)
                " UNIQUE (" + LocationEntry.COLUMN_LOCATION_LATITUDE + ", " +
                LocationEntry.COLUMN_LOCATION_LONGITUDE + ") ON CONFLICT ROLLBACK" +
                ");";
        

        
        /**
    	 * Crea la tabla de PIs. Esta tabla contiene los PIs que se han descargado para poder
    	 * reutilizarlos posteriormente sin tener que volver a descargarlos. Cada tupla contiene 
    	 * todos los datos de un PI.
    	 */
        final String SQL_CREATE_POI_TABLE = "CREATE TABLE " + PoiEntry.TABLE_NAME + " (" +

                PoiEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

				PoiEntry.COLUMN_POI_USER_ID + " INTEGER NOT NULL DEFAULT 1, " +
                PoiEntry.COLUMN_POI_NAME + " TEXT NOT NULL, " +
                PoiEntry.COLUMN_POI_COLOR + " INTEGER NOT NULL, " +
                PoiEntry.COLUMN_POI_IMAGE + " TEXT NOT NULL," +
                PoiEntry.COLUMN_POI_DESCRIPTION + " TEXT NOT NULL, " +
                
                PoiEntry.COLUMN_POI_ALTITUDE + " REAL NOT NULL, " +
                PoiEntry.COLUMN_POI_LATITUDE + " REAL NOT NULL, " +
                PoiEntry.COLUMN_POI_LONGITUDE + " REAL NOT NULL, " +
                
                PoiEntry.COLUMN_POI_WEBSITE + " TEXT, " +
                PoiEntry.COLUMN_POI_PRICE + " REAL DEFAULT 0, " +
                
				PoiEntry.COLUMN_POI_OPEN_HOURS + " TEXT DEFAULT '00:00', " +
				PoiEntry.COLUMN_POI_CLOSE_HOURS + " TEXT DEFAULT '00:00', " +
				
				PoiEntry.COLUMN_POI_MAX_AGE + " INTEGER DEFAULT 0, " +
				PoiEntry.COLUMN_POI_MIN_AGE + " INTEGER DEFAULT 0, " +

				//No habrá dos PIs con un mismo nombre y una misma posición
				" UNIQUE (" + PoiEntry.COLUMN_POI_NAME + ", " +
				PoiEntry.COLUMN_POI_LATITUDE + ", " +
				PoiEntry.COLUMN_POI_LONGITUDE + ") ON CONFLICT ROLLBACK" +

	
				" );";	
        
        
        /**
    	 * Crea la tabla location_poi. Esta tabla relaciona las posiciones con los PIs de forma que
    	 * para una posición puede haber varios PIs y un PI se puede haber descargado desde varias
    	 * posiciones.
    	 */
        final String SQL_CREATE_LOCATION_POI_TABLE = "CREATE TABLE " + LocationPoiEntry.TABLE_NAME + " (" +
        		//Columna con el ID de cada entrada de la tabla. 
        		//(Aunque las otras dos columnas formaban el ID (restricción PRIMARY KEY más abajo) )
        		LocationPoiEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

				LocationPoiEntry.COLUMN_ID_LOCATION + " INTEGER NOT NULL, " +
				LocationPoiEntry.COLUMN_ID_POI + " INTEGER NOT NULL, " +
				
			    // Set up the location column as a foreign key to location table.
			    " FOREIGN KEY (" + LocationPoiEntry.COLUMN_ID_LOCATION + ") REFERENCES " +
			    	LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + ") ON DELETE CASCADE, " +
			    
				//Set up the poi column as a foreign key to poi table.
				" FOREIGN KEY (" + LocationPoiEntry.COLUMN_ID_POI + ") REFERENCES " +
					PoiEntry.TABLE_NAME + " (" + PoiEntry._ID + ") ON DELETE CASCADE, " +
				
				//Solo hay una fila para cada par Posicion/PI
				" UNIQUE (" + LocationPoiEntry.COLUMN_ID_LOCATION + ", " +
				LocationPoiEntry.COLUMN_ID_POI + ") ON CONFLICT ROLLBACK" + 
                
                " );";

        
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_POI_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_POI_TABLE);
        
    }// Fin de onCreate de la Base de Datos

    
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PoiEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
