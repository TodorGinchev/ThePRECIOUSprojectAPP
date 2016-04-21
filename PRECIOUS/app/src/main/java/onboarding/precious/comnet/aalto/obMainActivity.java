package onboarding.precious.comnet.aalto;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import aalto.comnet.thepreciousproject.R;

public class obMainActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ob_main_activity);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.onboarding));
        }
    }

    public void signUp (View v){
        Intent i = new Intent(this,onboarding.precious.comnet.aalto.obSignUp.class);
        this.startActivity(i);
    }

    public void signIn (View v){
        Intent i = new Intent(this,onboarding.precious.comnet.aalto.obSignIn.class);
        this.startActivity(i);
    }
}