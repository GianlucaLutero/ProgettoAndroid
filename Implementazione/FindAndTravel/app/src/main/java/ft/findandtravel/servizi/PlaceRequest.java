package ft.findandtravel.servizi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import ft.findandtravel.R;

/**
 * Created by gianluca on 25/01/16.
 */
public class PlaceRequest{

    private static Place luogo;

    public static void getMuseum(final Location loc,final GoogleApiClient cli,final GoogleMap mMap,final Context context){
        if(loc != null){

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location="
                    +loc.getLatitude()+","+loc.getLongitude()
                    +"&radius=5000&types=museum&key=AIzaSyAtcUr-ci8vqcyogU-tebHMcyZmAZ00_i0";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response is: ", response);
                            try {
                                JSONObject aux = new JSONObject(response);
                                JSONArray arr = aux.getJSONArray("results");
                                Log.i("JSONArray",arr.toString());

                                for(int i = 0;i<arr.length();++i){

                                    JSONObject o = arr.getJSONObject(i);
                                    JSONObject a = (JSONObject)o.get("geometry");
                                    a = (JSONObject)a.get("location");
                                    final double lat = (double)a.get("lat");
                                    final double lng = (double)a.get("lng");
                                    String id = (String)o.get("place_id");

                                    Places.GeoDataApi.getPlaceById(cli, id).setResultCallback(new ResultCallback<PlaceBuffer>() {
                                        @Override
                                        public void onResult(PlaceBuffer places) {
                                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                                final Place myPlace = places.get(0);

                                                mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(lat, lng))
                                                        .title((String) myPlace.getName())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_musei)));

                                            } else {
                                                Log.i("Map", "place not found");
                                            }

                                            places.release();
                                        }
                                    });

                                    Log.i(id, lat + "," + lng);

                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mTextView.setText("That didn't work!");
                }
            });

            queue.add(stringRequest);


            LatLng position = new LatLng(loc.getLatitude(),loc.getLongitude());
           // mMap.addMarker(new MarkerOptions().position(position).title("Tua posizione").draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(position, 12.0f)));


        }
    }

    public static void getMonument(final Location loc,final GoogleApiClient cli,final GoogleMap mMap,final Context context){
        if(loc != null){

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location="
                    +loc.getLatitude()+","+loc.getLongitude()
                    +"&radius=5000&types=|&key=AIzaSyAtcUr-ci8vqcyogU-tebHMcyZmAZ00_i0";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response is: ", response);
                            try {
                                JSONObject aux = new JSONObject(response);
                                JSONArray arr = aux.getJSONArray("results");
                                Log.i("JSONArray",arr.toString());

                                for(int i = 0;i<arr.length();++i){

                                    JSONObject o = arr.getJSONObject(i);
                                    JSONObject a = (JSONObject)o.get("geometry");
                                    a = (JSONObject)a.get("location");
                                    final double lat = (double)a.get("lat");
                                    final double lng = (double)a.get("lng");
                                    String id = (String)o.get("place_id");

                                    Places.GeoDataApi.getPlaceById(cli, id).setResultCallback(new ResultCallback<PlaceBuffer>() {
                                        @Override
                                        public void onResult(PlaceBuffer places) {
                                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                                final Place myPlace = places.get(0);


                                                List<Integer> type = myPlace.getPlaceTypes();

                                                for (Integer i:type) {
                                                    if(i.equals(Place.TYPE_POINT_OF_INTEREST)){

                                                        mMap.addMarker(new MarkerOptions()
                                                                .position(new LatLng(lat, lng))
                                                                .title((String) myPlace.getName())
                                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

                                                    }
                                                }


                                            } else {
                                                Log.i("Map", "place not found");
                                            }

                                            places.release();
                                        }
                                    });

                                    Log.i(id, lat + "," + lng);

                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mTextView.setText("That didn't work!");
                }
            });

            queue.add(stringRequest);


            LatLng position = new LatLng(loc.getLatitude(),loc.getLongitude());
        //    mMap.addMarker(new MarkerOptions().position(position).title("Tua posizione").draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(position, 12.0f)));

        }
    }

    public static void getPlaceOfWorship(final Location loc,final GoogleApiClient cli,final GoogleMap mMap,final Context context){
        if(loc != null){

            RequestQueue queue = Volley.newRequestQueue(context);

            String url = "https://maps.googleapis.com/maps/api/place/radarsearch/json?location="
                    +loc.getLatitude()+","+loc.getLongitude()
                    +"&radius=5000&types=place_of_worship&key=AIzaSyAtcUr-ci8vqcyogU-tebHMcyZmAZ00_i0";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.i("Response is: ", response);
                            try {
                                JSONObject aux = new JSONObject(response);
                                JSONArray arr = aux.getJSONArray("results");
                                Log.i("JSONArray",arr.toString());

                                for(int i = 0;i<arr.length();++i){

                                    JSONObject o = arr.getJSONObject(i);
                                    JSONObject a = (JSONObject)o.get("geometry");
                                    a = (JSONObject)a.get("location");
                                    final double lat = (double)a.get("lat");
                                    final double lng = (double)a.get("lng");
                                    String id = (String)o.get("place_id");

                                    Places.GeoDataApi.getPlaceById(cli, id).setResultCallback(new ResultCallback<PlaceBuffer>() {
                                        @Override
                                        public void onResult(PlaceBuffer places) {
                                            if (places.getStatus().isSuccess() && places.getCount() > 0) {
                                                final Place myPlace = places.get(0);

                                                mMap.addMarker(new MarkerOptions()
                                                        .position(new LatLng(lat, lng))
                                                        .title((String) myPlace.getName())
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_luoghi_culto)));

                                            } else {
                                                Log.i("Map", "place not found");
                                            }

                                            places.release();
                                        }
                                    });

                                    Log.i(id, lat + "," + lng);

                                }
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //mTextView.setText("That didn't work!");
                }
            });

            queue.add(stringRequest);


           LatLng position = new LatLng(loc.getLatitude(),loc.getLongitude());
           // mMap.addMarker(new MarkerOptions().position(position).title("Tua posizione").draggable(true));
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(position, 12.0f)));


        }
    }


}
