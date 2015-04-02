package aalto.comnet.thepreciousgame3;

import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {//implements SensorEventListener {

	public GameThread getThread() {
		return thread;
	}
	
	private Context appContext;
	
	// //// THREAD AND TIMING //////
	// Thread in charge of game processing
	private GameThread thread = new GameThread();
	// Cada cuanto queremos procesar cambios (ms)
	private static int PROCESS_PERIOD = 50;
	// Cuando se realizó el último proceso
	private long lastProcess = 0;
	
    //Screen size
    private int screenWidth=0;
    private int screenHeigh=0;
	
    // //// Obstacles //////
	private Drawable drawableObstacles;
    private Vector<Grafico> Obstacles;
    private int numObstacles;
    private int numObstaclesRemain;
    
    //int [][] obstaclePositions;
    
    // //// Path //////
//	private Drawable drawablePaths;
//    private Vector<Grafico> Paths; 
//    private int numPaths = 100;
//    int [][] pathsPositions;
    

    
    // //// Objects to be collected //////
	private Drawable drawableFood[]= new Drawable[8];
    private Vector<Grafico> Food; 
    private int numFood;
    private int numFoodRemain;
    private Vector<Integer> randomNums;
    
    // //// Player /////
    Drawable drawablePlayer[] = new Drawable[11];
	private Grafico player;
	private int Score;
	private final int INITIAL_LIFE=10;
	private int Life;
	
	private int acelerationState = 0; //-1 decelerate down, 0 none, 1 accelerate up
	
    // Standard increment of rotation and acceleration
	private float playerAcceleration=3.0f; 
	private float playerDeceleration=2.0f;
	private float PLAYER_MAXIMUM_ACELERATION_SPEED=-10.0f;
	private  float PLAYER_MAXIMUM_DECELERATION_SPEED=30.0f;
	private long startTime = System.currentTimeMillis();
//	private float PLAYER_FORWARD_SPEED=7.0f;
	
//    // Sensors
//    private SensorManager mSensorManager;
//    private Sensor orientationSensor;
//    private boolean acelerateSensors;
    
    //// MULTIMEDIA //////
    boolean music;
    SoundPool soundPool;
    int idJump, idCollect;
   
    private double game_speed = -5.0;    
    
    private Activity parent;
    
    public SharedPreferences pref;
    
    
    
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        appContext = context.getApplicationContext();
        
        pref = context.getSharedPreferences( 
      		   "aalto.comnet.thepreciousproject.game3", Context.MODE_PRIVATE);
        music=pref.getBoolean("music",true);
        if(music)
        {
      	  soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC , 0);
      	  idJump = soundPool.load(context, R.raw.collect, 0);
      	  idCollect = soundPool.load(context, R.raw.jump, 0);
        }
        

        //TODO
//        acelerateSensors = pref.getBoolean("acelerateSensors", false);
      		
        drawableObstacles = context.getResources().    
        			getDrawable(R.drawable.valla_small);
//        drawablePaths = context.getResources().    
//    			getDrawable(R.drawable.path_large);   
        drawableFood[0] = context.getResources().
					getDrawable(R.drawable.apple_medium);
	    drawableFood[1] = context.getResources().
					getDrawable(R.drawable.banana_medium);
	    drawableFood[2] = context.getResources().
	              	getDrawable(R.drawable.carrot_medium);
	    drawableFood[3] = context.getResources().    
					getDrawable(R.drawable.cake_medium);
	    drawableFood[4] = context.getResources().
	          		getDrawable(R.drawable.humburger_medium);
	    drawableFood[5] = context.getResources().    
					getDrawable(R.drawable.pizza_medium);
	    drawableFood[6] = context.getResources().    
				getDrawable(R.drawable.running_game3);
	    drawableFood[7] = context.getResources().    
				getDrawable(R.drawable.bicycle_game3);
	    drawablePlayer[0] = context.getResources().getDrawable( 
	    			R.drawable.cerdito_xx_small);
	    drawablePlayer[1] = context.getResources().getDrawable( 
    			R.drawable.cerdito_x_small);
	    drawablePlayer[2] = context.getResources().getDrawable( 
    			R.drawable.cerdito_small);
	    drawablePlayer[3] = context.getResources().getDrawable( 
    			R.drawable.cerdito_medium);
	    drawablePlayer[4] = context.getResources().getDrawable( 
    			R.drawable.cerdito_large);
	    drawablePlayer[5] = context.getResources().getDrawable( 
    			R.drawable.cerdito_x_large);
	    drawablePlayer[6] = context.getResources().getDrawable( 
    			R.drawable.cerdito_xx_large);
	    drawablePlayer[7] = context.getResources().getDrawable( 
    			R.drawable.cerdito_3x_large);
	    drawablePlayer[8] = context.getResources().getDrawable( 
    			R.drawable.cerdito_4x_large);
	    drawablePlayer[9] = context.getResources().getDrawable( 
    			R.drawable.cerdito_5x_large);
	    drawablePlayer[10] = context.getResources().getDrawable( 
    			R.drawable.cerdito_6x_large);

	    Life = INITIAL_LIFE;
        Score=0;

        
