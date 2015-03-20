package aalto.comnet.thepreciousgame3;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.os.Bundle;

public class Game extends Activity {

	private GameView gameView;
	
	@Override public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game3);
		gameView = (GameView) findViewById(R.id.GameView);
		gameView.setParent(this);		
	}
	
	@Override
	protected void onPause() {
	   super.onPause();
	//   gameView.deactivateSensors();
	   gameView.getThread().pause();
	}
	 	
	@Override
	protected void onResume() {
	   super.onResume();
	//   gameView.activateSensors();
	   gameView.getThread().continueGame();	   
	}
	 
	@Override
	protected void onDestroy() {
		gameView.getThread().stopThread();
	   super.onDestroy();
	}
}
