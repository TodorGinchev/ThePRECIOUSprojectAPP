package aalto.comnet.thepreciousfoodintake;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class WristbandFoodRecognition extends Activity  {
	//private final int GALLERY_REQUEST_CODE = 2222;
	private ArrayList <Bitmap> BitmapImage = new ArrayList<Bitmap>();
	private ArrayList <String> ImageName = new ArrayList<String>();
	private int mPosition = 0;
	
	private Mat detectedFoodMat=new Mat();
	private Mat outputMat = new Mat();
	private Mat userMask;
	//private Mat userMask2;
	
	private String outputString="";	

    private final int SL=40,SH=256, VL=40,VH=256;
	private final int minObjectArea = 5000; // minimum area with respect to image size
	
	List<MatOfPoint> StoredContours = new ArrayList<MatOfPoint>(); 	
	Vector <String> mapControus2Food = new Vector<String>();
	
	public static final boolean DEBUG = false;
	public static final boolean USER_MASK = false;
	public static final boolean STORE_CONTOUR = false;
	
	String a =" ";
	
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_foodintake);   
       
        getPhoto();        
        whiteBalance(detectedFoodMat);
    	detectedFoodMat.copyTo(outputMat);
    	detectFood();      
       
    }
    /**
     * 
     * 
     */
    public void getPhoto(){	
		Mat inputMat = new Mat(479, 160, CvType.CV_8U);
		try{
			File ext_storage = Environment.getExternalStorageDirectory();
			String extPath = ext_storage.getPath();
	 	    BufferedReader entrada = new BufferedReader(  new FileReader(extPath+"/precious/BTdata2.txt"));
	 	    int ch;
	 	    while((ch=entrada.read())!=-1){
		 	   
		 	   for (int i=0;i<479;i++)
		 		   for(int j=0;j<160;j++)
		 			   inputMat.put(j, i, ch);
		 	entrada.close();
	 	   }
		    Vector<Mat> channels = new Vector<Mat>(3);
		    channels.set(0, inputMat);
		    channels.set(1, inputMat);
		    channels.set(2, inputMat);
		    Core.merge(channels, inputMat);
		  	Bitmap bmpOutputProcessColor = Bitmap.createBitmap(inputMat.width(), inputMat.height(), Bitmap.Config.RGB_565);
		  	Utils.matToBitmap(inputMat, bmpOutputProcessColor);  
		  	BitmapImage.add (bmpOutputProcessColor);
		  	ImageName.add("Input after borderDetection 1 " ); 
		 }
		 catch (Exception e) {
			Log.e("getPhoto","getPhoto error",e);
		 }	
    }
    /**
     * 
     * @param view
     */
    public void detectFood(){
    	BitmapImage.clear();
    	ImageName.clear();
    	mPosition=0;	  	
//	  	Bitmap bmpOutput = bmp.copy(bmp.getConfig(), true);	
//	  	Utils.matToBitmap(input, bmpOutput);  
//	  	BitmapImage.add (bmpOutput);
//	  	ImageName.add("1) Input converter in MatRGB"); 
    	if(detectedFoodMat.empty())
    		return;
		Mat input = new Mat();	
		if(USER_MASK)
			detectedFoodMat.copyTo(input, userMask);
		else
			detectedFoodMat.copyTo(input);	
		if(DEBUG){
		  	Bitmap bmpOutputProcessColor = Bitmap.createBitmap(input.width(), input.height(), Bitmap.Config.RGB_565);
		  	Utils.matToBitmap(input, bmpOutputProcessColor);  
		  	BitmapImage.add (bmpOutputProcessColor);
		  	ImageName.add("Input after borderDetection 1 " ); 
		}
		//Apply food detection method based on HSV color filtering
		foodIntakeDetectionMethodHSV(input);  
		//
//		foodIntakeDetectionMethodRGB(input);
    }            
    /**
     *         
     * @param view
     */
    public void onSwitch(View view) {    	
    	if(BitmapImage.size()>0){
		      ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		      imageView.setImageBitmap(BitmapImage.get(mPosition));
		      TextView textView = (TextView) findViewById(R.id.textView1);
		      textView.setText(ImageName.get(mPosition));		    	
				if (mPosition==BitmapImage.size()-1)
					mPosition=0;
				else mPosition+=1;
    	}
	}     

    /**
     * 
     * @param input
     */
    private void foodIntakeDetectionMethodHSV(Mat input){
    	processColor (input, 30, 70, SL,SH,VL,VH, "green",0);
		processColor (input, 11, 50, 50,SH,VL,VH, "yellow",0);	
	  	processColor (input, 175, 20, SL,SH,VL,VH, "orange",0);
	  	processColor (input, 160, 7, SL,SH,VL,VH, "red",0);
	  	processColor (input, 40, 70, SL,SH,20,VH, "dark green",0);
	  	processColor (input, 160, 9, SL,SH,20,VH, "dark red",0);		
		if(STORE_CONTOUR)
			return;
		borderDetection(input,20,3,3);//100,3		
		if(DEBUG){
		  	Bitmap bmpOutputProcessColor2 = Bitmap.createBitmap(input.width(), input.height(), Bitmap.Config.RGB_565);	
		  	Utils.matToBitmap(input, bmpOutputProcessColor2);  
		  	BitmapImage.add (bmpOutputProcessColor2);
		  	ImageName.add("Input after borderDetection 2 " ); 
		}		
	  	processColor (input, 30, 70, SL,SH,VL,VH, "green",0);
		processColor (input, 11, 50, 50,SH,VL,VH, "yellow",0);
	  	processColor (input, 175, 20, SL,SH,VL,VH, "orange",0);
	  	processColor (input, 160, 7, SL,SH,VL,VH, "red",0);
		if(DEBUG){
		  	Bitmap bmpOutput = Bitmap.createBitmap(outputMat.width(), outputMat.height(), Bitmap.Config.RGB_565);
		  	Utils.matToBitmap(outputMat, bmpOutput);  
		  	BitmapImage.add (bmpOutput);
		  	ImageName.add(outputString); 
		}
		else{
			Bitmap bmpOutput = Bitmap.createBitmap(outputMat.width(), outputMat.height(), Bitmap.Config.RGB_565);
		  	Utils.matToBitmap(outputMat, bmpOutput);  
			ImageView imageView = (ImageView) findViewById(R.id.imageView1);imageView.setImageBitmap(bmpOutput);
		}    	
    }
    /**
     * 
     */
