package aalto.comnet.thepreciousfoodintake;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//TODO CHANGE NON-FINAL VARIABLES INITIALIZATION, initialize them somewhere else



public class MainActivity extends Activity {
	
	private Uri imageUri;
	
	private ArrayList <Bitmap> BitmapImage = new ArrayList<Bitmap>();
	private ArrayList <String> ImageName = new ArrayList<String>();
	private int mPosition = 0;
	
	private Bitmap bmp;
	private Mat detectedFoodMat=new Mat();
	private Mat outputMat = new Mat();
	private Mat userMask;
	private Mat userMask2;
	
	private String outputString="";
	

    private final int SL=40,SH=256, VL=40,VH=256;
//	private final int 	HLorange=5, HHorange=22, //HLorange=0, HHorange=22, 
//						HLyellow=20, HHyellow=25,//38//HLyellow=22, HHyellow=38,
//						HLgreen=25, HHgreen=75,//38
//						HLyellow_green=22, HHyellow_green=61,
//						HLblue=75, HHblue=130,
//						Hlviolet=130, HHviolet=160,
//						//HLbrown=5, HHbrown=17,
//						HLred=160, HHred=11;
//	private final int OpenCloseSES=0; //Structure Element Size of the morphological opening and closing
	private final int minObjectArea = 5000; // minimum area with respect to image size
	
	List<MatOfPoint> StoredContours = new ArrayList<MatOfPoint>(); 	
	Vector <String> mapControus2Food = new Vector<String>();
	
	public static final boolean DEBUG = true;
	public static final boolean USER_MASK = false;
	public static final boolean STORE_CONTOUR = false;
	
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        }
    }   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_foodintake);
     
        //Select a picture
        Button bSelectImage = (Button) findViewById(R.id.button1);
        bSelectImage.setOnClickListener( new OnClickListener() {
            public void onClick(View v) {
            	BitmapImage.clear();
            	ImageName.clear();
            	outputString="";
//            	final Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);        	 
//                startActivityForResult(galleryIntent,GALLERY_REQUEST_CODE);
            	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            	imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"precious" +        
            	                        String.valueOf(System.currentTimeMillis()) + ".jpg"));
            	intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
            	startActivityForResult(intent, 0);
             }
              });
        
        if(DEBUG){
        	Button bNextImage = (Button) findViewById(R.id.button2);
        	bNextImage.setVisibility(View.VISIBLE);
        }
        	
        //Load contour database
        boolean moreFileContrours = false;
        int count=0;
        String fileName;
        do{
        	fileName="/foodDetector/contours/"+count+".txt";
        	count++;
        	moreFileContrours = readFileContours(fileName);        	
        } while (moreFileContrours);
       readMap("/foodDetector/contours/map.txt");


    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG",""+requestCode);
        // To Handle Camera Result
        if (resultCode==RESULT_OK && requestCode == 0) {
        	//Get the photo
        	bmp = BitmapFactory.decodeFile(imageUri.getPath());
        	//Delete photo from memory
			new File (imageUri.getPath()).delete();			
			
			
			//resize image to more suitable size
			if(bmp.getWidth()>bmp.getHeight()){            	
				int scaleFactor = bmp.getWidth()/640;
				scaleFactor = (scaleFactor<1)? 1 : scaleFactor;
				bmp=Bitmap.createScaledBitmap(bmp, bmp.getWidth()/scaleFactor, bmp.getHeight()/scaleFactor, false);            	
			}
			else{
				int scaleFactor = bmp.getHeight()/640;
				scaleFactor = (scaleFactor<1)? 1 : scaleFactor;
				bmp=Bitmap.createScaledBitmap(bmp, bmp.getWidth()/scaleFactor, bmp.getHeight()/scaleFactor, false);
			}
            
            Utils.bitmapToMat(bmp, detectedFoodMat);             
        	detectedFoodMat.copyTo(outputMat);
            ImageView imageView = (ImageView)(findViewById(R.id.imageView1));
            imageView.setImageBitmap(bmp);
            Button button3 = (Button) findViewById(R.id.button3);
        	button3.setVisibility(View.VISIBLE);  
        	TextView textView = (TextView) findViewById(R.id.textView1);
        	textView.setText("");
			
        }
    else if (resultCode == RESULT_CANCELED) {
        Toast.makeText(this, getString(R.string.picture_not_loaded), Toast.LENGTH_SHORT).show();
       }         
    }
    
    
    public void OnClickDetectFood(View view){
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
		Mat inputWhiteBalanced = detectedFoodMat.clone();
		whiteBalance(inputWhiteBalanced);
		if(USER_MASK)
			inputWhiteBalanced.copyTo(input, userMask);
		else
			inputWhiteBalanced.copyTo(input);
		inputWhiteBalanced.release();	  
		
		Vector<Mat> channels = new Vector<Mat>(3);
	    Core.split(input, channels); 
	    
	    Mat iR = channels.get(0);
	    Mat iG = channels.get(1);
	    Mat iB = channels.get(2);
	   

//	  	Bitmap bmpiR = bmp.copy(bmp.getConfig(), true);	
//	  	Utils.matToBitmap(iR, bmpiR);  
//	  	BitmapImage.add (bmpiR);
//	  	ImageName.add("iR"); 
//	  	
//	  	Bitmap bmpiG = bmp.copy(bmp.getConfig(), true);	
//	  	Utils.matToBitmap(iG, bmpiG);  
//	  	BitmapImage.add (bmpiG);
//	  	ImageName.add("iG"); 
//	  	
//	  	Bitmap bmpiB = bmp.copy(bmp.getConfig(), true);	
//	  	Utils.matToBitmap(iB, bmpiB);  
//	  	BitmapImage.add (bmpiB);
//	  	ImageName.add("iB"); 
	  	
	  	Mat iR_iG = new Mat();
	  	Core.subtract(iR, iG, iR_iG);
	  	//Core.subtract(iR_iG, iB, iR_iG);
	  	Imgproc.threshold(iR_iG, iR_iG, 20, 255, Imgproc.THRESH_BINARY);
	  	
	  	Bitmap bmpiR_iG = bmp.copy(bmp.getConfig(), true);	
	  	Utils.matToBitmap(iR_iG, bmpiR_iG);  
	  	BitmapImage.add (bmpiR_iG);
	  	ImageName.add("iR_iG"); 
	  	
	  	List<MatOfPoint> contours = new ArrayList<MatOfPoint>();   
	    Mat hierarchy=new Mat();
	  	Mat detectedColorCopy = iR_iG.clone();
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
	        	
	        	if(DEBUG){
		          	Bitmap bmpOutputContours = bmp.copy(bmp.getConfig(), true);	
		          	Utils.matToBitmap(detectedObject, bmpOutputContours);  
		          	BitmapImage.add (bmpOutputContours);
		          	ImageName.add("detectedObject"); 
	        	}
	        }
	    }
	  	
	  	
	  	
	  	
	  	//Imgproc.threshold(iR_iG, iR_iG, 50, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
