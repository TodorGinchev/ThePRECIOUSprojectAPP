package onboarding.precious.comnet.aalto;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import aalto.comnet.thepreciousproject.R;

public class obRequestGroupID  extends Activity {

    private static EditText et;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ob_group_id_request);
        et = (EditText) findViewById(R.id.editText);
    }

    public void onOKpressed (View v){
        int code = Integer.parseInt(et.getText().toString());
        if(code!=123 && code!=130 && code!=678 && code!=387 && code!=827){
            Toast.makeText(this,getResources().getString(R.string.wrong_group_id),Toast.LENGTH_LONG).show();
        }
        else {
            Intent intent = new Intent();
            intent.putExtra("group_ID", code);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