//    private void foodIntakeDetectionMethodRGB(Mat input){
//
//    	//Split image in R, G and B channels
//    	Vector<Mat> channels = new Vector<Mat>(3);
//    			
//		Core.split(input, channels); 
//    		      
//        Mat iR = channels.get(0);
//        Mat iG = channels.get(1);
//        Mat iB = channels.get(2);
//       
//      	/*
//      	 * First subtract R and G component. This would filter white/gray color, green color and others
//      	 */
//      	Mat iR_iG = new Mat();
//      	Core.subtract(iR, iG, iR_iG);
//      	Imgproc.threshold(iR_iG, iR_iG, 15, 255, Imgproc.THRESH_BINARY);
//      	
//      	if(DEBUG){
//    	  	Bitmap bmpiR_iG = Bitmap.createBitmap(iR_iG.width(), iR_iG.height(), Bitmap.Config.RGB_565);
//    	  	Utils.matToBitmap(iR_iG, bmpiR_iG);  
//    	  	BitmapImage.add (bmpiR_iG);
//    	  	ImageName.add("iR_iG"); 
//      	} 		  	
//      	/*
//      	 * Now subtract G and R component. This would filter white/gray color, red color and others
//      	 */	  	
//      	Mat iG_iB = new Mat();
//      	Core.subtract(iG, iB, iG_iB);
//      	Imgproc.threshold(iG_iB, iG_iB, 20, 255, Imgproc.THRESH_BINARY);
//      	
//      	if(DEBUG){
//    	  	Bitmap bmpiG_iB = Bitmap.createBitmap(iG_iB.width(), iG_iB.height(), Bitmap.Config.RGB_565);
//    	  	Utils.matToBitmap(iG_iB, bmpiG_iB);  
//    	  	BitmapImage.add (bmpiG_iB);
//    	  	ImageName.add("iG_iB"); 
//      	}
//      	
//      	iR.release();
//      	iG.release();
//      	iB.release();	  	
//      	
//    		//Now detect object contours, estimate color and map shape	  	
////      	detectObject (iR_iG, "red");
//      	iR_iG.release();
////      	detectObject (iG_iB, "green");
//      	iG_iB.release();
//      	*Hacer lo de detect object
//    }    
	 /**
	  *    
	  * @param input
	  * @param HL
	  * @param HH
	  * @param SL
	  * @param SH
	  * @param VL
	  * @param VH
	  * @param colorName
	  * @param OpenCloseSES
	  * @param meanColor
	  * @param devColor
	  */
	private void processColor ( Mat input, int HL, int HH, int SL, int SH, int VL, int VH, String colorName, int OpenCloseSES){	
	
		//Filter color spectrum
		Mat detectedColorMask = new Mat();
		if(HL<HH){
			detectedColorMask = filterColor(input, HL, HH, SL, SH, VL, VH, OpenCloseSES);
		}
		else{ //Only for orange-red-violet transition
			Mat detectedColorMask1 = new Mat();
			Mat detectedColorMask2 = new Mat();
			detectedColorMask1 = filterColor(input, HL, 179, SL, SH, VL, VH, OpenCloseSES);
			detectedColorMask2 = filterColor(input, 0, HH, SL, SH, VL, VH, OpenCloseSES);
			Core.add(detectedColorMask1, detectedColorMask2, detectedColorMask);
		}
		
		if(DEBUG){
		  	Bitmap bmpOutputProcessColor = Bitmap.createBitmap(detectedColorMask.width(), detectedColorMask.height(), Bitmap.Config.RGB_565);
		  	Utils.matToBitmap(detectedColorMask, bmpOutputProcessColor);  
		  	BitmapImage.add (bmpOutputProcessColor);
		  	ImageName.add("Detected color mask " + colorName); 
		}
	
	    //
	    // Find contours
	    //Objects inside other objects are ignored: 
	    //	http://docs.opencv.org/trunk/doc/py_tutorials/py_imgproc/py_contours/py_contours_hierarchy/py_contours_hierarchy.html 
	    //TODO	Find out if there are objects inside the object
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();   
	    Mat hierarchy=new Mat();
	
	    Mat detectedColorCopy = detectedColorMask.clone();
	    Imgproc.findContours(detectedColorCopy, contours,hierarchy, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);
	
	    for(int i=0; i< contours.size();i++){
	    	double contourArea=Imgproc.contourArea(contours.get(i));
	    	//Look for objects with are bigger than minObjectArea pixels 
	        if (contourArea > minObjectArea ){
	        	//Fill the contour with white in order to create a mask
	        	Mat contourFound = new Mat(input.rows(), input.cols(), CvType.CV_8U);
	        	Imgproc.drawContours(contourFound, contours, i, new Scalar(255),-1);
	        	//Copy the detected object in a new matrix
	        	Mat detectedObject= new Mat();
	        	input.copyTo(detectedObject, contourFound);
	        	//Extract the detected object from the image
	        	Rect BBrect = Imgproc.boundingRect(contours.get(i));
	        	detectedObject = detectedObject.submat(BBrect);
	        	
	        	if(DEBUG){
		          	Bitmap bmpOutputContours = Bitmap.createBitmap(detectedObject.width(), detectedObject.height(), Bitmap.Config.RGB_565);	
		          	Utils.matToBitmap(detectedObject, bmpOutputContours);  
		          	BitmapImage.add (bmpOutputContours);
		          	ImageName.add("detectedObject "+colorName); 
	        	}
	        	          	
	        	double [][] MeanDev;
	        	Imgproc.cvtColor(detectedObject, detectedObject, Imgproc.COLOR_RGB2HSV);
	        	if(colorName.equals("orange")||colorName.equals("red")||colorName.equals("dark red"))
	        		MeanDev=calcHueMeanStdDev(detectedObject,true);
        		else
        			MeanDev=calcHueMeanStdDev(detectedObject,false);
	        	//TODO filter by sat and hue?
	        	Log.i("FOOD_INTAKE","Object "+i+" Sat= "+MeanDev[0][1]+" Value="+MeanDev[0][2]);        	
	        	if(MeanDev[0][1]<50 || MeanDev[0][2]<50){  //if(mean.get(1,0)[0]<100 || mean.get(2,0)[0]<100){  
	        		Log.i("FOOD_INTAKE","Object "+i+"IGNORING Sat= "+MeanDev[0][1]+" Value="+MeanDev[0][2]);
	        		continue;
	        	}	        	
	        		
//	        	double ColorDeviation = Math.abs(meanColor-MeanDev[0][0]);
//	        	if (ColorDeviation>90)
//	        		ColorDeviation  = 180-ColorDeviation;
//	        	if(ColorDeviation>devColor){
//	        		Log.i("TAG","IGNORING meanColor="+meanColor+" meanImage="+MeanDev[0][0]+" devColor="+devColor);
//	        		continue;
//	        	}   
	        	
	        	/*
	        	 * If the standard deviation is high, two colors have been detected and one should be filtered.
	        	 * Lets now calculate the histogram of the Hue channel. After that, lets find
	        	 * the most common value. Finally, lets calculate the 20dB BW.
	        	 */
	        	if(MeanDev[1][0]>4){
	        		filterSpectrumBW(detectedObject, colorName, i);
	        	}  
	        	/*
	        	 *  Calculate matching 
	        	 */	        	
	        	double[] matching = new double [StoredContours.size()];
	        	int []matchedContourDir= new int [StoredContours.size()];//Where in the map contours file is located the better matched contour (line-1=direction)
	        	for ( int count=0; count<StoredContours.size();count++){
	        		matching[count] =Imgproc.matchShapes(contours.get(i), StoredContours.get(count), 1, 0.0);	
	        		matchedContourDir[count] = count;
	        		Log.i("FOOD_INTAKE","Matching "+colorName+" "+count+" = "+matching[count]);
	        	}  	
	        	double[] matching_aux = matching.clone();	  
	        	int[] matchedContourDir_aux = matchedContourDir.clone();
	        	for ( int count=0; count<StoredContours.size();count++){
	        		double [] minLoc = new double [2];
	        		minLoc = findMinLoc(matching_aux);
	        		matching[count]=minLoc[0];
	        		matchedContourDir[count]=matchedContourDir_aux[(int)minLoc[1]];
	        		matching_aux[(int)minLoc[1]]=999;
	        	}
	        	for ( int count=0; count<StoredContours.size();count++){
	        		if(matching[count]>0.5)
	        			break;
	        		Log.i("FOOD_INTAKE","Matching: "+count+" "+matching[count]+" "+matchedContourDir[count]);
	        		if( 	(mapControus2Food.get(matchedContourDir[count]).equals("banana") && colorName.equals("yellow") && matching[count]<0.35 )
	    	        		|| 	(mapControus2Food.get(matchedContourDir[count]).equals("lemon") && colorName.equals("yellow") && matching[count]<0.1 )
	    	        		||  (mapControus2Food.get(matchedContourDir[count]).equals("apple")  && !colorName.equals("orange") && !colorName.equals("yellow") && matching[count]<0.1 )
	    	        		||  (mapControus2Food.get(matchedContourDir[count]).equals("cucumber")  && (colorName.equals("green") ||colorName.equals("dark green"))
	    	        					&& matching[count]<0.1) ){
	    	        		
	    		        		outputString=outputString.concat(" "+colorName+" "+mapControus2Food.get(matchedContourDir[count])+";");
	    		                Scalar rectColor = (colorName.equals("red"))? new Scalar(255,0,0) : (colorName.equals("yellow"))? new Scalar(0,0,255) :
	    		            		(colorName.equals("green"))? new Scalar(0,255,0) : (colorName.equals("orange"))? new Scalar(255,128,0) : 
	    		        			new Scalar(0,0,255);
	    		        		Imgproc.drawContours(outputMat, contours, i, rectColor);
	    		        		Mat aux = new Mat();
	    		        		input.copyTo(aux, contourFound);
	    		        		Core.subtract(input, aux, input);
	    		        		aux.release();
	    		        		break;
	    	        	}
	        	}

	    	  	Bitmap bmpOutputFInal = Bitmap.createBitmap(outputMat.width(), outputMat.height(), Bitmap.Config.RGB_565);
	    	  	Utils.matToBitmap(outputMat, bmpOutputFInal);  
	
	    	  	
			    ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			    imageView.setImageBitmap(bmpOutputFInal);
			    TextView textView = (TextView) findViewById(R.id.textView1);
			    textView.setText(outputString);	
	            
	        }//End if (Imgproc.contourArea(contours.get(i)) > 1000)        
	    }//End loop contours      
	}
	/**
	 * 
	 * @param image
	 * @param hL
	 * @param hH
	 * @param sL
	 * @param sH
	 * @param vL
	 * @param vH
	 * @param OpenCloseSES
	 * @return
	 */
	private Mat filterColor(Mat image, int hL,int hH,int sL,int sH,int vL,int vH, int OpenCloseSES){
		//Convert RGB matrix into HSV matrix
		Mat hsvimage = new Mat(image.cols(),image.rows(),CvType.CV_8UC3);
		hsvimage.setTo(new Scalar(0,0,0));
		Imgproc.cvtColor(image, hsvimage, Imgproc.COLOR_RGB2HSV);
		Mat binImage = new Mat();     
		Core.inRange(hsvimage, new Scalar(hL, sL, vL), new Scalar(hH, sH, vH), binImage); //Threshold the image
		
		if (OpenCloseSES>0){
			//morphological opening (remove small objects from the foreground)
			Imgproc.erode(binImage, binImage, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(OpenCloseSES, OpenCloseSES)) );
			Imgproc.dilate( binImage, binImage, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(OpenCloseSES, OpenCloseSES)) ); 
			//morphological closing (fill small holes in the foreground)
			Imgproc.dilate( binImage, binImage, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(OpenCloseSES, OpenCloseSES)) ); 
			Imgproc.erode(binImage, binImage, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(OpenCloseSES, OpenCloseSES)) );
		}
		return binImage;	
	}
	/**
	 * 
	 * @param image
	 * @param threshold
	 * @param EEsize
	 * @param BlurSize
	 * @return
	 */
	public Mat borderDetection (Mat image, int threshold, int EEsize, int BlurSize){	
		//Mat output=image;//Store input image
		int ratio = 3;	
		/// Reduce noise with a kernel 3x3	
		Mat detected_edges=image.clone();
		
		if(BlurSize>0)
			Imgproc.blur( image, detected_edges, new Size(BlurSize,BlurSize) );
	
		/// Canny detector
		Imgproc.Canny( detected_edges, detected_edges, (double)threshold, (double)threshold*ratio);
	
		if (EEsize>0)
			Imgproc.dilate( detected_edges, detected_edges, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(EEsize, EEsize)) ); 
	
		//Delete corners from the original image
		double []border = new double[4];
		border[0]=0;border[1]=0;border[2]=0;border[3]=0;
		
		for (int x=0;x<image.rows();x++)
			for (int y=0;y<image.cols();y++){
				double []data = detected_edges.get(x,y);
				boolean Delete = (int)data[0]!=0;
				if(Delete){	
					image.put(x,y,border);
				}
			} 	
		return image;
	}
	/**
	 * 
	 */
