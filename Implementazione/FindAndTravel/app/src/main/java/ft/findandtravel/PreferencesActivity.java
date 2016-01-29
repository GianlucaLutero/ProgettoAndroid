package ft.findandtravel;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import ft.findandtravel.modello.DataBaseModel;
import ft.findandtravel.modello.PlacePreference;
import ft.findandtravel.modello.PlacePreferenceAdapter;
import ft.findandtravel.servizi.DataBaseHelper;

public class PreferencesActivity extends AppCompatActivity {

    private ListView l;
    private ArrayList<PlacePreference> list;
    private PlacePreferenceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        l = (ListView) findViewById(R.id.listView);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        DataBaseHelper dataBaseHelper = new DataBaseHelper(getBaseContext());
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        String projection[]={
                DataBaseModel.Preference.COLUMN_NAME_PLACE_NAME,
                DataBaseModel.Preference.COLUMN_NAME_PLACE_POSITION
        };

        Cursor c = db.query(
                DataBaseModel.Preference.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                DataBaseModel.Preference.COLUMN_NAME_PLACE_NAME+" DESC"
        );

        list = new ArrayList<>();

        c.moveToFirst();

        while(c.moveToNext()){
            PlacePreference tmp = new PlacePreference();

            String name = c.getString(
                    c.getColumnIndexOrThrow(DataBaseModel.Preference.COLUMN_NAME_PLACE_NAME)
            );

            String position = c.getString(
                    c.getColumnIndexOrThrow(DataBaseModel.Preference.COLUMN_NAME_PLACE_POSITION)
            );

            String latLng[] = position.split(",");


            LatLng pos = new LatLng(Float.parseFloat(latLng[0]),Float.parseFloat(latLng[1]));

            tmp.setName(name);
            tmp.setLocation(pos);

            list.add(tmp);
        }

        adapter = new PlacePreferenceAdapter(getBaseContext(),android.R.layout.simple_list_item_1,list);
        l.setAdapter(adapter);

        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlacePreference aux = (PlacePreference) l.getItemAtPosition(position);

                Intent intent = new Intent(PreferencesActivity.this, PlaceDetail.class);

                intent.putExtra("place", aux.getLocation());

                startActivity(intent);
            }
        });

        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Snackbar.make(view, "Cancellato", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                buildAlertMessageCancel(position);
                return true;
            }
        });
    }

    private void buildAlertMessageCancel(final int position) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vuoi cancellare questo luogo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        DataBaseHelper dataBaseHelper2 = new DataBaseHelper(getBaseContext());
                        SQLiteDatabase db2 = dataBaseHelper2.getWritableDatabase();

                        String selection = DataBaseModel.Preference.COLUMN_NAME_PLACE_NAME+"= ?";
                        String[] selectionArgs ={((PlacePreference)l.getItemAtPosition(position)).getName()};

                        //Log.i("Cancellato", ((PlacePreference) l.getItemAtPosition(position)).getName());
                        db2.delete(DataBaseModel.Preference.TABLE_NAME, selection, selectionArgs);
                        list.remove(position);
                        adapter.notifyDataSetChanged();
                        // db2.delete(DataBaseModel.Preference.TABLE_NAME,selection,null);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