//	  	borderDetection(iR_iG,70,3,3);//100,3
//	  	
//	  	
//	  	Bitmap bmpiR_iGth = bmp.copy(bmp.getConfig(), true);	
//	  	Utils.matToBitmap(iR_iG, bmpiR_iGth);  
//	  	BitmapImage.add (bmpiR_iGth);
//	  	ImageName.add("iR_iGth");
	  	
	  	Mat iG_iB = new Mat();
	  	Core.subtract(iG, iB, iG_iB);
	  	//Core.subtract(iG_iR, iB, iG_iR);
	  	Imgproc.threshold(iG_iB, iG_iB, 20, 255, Imgproc.THRESH_BINARY);
	  	
	  	Bitmap bmpiG_iB = bmp.copy(bmp.getConfig(), true);	
	  	Utils.matToBitmap(iG_iB, bmpiG_iB);  
	  	BitmapImage.add (bmpiG_iB);
	  	ImageName.add("iG_iR"); 
	  	
	  	contours.clear();
	  	detectedColorCopy = iG_iB.clone();
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
	        	
	        	if(DEBUG){
		          	Bitmap bmpOutputContours2 = bmp.copy(bmp.getConfig(), true);	
		          	Utils.matToBitmap(detectedObject, bmpOutputContours2);  
		          	BitmapImage.add (bmpOutputContours2);
		          	ImageName.add("detectedObject2"); 
	        	}
	        }
	    }
	  	
	  	//Imgproc.threshold(iG_iB, iG_iB, 50, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
