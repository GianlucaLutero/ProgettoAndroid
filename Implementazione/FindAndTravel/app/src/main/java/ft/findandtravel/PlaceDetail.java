package ft.findandtravel;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ft.findandtravel.modello.DataBaseModel;
import ft.findandtravel.modello.Reviewer;
import ft.findandtravel.modello.ReviewerAdapter;
import ft.findandtravel.servizi.DataBaseHelper;


public class PlaceDetail extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient client;
    private LatLng position;
    private String name;
    private String type;
    private RequestQueue queue;
    private TextView textView;
    private ListView review;
    private ImageView placeImage;
    private FloatingActionButton savePlace;
    private FloatingActionButton openMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        Bundle bundle = getIntent().getExtras();

        queue = Volley.newRequestQueue(this);
        position = (LatLng)bundle.get("place");
        textView =  (TextView) findViewById(R.id.dettagli);
        placeImage = (ImageView) findViewById(R.id.place_image);
        review = (ListView) findViewById(R.id.body);
        savePlace = (FloatingActionButton)findViewById(R.id.fab_place);
        openMap = (FloatingActionButton)findViewById(R.id.open_map);

        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();

            Log.i("client", "inizializzato");
        }

       getPlace(position);
       getPlaceReview(position);

       openMap.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("geo:"+position.latitude+","+position.longitude+"?z=20"));
               startActivity(intent);
           }
       });


    }

    @Override
    public void onConnected(Bundle bundle) {
        // getPlace(position);
        Log.i("onConnected", "getPlace");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        client.connect();
        super.onStart();

    }

    @Override
    protected void onStop() {
        client.disconnect();
        super.onStop();
    }


    public void getPlace(final LatLng loc){

        String url = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location="
                +loc.latitude+","+loc.longitude
                +"&radius=1&types=all&key=AIzaSyAtcUr-ci8vqcyogU-tebHMcyZmAZ00_i0";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       Log.i("response",response);

                        try {
                            JSONObject aux = new JSONObject(response);
                            JSONArray arr = aux.getJSONArray("results");
                            JSONObject o = arr.getJSONObject(0);

                            final String id = (String)o.get("place_id");

                            Log.i("response",response);
                            Places.GeoDataApi.getPlaceById(client, id).setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(PlaceBuffer places) {
                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                        final Place myPlace = places.get(0);
                                        name = (String)myPlace.getName();
                                        List place_type = myPlace.getPlaceTypes();

                                        boolean found = false;

                                        for(int k = 0;k<place_type.size();++k) {
                                            switch ((int) place_type.get(k)) {
                                                case Place.TYPE_MUSEUM:
                                                    type = "museum";
                                                    Log.i("Museo",name);
                                                    found = true;
                                                    break;
                                                case Place.TYPE_STADIUM:
                                                    type = "stadium";
                                                    Log.i("Stadio",name);
                                                    found = true;
                                                    break;
                                                case Place.TYPE_PLACE_OF_WORSHIP:
                                                    type = "place_of_worship";
                                                    Log.i("Chiesa",name);
                                                    found = true;
                                                    break;
                                                default:
                                                    type = "no_type";
                                            }

                                            if (found){
                                                break;
                                            }
                                        }

                                        textView.setText(myPlace.getName()+"\n"+myPlace.getAddress());

                                    } else {
                                        Log.i("Map", "place not found");
                                    }

                                    places.release();
                                }
                            });


                            Places.GeoDataApi.getPlacePhotos(client, id).setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {
                                @Override
                                public void onResult(final PlacePhotoMetadataResult placePhotoMetadataResult) {

                                    if (placePhotoMetadataResult.getStatus().isSuccess()) {

                                        PlacePhotoMetadataBuffer photoMetadataBuffer = placePhotoMetadataResult.getPhotoMetadata();

                                        if (photoMetadataBuffer.getCount() > 0) {
                                            PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                                            photo.getPhoto(client).setResultCallback(new ResultCallback<PlacePhotoResult>() {
                                                @Override
                                                public void onResult(PlacePhotoResult placePhotoResult) {
                                                    placeImage.setImageBitmap(placePhotoResult.getBitmap());
                                                }
                                            });

                                        } else {
                                            placeImage.setImageResource(R.drawable.dummy);
                                        }

                                        photoMetadataBuffer.release();
                                    }



                                }
                            });


                            savePlace.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    DataBaseHelper dataBaseHelper = new DataBaseHelper(getBaseContext());
                                    SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

                                    ContentValues values = new ContentValues();
                                    values.put(DataBaseModel.Preference.COLUMN_NAME_PLACE_NAME, name);
                                    values.put(DataBaseModel.Preference.COLUMN_NAME_PLACE_POSITION,loc.latitude+","+loc.longitude);
                                    values.put(DataBaseModel.Preference.COLUMN_NAME_PLACE_TYPE, type);

                                    Log.i("Nome",name);
                                    Log.i("Position",loc.latitude+","+loc.longitude);
                                    Log.i("Type",type);

                                    db.insert(
                                            DataBaseModel.Preference.TABLE_NAME,
                                            null,
                                            values
                                    );

                                    Snackbar.make(v, "Luogo Salvato", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        queue.add(stringRequest);
    }


    public void getPlaceReview(LatLng loc){

        String url = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location="
                +loc.latitude+","+loc.longitude
                +"&radius=1&types=all&key=AIzaSyAtcUr-ci8vqcyogU-tebHMcyZmAZ00_i0";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    JSONObject aux = new JSONObject(response);
                    JSONArray arr = aux.getJSONArray("results");
                    JSONObject o = arr.getJSONObject(0);

                    String id = (String)o.get("place_id");

                    String detail = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+id+"&key=AIzaSyAtcUr-ci8vqcyogU-tebHMcyZmAZ00_i0";

                    StringRequest commenti = new StringRequest(Request.Method.GET, detail, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {


                                int i = response.indexOf("reviews");
                                if(i > -1) {
                                    Log.i("prova", response.substring(i));

                                    try {
                                        JSONObject responseWrapper = new JSONObject("{\"" + response.substring(i));
                                        JSONArray reviewsWrapper = responseWrapper.getJSONArray("reviews");

                                        ArrayList<Reviewer> lista = new ArrayList<>();

                                        Log.i("Lista", reviewsWrapper.toString());
                                        Log.i("Lunghezza",String.valueOf(reviewsWrapper.length()));
                                        for(int j = 0;j<reviewsWrapper.length();j++) {

                                            Log.i("reviewer", String.valueOf(j));
                                            JSONObject tmp = (JSONObject) reviewsWrapper.get(j);
                                            Reviewer elemento = new Reviewer((String)tmp.get("author_name"),(int)tmp.get("rating"),(String)tmp.get("text"));
                                            lista.add(elemento);
                                        }

                                        ReviewerAdapter adapter = new ReviewerAdapter(getBaseContext(),android.R.layout.simple_list_item_1,lista);

                                        review.setAdapter(adapter);
                                    //    review.setText((String)tmp.get("author_name")+"\n"+(String)tmp.get("text"));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }else{
                             //       review.setText("Nessuna recensione");
                                }


                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    queue.add(commenti);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }
}
