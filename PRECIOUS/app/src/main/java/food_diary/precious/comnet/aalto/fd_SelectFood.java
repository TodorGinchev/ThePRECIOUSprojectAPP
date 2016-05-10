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


        //SPANISH FOOD DB
        String str[]= getResources().getStringArray(R.array.food_db_es);

        AutoCompleteTextView t1 = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView1);

        ArrayAdapter<String> adp=new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,str);

        t1.setThreshold(1);
        t1.setAdapter(adp);

        //ENGLISH FOOD DB
        String str2[]= getResources().getStringArray(R.array.food_db_uk);

        AutoCompleteTextView t2 = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView2);

        ArrayAdapter<String> adp2=new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,str2);

        t2.setThreshold(1);
        t2.setAdapter(adp2);

        //FINNISH FOOD DB
        String str3[]= getResources().getStringArray(R.array.food_db_fi);

        AutoCompleteTextView t3 = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView3);

        ArrayAdapter<String> adp3=new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,str3);

        t3.setThreshold(1);
        t3.setAdapter(adp3);



    }

}
