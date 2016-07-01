package my_favourites.precious.comnet.aalto;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aalto.comnet.thepreciousproject.R;

public class FA_Adapter extends ArrayAdapter {
    public static final String TAG = "FA_Adapter";
    public static final String FA_PREFS_NAME = "FAsubappPreferences";
    private Context context;
    private final String[] values;

    public FA_Adapter(Context context, String[] values) {
        super(context, R.layout.fa_adapter, values);
        this.context = context;
        this.values = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        // get layout from mobile.xml
        gridView = inflater.inflate(R.layout.fa_adapter, null);

        // set value into textview
        TextView textView = (TextView) gridView
                .findViewById(R.id.grid_item_label);
        textView.setText(values[position]);

        // set image based on selected text
        ImageView imageView = (ImageView) gridView
                .findViewById(R.id.grid_item_image);

        // Change the icon for Windows and iPhone
        String ActivityTypeNoSpace = values[position].replace(" ", "_");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("á", "a");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("é", "e");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("í", "i");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("ó", "o");
        ActivityTypeNoSpace = ActivityTypeNoSpace.replace("ú", "u");
        imageView.setImageResource(context.getResources().getIdentifier(ActivityTypeNoSpace, "drawable", context.getPackageName()));

        SharedPreferences fa_preferences = my_favourites_activity.appConext.getSharedPreferences(FA_PREFS_NAME, 0);
        String favourite_activities = fa_preferences.getString("favourite_activities","");
        List<String> list = new ArrayList<String>(Arrays.asList(TextUtils.split(favourite_activities, ",")));

        for(int i=0; i<list.size();i++){
            if(list.get(i).equals(values[position])) {
                imageView.setColorFilter(my_favourites_activity.appConext.getResources().getColor(R.color.myFavourites));
                textView.setTextColor(my_favourites_activity.appConext.getResources().getColor(R.color.myFavourites));
            }
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return values.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
