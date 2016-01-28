package ft.findandtravel;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
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
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import com.google.android.gms.location.places.internal.PlacesParams;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ft.findandtravel.modello.Reviewer;
import ft.findandtravel.modello.ReviewerAdapter;


public class PlaceDetail extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient client;
    private LatLng position;
    private Place luogo;
    private RequestQueue queue;
    private TextView textView;
    private ListView review;
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
        review = (ListView) findViewById(R.id.body);

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

                                        photoMetadataBuffer.release();
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
                                /*
                                //Log.d("reviews",response);
                                // Log.i("reviews",responseWrapper.toString());
                                JSONObject g = (JSONObject) responseWrapper.get("reviews");
                                JSONArray reviewWrapper = responseWrapper.getJSONArray("reviews");

                                if(reviewWrapper.length() > 0){

                                    review.setText(reviewWrapper.toString());

                                }else{
                                    review.setText("Non ci sono commenti");
                                }

                                Log.d("reviews",g.toString());
*/


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
