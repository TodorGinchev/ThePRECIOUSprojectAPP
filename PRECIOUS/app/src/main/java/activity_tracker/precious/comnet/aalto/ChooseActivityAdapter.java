package activity_tracker.precious.comnet.aalto;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ui.precious.comnet.aalto.precious.R;

public class ChooseActivityAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public ChooseActivityAdapter(Context context, String[] values) {
        super(context, R.layout.pa_adapter_layout, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.pa_adapter_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        textView.setText(values[position]);
        // Change the icon for Windows and iPhone
        String s = values[position];
//        if (s.startsWith("Windows7") || s.startsWith("iPhone")
//                || s.startsWith("Solaris")) {
//            imageView.setImageResource(R.drawable.running);
//        } else {
//            imageView.setImageResource(R.drawable.walking);
//        }
        if (s.startsWith("Walking"))
            imageView.setImageResource(R.drawable.walking);
        else if (s.startsWith("Running"))
            imageView.setImageResource(R.drawable.running);
        else if (s.startsWith("Riding a bicycle"))
            imageView.setImageResource(R.drawable.bicycle);

        return rowView;
    }
}