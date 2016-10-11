package activity_tracker.precious.comnet.aalto;


import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import aalto.comnet.thepreciousproject.R;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

public class ChooseActivity extends ListActivity {
    public static final String TAG = "atChooseActivity";
    public static final String FA_PREFS_NAME = "FAsubappPreferences";

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String[] values = getResources().getStringArray(R.array.pa_names);
        List<String> values_array = Arrays.asList(values);
        Collections.sort(values_array, String.CASE_INSENSITIVE_ORDER);

        try {
            SharedPreferences fa_preferences = PRECIOUS_APP.getAppContext().getSharedPreferences(FA_PREFS_NAME, 0);
            String favourite_activities = fa_preferences.getString("favourite_activities", "");
            List<String> list = new ArrayList<String>(Arrays.asList(TextUtils.split(favourite_activities, ",")));
            int cont = 0;
            for (int j = 0; j < values_array.size(); j++)
                for (int i = 0; i < list.size(); i++) {

                    if (list.get(i).equals(values_array.get(j))) {
                        Collections.swap(values_array, j, cont);
                        cont++;
                    }
                }

            values = values_array.toArray(new String[values_array.size()]);

            ChooseActivityAdapter adapter = new ChooseActivityAdapter(this, values);
            setListAdapter(adapter);
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
    }

    @Override
    protected void onPause() {
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
        } catch (Exception e) {
            Log.e(TAG, " ", e);

        }
        super.onPause();
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        //Store app usage
        try{
            sql_db.precious.comnet.aalto.DBHelper.getInstance().insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
        }catch (Exception e) {
            Log.e(TAG, " ", e);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
//        Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("activity",item);
        intent.putExtra("activity_position",position);
        setResult(RESULT_OK, intent);
        finish();
    }
}