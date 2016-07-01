package my_favourites.precious.comnet.aalto;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import aalto.comnet.thepreciousproject.R;


public class FA_SecondActivity extends Fragment {
    public static final String TAG = "FA_SecondActivity";
    public static final String FA_PREFS_NAME = "FAsubappPreferences";
    public static final String OG_PREFS_NAME = "OGsubappPreferences";
    public static SharedPreferences fa_preferences;
    public static SharedPreferences og_preferences;

    public TextView tv;
    private View v;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        v = inflater.inflate(R.layout.fa_layout2, null);
        fa_preferences = this.getActivity().getSharedPreferences(FA_PREFS_NAME, 0);
        og_preferences = this.getActivity().getSharedPreferences(OG_PREFS_NAME, 0);
        tv = (TextView) v.findViewById(R.id.textView2);
        updateView();

        return v;
    }

    @SuppressLint("StringFormatMatches")
    public void updateView(){
        int preferred_OG_Box_aux = og_preferences.getInt("preferredBox1", -1);
        if(preferred_OG_Box_aux==-1) {
            tv.setText(getResources().getString(R.string.imporance_ruler_2nd_screen_no_selection));
            return;
        }
        else{
            tv.setText(getResources().getString(R.string.fa_2nd_screen_intro1));
        }


        GridView gridView;
        String[] values = getResources().getStringArray(R.array.pa_names);
        List<String> values_array = Arrays.asList(values);
        Collections.sort(values_array, String.CASE_INSENSITIVE_ORDER);
        values = values_array.toArray(new String[values_array.size()]);
        gridView = (GridView) v.findViewById(R.id.gridView1);

        gridView.setAdapter(new FA_Adapter(my_favourites_activity.appConext, values));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ImageView iv = (ImageView) v.findViewById(R.id.grid_item_image);
                TextView tv = (TextView) v.findViewById(R.id.grid_item_label);


                String favourite_activities = fa_preferences.getString("favourite_activities","");
                List<String> list = new ArrayList<String> (Arrays.asList(TextUtils.split(favourite_activities, ",")));

                boolean isFavouriteAlready=false;
                for(int i=0; i<list.size();i++){
                    if(list.get(i).equals(tv.getText())){
                        isFavouriteAlready=true;
                        list.remove(i);
                        iv.setColorFilter(getResources().getColor(R.color.black));
                        tv.setTextColor(getResources().getColor(R.color.black));
                    }
                }

                if(!isFavouriteAlready){
                    iv.setColorFilter(getResources().getColor(R.color.myFavourites));
                    tv.setTextColor(getResources().getColor(R.color.myFavourites));
                    list.add(list.size(),tv.getText().toString());
                }

                Log.i(TAG,"FA="+TextUtils.join(",", list));
                SharedPreferences.Editor editor = fa_preferences.edit();
                editor.putString("favourite_activities", TextUtils.join(",", list));
                editor.apply();
            }
        });
    }
}