//	  	borderDetection(iG_iB,70,3,3);//100,3
//	  	
//	  	Bitmap bmpiG_iBth = bmp.copy(bmp.getConfig(), true);	
//	  	Utils.matToBitmap(iG_iB, bmpiG_iBth);  
//	  	BitmapImage.add (bmpiG_iBth);
//	  	ImageName.add("iG_iBth"); 
	  	
	  	
	    
	    
    }
            
            
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
    
    
private void processColor ( Mat input, int HL, int HH, int SL, int SH, int VL, int VH, String colorName, int OpenCloseSES,int meanColor, int devColor){	

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
	  	Bitmap bmpOutputProcessColor = bmp.copy(bmp.getConfig(), true);	
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
        	
        	if(DEBUG){
	          	Bitmap bmpOutputContours = bmp.copy(bmp.getConfig(), true);	
	          	Utils.matToBitmap(detectedObject, bmpOutputContours);  
	          	BitmapImage.add (bmpOutputContours);
	          	ImageName.add("detectedObject "+colorName); 
        	}
        	          	
        	
        	//Calculate Hue mean and standard deviation
        	MatOfDouble mean = new MatOfDouble();// = Core.mean(hsvimage, contourFound).val;        	
        	MatOfDouble stdDev = new MatOfDouble();
        	
        	Imgproc.cvtColor(detectedObject, detectedObject, Imgproc.COLOR_RGB2HSV);
        	//We want to calculate mean and standard deviation. In HSV, red color may have hue range
        	//from 0 to 5 and from 175 to 180. If we calculate directly the mean and stddev, the results
        	//will be wrong. Lets then translate the hue range 0-90 to 180-270, in order to have a
        	//a total range of 90-270. After that, we can subtract 90 from the calculated mean. The
        	//standard deviation will be the same.
        	if(colorName.equals("orange")||colorName.equals("red")||colorName.equals("dark red")){
        	    Vector<Mat> channels = new Vector<Mat>(3);
        	    Core.split(detectedObject, channels);   
        	    Mat Hue = channels.get(0);
        	    //double []hueVal;
        	    
        	    Mat HueThres = new Mat();
        	    Imgproc.threshold(Hue, HueThres, 90, 180, Imgproc.THRESH_BINARY_INV);
        	    Core.add(Hue, HueThres, Hue);     	  	
        	    Core.merge(channels, detectedObject);
        	    Core.meanStdDev(detectedObject, mean, stdDev, contourFound);        	    
        	    if(mean.get(0, 0)[0]>180){
        	    	double[] realMean = new double[1];
        	    	realMean[0] = mean.get(0, 0)[0] - 180;
        	    	mean.put(0, 0, realMean);
        	    }
    		}
        	else 
        		Core.meanStdDev(detectedObject, mean, stdDev, contourFound);
        	
        	//TODO filter by sat and hue?
        	Log.i("TAG","Sat= "+mean.get(1, 0)[0]+" Value="+mean.get(2, 0)[0]);        	
        	if(mean.get(1,0)[0]<50 || mean.get(2,0)[0]<50){  //if(mean.get(1,0)[0]<100 || mean.get(2,0)[0]<100){  
        		Log.i("TAG","IGNORING Sat= "+mean.get(1, 0)[0]+" Value="+mean.get(2, 0)[0]);
        		continue;
        	}
        	
        		
        	double ColorDeviation = Math.abs(meanColor-mean.get(0, 0)[0]);
        	if (ColorDeviation>90)
        		ColorDeviation  = 180-ColorDeviation;
        	if(ColorDeviation>devColor){
        		Log.i("TAG","IGNORING meanColor="+meanColor+" meanImage="+mean.get(0, 0)[0]+" devColor="+devColor);
        		continue;
        	}      	

        	double matching1=5;
        	double matching2=5;
        	int matchedContourDir1=-1;//Where in the map contours file is located the better matched contour (line-1=direction)
        	int matchedContourDir2=-1;
        	for ( int count=0; count<StoredContours.size();count++){
        		double aux_matching =Imgproc.matchShapes(contours.get(i), StoredContours.get(count), 1, 0.0);
        		if(aux_matching<matching1){
        			matching2=matching1;
        			matching1=aux_matching;
        			matchedContourDir2=matchedContourDir1;
        			matchedContourDir1=count;
        		}else if(aux_matching<matching2){        			
        			matching2=aux_matching;
        			matchedContourDir2=count;
        		}
        		Log.i("TAG","Matching "+colorName+" "+count+" = "+aux_matching);
        	}  		
        	Log.i("TAG","Matching RESULTS "+matching1+" "+matching2);
        	//STORE CONTOUR INFORMATION
        	if(STORE_CONTOUR){
	        	writeStingInExternalFile((int)contours.get(i).size().height+";","remember_to_delete.txt");
	        	for(int k=0; k < (int)contours.get(i).size().height;k++){
	        		writeStingInExternalFile(contours.get(i).get(k, 0)[0]+","+contours.get(i).get(k, 0)[1]+";","remember_to_delete.txt");
	        	}
        	}
        	if(matchedContourDir1==-1)
        		continue;
        	if( 	(mapControus2Food.get(matchedContourDir1).equals("banana") && colorName.equals("yellow") && (matching1<0.35 || matching2<0.35) )
        		|| 	(mapControus2Food.get(matchedContourDir1).equals("lemon") && colorName.equals("yellow") && (matching1<0.15 || matching2<0.15) )
        		||  (mapControus2Food.get(matchedContourDir1).equals("apple")  && !colorName.equals("orange") && (matching1<0.1 || matching2<0.1) )
        		||  (mapControus2Food.get(matchedContourDir1).equals("cucumber")  && (colorName.equals("green") ||colorName.equals("dark green"))
        					&& matching1<0.1 && matching2<0.1) ){
        		
	        		outputString=outputString.concat(" "+colorName+" "+mapControus2Food.get(matchedContourDir2)+";");
	                Scalar rectColor = (colorName.equals("red"))? new Scalar(255,0,0) : (colorName.equals("yellow"))? new Scalar(0,0,255) :
	            		(colorName.equals("green"))? new Scalar(0,255,0) : (colorName.equals("orange"))? new Scalar(255,128,0) : 
	        			new Scalar(0,0,255);
	        		Imgproc.drawContours(outputMat, contours, i, rectColor);
	        		Mat aux = new Mat();
	        		input.copyTo(aux, contourFound);
	        		Core.subtract(input, aux, input);
        	}
        	else{
            	if(matchedContourDir2==-1)
            		continue;
            	if( 	(mapControus2Food.get(matchedContourDir2).equals("banana") && colorName.equals("yellow") && matching2<0.35)
            		|| 	(mapControus2Food.get(matchedContourDir2).equals("lemon") && colorName.equals("yellow") && matching2<0.1)
            		||  (mapControus2Food.get(matchedContourDir2).equals("apple")  && !colorName.equals("orange") && matching2<0.1)
            		||  (mapControus2Food.get(matchedContourDir2).equals("cucumber")  && (colorName.equals("green") ||colorName.equals("dark green")) && matching2<0.3) ){
            		

            		outputString=outputString.concat(" "+colorName+" "+mapControus2Food.get(matchedContourDir2)+";");
                    Scalar rectColor = (colorName.equals("red"))? new Scalar(255,0,0) : (colorName.equals("yellow"))? new Scalar(0,0,255) :
                		(colorName.equals("green"))? new Scalar(0,255,0) : (colorName.equals("orange"))? new Scalar(255,128,0) : 
            			new Scalar(0,0,255);
            		Imgproc.drawContours(outputMat, contours, i, rectColor);
            		Mat aux = new Mat();
            		input.copyTo(aux, contourFound);
            		Core.subtract(input, aux, input);
            	}        		
        	}
        	
    	  	Bitmap bmpOutputFInal = bmp.copy(bmp.getConfig(), true);	
    	  	Utils.matToBitmap(outputMat, bmpOutputFInal);  

    	  	
		    ImageView imageView = (ImageView) findViewById(R.id.imageView1);
		    imageView.setImageBitmap(bmpOutputFInal);
		    TextView textView = (TextView) findViewById(R.id.textView1);
		    textView.setText(outputString);	
            
        }//End if (Imgproc.contourArea(contours.get(i)) > 1000)        
    }//End loop contours  
    
}

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

