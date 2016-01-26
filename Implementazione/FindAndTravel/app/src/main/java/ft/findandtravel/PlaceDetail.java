package ft.findandtravel;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;

import ft.findandtravel.servizi.PlaceRequest;

public class PlaceDetail extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient client;
    private LatLng position;
    private Place luogo;
    private RequestQueue queue;
    private TextView textView;
    private ImageView placeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        Bundle bundle = getIntent().getExtras();

        queue = Volley.newRequestQueue(this);
        position = (LatLng)bundle.get("place");
        textView =  (TextView) findViewById(R.id.dettagli);
        placeImage = (ImageView) findViewById(R.id.place_image);

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


    }

    @Override
    public void onConnected(Bundle bundle) {
        // getPlace(position);
        Log.i("onConnected","getPlace");
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


    public void getPlace(LatLng loc){

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

                            String id = (String)o.get("place_id");

                            Places.GeoDataApi.getPlaceById(client, id).setResultCallback(new ResultCallback<PlaceBuffer>() {
                                @Override
                                public void onResult(PlaceBuffer places) {
                                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                        final Place myPlace = places.get(0);

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
                                    }

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
        queue.start();
    }

}