//        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
//        List<Sensor> listSensors = mSensorManager.getSensorList( Sensor.TYPE_ACCELEROMETER); //in case we need to use more sensors
//	            
       // if(pref.getBoolean("sensores",true) && !listSensors.isEmpty())
       // {
//      	  Sensor orientationSensor = listSensors.get(0);
//      	  mSensorManager.registerListener(this, orientationSensor,
//	                                      SensorManager.SENSOR_DELAY_GAME);
	   // }
  }
    
    @Override protected void onSizeChanged(int ancho, int alto, 
            int ancho_anter, int alto_anter) {
		super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
		//Get screen size
		screenWidth = ancho;
		screenHeigh = alto;
		
		//Set player parameters
		int initialDrawableNum = (int) (drawablePlayer.length-drawablePlayer.length*pref.getFloat("questionare_score", 0) -1);
		player = new Grafico(this, drawablePlayer[initialDrawableNum]);
		player.setTag(initialDrawableNum);
		player.setPosX(player.getAncho());
		player.setPosY((alto-1.1*player.getAlto()));
		//Set obstacle parameters
		Grafico obstacle = new Grafico(this, drawableObstacles);
		int obstacleHeigh = obstacle.getAlto();
		int obstacleWidth = obstacle.getAncho();
		numObstacles = (screenHeigh/obstacleHeigh)/3;
		numObstaclesRemain = numObstacles;
        Obstacles = new Vector<Grafico>();        
        for (int i = 0; i < numObstacles; i++) {
              obstacle = new Grafico(this, drawableObstacles);
              boolean isNear;
              do{
            	  isNear = false;
	              obstacle.setPosX(screenWidth-obstacleWidth);
	              obstacle.setPosY(Math.random()*screenHeigh);
	              for (int j=Obstacles.size()-1;j>=0;j--)
	            	  if(!(Obstacles.get(j).distancia(obstacle)>1.2*obstacle.getAlto()))
	            		  isNear=true;
              }while (isNear);
              obstacle.setTag(1);
              Obstacles.add(obstacle);
    	}
		//Set food parameters
		Grafico food = new Grafico(this, drawableFood[0]);
		//int foodHeigh = food.getAlto();
		int foodWidth = food.getAncho();
		numFood = (screenHeigh/obstacleHeigh)/3;
		numFoodRemain = numObstacles;
        Food = new Vector<Grafico>();     
        //Add random food, without repeating the same food
		int imageNum;		
		randomNums = new Vector<Integer>();
		boolean selectedInPast;
        for (int i = 0; i < numFood; i++) {      
			do{	
				selectedInPast=false;
				imageNum =  (int)(Math.random()*drawableFood.length);
				for (int j=0; j<randomNums.size();j++)
					if(randomNums.get(j)==imageNum)
						selectedInPast=true;
			} while (selectedInPast);
			randomNums.add(imageNum);
			food = new Grafico(this, drawableFood[imageNum]);
			food.setTag(imageNum);
			boolean isNear;
			do{
				isNear = false;
				food.setPosX(2*screenWidth/3-foodWidth);
				food.setPosY(Math.random()*screenHeigh);
				for (int j=Food.size()-1;j>=0;j--)
					if(!(Food.get(j).distancia(food)>1.2*food.getAlto()))
						isNear=true;
			}while (isNear);
			Food.add(food);
        }
        //Start thread
		lastProcess = System.currentTimeMillis();
		if(!thread.isAlive())
			thread.start(); 
		}
    
    @SuppressLint("DrawAllocation")
	@Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draw obstacles and food
        try{
        for (int i = Obstacles.size()-1; i>=0; i--)
            Obstacles.get(i).dibujaGrafico(canvas);
        for (int i = Food.size()-1; i>=0; i--)
            Food.get(i).dibujaGrafico(canvas);

        }catch (Exception e){
        	Log.e("onDraw Error"," ...",e);
        }
        //Draw player
        player.dibujaGrafico(canvas);
        
        //Update text info
        Path trazo = new Path();
        //canvas.drawColor(Color.WHITE);
        trazo.moveTo(1, 1);
        trazo.lineTo(screenWidth, 1);
        Paint pincel = new Paint();
        pincel.setColor(Color.BLUE);
        pincel.setTextSize(20);
        canvas.drawTextOnPath(appContext.getString(R.string.score)+" "+Score, trazo, 20, 20, pincel);
        
        pincel.setColor(Color.RED);
        pincel.setStrokeWidth(3);
        canvas.drawRect(screenWidth-screenWidth/5-5,5,screenWidth-5,screenHeigh/15,pincel);
        pincel.setColor(Color.GREEN);
        if(Life!=INITIAL_LIFE)
        	canvas.drawRect(screenWidth-screenWidth/5-5,  5,
        			      (screenWidth-(screenWidth/5)*(INITIAL_LIFE-Life)/INITIAL_LIFE)-5,screenHeigh/15,pincel);
        else canvas.drawRect(screenWidth-screenWidth/5-5,  5,
			      		   screenWidth-5,screenHeigh/15,pincel);
        
