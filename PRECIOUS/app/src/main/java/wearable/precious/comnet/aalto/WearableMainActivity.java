package wearable.precious.comnet.aalto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import aalto.comnet.thepreciousproject.R;

public class WearableMainActivity extends Activity {

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wr_main);

        mContext=this;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.wearable));
        }
        //Set toolbar title and icons
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.wearable_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.wearable));
        toolbar.setNavigationIcon(R.drawable.wr_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume (){
        super.onResume();
        Intent backgroundService = new Intent(mContext, BackgroundService.class);
        mContext.startService(backgroundService);
    }

    public static void PairWearable (View v){
        Intent i = new Intent(mContext, wearable.precious.comnet.aalto.ScanActivity.class);
        mContext.startActivity(i);
    }
}
