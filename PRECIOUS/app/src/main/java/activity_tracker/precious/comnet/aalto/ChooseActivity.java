package activity_tracker.precious.comnet.aalto;


import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import aalto.comnet.thepreciousproject.R;

public class ChooseActivity extends ListActivity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String[] values = getResources().getStringArray(R.array.pa_names);
        ChooseActivityAdapter adapter = new ChooseActivityAdapter(this, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("activity",item);
        setResult(RESULT_OK, intent);
        finish();
    }
}