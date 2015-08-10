package aalto.comnet.thepreciousviewer;

import java.util.Calendar;
import java.util.Vector;

import aalto.comnet.thepreciousproject.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class TimelineView extends View {
	
		private Context appContext;
		
	   Paint paint = new Paint();
	   int alto=0;
	   int ancho=0;
	   
	   
	   public TimelineView(Context context, AttributeSet attrs) {
	          super(context, attrs);	
	          appContext = context.getApplicationContext();
	   }

	   @Override protected void onSizeChanged(int ancho, int alto,
               int ancho_anterior, int alto_anterior){
		   this.alto = alto;
		   this.ancho = ancho;
	   }
	 
	   @Override protected void onDraw(Canvas canvas) {
		   	paint.setColor(0xffeeeeee); 
		   	canvas.drawRect(ancho/3, 0, 2*ancho/3, alto, paint);
		   	
	        paint.setColor(0xff000000); 
	        paint.setTextSize(ancho/12); 
	        canvas.drawText("   0:00", 	ancho/3-ancho/4, ancho/15, paint);
	        canvas.drawText("   3:00", 	ancho/3-ancho/4, 1*alto/8+ancho/20, paint);
	        canvas.drawText("   6:00", 	ancho/3-ancho/4, 2*alto/8+ancho/30, paint);
	        canvas.drawText("   9:00", 	ancho/3-ancho/4, 3*alto/8+ancho/40, paint);
	        canvas.drawText("12:00", 	ancho/3-ancho/4, 4*alto/8, paint);
	        canvas.drawText("15:00", 	ancho/3-ancho/4, 5*alto/8-ancho/45, paint);
	        canvas.drawText("18:00", 	ancho/3-ancho/4, 6*alto/8-ancho/40, paint);
	        canvas.drawText("21:00", 	ancho/3-ancho/4, 7*alto/8-ancho/35, paint);
	        canvas.drawText("24:00", 	ancho/3-ancho/4, alto-ancho/30, paint);

	        
	        paint.setColor(0xffaaaaaa); 
	        canvas.drawText(appContext.getString(R.string.still), 	2*ancho/3+ancho/40,   ancho/12, paint);
	        paint.setColor(0xff00ff00); 
	        canvas.drawText(appContext.getString(R.string.walk), 	2*ancho/3+ancho/40, 2*ancho/12, paint);
	        paint.setColor(0xffaa00aa); 
	        canvas.drawText(appContext.getString(R.string.bicycle), 	2*ancho/3+ancho/40, 3*ancho/12, paint);
	        paint.setColor(0xff0000ff); 
	        canvas.drawText(appContext.getString(R.string.vehicle), 	2*ancho/3+ancho/40, 4*ancho/12, paint);
	        paint.setColor(0xffff0000); 
	        canvas.drawText(appContext.getString(R.string.run), 		2*ancho/3+ancho/40, 5*ancho/12, paint);
	        paint.setColor(0xff996600); 
	        canvas.drawText(appContext.getString(R.string.tilt), 	2*ancho/3+ancho/40, 6*ancho/12, paint);
	        
	        
		   	Vector<String> LogTimeline = MainActivity.getTimeline();
		   	int StopProgress=0;
		   	for (int i=0;i<LogTimeline.size();i++){
		   		//Log.i("DWF",LogTimeline.get(i));
		   		String line = LogTimeline.get(i);
		   		Long startTime= Long.parseLong(line.substring(0,line.indexOf(";")));
		   		line = line.substring(line.indexOf(";")+1);
		   		Long duration = Long.parseLong(line.substring(0,line.indexOf(";")))/1000;
		   		line = line.substring(line.indexOf(";")+1);
		   		int activityType = Integer.parseInt(line.substring(0,line.indexOf(";")));		   		
		   		
		   		if(duration>10){		   			
			   		int startH;
			   		int startM;
			   		int stopH;
			   		int stopM;
			   		if(i!=0){
			   			
			   			Calendar c = Calendar.getInstance();
				   		c.setTimeInMillis(startTime);
				   		startH = c.get(Calendar.HOUR_OF_DAY);
				   		startM = c.get(Calendar.MINUTE);
				   		c.setTimeInMillis(startTime+duration*1000);
				   		stopH = c.get(Calendar.HOUR_OF_DAY);
				   		stopM = c.get(Calendar.MINUTE);
			   		}
			   		else{
			   			startH=0;
			   			startM=0;
			   			stopH=0;
			   			stopM=0;
			   		}		   			
		   			//Log.i("HEI",startH+":"+startM+" "+duration+" "+stopH+":"+stopM+" : "+activityType);  	
		   			
			   		switch (activityType) {
						case 1	:	paint.setColor(0xffaaaaaa); break;
						case 2	:	paint.setColor(0xff00ff00); break;
						case 3	:	paint.setColor(0xffaa00aa); break;
						case 4	:	paint.setColor(0xff0000ff); break;
						case 5	:	paint.setColor(0xffff0000); break;
						case 6	:	paint.setColor(0xff777700); break;	
						default	:	paint.setColor(0xffaaaaaa); break;
					}
			   		
			        paint.setStrokeWidth(3);
			        float FStartProgress = ((float)(startH*60+startM)/(float)(24*60))*alto;			        
			        int StartProgress = (int)FStartProgress;
			        float FStopProgress = ((float)(stopH*60+stopM)/(float)(24*60))*alto;			        
			        StopProgress = (int)FStopProgress;
//			        Log.i ("CALC",(startH*60+startM)+"/"+(24*60)+"="+FStartProgress+""+activityType);
//			        Log.i ("CALC",(stopH*60+stopM)+"/"+(24*60)+"="+FStartProgress+"");
//				   	Log.i ("PROG",FStartProgress+" "+FStopProgress);
				   	//canvas.drawRect(ancho/3, 0.5*alto, 2*ancho/3,alto , paint);
				   	//canvas.drawRect(ancho/3, (int)(0.5*alto), 2*ancho/3,(int)(1*alto) , paint);
			        canvas.drawRect(ancho/3, StartProgress, 2*ancho/3,StopProgress, paint);
			       // progress = newProgress;
		   		}
		   	}
		   	
		   	paint.setColor(0xffeeeeee); 
		   	canvas.drawRect(ancho/3, StopProgress, 2*ancho/3, alto, paint);
	   }
	   
	    /**
	     * Get activity duration from date in millis since 1970
	     */    
		public String getStringDate (long date){
			int durationHours = (int)(date/3600);
			  int durationMin = (int)(date%3600/60);
			  int durationSec = (int)(date%60);
			  return durationHours + "h"+durationMin+"m"+durationSec+"s";
		}
	}