//	@Override
//	public boolean onTouchEvent (MotionEvent event) {
//			if(!USER_MASK)
//				return true;
//			Button bDetectFood = (Button) findViewById(R.id.button3);
//			if(event.getAction()==MotionEvent.ACTION_DOWN){
//		    	userMask= Mat.zeros(detectedFoodMat.rows(), detectedFoodMat.cols(), detectedFoodMat.type());
//		    	userMask2= new Mat();
//		    	detectedFoodMat.copyTo(userMask2);	    	
//	//			TextView textView = (TextView) findViewById(R.id.textView1);
//	//		    textView.setText("NO");
//			}
//			else if (event.getAction()==MotionEvent.ACTION_UP){
//			    Bitmap bmpInput = bmp;
//			    Mat output = new Mat();
//			    Core.divide(userMask, new Scalar (5,5,5), output);
//			    Core.add(detectedFoodMat, output, output);
//			    try{
//				    Utils.matToBitmap(output, bmpInput);
//				    ImageView imageView = (ImageView) findViewById(R.id.imageView1);
//					imageView.setImageBitmap(bmpInput);
//			    }catch (Exception e){};
//				
//			    //View view = (View)findViewById(R.id.button1);
//			    detectFood ();
//	//			TextView textView = (TextView) findViewById(R.id.textView1);
//	//		    textView.setText("YES");		    
//			}		
//			else if (bDetectFood.getVisibility()== View.VISIBLE){
//				int touchX = (int)event.getX();
//				int touchY = (int)event.getY();
//				ImageView imageView = (ImageView) findViewById(R.id.imageView1);
//				int imageLocation [] = new int [2];
//				imageView.getLocationOnScreen(imageLocation);		
//				if(touchX>imageLocation[0] && touchY>imageLocation[1]
//					&& touchX<imageLocation[0]+imageView.getWidth() && touchY<imageLocation[1]+imageView.getHeight()){
//	//				TextView textView = (TextView) findViewById(R.id.textView1);
//	//			    textView.setText("YES");
//				    double centerX = (touchX-imageLocation[0])*detectedFoodMat.width()/imageView.getWidth();
//				    double centerY = (touchY-imageLocation[1])*detectedFoodMat.height()/imageView.getHeight();
//				    Point center = new Point (centerX,centerY);
//				    //Point center = new Point((double)touchX-imageLocation[0],(double)touchY-imageLocation[1]);
//				    Core.circle(userMask, center, 50, new Scalar(255,255,255),-1);
//				    Core.circle(userMask2, center, 50, new Scalar(255,255,255),-1); 
//				    Bitmap bmpInput = bmp;
//				    Utils.matToBitmap(userMask2, bmpInput);
//					imageView.setImageBitmap(bmpInput);
//				}
//	//			else{
//	//				TextView textView = (TextView) findViewById(R.id.textView1);
//	//			    textView.setText("NO");
//	//			}
//			}
//	    return true;
//	}
	/**
	 * White balance
	 * Documentation: http://web.stanford.edu/~sujason/ColorBalancing/simplestcb.html#nogo
	 */
	public void whiteBalance (Mat input){	
		//TODO find a faster way
	  	//
		//Split image in 3 matrixes containing each RGB color and reshape each matrix to 1-row matrix
		//
	    Vector<Mat> channels = new Vector<Mat>(3);
	    Core.split(input, channels);   
	    Mat reshapedR = channels.get(0).reshape(1,1);
	    Mat reshapedG = channels.get(1).reshape(1,1);
	    Mat reshapedB = channels.get(2).reshape(1,1);
	    
	    //Sort data and look for quantiles with probability 0.5% and 0.95%    
	    Core.sort(reshapedR, reshapedR, Core.SORT_ASCENDING);
	    int n_min = (int) Math.round(reshapedR.cols() * 0.005);
	    double satR_min=reshapedR.get(0, n_min)[0];
	    int n_max = (int) Math.round(reshapedR.cols() * 0.995);
	    double satR_max=reshapedR.get(0, n_max)[0];
	    
	    Core.sort(reshapedG, reshapedG, Core.SORT_ASCENDING);
	    n_min = (int) Math.round(reshapedG.cols() * 0.005);
	    double satG_min=reshapedG.get(0, n_min)[0];
	    n_max = (int) Math.round(reshapedG.cols() * 0.995);
	    double satG_max=reshapedG.get(0, n_max)[0];
	    
	    Core.sort(reshapedB, reshapedB, Core.SORT_ASCENDING);
	    n_min = (int) Math.round(reshapedB.cols() * 0.005);
	    double satB_min=reshapedB.get(0, n_min)[0];
	    n_max = (int) Math.round(reshapedB.cols() * 0.995);
	    double satB_max=reshapedB.get(0, n_max)[0];
	    
	    //Saturate    
		double bottomR = reshapedR.get(0, 0)[0];
		double topR = reshapedR.get(0,reshapedR.cols()-1)[0];
		double bottomG = reshapedG.get(0, 0)[0];
		double topG = reshapedG.get(0,reshapedG.cols()-1)[0];
		double bottomB = reshapedB.get(0, 0)[0];
		double topB = reshapedB.get(0,reshapedB.cols()-1)[0];
	    Log.i("FOOD_INTAKE_whiteBalance"," "+bottomR+" "+topR+" "+bottomG+" "+topG+" "+bottomB+" "+topB);
	    for (int i=0;i<input.cols();i++)
	    	for(int j=0;j<input.rows();j++){
	    		double []data=input.get(j, i);
	    		
				if(data[0]<satR_min)
					data[0]=satR_min;
				else if(data[0]>satR_max)
					data[0]=satR_max;
				data[0] = (data[0]-bottomR)*255/(topR-bottomR);	
				
				if(data[1]<satG_min)
					data[1]=satG_min;
				else if(data[1]>satG_max)
					data[1]=satG_max;
				data[1] = (data[1]-bottomG)*255/(topG-bottomG);
				
				if(data[2]<satB_min)
					data[2]=satB_min;
				else if(data[2]>satB_max)
					data[2]=satB_max;  			
				data[2] = (data[2]-bottomB)*255/(topB-bottomB);
				
	    		input.put(j, i, data);
	    	}    
	    if(DEBUG){
		    Bitmap bmpDetectedColor2= Bitmap.createBitmap(input.width(), input.height(), Bitmap.Config.RGB_565);	
		    Utils.matToBitmap(input, bmpDetectedColor2);
		    BitmapImage.add (bmpDetectedColor2);
		    ImageName.add("white balance2");    
	    }
	}
	/**
	 * 
	 * @param input
	 * @return
	 */
	public MatOfDouble cvtRGB2HSV (MatOfDouble input){
		double iR = input.get(0, 0)[0];
		double iG = input.get(1, 0)[0];
		double iB = input.get(2, 0)[0];
		if(iR==iG && iR==iB)
			return input; 
		double max = (iR>iG && iR>iB)? iR : (iG>iB)? iG : iB;
		double min = (iR<iG && iR<iB)? iR : (iG<iB)? iG : iB;
		double c = max-min;
		double  oH, oS, oV ;
		
		if(max==iR)
			oH = (Math.abs(iG-iB)/c) %6;
		else if (max==iG)
			oH = (iB-iR)/c + 2;
		else if (max==iB)
			oH = (iR-iG)/c + 4;
		else oH=-1;
		
		oH = oH * 30;	
		oV = max;
		oS = (max==0)? 0  : 255*c/max;	
		double []outputD = {oH,oS,oV}; 
		input.put(0, 0, outputD);	
		return input;
	}
	/**
	 * 
	 * @param data
	 * @param fileName
	 * @return
	 */
	public int writeStingInExternalFile(String data, String fileName){
		int fileSize=0;
	    try {
	    	if(isExternalStorageWritable()){
	    		 File ext_storage = Environment.getExternalStorageDirectory();
	    		 String extPath = ext_storage.getPath();
	    		 File folder = new File(extPath+"/foodDetector");
	    		 boolean success = false;
	    		 if(!folder.exists())
	    			 success = folder.mkdir();
	    		 if(folder.exists() || success){
		      	     File file = new File (folder, fileName);
		      	     if(!file.exists()){
		      	     	file.createNewFile();
		      	     }
		      	     FileOutputStream f = new FileOutputStream(file, true);
					 String texto = data;
					 f.write(texto.getBytes());
					 f.close();
		             Log.i("FOOD_INTAKE_writeStingInExternalFile"+fileName, "Stored "+data);
		             fileSize=(int)file.length();
	    		 }
	    		 else Log.e("FOOD_INTAKE_writeStingInExternalFile","unable to create folder or file");
	    	}
	    } catch (Exception e) {
	           Log.e("FOOD_INTAKE_writeStingInExternalFile", e.getMessage(), e);
	    }
	    return fileSize;
	}
	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
	    String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    }
	    return false;
	}