@Override
public boolean onTouchEvent (MotionEvent event) {
		Button bDetectFood = (Button) findViewById(R.id.button3);
		if(event.getAction()==MotionEvent.ACTION_DOWN){
	    	userMask= Mat.zeros(detectedFoodMat.rows(), detectedFoodMat.cols(), detectedFoodMat.type());
	    	userMask2= new Mat();
	    	detectedFoodMat.copyTo(userMask2);
	    	
//			TextView textView = (TextView) findViewById(R.id.textView1);
//		    textView.setText("NO");
		}
		else if (event.getAction()==MotionEvent.ACTION_UP){
		    Bitmap bmpInput = bmp;
		    Mat output = new Mat();
		    Core.divide(userMask, new Scalar (5,5,5), output);
		    Core.add(detectedFoodMat, output, output);
		    try{
			    Utils.matToBitmap(output, bmpInput);
			    ImageView imageView = (ImageView) findViewById(R.id.imageView1);
				imageView.setImageBitmap(bmpInput);
		    }catch (Exception e){};
			
		    View view = (View)findViewById(R.id.button1);
		    OnClickDetectFood (view);
//			TextView textView = (TextView) findViewById(R.id.textView1);
//		    textView.setText("YES");		    
		}		
		else if (bDetectFood.getVisibility()== View.VISIBLE){
			int touchX = (int)event.getX();
			int touchY = (int)event.getY();
			ImageView imageView = (ImageView) findViewById(R.id.imageView1);
			int imageLocation [] = new int [2];
			imageView.getLocationOnScreen(imageLocation);		
			if(touchX>imageLocation[0] && touchY>imageLocation[1]
				&& touchX<imageLocation[0]+imageView.getWidth() && touchY<imageLocation[1]+imageView.getHeight()){
//				TextView textView = (TextView) findViewById(R.id.textView1);
//			    textView.setText("YES");
			    double centerX = (touchX-imageLocation[0])*detectedFoodMat.width()/imageView.getWidth();
			    double centerY = (touchY-imageLocation[1])*detectedFoodMat.height()/imageView.getHeight();
			    Point center = new Point (centerX,centerY);
			    //Point center = new Point((double)touchX-imageLocation[0],(double)touchY-imageLocation[1]);
			    Core.circle(userMask, center, 50, new Scalar(255,255,255),-1);
			    Core.circle(userMask2, center, 50, new Scalar(255,255,255),-1); 
			    Bitmap bmpInput = bmp;
			    Utils.matToBitmap(userMask2, bmpInput);
				imageView.setImageBitmap(bmpInput);
			}
//			else{
//				TextView textView = (TextView) findViewById(R.id.textView1);
//			    textView.setText("NO");
//			}
		}
	
	
    return true;
}
/*
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
    Log.i("SAT"," "+bottomR+" "+topR+" "+bottomG+" "+topG+" "+bottomB+" "+topB);
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
	    Bitmap bmpDetectedColor2= bmp.copy(bmp.getConfig(), true);	
	    Utils.matToBitmap(input, bmpDetectedColor2);
	    BitmapImage.add (bmpDetectedColor2);
	    ImageName.add("white balance2");    
    }
}

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
	             Log.i("File "+fileName, "Stored "+data);
	             fileSize=(int)file.length();
    		 }
    		 else Log.e("ActivityRecognitionIntent","unable to create folder or file");
    	}
    } catch (Exception e) {
           Log.e("Error opening file", e.getMessage(), e);
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


/*
 * TODO
 * TODO
 * TODO
 * Vamos a ver... o sea, estoy usando substring y indexOf sobre un string super largo un montón de veces
 * cómo espero que se ejecute en menos de 100ms? MAAAAAAL
 */

