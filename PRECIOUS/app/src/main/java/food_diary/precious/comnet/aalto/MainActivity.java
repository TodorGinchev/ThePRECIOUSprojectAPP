package food_diary.precious.comnet.aalto;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import aalto.comnet.thepreciousproject.R;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fd_main_activity);

        //If Android version >=5.0, set status bar background color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.foodDiary));
        }

        //Set toolbar title and icons
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.food_diary_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.foodDiary));
        toolbar.setNavigationIcon(R.drawable.fd_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Init Tab host
        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();


        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec(getResources().getString(R.string.photo_stream));
        spec.setContent(R.id.tab1);
        spec.setIndicator(getResources().getString(R.string.photo_stream));
        host.addTab(spec);
        //Tab 2
        spec = host.newTabSpec(getResources().getString(R.string.daily_view));
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.daily_view));
        host.addTab(spec);
        //Tab 3
        spec = host.newTabSpec(getResources().getString(R.string.weekly_view));
        spec.setContent(R.id.tab3);
        spec.setIndicator(getResources().getString(R.string.weekly_view));
        host.addTab(spec);
        //Tab 4
        spec = host.newTabSpec(getResources().getString(R.string.monthly_view));
        spec.setContent(R.id.tab4);
        spec.setIndicator(getResources().getString(R.string.monthly_view));
        host.addTab(spec);
        //Set tab titles to lowercase
        for (int i = 0; i < 4;i++) {
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setAllCaps(false);
            tv.setTypeface(null, Typeface.NORMAL);
//            tv.setTextSize(R.dimen.fd_tab_host_text_size);
        }
    }

    public void openDatePicker(View v){
        Intent i = new Intent(this, fdDatePicker.class);
        startActivity(i);

    }


}