//	/**
//	 * TODO
//	 * TODO
//	 * TODO
//	 * Vamos a ver... o sea, estoy usando substring y indexOf sobre un string super largo un montón de veces
//	 * cómo espero que se ejecute en menos de 100ms? MAAAAAAL
//	 */
//	public boolean readFileContours(String fileName){
//		Log.i("FOOD_INTAKE_readFileContours","Reading file"+fileName);
//		MatOfPoint mop=new MatOfPoint(); 
//		  //Read File
//		  long timeStamp = System.currentTimeMillis();	  
//		  String line = "";
//		  try {
//		    		 File ext_storage = Environment.getExternalStorageDirectory();
//		     		 String extPath = ext_storage.getPath();
//		     		 File folder = new File(extPath);
//		     		 if(!folder.exists())
//		     			 return false;
//		     		 if(folder.exists()){
//			      	     File file = new File (folder, fileName);
//			      	     if(!file.exists())
//		      	     		return false;
//			      	     FileInputStream f = new FileInputStream(file);
//			      	     BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
//			      	     String aux_line;
//			    		 while ((aux_line = entrada.readLine()) != null) {
//				    		 line = aux_line;
//			    		 }
//			    		 f.close();
//		     		 }    		 
//	 		 } catch (Exception e){ }//TODO handle exception 
//		try{
//			int size = Integer.parseInt(line.substring(0,line.indexOf(";")));
//			
//			for(int i=0;i<size;i++){
//				line = line.substring(line.indexOf(";")+1);
//				double []data = new double [2];
//				data[0] = Double.parseDouble(line.substring(0,line.indexOf(",")));
//				line = line.substring(line.indexOf(",")+1);
//				data[1] = Double.parseDouble(line.substring(0,line.indexOf(";")));
//				Mat aux = Mat.zeros(1, 1,CvType.CV_32SC2);
//				aux.put(0, 0, data);
//				mop.push_back(aux);			
//			}	
//			StoredContours.add(mop);
//		}catch (Exception e){
//			Log.e("readFileContours","Bad file format",e);
//		}	
//		Log.i("FOOD_INTAKE_readFileContours","TIME readFileContours"+(System.currentTimeMillis()-timeStamp));	
//		return true;
//	}
	
	/**
	 * 
	 * 
	 */
	public void readAssetsContours(){
		long timeStamp = System.currentTimeMillis();	
	       MatOfPoint mop=new MatOfPoint();
	       AssetManager assetManager = getAssets();
	    // To get names of all files inside the "contours" folder
	       try {
//	    	   /*
//	    	    * Read contours
//	    	    */
	    	   String[] files = assetManager.list("contours");
	           InputStream input;
	           for(int i=0; i<files.length; i++){               
	        	   Log.i("ASSET contours FILES",("\n File :"+i+" Name => "+files[i]));
	        	   input = assetManager.open("contours/"+files[i]);        	  
	        	   int buffer_size = input.available();
	        	   byte[] buffer = new byte[buffer_size];
	        	   input.read(buffer);
	        	   input.close();  
	        	   // byte buffer into a string
	        	   String line = new String(buffer); 
	        	   try{
	        		   int size = Integer.parseInt(line.substring(0,line.indexOf(";")));	      			
	        		   for(int j=0;j<size;j++){
        			   		line = line.substring(line.indexOf(";")+1);
    			   			double []data = new double [2];
    			   			data[0] = Double.parseDouble(line.substring(0,line.indexOf(",")));
    			   			line = line.substring(line.indexOf(",")+1);
    			   			data[1] = Double.parseDouble(line.substring(0,line.indexOf(";")));
    			   			Mat aux = Mat.zeros(1, 1,CvType.CV_32SC2);
    			   			aux.put(0, 0, data);
		      				mop.push_back(aux);			
	        		   }	
	        		   StoredContours.add(mop);
	        		   mop = new MatOfPoint();
	      		 	}catch (Exception e){
	      		 		Log.e("readFileContours","Bad file format",e);
	      		  	}	
	             Log.i("FOOD_INTAKE_readFileContours","TIME readFileContours"+(System.currentTimeMillis()-timeStamp));	
	           }
//	           InputStream input;
	           /*
	            * Read contours
	            */	           
//	           String line="";
//	           InputStream input = assetManager.open("contours.txt");    
//        	   BufferedReader entrada = new BufferedReader(  new InputStreamReader(input));
//        	   while ((line = entrada.readLine()) != null) {
//        		   //Log.i("LINE",line);
//        		   try{
//	        		   int size = Integer.parseInt(line.substring(0,line.indexOf(";")));	      			
//	        		   for(int j=0;j<size;j++){
//        			   		line = line.substring(line.indexOf(";")+1);
//    			   			double []data = new double [2];
//    			   			data[0] = Double.parseDouble(line.substring(0,line.indexOf(",")));
//    			   			line = line.substring(line.indexOf(",")+1);
//    			   			data[1] = Double.parseDouble(line.substring(0,line.indexOf(";")));
//    			   			Mat aux = Mat.zeros(1, 1,CvType.CV_32SC2);
//    			   			aux.put(0, 0, data);
//		      				mop.push_back(aux);			
//	        		   }	
//	        		   StoredContours.add(mop);
//	      		 	}catch (Exception e){
//	      		 		Log.e("readFileContours","Bad file format",e);
//	      		  	}	
//        	   }
//        	   input.close();
	           /*
	            * Read map
	            */	  
	           String line = "";
        	   input = assetManager.open("map.txt");    
        	   BufferedReader entrada = new BufferedReader(  new InputStreamReader(input));
        	   while ((line = entrada.readLine()) != null) {
        		  // Log.i("LINE",line);
        		   mapControus2Food.add(line);
        	   }
        	   input.close();  
	           
	           
	       } catch (IOException e1) {
	           // TODO Auto-generated catch block
	           e1.printStackTrace();
	       }
	}
