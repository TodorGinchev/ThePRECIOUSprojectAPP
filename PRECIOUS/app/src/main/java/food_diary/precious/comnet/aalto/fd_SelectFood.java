package food_diary.precious.comnet.aalto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;

public class fd_SelectFood extends AppCompatActivity {

    public static final String TAG = "fd_SelectFood";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fd_select_food);


        //Init automplete edit text
        String str[]= getResources().getStringArray(R.array.food_names);
        final AutoCompleteTextView tvAuto = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView1);
        ArrayAdapter<String> adp=new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,str);
        tvAuto.setThreshold(1);
        tvAuto.setAdapter(adp);
        //Init food composition TextViews
        final TextView tvEnerc1000 = (TextView) findViewById(R.id.tvEnerc1000);
        final TextView tvCho = (TextView) findViewById(R.id.tvCho);
        final TextView tvFasat = (TextView) findViewById(R.id.tvFasat);
        final TextView tvFats = (TextView) findViewById(R.id.tvFats);
        final TextView tvNa1000 = (TextView) findViewById(R.id.tvNa1000);
        final TextView tvProt = (TextView) findViewById(R.id.tvProt);
        final TextView tvSugar = (TextView) findViewById(R.id.tvSugar);

        tvAuto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            @SuppressWarnings("all")
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String str[]= getResources().getStringArray(R.array.food_names);
                String selectedFood = tvAuto.getText().toString();
                int index = -1;
                for (int i=0;i<str.length;i++) {
                    if (str[i].equals(selectedFood)) {
                        index = i;
                        break;
                    }
                }
                Log.i(TAG, "Id=_" + index);

                tvEnerc1000.setText(getString(R.string.enerc) + ": " + (double)(getResources().getIntArray(R.array.food_db_enerc1000)[index] / 1000) + "kJ");
                    tvCho.setText(getString(R.string.cho)+": "+(double)getResources().getIntArray(R.array.food_db_cho)[index]/1000000+"g");
                    tvFasat.setText(getString(R.string.fasat)+": "+(double)getResources().getIntArray(R.array.food_db_fasat)[index]/1000000+"g");
                    tvFats.setText(getString(R.string.fat)+": "+(double)getResources().getIntArray(R.array.food_db_fats)[index]/1000000+"g");
                    tvNa1000.setText(getString(R.string.na)+": "+(double)getResources().getIntArray(R.array.food_db_na1000)[index]/1000+"mg");
                    tvProt.setText(getString(R.string.prot)+": "+(double)getResources().getIntArray(R.array.food_db_prot)[index]/1000000+"g");
                    tvSugar.setText(getString(R.string.sugar)+": "+(double)getResources().getIntArray(R.array.food_db_sugar)[index]/1000000+"g");
            }
        });
//        int activity_id = getResources().getIdentifier(ActivityTypeNoSpace, "array", this.getPackageName());
    }
}
