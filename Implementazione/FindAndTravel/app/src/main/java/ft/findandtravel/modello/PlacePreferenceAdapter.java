package ft.findandtravel.modello;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ft.findandtravel.R;

public class PlacePreferenceAdapter extends ArrayAdapter<PlacePreference>{

    public PlacePreferenceAdapter(Context context, int resource, List<PlacePreference> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.preference_list, null);

        TextView name = (TextView) convertView.findViewById(R.id.place_title);
        ImageView logo = (ImageView) convertView.findViewById(R.id.logo);

        PlacePreference p = getItem(position);

        name.setText(p.getName());

        logo.setImageResource(R.drawable.place_icon);
        return convertView;
    }
}