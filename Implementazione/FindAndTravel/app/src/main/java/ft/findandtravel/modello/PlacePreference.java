package ft.findandtravel.modello;

import com.google.android.gms.maps.model.LatLng;

public class PlacePreference {

    String name;
    String type;
    LatLng location;

    public PlacePreference(){}

    public PlacePreference(String s,LatLng l){
        this.name = s;
        this.location = l;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