public boolean readFileContours(String fileName){
	Log.i("READING FILE",fileName);
	MatOfPoint mop=new MatOfPoint(); 
	  //Read File
	  long timeStamp = System.currentTimeMillis();	  
	  String line = "";
	  try {
	    		 File ext_storage = Environment.getExternalStorageDirectory();
	     		 String extPath = ext_storage.getPath();
	     		 File folder = new File(extPath);
	     		 if(!folder.exists())
	     			 return false;
	     		 if(folder.exists()){
		      	     File file = new File (folder, fileName);
		      	     if(!file.exists())
	      	     		return false;
		      	     FileInputStream f = new FileInputStream(file);
		      	     BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
		      	     String aux_line;
		    		 while ((aux_line = entrada.readLine()) != null) {
			    		 line = aux_line;
		    		 }
		    		 f.close();
	     		 }    		 
 		 } catch (Exception e){ }//TODO handle exception 
	try{
		int size = Integer.parseInt(line.substring(0,line.indexOf(";")));
		
		for(int i=0;i<size;i++){
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
	}catch (Exception e){
		Log.e("readFileContours","Bad file format",e);
	}	
	Log.i("TIME readFileContours",""+(System.currentTimeMillis()-timeStamp));	
	return true;
}

public boolean readMap(String fileName){
	Log.i("READING FILE",fileName);
	  //Read File
	  long timeStamp = System.currentTimeMillis();	 
	  try {
    		 File ext_storage = Environment.getExternalStorageDirectory();
     		 String extPath = ext_storage.getPath();
     		 File folder = new File(extPath);
     		 if(!folder.exists())
     			 return false;
     		 if(folder.exists()){
	      	     File file = new File (folder, fileName);
	      	     if(!file.exists())
      	     		return false;
	      	     FileInputStream f = new FileInputStream(file);
	      	     BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
	      	     String aux_line;
	    		 while ((aux_line = entrada.readLine()) != null) {
	    			 mapControus2Food.add(aux_line);
	    		 }
	    		 f.close();
     		 }    		 
 		 } catch (Exception e){
 			Log.e("readMap","",e); 
 		 }
	Log.i("TIME readMap",""+(System.currentTimeMillis()-timeStamp));	
	return true;
}





}