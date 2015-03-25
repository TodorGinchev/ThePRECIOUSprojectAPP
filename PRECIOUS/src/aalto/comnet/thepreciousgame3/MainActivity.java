package aalto.comnet.thepreciousgame3;

import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends Activity {

	
	private Vector<ImageButton> imageButtons = new Vector<ImageButton>();
	private Vector<ImageButton> selectedImageButtons = new Vector<ImageButton>();
	private Vector<Drawable> imageDrawables = new Vector<Drawable>();
	
	private Integer[] orderFatFromLess;
	private Vector<Integer> randomNums = new Vector<Integer>();
	
	private CheckBox automaticSubmit; //submit questions without pressing submit button
	private Button bSubmit;
	private Button bNext;
	
	private int contSelectedImages=0;
	private Vector<Integer> answerOrder = new Vector<Integer>();
	
	private static final int NUM_QUESTIONS = 3; //number of questions asked before starting the next game
	private int numAnswers; //number of answered questions
	private int score; //player's score
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_game3);
		//Initialize vector with drawables from resource images		
		imageDrawables.add(this.getResources().getDrawable(R.drawable.apple_medium)); //0
		imageDrawables.add(this.getResources().getDrawable(R.drawable.banana_medium)); //1
		imageDrawables.add(this.getResources().getDrawable(R.drawable.cake_medium)); //2
		imageDrawables.add(this.getResources().getDrawable(R.drawable.candy_medium)); //3
		imageDrawables.add(this.getResources().getDrawable(R.drawable.carrot_medium)); //4
		imageDrawables.add(this.getResources().getDrawable(R.drawable.hotdog_medium_background)); //5
		imageDrawables.add(this.getResources().getDrawable(R.drawable.humburger_medium_background)); //6
		imageDrawables.add(this.getResources().getDrawable(R.drawable.icecream_big_background)); //7
		imageDrawables.add(this.getResources().getDrawable(R.drawable.pizza_medium_background)); //8
		orderFatFromLess = new Integer []{0,4,1,3,7,2,5,8,6};
		
		//Initialize image vector with imageButtons
		imageButtons.add((ImageButton)findViewById(R.id.imageButton1));
		imageButtons.add((ImageButton)findViewById(R.id.imageButton2));
		imageButtons.add((ImageButton)findViewById(R.id.imageButton3));
		imageButtons.add((ImageButton)findViewById(R.id.imageButton4));
		//Initialize selected image vector with imageButtons
		selectedImageButtons.add((ImageButton)findViewById(R.id.imageButton5));
		selectedImageButtons.add((ImageButton)findViewById(R.id.imageButton6));
		selectedImageButtons.add((ImageButton)findViewById(R.id.imageButton7));
		selectedImageButtons.add((ImageButton)findViewById(R.id.imageButton8));		
		//Declare OnClick events of button 1 to 4
		for(int i=0; i<imageButtons.size();i++)
			imageButtons.get(i).setOnClickListener(new OnClickListener() {
				                	public void onClick(View view) {
				                		onImageSelected(view);
				                	}
								});
		//Declare OnClick events of button 5 to 8
				for(int i=0; i<selectedImageButtons.size();i++)
					selectedImageButtons.get(i).setOnClickListener(new OnClickListener() {
						                	public void onClick(View view) {
						                		onImageSelected(view);
						                	}
										});
		
		//Initialize submit button and automatic submit check box
		bSubmit = (Button) findViewById(R.id.buttonGameScore);
		automaticSubmit = (CheckBox) findViewById(R.id.checkBox1);	
		bNext = (Button) findViewById(R.id.button2);
		//Set submit button OnClick function and make it invisible
		bSubmit.setOnClickListener(new OnClickListener() {			
									@Override
									public void onClick(View v) {
										showAnswer();
									}
								});
				
		bNext.setOnClickListener(new OnClickListener() {			
									@Override
									public void onClick(View v) {
										nextAnswer();
									}
								});
		score=0;
		numAnswers=0;
		shuffleImages();
	}
	
	
	public void shuffleImages(){
		//Hide submit buttons
		bSubmit.setVisibility(Button.INVISIBLE);
		bNext.setVisibility(Button.INVISIBLE);
		//Disable onClick events of buttons 5 to 8
		for(int i=0; i<selectedImageButtons.size();i++)
			selectedImageButtons.get(i).setClickable(false);

		int imageNum;		
		boolean selectedInPast;
		
		for( int i=0; i<imageButtons.size();i++){
			do{	
				selectedInPast=false;
				imageNum =  (int)(Math.random()*imageDrawables.size());
				for (int j=0; j<randomNums.size();j++)
					if(randomNums.get(j)==imageNum)
						selectedInPast=true;
			} while (selectedInPast);
			randomNums.add(imageNum);
			imageButtons.get(i).setImageDrawable(imageDrawables.get(imageNum)); //set random image
			imageButtons.get(i).setClickable(true); //enable OnClick
		}
		
	}
	
	public void onImageSelected(View view){
		switch (view.getId()) {
		
		case R.id.imageButton1	:	//Disable onClick on clicked button
									imageButtons.get(0).setClickable(false);
									//Set selected image
									selectedImageButtons.get(contSelectedImages).setImageDrawable(imageButtons.get(0).getDrawable());
									//Enable onClick on image button
									selectedImageButtons.get(contSelectedImages).setClickable(true);
									contSelectedImages++;
									answerOrder.add(0);
									break;	
		case R.id.imageButton2	:	//Disable onClick on clicked button
									imageButtons.get(1).setClickable(false);
									//Set selected image
									selectedImageButtons.get(contSelectedImages).setImageDrawable(imageButtons.get(1).getDrawable());
									//Enable onClick on image button
									selectedImageButtons.get(contSelectedImages).setClickable(true);
									contSelectedImages++;
									answerOrder.add(1);
									break;
		case R.id.imageButton3	:	//Disable onClick on clicked button
									imageButtons.get(2).setClickable(false);
									//Set selected image
									selectedImageButtons.get(contSelectedImages).setImageDrawable(imageButtons.get(2).getDrawable());
									//Enable onClick on image button
									selectedImageButtons.get(contSelectedImages).setClickable(true);
									contSelectedImages++;
									answerOrder.add(2);
									break;	
		case R.id.imageButton4	:	//Disable onClick on clicked button
									imageButtons.get(3).setClickable(false);
									//Set selected image
									selectedImageButtons.get(contSelectedImages).setImageDrawable(imageButtons.get(3).getDrawable());
									//Enable onClick on image button
									selectedImageButtons.get(contSelectedImages).setClickable(true);
									contSelectedImages++;
									answerOrder.add(3);
									break;	
		case R.id.imageButton5	:	contSelectedImages--;
									selectedImageButtons.get(contSelectedImages).setClickable(false);
									selectedImageButtons.get(contSelectedImages).setImageResource(R.drawable.ic_launcher);
									
									imageButtons.get(answerOrder.get(contSelectedImages)).setClickable(true);
									answerOrder.remove(contSelectedImages);
									break;	
		case R.id.imageButton6	:	contSelectedImages--;
									selectedImageButtons.get(contSelectedImages).setClickable(false);
									selectedImageButtons.get(contSelectedImages).setImageResource(R.drawable.ic_launcher);
									
									imageButtons.get(answerOrder.get(contSelectedImages)).setClickable(true);
									answerOrder.remove(contSelectedImages);
									break;	
		case R.id.imageButton7	:	contSelectedImages--;
									selectedImageButtons.get(contSelectedImages).setClickable(false);
									selectedImageButtons.get(contSelectedImages).setImageResource(R.drawable.ic_launcher);
									
									imageButtons.get(answerOrder.get(contSelectedImages)).setClickable(true);
									answerOrder.remove(contSelectedImages);
									break;	
		case R.id.imageButton8	:	contSelectedImages--;
									selectedImageButtons.get(contSelectedImages).setClickable(false);
									selectedImageButtons.get(contSelectedImages).setImageResource(R.drawable.ic_launcher);
									
									imageButtons.get(answerOrder.get(contSelectedImages)).setClickable(true);
									answerOrder.remove(contSelectedImages);
									break;	
		default					:	break;
		}
		
		if(contSelectedImages>4)
			contSelectedImages=4;
		else if (contSelectedImages<-1)
			contSelectedImages=-1;
		
		if(contSelectedImages==4){
			if(automaticSubmit.isChecked())
			{
				showAnswer();
			}
			bSubmit.setVisibility(Button.VISIBLE);			
		}
		else bSubmit.setVisibility(Button.INVISIBLE);
		
	}
	
	private void showAnswer(){
		Integer[] randomNumsMapped = {randomNums.get(answerOrder.get(0)),randomNums.get(answerOrder.get(1)),
				randomNums.get(answerOrder.get(2)),randomNums.get(answerOrder.get(3))};
		Vector <Integer> correctAnswerOrder = new Vector<Integer>();
		
		for (int i=0; i<orderFatFromLess.length; i++)
		for(int j=0; j<randomNumsMapped.length;j++)
		if (orderFatFromLess[i]==randomNumsMapped[j])
		correctAnswerOrder.add(randomNumsMapped[j]);
//		Log.i("My answer",""+answerOrder.get(0)+answerOrder.get(1)+answerOrder.get(2)+answerOrder.get(3));	
//		Log.i("My mapped answer",""+randomNumsMapped[0]+randomNumsMapped[1]+randomNumsMapped[2]+randomNumsMapped[3]);	
//		Log.i("Correct answer",""+correctAnswerOrder.get(0)+correctAnswerOrder.get(1)+correctAnswerOrder.get(2)+correctAnswerOrder.get(3));	
		
		//Show corrected answer to user		
		TextView tv = (TextView) findViewById(R.id.TextView01);
		tv.setVisibility(TextView.VISIBLE);
		ImageButton solutionImage0 = (ImageButton) findViewById(R.id.ImageButton5s);
		solutionImage0.setVisibility(ImageButton.VISIBLE);
		solutionImage0.setImageDrawable(imageDrawables.get(correctAnswerOrder.get(0)));
		ImageButton solutionImage1 = (ImageButton) findViewById(R.id.ImageButton6s);
		solutionImage1.setVisibility(ImageButton.VISIBLE);
		solutionImage1.setImageDrawable(imageDrawables.get(correctAnswerOrder.get(1)));
		ImageButton solutionImage2 = (ImageButton) findViewById(R.id.ImageButton7s);
		solutionImage2.setVisibility(ImageButton.VISIBLE);
		solutionImage2.setImageDrawable(imageDrawables.get(correctAnswerOrder.get(2)));
		ImageButton solutionImage3 = (ImageButton) findViewById(R.id.ImageButton8s);
		solutionImage3.setVisibility(ImageButton.VISIBLE);		
		solutionImage3.setImageDrawable(imageDrawables.get(correctAnswerOrder.get(3)));
		bNext.setVisibility(Button.VISIBLE);
		
		//Disable OnClick on images 5 to 8
		for(int i=0; i<selectedImageButtons.size();i++)
			selectedImageButtons.get(i).setClickable(false);
		
		//Update score
		for (int i=0;i<4;i++){
			if(randomNumsMapped[i]==correctAnswerOrder.get(i))
				score++;
		}
		numAnswers++;
		TextView scoreText = (TextView) findViewById(R.id.textView3);
		scoreText.setText(getString(R.string.score)+" "+ score+"/"+numAnswers*4);
	}
	
	private void nextAnswer(){
		
		if(numAnswers>NUM_QUESTIONS-1){
			//If user has answered all the questions, calculate score and start the next game
			SharedPreferences pref = this.getSharedPreferences( 
					"aalto.comnet.thepreciousproject.game3", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor1 = pref.edit();
			
			Float questionare_score = (float)score/((float)numAnswers*4);
			editor1.putFloat("questionare_score",questionare_score );
			editor1.commit();
		    Intent i = new Intent(this, Game.class);
		  
		    startActivityForResult(i, 5665);	
		}	
		else{
			randomNums.clear();
			answerOrder.clear();
			contSelectedImages=0;
			//reset images button from 5 to 8
			for(int i=0; i<selectedImageButtons.size();i++)
				selectedImageButtons.get(i).setImageResource(R.drawable.ic_launcher);
			//set solution images to invisible
			TextView tv = (TextView) findViewById(R.id.TextView01);
			tv.setVisibility(TextView.INVISIBLE);
			ImageButton solutionImage0 = (ImageButton) findViewById(R.id.ImageButton5s);
			solutionImage0.setVisibility(ImageButton.INVISIBLE);
			ImageButton solutionImage1 = (ImageButton) findViewById(R.id.ImageButton6s);
			solutionImage1.setVisibility(ImageButton.INVISIBLE);
			ImageButton solutionImage2 = (ImageButton) findViewById(R.id.ImageButton7s);
			solutionImage2.setVisibility(ImageButton.INVISIBLE);
			ImageButton solutionImage3 = (ImageButton) findViewById(R.id.ImageButton8s);
			solutionImage3.setVisibility(ImageButton.INVISIBLE);					
			shuffleImages();
		}
	}
	
    @Override protected void onActivityResult (int requestCode, int resultCode, Intent data){
   	 super.onActivityResult(requestCode, resultCode, data);
	if (requestCode==5665 & resultCode==RESULT_OK & data!=null) {
			int score = data.getExtras().getInt("score");
			SharedPreferences mPrefs = getApplicationContext().getSharedPreferences(
	        		"aalto.comnet.thepreciousproject.game3", Context.MODE_PRIVATE);
			Editor editor = mPrefs.edit();
            editor.putInt("score", score);
            editor.commit();
		    Intent i = new Intent(this, gameScore.class);
		    startActivity(i);	
		}
    }
    
}

