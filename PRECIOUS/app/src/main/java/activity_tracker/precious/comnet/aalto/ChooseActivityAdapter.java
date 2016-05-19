package activity_tracker.precious.comnet.aalto;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class ChooseActivityAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public ChooseActivityAdapter(Context context, String[] values) {
        super(context, R.layout.at_pa_adapter_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.at_pa_adapter_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position]);
        // Change the icon for Windows and iPhone
        String s = values[position];
        String ActivityTypeNoSpace = values[position].replace(" ", "_");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("á", "a");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("é", "e");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("í", "i");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("ó", "o");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("ú", "u");
        imageView.setImageResource(context.getResources().getIdentifier(ActivityTypeNoSpace, "drawable", context.getPackageName()));

        return rowView;
    }
}