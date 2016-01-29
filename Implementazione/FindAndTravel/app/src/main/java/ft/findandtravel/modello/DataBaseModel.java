package ft.findandtravel.modello;

import android.provider.BaseColumns;

/**
 * Modello del database
 * */
public class DataBaseModel {

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Preference.TABLE_NAME + " ("+
                    Preference.COLUMN_NAME_PLACE_NAME + TEXT_TYPE +" PRIMARY KEY,"+
                    Preference.COLUMN_NAME_PLACE_POSITION + TEXT_TYPE+
                    ")";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Preference.TABLE_NAME;

    public DataBaseModel(){}

    public static abstract class Preference implements BaseColumns{
        public static final String TABLE_NAME = "preferiti";
        public static final String COLUMN_NAME_PLACE_NAME = "place_name";
        public static final String COLUMN_NAME_PLACE_POSITION = "place_position";

    }
}
