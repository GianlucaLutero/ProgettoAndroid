package ft.findandtravel.servizi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ft.findandtravel.modello.DataBaseModel;


public class DataBaseHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "SavedPlace.db";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
         Log.i("Database","creating");
         db.execSQL(DataBaseModel.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         Log.i("Database","updating");
         db.execSQL(DataBaseModel.SQL_DELETE_ENTRIES);
         onCreate(db);
    }
}
