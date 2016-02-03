package ft.findandtravel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ft.findandtravel.modello.PlaceSearch;
import ft.findandtravel.modello.PlaceSearchAdapter;


public class SearchActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    private ListView listView;
    private SearchView searchView;
    private GoogleApiClient client;
    private AutocompleteFilter typeFilter;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = (SearchView) findViewById(R.id.searchView);
        listView = (ListView)findViewById(R.id.result_list);
        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();

        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();

            Log.i("client", "inizializzato");
        }

        searchView.setSubmitButtonEnabled(true);

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace(v);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                RequestQueue queue = Volley.newRequestQueue(getBaseContext());

                String newQuery = query.replaceAll(" ","+");

                String url ="https://maps.googleapis.com/maps/api/place/textsearch/json?query="+newQuery+"&type=museum|place_of_worship|stadium|point_of_interest&key=AIzaSyAtcUr-ci8vqcyogU-tebHMcyZmAZ00_i0";

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Ricerca",response);

                        try {
                            JSONObject tmp = new JSONObject(response);
                            JSONArray resultJsonArray = (JSONArray)tmp.get("results");

                            ArrayList<PlaceSearch> list = new ArrayList<>();

                            for(int i = 0;i<resultJsonArray.length();++i){
                                JSONObject aux = (JSONObject)resultJsonArray.get(i);
                                PlaceSearch placeSearch = new PlaceSearch();

                                JSONObject geometry = (JSONObject)aux.get("geometry");
                                geometry = (JSONObject)geometry.get("location");

                                placeSearch.setName(aux.getString("name"));
                                placeSearch.setId(aux.getString("place_id"));
                                placeSearch.setAddress(aux.getString("formatted_address"));
                                placeSearch.setPosition(new LatLng(Float.parseFloat(geometry.getString("lat")), Float.parseFloat(geometry.getString("lng"))));

                                list.add(placeSearch);

                            }

                            PlaceSearchAdapter adapter = new PlaceSearchAdapter(getBaseContext(),android.R.layout.simple_list_item_1,list);
                            listView.setAdapter(adapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    PlaceSearch search = (PlaceSearch)listView.getItemAtPosition(position);

                                    Intent intent = new Intent(SearchActivity.this, PlaceDetail.class);

                                    intent.putExtra("place", search.getPosition());

                                    startActivity(intent);
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

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
              //  textView.setText("Text change");
                return true;
            }
        });


    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void findPlace(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.

        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                searchView.setQuery(place.getName(),true);
                Log.i("Test", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("Test", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}