//	/**
//	 * 
//	 * @param fileName
//	 * @return
//	 */
//	public boolean readMap(String fileName){
//		Log.i("FOOD_INTAKE_readMap","Reading file"+fileName);
//		  //Read File
//		  long timeStamp = System.currentTimeMillis();	 
//		  try {
//	    		 File ext_storage = Environment.getExternalStorageDirectory();
//	     		 String extPath = ext_storage.getPath();
//	     		 File folder = new File(extPath);
//	     		 if(!folder.exists())
//	     			 return false;
//	     		 if(folder.exists()){
//		      	     File file = new File (folder, fileName);
//		      	     if(!file.exists())
//	      	     		return false;
//		      	     FileInputStream f = new FileInputStream(file);
//		      	     BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
//		      	     String aux_line;
//		    		 while ((aux_line = entrada.readLine()) != null) {
//		    			 mapControus2Food.add(aux_line);
//		    		 }
//		    		 f.close();
//	     		 }    		 
//	 		 } catch (Exception e){
//	 			Log.e("readMap","",e); 
//	 		 }
//		Log.i("TIME readMap",""+(System.currentTimeMillis()-timeStamp));	
//		return true;
//	}
	/**
	 * Takes an input image in RGB format, converts it in HSV format and calculates mean and standard deviation of the Hue component
	 * When remapHue is true, the hue range is remapped from [0,180] to [90,270]
	 */
	public double[][]  calcHueMeanStdDev (Mat input, boolean remapHue) {	
		MatOfDouble mean = new MatOfDouble();// = Core.mean(hsvimage, contourFound).val;        	
		MatOfDouble stdDev = new MatOfDouble();		
		/*	We want to calculate mean and standard deviation. In HSV, red color may have hue range
		*	from 0 to 5 and from 175 to 180. If we calculate directly the mean and stddev, the results
		*	will be wrong. Lets then translate the hue range 0-90 to 180-270, in order to have a
		*	a total range of 90-270. After that, we can subtract 90 from the calculated mean. The
		*	standard deviation will be the same.
		*/
		if(remapHue){
		    Vector<Mat> channels = new Vector<Mat>(3);
		    Core.split(input, channels);   
		    Mat Hue = channels.get(0);
		    //double []hueVal;
		    
		    Mat HueThres = new Mat();
		    Imgproc.threshold(Hue, HueThres, 90, 180, Imgproc.THRESH_BINARY_INV);
		    Core.add(Hue, HueThres, Hue); 
		    Mat aux = new Mat();
		    Core.merge(channels, aux);
				    Mat Value = channels.get(2);
				    Mat aux_mask = new Mat();
				    Imgproc.threshold(Value, aux_mask, 5, 255, Imgproc.THRESH_BINARY);
		    Core.meanStdDev(aux, mean, stdDev,aux_mask);//, aux_mask);        	    
		    if(mean.get(0, 0)[0]>180){
		    	double[] realMean = new double[1];
		    	realMean[0] = mean.get(0, 0)[0] - 180;
		    	mean.put(0, 0, realMean);
		    }
		}
		else {
			Vector<Mat> channels = new Vector<Mat>(3);
		    Core.split(input, channels);   
			    Mat Value = channels.get(2);
				Mat aux_mask = new Mat();
				Imgproc.threshold(Value, aux_mask, 5, 255, Imgproc.THRESH_BINARY);
			Core.meanStdDev(input, mean, stdDev,aux_mask);//, aux_mask);
		}
		double [][]output = new double[2][3];
		output[0][0]=mean.get(0, 0)[0];
		output[0][1]=mean.get(1, 0)[0];
		output[0][2]=mean.get(2, 0)[0];
		output[1][0]=stdDev.get(0, 0)[0];	
		output[1][1]=stdDev.get(1, 0)[0];
		output[1][2]=stdDev.get(2, 0)[0];		
		return output;
	}
	/**
	 * 
	 * @param array
	 * @return
	 */
	private double [] findMinLoc (double[] array){
		double min = 999;
		int location = -1;
		for (int i=0; i<array.length;i++)
			if(array[i]<min){
				min=array[i];
				location=i;
			}
		double [] out = new double[2];
		out[0]=min;
		out[1]=(double)location;
		return out;
	}
	/**
	 * 
	 * @param detectedObject
	 * @param colorName
	 * @param i
	 */
	public void filterSpectrumBW (Mat detectedObject, String colorName, int i){
		
		//Get the Hue Sat and Val channels from the detectedObject matrix
		List<Mat> channels = new Vector<Mat>(3);
		Core.split(detectedObject, channels); 
		
		//Get only the Hue channel, it contains the color spectrum information
		List<Mat> matList = new LinkedList<Mat>();
		matList.add(channels.get(0));
		//Calculate histogram
		Mat histogram = new Mat();
		MatOfFloat ranges=new MatOfFloat(0,256);
		Imgproc.calcHist(
		        matList, 
		        new MatOfInt(0), 
		        new Mat(), 
		        histogram , 
		        new MatOfInt(256), 
		        ranges);
		
		Log.i("FOOD_INTAKE","Object "+i+":"+"histogram= "+histogram.dump());
		histogram.put(0, 0, 0);
		//Search for the maximum Hue value
		Core.MinMaxLocResult minMax=Core.minMaxLoc(histogram);
		Log.i("FOOD_INTAKE","Object "+i+" Max="+  minMax.maxVal+" Loc="+minMax.maxLoc);
		//            Log.i("FOOD_INTAKE","Object "+i+" His Size="+ histogram.size());
		//            double[] aaaaa = histogram.get(27,0);
		//            Log.i("FOOD_INTAKE","Object "+i+" His val="+ aaaaa[0]);
		
		//Obtain BW
		//Do not take into account Hue=0 or Hue=179, start from 1 or from 178
		int Hfreq = (int)minMax.maxLoc.y;
		do{
			if(Hfreq>=179)
				Hfreq=1;
			else
				Hfreq++;
		}while ( (histogram.get(Hfreq,0)[0]> (minMax.maxVal/25)) &&
				( (histogram.get(Hfreq,0)[0]>(minMax.maxVal/10)) || (histogram.get(Hfreq+1,0)[0]>(histogram.get(Hfreq,0)[0])) ) );            
		int Lfreq = (int)minMax.maxLoc.y;
		do{
			if(Lfreq<=1)
				Lfreq=178;
			else
				Lfreq--;
		}while ( (histogram.get(Lfreq,0)[0]> (minMax.maxVal/25)) &&
				( (histogram.get(Lfreq,0)[0]>(minMax.maxVal/10)) || (histogram.get(Lfreq+1,0)[0]>(histogram.get(Lfreq,0)[0])) ) );
		Log.i("FOOD_INTAKE","Object "+i+" BW= "+Hfreq+"-"+Lfreq);
		
		//Convert to RGB color space again
		Imgproc.cvtColor(detectedObject, detectedObject, Imgproc.COLOR_HSV2RGB);
		            	
			
		/*
		 * Filter color spectrum according to max Hue location and BW
		 */
		int OpenCloseSES=0;
		Mat detectedColorMask = new Mat();
		
		if(Lfreq<Hfreq){
			detectedColorMask = filterColor(detectedObject, Lfreq, Hfreq, SL, SH, VL, VH, OpenCloseSES);
		}
		else{ 
			//Only for orange-red-violet transition
			Mat detectedColorMask1 = new Mat();
			Mat detectedColorMask2 = new Mat();
			detectedColorMask1 = filterColor(detectedObject, Lfreq, 179, SL, SH, VL, VH, OpenCloseSES);
			detectedColorMask2 = filterColor(detectedObject, 0, Hfreq, SL, SH, VL, VH, OpenCloseSES);
			Core.add(detectedColorMask1, detectedColorMask2, detectedColorMask);
		}
		
	//	if(DEBUG){
	//	  	Bitmap bmpOutputProcessColor = Bitmap.createBitmap(detectedObject.width(), detectedObject.height(), Bitmap.Config.RGB_565);
	//	  	Utils.matToBitmap(detectedColorMask, bmpOutputProcessColor);  
	//	  	BitmapImage.add (bmpOutputProcessColor);
	//	  	ImageName.add("Detected color mask " + colorName); 
	//	}	   		    		
		
		Mat detectedColorMaskout = new Mat();
		detectedObject.copyTo(detectedColorMaskout,detectedColorMask);
		if(DEBUG){
		  	Bitmap bmpOutputProcessColor2 =  Bitmap.createBitmap(detectedColorMaskout.width(), detectedColorMaskout.height(), Bitmap.Config.RGB_565);
		  	Utils.matToBitmap(detectedColorMaskout, bmpOutputProcessColor2);  
		  	BitmapImage.add (bmpOutputProcessColor2);
		  	ImageName.add("Detected color mask out " + colorName); 
		}
	}
	
	class MiTask extends AsyncTask<Integer, Void, Integer> {

	    @Override
	    protected Integer doInBackground(Integer... n) {
	    	readAssetsContours(); 
	           return 0;
	    }
	    @Override
	    protected void onPostExecute(Integer res) {
	           //salida.append(res + "\n");
	    }
	}
	 public void saveInfo(View view){
		 Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
		 finish();
	 }
}
