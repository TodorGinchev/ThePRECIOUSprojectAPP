package activity_tracker.precious.comnet.aalto;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import aalto.comnet.thepreciousproject.R;

public class ChooseActivity extends ListActivity {
    public static final String TAG = "atChooseActivity";

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String[] values = getResources().getStringArray(R.array.pa_names);
        List<String> values_array = Arrays.asList(values);
        Collections.sort(values_array, String.CASE_INSENSITIVE_ORDER);
        values = values_array.toArray(new String[values_array.size()]);

        ChooseActivityAdapter adapter = new ChooseActivityAdapter(this, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onPause() {
        //Store app usage
        try {
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onPause");
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
            sql_db.precious.comnet.aalto.DBHelper.getInstance(this).insertAppUsage(System.currentTimeMillis(), TAG, "onResume");
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