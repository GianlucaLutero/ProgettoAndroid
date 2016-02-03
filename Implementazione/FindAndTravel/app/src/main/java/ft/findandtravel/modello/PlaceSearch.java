package ft.findandtravel.modello;


import com.google.android.gms.maps.model.LatLng;

public class PlaceSearch {

    private String name;
    private String address;
    private String id;
    private LatLng position;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public LatLng getPosition() {
        return position;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
