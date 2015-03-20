package aalto.comnet.thepreciousgame1;

import java.util.List;
import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View implements SensorEventListener {

	public GameThread getThread() {
		return thread;
	}
	
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
	private Drawable drawableObstacles[]= new Drawable[7]; //TODO change it to dynamic length
    private Vector<Grafico> Obstacles; 
    private int numObstacles = 100;
    int [][] obstaclePositions;

    
    // //// Objects to be collected //////
	private Drawable drawableFood[]= new Drawable[7];
    private Vector<Grafico> Food; 
    private int numFood;
    
    // //// Player /////
	private Grafico player;
	
	private int acelerationState = 0; //-1 decelerate, 0 none, 1 acelerate
	private int inclinationState = 0; //-1 left, 0 none, 1 right
	private int score=0;
	private int scoreLevels=0;
	
	private String infoText="";
	
    // Standard increment of rotation and acceleration
	private float playerAcceleration;
	private float playerDeceleration = 2.0f;
	private float PLAYER_MAXIMUM_ACELERATION_SPEED;
	private float PLAYER_MAXIMUM_TOTAL_SPEED = 10.0f;
	
    // Sensors
    private SensorManager mSensorManager;
    private Sensor orientationSensor;
    private boolean acelerateSensors;
    
    //// MULTIMEDIA //////
    boolean music;
    SoundPool soundPool;
    int idJump, idCollect;
    
    
    private Activity parent;
    
    
    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawablePlayer;
        
        SharedPreferences pref = context.getSharedPreferences( 
      		   "aalto.comnet.thepreciousproject.game1", Context.MODE_PRIVATE);
        music=pref.getBoolean("music",true);
        if(music)
        {
      	  soundPool = new SoundPool( 5, AudioManager.STREAM_MUSIC , 0);
      	  idJump = soundPool.load(context, R.raw.collect, 0);
      	  idCollect = soundPool.load(context, R.raw.jump, 0);
        }
        playerAcceleration = pref.getFloat("aceleration",1f);
        PLAYER_MAXIMUM_ACELERATION_SPEED = pref.getFloat("acelerationMax",-5f);
        PLAYER_MAXIMUM_TOTAL_SPEED = pref.getFloat("speedMax",10f);
        acelerateSensors = pref.getBoolean("acelerateSensors", false);
      		
        drawableObstacles[0] = context.getResources().    
        			getDrawable(R.drawable.obstacle_large);
        //https://www.iconfinder.com/icons/10572/blue_wall_icon#size=24
        drawableObstacles[1] = context.getResources().
        			getDrawable(R.drawable.obstacle2_large);
        drawableObstacles[2] = context.getResources().
        			getDrawable(R.drawable.obstacle3_medium);
        drawableFood[0] = context.getResources().    
					getDrawable(R.drawable.apple_small);
        drawableFood[1] = context.getResources().
					getDrawable(R.drawable.banana_small);
        drawableFood[2] = context.getResources().
                  	getDrawable(R.drawable.candy_small);
        drawableFood[3] = context.getResources().    
					getDrawable(R.drawable.carrot_small);
	    drawableFood[4] = context.getResources().
					getDrawable(R.drawable.hotdog_small);
	    drawableFood[5] = context.getResources().
              		getDrawable(R.drawable.humburger_small);
	    drawableFood[6] = context.getResources().    
					getDrawable(R.drawable.pizza_small);
	    drawablePlayer = context.getResources().getDrawable( 
	    			R.drawable.bee1);

        player = new Grafico(this, drawablePlayer);
        
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> listSensors = mSensorManager.getSensorList( Sensor.TYPE_ACCELEROMETER); //in case we need to use more sensors
	            
       // if(pref.getBoolean("sensores",true) && !listSensors.isEmpty())
       // {
      	  Sensor orientationSensor = listSensors.get(0);
      	  mSensorManager.registerListener(this, orientationSensor,
	                                      SensorManager.SENSOR_DELAY_GAME);
	   // }
  }
    
    @Override protected void onSizeChanged(int ancho, int alto, 
            int ancho_anter, int alto_anter) {
		super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
		screenWidth = ancho;
		screenHeigh = alto;
		//Set player text info
		infoText = ("Speed: "+ PLAYER_MAXIMUM_TOTAL_SPEED + "   Acceleration: " + playerAcceleration
		+ "   Score: "+score);
		//				//
		//Draw obstacles//		
		//				//
		Grafico obstacle = new Grafico(this, drawableObstacles[scoreLevels]);
		int obstacleHeigh = obstacle.getAlto();
		int obstacleWidth = obstacle.getAncho();				
		int num_levels = alto / (obstacleHeigh +new Grafico(this, drawableFood[0]).getAlto());
		int objects_per_level = ancho / (obstacleWidth /*+ Food.get(0).getAncho()*/);
		
		Log.i("TAG",objects_per_level+"");
		
		int [][] pos1= new int [100][2];
		int obsNum = 0;
		for (int i=num_levels-1; i>0 ;i--){
			for (int j=objects_per_level-1;j>0;j--){
				double randomNum = Math.random();
				if(randomNum<=0.2) //20% left part of the screen
					pos1[obsNum] = new int[] {0,i*alto/num_levels-obstacleHeigh/2};
				else if (randomNum<=0.4) //20% right part
					pos1[obsNum] = new int[] {ancho-obstacleWidth,i*alto/num_levels-obstacleHeigh/2};
				else if (randomNum<=0.7) //30% center-left
					pos1[obsNum] = new int[] {ancho/2-obstacleWidth,i*alto/num_levels-obstacleHeigh/2};
										 //30% center-right
				else pos1[obsNum] = new int[] {ancho/2,i*alto/num_levels-obstacleHeigh/2};
				obsNum++;
			}
		}
		numObstacles = obsNum;
		
		int [][]pos2 = new int[numObstacles][2];
		//Copy only those objects that can fit on the screen
		for (int i=0; i<numObstacles; i++)
				pos2[i] = pos1[i];
		obstaclePositions=pos2;
		
        Obstacles = new Vector<Grafico>();        
        for (int i = 0; i < numObstacles; i++) {
              obstacle = new Grafico(this, drawableObstacles[scoreLevels]);
              obstacle.setPosX(obstaclePositions[i][0]);
              obstacle.setPosY(obstaclePositions[i][1]);
              Obstacles.add(obstacle);
        }

		//									//
        // Draw player in initial position	//
		//									//
		player.setPosX(0.5* (ancho-player.getAncho()));
		player.setPosY((alto-player.getAlto()));
		
		//			//
		//Draw food	//
		//			//		
		Food = new Vector<Grafico>();
        numFood = (num_levels-1)*2;
        for (int i = 0; i < numFood; i++) {
        	  int randomFood = (int)((Math.random()*6));//TODO
              Grafico food = new Grafico(this, drawableFood[randomFood]);
              Food.add(food);
        }
        
       // int [][] pos3= new int [numFood][2];
        int foodHeigh = Food.get(0).getAlto();
        int Xpos=-1;
        int prevXpos=-1;
		for (int i=num_levels-1; i>0 ;i--){
			for (int j=2;j>0;j--){
				do {			
					double randomNum = Math.random();
					if(randomNum<=0.2){ //20% left part of the screen
						Food.get(i*2-j).setPosX(0);
						Food.get(i*2-j).setPosY(i*alto/num_levels+1*obstacleHeigh/2);
						Xpos=0;
					}
					else if (randomNum<=0.4){ //20% right part
						Food.get(i*2-j).setPosX(ancho-obstacleWidth);
						Food.get(i*2-j).setPosY(i*alto/num_levels+1*obstacleHeigh/2);
						Xpos=1;
					}
					else if (randomNum<=0.7){ //30% center-left
						Food.get(i*2-j).setPosX(ancho/2-obstacleWidth);
						Food.get(i*2-j).setPosY(i*alto/num_levels+1*obstacleHeigh/2);
						Xpos=2;
					}
					else { 					  //30% center-right
						Food.get(i*2-j).setPosX(ancho/2);
						Food.get(i*2-j).setPosY(i*alto/num_levels+1*obstacleHeigh/2);	
						Xpos=3;
					}
				} while (prevXpos==Xpos || Food.get(i*2-j).distancia(player)<2*player.getAncho());
			prevXpos=Xpos;
			}
		}		


		//TODO add food too
		lastProcess = System.currentTimeMillis();
		if(!thread.isAlive())
			thread.start(); 
		}
    
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
      	//Update text info
        Path trazo = new Path();
        //canvas.drawColor(Color.WHITE);
        trazo.moveTo(1, 1);
        trazo.lineTo(screenWidth, 1);
        Paint pincel = new Paint();
        pincel.setColor(Color.BLUE);
        pincel.setTextSize(20);
        canvas.drawTextOnPath(infoText, trazo, 20, 20, pincel);
        try{
	        for (int i = Obstacles.size()-1; i>=0; i--){
	            Obstacles.get(i).dibujaGrafico(canvas);
	        } 
	        for (int i = Food.size()-1; i>=0; i--){
	            Food.get(i).dibujaGrafico(canvas);
	        } 
        }catch (Exception e){
        	Log.e("onDraw Error"," ",e);
        }
        player.dibujaGrafico(canvas);
    }

    protected void updateView() 
    {
        long now = System.currentTimeMillis();
        // No hagas nada si el período de proceso no se ha cumplido.
        if (lastProcess + PROCESS_PERIOD > now) {
              return;
        }
        // Para una ejecución en tiempo real calculamos retardo          
        double retardment = (now - lastProcess) / PROCESS_PERIOD;
        lastProcess = now; // Para la próxima vez
        switch(inclinationState){
        case	-1	:	player.setAngulo(-20);break;
        case	 0	:	player.setAngulo(0);break;
        case	 1	:	player.setAngulo(20);break;
        default		:	player.setAngulo(0);break;
        }
        
        double nIncY;
        double nIncX;
        switch (acelerationState) {
        case -1	:  	if(player.getIncY()<PLAYER_MAXIMUM_TOTAL_SPEED)
        				nIncY = player.getIncY() + playerDeceleration *
        					Math.cos(Math.toRadians(player.getAngulo())) * retardment;
        			else nIncY = player.getIncY();
        			nIncX = 0;        				
    		   		break;
        case 0	:  	nIncY = 0;
        			nIncX = 0;
        			break;
        case 1	:  	if(player.getIncY()>PLAYER_MAXIMUM_ACELERATION_SPEED)
        				nIncY = player.getIncY() - playerAcceleration *
        					Math.cos(Math.toRadians(player.getAngulo())) * retardment;
        			else nIncY = player.getIncY();
					if(inclinationState==-1)
						nIncX = player.getIncX() + 2*playerAcceleration *
								Math.sin(Math.toRadians(player.getAngulo())) * retardment;
					else if(inclinationState==1)
						nIncX = player.getIncX() + 2*playerAcceleration *
								Math.sin(Math.toRadians(player.getAngulo())) * retardment;
					else nIncX = 0;
    				break;
        default	:	nIncY = 0;
        		  	nIncX = 0;
        		  	break;
		}
        int obsHeigh=Obstacles.get(0).getAlto();
        int obsWidth=Obstacles.get(0).getAncho();
        int playHeigh=player.getAlto();
        int playWidth=player.getAncho();
        //
        //check if player is on the bottom of the screen
        //
        if (player.getPosY()>screenHeigh-player.getAlto() && (player.getIncY()>1 || acelerationState==-1)){
        	acelerationState=0;
            player.setIncY(0);
        	player.setPosY(screenHeigh-player.getAlto());
        }
        //right part of the screen
        else if  (player.getPosX()>screenWidth-player.getAncho() && player.getIncX()>0){
        	player.setIncX(0);
        	player.setPosX(screenWidth-player.getAncho());
        }
        //left part of the screen
        else if (player.getPosX()<player.getAncho()/10 && player.getIncX()<0){
        	player.setIncX(0);
        	player.setPosX(player.getAncho()/10);       	
        }
        else if (Math.abs( Math.hypot(nIncX,nIncY) ) <= PLAYER_MAXIMUM_TOTAL_SPEED){
                player.setIncX(nIncX);
                player.setIncY(nIncY);
          }
          player.incrementaPos(retardment); // Update position in X and Y coordinates
		//
		//Check if player is touching some of the obstacles   
		//
        double playx = player.getPosX();
      	double playy = player.getPosY();
        for(int i=numObstacles-1;i>=0;i--){
        	double obx = Obstacles.get(i).getPosX();
        	double oby = Obstacles.get(i).getPosY(); 
        	boolean nearObstacle=false;
        	//check if player is under or above the obstacle
        	if(playx<obx+obsWidth && obx<playx+playWidth){
        		  //if player is under the obstacle
        		  if(playy>=oby+obsHeigh/2 && playy<=oby+obsHeigh){
        			  player.setPosY(oby+obsHeigh);
        			  player.setIncY(0);
        			  nearObstacle=true;
        		  }
        		//if player is above the obstacle
        		  else if(playy+playHeigh>oby && playy+playHeigh<oby+obsHeigh/2){
        			  player.setPosY(oby-playHeigh);
        			  player.setIncY(0);
        			  nearObstacle=true;
        		  }
        	}        	  
        	if(!nearObstacle&& playy<oby+obsHeigh && playy+playHeigh>oby){
				  //if player is at right
				  if(playx>=obx+obsWidth/2 && playx<=obx+obsWidth){
					  player.setPosX(obx+obsWidth);
					  player.setIncX(0);
				  }
				  //if player is at left
				  else if (playx+playWidth>obx && playx+playWidth<obx+obsWidth/2){
					  player.setPosX(obx-playWidth);
					  player.setIncX(0);					  
				  }
        	}

        }//END for numObstacles
        //
        //Check if there is a food object near player
        //
        for(int i=numFood-1;i>=0;i--){
        	if(Food.get(i).distancia(player)<=playWidth){
        		Food.remove(i);
        		numFood--;
        		score++;
        		infoText = ("Speed: "+ PLAYER_MAXIMUM_TOTAL_SPEED + "   Acceleration: " + playerAcceleration
        					+ "   Score: "+score);
        		//if(music)
        			//soundPool.play(idCollect, 1, 1, 1, 0, 1); //TODO
        	}
        }
        
    	if(playy<10){
  		  scoreLevels++;
  		  if(scoreLevels>=3){
  			  scoreLevels=0;
  			  //TODO end of game, show score
  		  }
  		  onSizeChanged(screenWidth, screenHeigh, screenWidth, screenHeigh);
  	}
          
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
//       super.onTouchEvent(event);
    if(!acelerateSensors){
	       float x = event.getX();
	       float y = event.getY();
	       //Log.i("XY",x+" "+y);
	       switch (event.getAction()) {
		       case MotionEvent.ACTION_DOWN:
		    	   acelerationState=1;
				    break;
		       case MotionEvent.ACTION_UP:
		    	   	acelerationState=-1;
				    break;			    
	       }
    }
    	   	    
        return true;
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){}
    
    @Override 
    public void onSensorChanged(SensorEvent event) {
    		float valorX = event.values[0];
    		float valorY = event.values[1];
    		
    		//Log.i("VALUES", "X:"+valorX+" Y:"+valorY);
    		if(valorX>2)
    			inclinationState=-1;
    		else if(valorX<-2)
    			inclinationState=1;
    		else inclinationState =0;
    		
    		if(acelerateSensors){
    		if(valorY>8)
    			acelerationState=-1;
    		else if(valorY<6)
    			acelerationState=1;
    		else inclinationState =0;
    		}
    		
    }
    
    public void activateSensors()
    {
    	mSensorManager.registerListener(this, orientationSensor,
    			SensorManager.SENSOR_DELAY_GAME);
    }
    
    public void deactivateSensors()
    {
    	mSensorManager.unregisterListener(this,orientationSensor);
    }
    
     
    public void setParent(Activity parent) {
    this.parent = parent;
    }
    
    private void exit() {
    	Bundle bundle = new Bundle();
    	bundle.putInt("score", score);
    	Intent intent = new Intent();
    	intent.putExtras(bundle);
    	parent.setResult(Activity.RESULT_OK, intent);
    	parent.finish();
    	}
}
