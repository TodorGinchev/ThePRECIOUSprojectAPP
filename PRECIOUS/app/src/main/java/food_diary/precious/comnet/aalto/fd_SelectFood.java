package food_diary.precious.comnet.aalto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import aalto.comnet.thepreciousproject.R;

public class fd_SelectFood extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fd_select_food);


        //FOOD DB
        String str[]= getResources().getStringArray(R.array.food_names);

        AutoCompleteTextView t1 = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView1);

        ArrayAdapter<String> adp=new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,str);

        t1.setThreshold(1);
        t1.setAdapter(adp);

    }

}