//        trazo.moveTo(1, 1);
//        trazo.lineTo(screenWidth, 1);
//        Paint pincel = new Paint();
//        pincel.setColor(Color.BLUE);
//        pincel.setTextSize(20);
//        canvas.drawTextOnPath("Score: "+Score, trazo, 20, 20, pincel);
    }

    protected void updateView() 
    {
        long now = System.currentTimeMillis();
        float speedPonderation = (float)(now-startTime)/1000/60;
    	playerAcceleration=3.0f*(1+speedPonderation); 
    	playerDeceleration=2.0f*(1+speedPonderation);
    	PLAYER_MAXIMUM_ACELERATION_SPEED=-10.0f;
    	PLAYER_MAXIMUM_DECELERATION_SPEED=30.0f;
    	game_speed = -5.0*(1+speedPonderation);
        // No hagas nada si el período de proceso no se ha cumplido.
        if (lastProcess + PROCESS_PERIOD > now) {
              return;
        }
        // Para una ejecución en tiempo real calculamos retardo          
        double retardment = (now - lastProcess) / PROCESS_PERIOD;
        lastProcess = now; // Para la próxima vez
        
        double nIncY;
        switch (acelerationState) {
        case -1	:	if(player.getIncY()<PLAYER_MAXIMUM_DECELERATION_SPEED)
        				nIncY = player.getIncY() + playerDeceleration *
        					Math.cos(Math.toRadians(player.getAngulo())) * retardment;  
        			else nIncY = player.getIncY();
    		   		break;
        case 0	:  	nIncY = 0;
        			break;
        case 1	:  	if(player.getIncY()>PLAYER_MAXIMUM_ACELERATION_SPEED)
        				nIncY = player.getIncY() - playerAcceleration *
        					Math.cos(Math.toRadians(player.getAngulo())) * retardment;
        			else{
        				acelerationState=-1;
        				nIncY = player.getIncY();	
        			}
    				break;
        default	:	nIncY = 0;
        		  	break;
		}
//        int obsHeigh=Obstacles.get(0).getAlto();
//        int obsWidth=Obstacles.get(0).getAncho();
//        int playHeigh=player.getAlto();
//        int playWidth=player.getAncho();
        //
        //check if player is on top or bottom of the screen
        //
//        if (player.getPosY()+player.getAlto()>screenHeigh-7 && (player.getIncY()>1 || acelerationState==-1)){
//        	acelerationState=0;
//            player.setIncY(0);
//        	player.setPosY(screenHeigh-7-player.getAlto());
//        }
//        else if (player.getPosY()<1 && (player.getIncY()>-1 || acelerationState==1)){
//        	acelerationState=-1;
//            player.setIncY(0);
//        	player.setPosY(1);
//        }
//        else{
//	        player.setIncX(0);
//	        player.setIncY(nIncY);
//        }
//        double prevPosY = player.getPosY();
//        player.incrementaPos(retardment); // Update position in X and Y coordinates
//        if(prevPosY-player.getPosY()>screenHeigh/2)
//        {
//        	acelerationState=0;
//            player.setIncY(0);
//        	player.setPosY(screenHeigh-7-player.getAlto());
//        }
//        else if(player.getPosY()-prevPosY>screenHeigh/2)
//        {
//        	acelerationState=-1;
//            player.setIncY(0);
//        	player.setPosY(7);
//        }

        player.setIncX(0);
        player.setIncY(nIncY);
        player.incrementaPos(retardment); // Update position in X and Y coordinates
          
		//
		//Check if player is touching some of the obstacles   
		//
        for(int i=Obstacles.size()-1;i>=0;i--){
        	Grafico obstacle = Obstacles.get(i);
        	
        	//Check if player is hitting an obstacle        	
        	if(obstacle.getTag()==1 && (obstacle.distancia(player)<(obstacle.getAlto()+obstacle.getAncho())/2 )){
        		Obstacles.get(i).setDrawable(this.getResources().getDrawable(R.drawable.valla_rota_small));
        		Obstacles.get(i).setTag(-1);
        		Life--;
        	}
        	
        	//Update Obstacles Location
        	if( numObstaclesRemain>0 && (obstacle.getPosX() < screenWidth/2)){
        		numObstaclesRemain--;
        		addObstacle();
        	}
        	else if(obstacle.getPosX() <= -1*obstacle.getAncho()/3){
        		Obstacles.remove(i);
        		addObstacle();
        	}
        	else{
	        	obstacle.setIncX(game_speed);
	        	obstacle.incrementaPos(retardment);
        	}
        	if(Obstacles.size()<4 && numObstaclesRemain<=0) 
    		addObstacle();
        }//END for numObstacles
        
		//
		//Check if player is touching some of the food   
		//
        for(int i=Food.size()-1;i>=0;i--){
        	Grafico food = Food.get(i);
        	     	
        	if(food.distancia(player)<(food.getAlto()+food.getAncho())/2){
        		switch (food.getTag()) {
				case 0:	
				case 1: 
				case 2: Score++; //increment score					
					break;
				case 3: 
				case 4:	
				case 5: changePlayerFat(true); //make player bigger
					break;
				case 6: 
				case 7:	changePlayerFat(false); //make player smaller	
					break;

				default:
					break;
				}
        		Food.remove(i);
        		numFoodRemain++;
        	}
        	
        	//Update Food Location
        	if( numFoodRemain>0 && (food.getPosX() < screenWidth/3) ){
        		numFoodRemain--;
        		addFood();
        	}
        	else if(food.getPosX() <= -1*food.getAncho()/3){
        		Food.remove(i); 
        		addFood();
        	}    
        	else{
	        	food.setIncX(game_speed);
	        	food.incrementaPos(retardment);
        	}
        	if(Food.size()<4 && numFoodRemain<=0) 
    		addFood();
        }//END for numFood
        
        
     
//        		//if(music)
//        			//soundPool.play(idCollect, 1, 1, 1, 0, 1); //TODO

        if(Life<=0)
        	exit();
    
  	}
          
   
    
    class GameThread extends Thread 
    {
    	private boolean pause, running;  
        public synchronized void pause() {
              pause = true;      
        }  
        public synchronized void continueGame() {
              pause = false;
              notify();
        }  
        public void stopThread() {
              running = false;
              if (pause) continueGame();
        }       
        @Override
        public void run() 
        {
              running = true;
              while (running) 
              {
                     updateView();
                     synchronized (this) 
                     {
                           while (pause) 
                           {
                                  try 
                                  {
                                        wait();
                                  } catch (Exception e) {
                                  						}
                           }
                     }
              }
        }
    }
    
    @Override
    public boolean onTouchEvent (MotionEvent event) {
    	if(event.getAction()==MotionEvent.ACTION_DOWN)
    		acelerationState=1;
        return true;
    }
    
    public void changePlayerFat(boolean fat){
    	
    	int drawNum;
    	if(fat)
    		drawNum = (player.getTag()>=drawablePlayer.length-1)?	drawablePlayer.length-1 : player.getTag()+1;
    	else
    		drawNum = (player.getTag()<=0)?	0 : player.getTag()-1;
    	
    	//As player.setDrawable is not working, lets try something else:
    	Grafico player2 = new Grafico (this,drawablePlayer[drawNum]);
    	player2.setIncX(player.getIncX());
    	player2.setIncY(player.getIncY());
    	player2.setAngulo(player.getAngulo());
    	player2.setPosX(player.getPosX());
    	player2.setPosY(player.getPosY());
    	player2.setTag(drawNum);
    	
    	player = player2;
    }

    public void addObstacle(){
        Grafico obstacle = new Grafico(this, drawableObstacles);
        boolean isNear;
        int cont1=0;
        //Log.i("LOOP","entrando do while valla");
        do{
      	  isNear = false;
            obstacle.setPosX(screenWidth-obstacle.getAncho());
            obstacle.setPosY(Math.random()*screenHeigh);
            for (int j=Obstacles.size()-1;j>=0;j--)
          	  if(!(Obstacles.get(j).distancia(obstacle)>2*obstacle.getAlto()))
          		  isNear=true;
            for (int j=Food.size()-1;j>=0;j--)
        	  if(!(Food.get(j).distancia(obstacle)>2*obstacle.getAlto()))
        		  isNear=true;
            cont1++;
            //Log.i("LOOP","con1: "+cont1);
        }while (isNear&&cont1<=5);
        //Log.i("LOOP","saliendo do while valla");
        if(cont1>=5)
        	{}//numFoodRemain++;
        else if(Obstacles.size()<numObstacles*2){
        	obstacle.setTag(1);
        	Obstacles.add(obstacle);        
        }
        		 
    }
    
    public void addFood(){
		Boolean selectedInPast;
		int imageNum;
		//Log.i("LOOP","entrando do while comida1");
		do{	
			selectedInPast=false;
			imageNum =  (int)(Math.random()*drawableFood.length);
			for (int j=0; j<randomNums.size();j++)
				if(randomNums.get(j)==imageNum)
					selectedInPast=true;
		} while (selectedInPast);
		//Log.i("LOOP","saliendo do while comida1");
		randomNums.add(imageNum);
		if(randomNums.size()>drawableFood.length-3)
			randomNums.remove(0);
		Grafico food = new Grafico(this, drawableFood[imageNum]);
		food.setTag(imageNum);
		boolean isNear;
		int cont=0;
		//Log.i("LOOP","entrando do while comida2");
		do{
			isNear = false;
			food.setPosX(screenWidth-food.getAncho());
			food.setPosY(Math.random()*screenHeigh);
			for (int j=Food.size()-1;j>=0;j--)
				if(!(Food.get(j).distancia(food)>4*food.getAlto()))
					isNear=true;
			for (int j=Obstacles.size()-1;j>=0;j--)
				if(!(Obstacles.get(j).distancia(food)>4*food.getAlto()))
					isNear=true;
			cont++;
			//Log.i("LOOP","cont: "+cont);
		//}while (isNear&&cont<=10);
		}while ( (isNear || food.getPosY()<food.getAlto() || food.getPosY()>screenHeigh-food.getAlto() ) && cont<=5);
		//Log.i("LOOP","salinedo do while comida2");
		if(cont>=5)
			{}//numFoodRemain++;
		else if(Food.size()<numFood*2)
			Food.add(food);
	}      
    
    
    public void setParent(Activity parent) {
    this.parent = parent;
    }
    
    private void exit() {
    	Bundle bundle = new Bundle();
    	bundle.putInt("score", Score);
    	Intent intent = new Intent();
    	intent.putExtras(bundle);
    	parent.setResult(Activity.RESULT_OK, intent);
    	parent.finish();
    	}
}
