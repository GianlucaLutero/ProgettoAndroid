package ft.findandtravel.modello;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import ft.findandtravel.R;

public class ReviewerAdapter extends ArrayAdapter<Reviewer> {
    public ReviewerAdapter(Context context, int resource, List<Reviewer> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position,View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.reviewer_list, null);

        TextView author = (TextView)convertView.findViewById(R.id.author);
        RatingBar rating = (RatingBar)convertView.findViewById(R.id.ratingBar);
        TextView textComment = (TextView)convertView.findViewById(R.id.textComment);

        Reviewer tmp = getItem(position);

        rating.setIsIndicator(true);
        author.setText(tmp.getAuthor());
        rating.setRating(tmp.getRating());
        textComment.setText(tmp.getText());


        return convertView;
    }
}
