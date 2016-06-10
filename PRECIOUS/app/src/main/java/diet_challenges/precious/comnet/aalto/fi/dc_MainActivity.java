package diet_challenges.precious.comnet.aalto.fi;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import aalto.comnet.thepreciousproject.R;

public class dc_MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dc_main_activity);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.dietaryChallenge));
        }
        // Floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_diet_challenge);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i =new Intent(fd_MainActivity.getContext(),fd_SelectFood.class);
//                startActivity(i);
            }
        });

        //Set toolbar title and icons
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.dietary_challenge_title));
        toolbar.setTitleTextColor(getResources().getColor(R.color.dietaryChallenge));
        toolbar.setNavigationIcon(R.drawable.dc_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }



}
