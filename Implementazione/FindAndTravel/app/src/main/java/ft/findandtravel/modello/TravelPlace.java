package ft.findandtravel.modello;

import com.google.android.gms.maps.model.LatLng;

/**
 * Modello dei luoghi di interesse
 */
public class TravelPlace {

    private LatLng location;
    private String id;
    private String place_id;
    private String reference;
    private String type;

    public LatLng getLocation(){
        return location;
    }

    public String getId(){
        return id;
    }

    public String getPlace_id(){
        return place_id;
    }

    public String getReference(){
        return reference;
    }

    public String getType(){
        return type;
    }

    public void setLocation(LatLng l){
        location = l;
    }

    public void setLocation(float lat,float lon){
        LatLng aux = new LatLng(lat,lon);
        setLocation(aux);
    }

    public void setId(String i){
        id = i;
    }

    public void setPlace_id(String pi){
        place_id = pi;
    }

    public void setReference(String r){
        reference = r;
    }

    public void setType(String t){
        type = t;
    }


}
