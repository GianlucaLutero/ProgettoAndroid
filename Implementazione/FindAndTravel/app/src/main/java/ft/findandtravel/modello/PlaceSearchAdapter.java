package ft.findandtravel.modello;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ft.findandtravel.R;


public class PlaceSearchAdapter extends ArrayAdapter<PlaceSearch> {

    public PlaceSearchAdapter(Context context, int resource, List<PlaceSearch> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.search_list, null);

        TextView name = (TextView) convertView.findViewById(R.id.search_name);
        TextView address = (TextView) convertView.findViewById(R.id.search_address);

        PlaceSearch c = getItem(position);

        name.setText(c.getName());
        address.setText(c.getAddress());


        return convertView;
    }
}
