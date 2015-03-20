package aalto.comnet.thepreciousgame3;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class gameScore extends Activity{
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_score_game3);
		
		SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(
        		"aalto.comnet.thepreciousproject.game3", Context.MODE_PRIVATE);
		int score = mPrefs.getInt("score", -1);
		
		TextView tv = (TextView) findViewById(R.id.textViewScore);
		tv.setText("Score: "+score);
		
	}
	
	public void restartGame (View v){
	    Intent i = new Intent(this, MainActivity.class);
	    startActivity(i);	
	}

	
}
