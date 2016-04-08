package activity_tracker.precious.comnet.aalto;


import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ChooseActivity extends ListActivity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        String[] values = new String[]  { "Walking","Running","Riding a bicycle",
                "Walking","Running","Riding a bicycle",
                "Walking","Running","Riding a bicycle",
                "Walking","Running","Riding a bicycle",
                "Walking","Running","Riding a bicycle",
                "Walking","Running","Riding a bicycle",
                "Walking","Running","Riding a bicycle",
                "Walking","Running","Riding a bicycle"
        };
        ChooseActivityAdapter adapter = new ChooseActivityAdapter(this, values);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
        finish();
    }
}