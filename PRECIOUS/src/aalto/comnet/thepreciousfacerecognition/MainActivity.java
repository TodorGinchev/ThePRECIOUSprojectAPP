package aalto.comnet.thepreciousfacerecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import aalto.comnet.thepreciousproject.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//TODO CHANGE NON-FINAL VARIABLES INITIALIZATION, initialize them somewhere else
public class MainActivity extends Activity {
	
private Uri imageUri;	
private Bitmap bmp;
private Mat inputMat=new Mat();
private Rect BBrect;

public static final boolean DEBUG = false;

static {
    if (!OpenCVLoader.initDebug()) {
        // Handle initialization error
    }
}   
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_facedetection);   
   
    //Define onClick events
    Button bSelectImage = (Button) findViewById(R.id.button1);
    bSelectImage.setOnClickListener( new OnClickListener() {
        public void onClick(View v) {
        	takePhoto();
         }
          });        
    if(DEBUG){
    	Button bNextImage = (Button) findViewById(R.id.button2);
    	bNextImage.setVisibility(View.VISIBLE);
    }     
    takePhoto(); 
}
/**
 * 
 */
public void takePhoto() {
	//First delete old image data
	if(bmp!=null)
		bmp.recycle();
	//Take a picture, save it in the memory
	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"precious" +        
	                        String.valueOf(System.currentTimeMillis()) + ".jpg"));
	intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
	intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
	startActivityForResult(intent, 0);
 }    
/**
 * Handle result of camera Intent
 */
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // To Handle Camera Result
    if (resultCode==RESULT_OK && requestCode == 0) {
    	//Get the photo
    	try{        		
    		bmp = BitmapFactory.decodeFile(imageUri.getPath());
    	}catch (Exception e){
    		Log.e("FACE_DETECTION", "memory problem",e);
        	return;
    	}
    	if(bmp==null){
    		Log.e("FACE_DETECTION", "BITMAP is null");
    		Toast.makeText(this, getString(R.string.error_photo), Toast.LENGTH_LONG).show();
    		//First delete old image data
        	//Take a picture, save it in the memory
        	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        	imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"precious" +        
        	                        String.valueOf(System.currentTimeMillis()) + ".jpg"));
        	intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, imageUri);
        	startActivityForResult(intent, 0);
        	return;
    	}
    	
    	//Delete photo from memory
		new File (imageUri.getPath()).delete();			
	    boolean toast_size=false;
		if(bmp.getWidth()!=640 && bmp.getHeight()!=640){
			toast_size=true;				
			//resize image to more suitable size, around 640x480, depending on width/height ratio
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
		}
        //convert bitmap format in OpenCV-compatible Mat format
        Utils.bitmapToMat(bmp, inputMat);
        //whiteBalance(inputMat);        
    	//detectFace();
        MiThread tarea = new MiThread();
        tarea.start();
    	
    	if(toast_size)
    		Toast.makeText(this, R.string.recom_image_size, Toast.LENGTH_LONG).show();
    }
  else if (resultCode == RESULT_CANCELED) {
	  	Toast.makeText(this, getString(R.string.picture_not_loaded), Toast.LENGTH_SHORT).show();
   }         
}       
/**
 * 
 * @param view
 */
@SuppressLint("SdCardPath") public void detectFace(){
	String toPath = "/data/data/" + getPackageName()+"/haarcascade_frontalface_alt.xml";  // Your application path
	AssetManager assetManager = getAssets();
	copyAsset(assetManager, "haarcascade_frontalface_alt.xml", toPath);
	Log.i("FOOD_DETECTOR",toPath);
    CascadeClassifier faceDetector = new CascadeClassifier(toPath); 
    MatOfRect faceDetections = new MatOfRect();
    faceDetector.detectMultiScale(inputMat, faceDetections,1.1, 2, 0,new Size(30,30), new Size());
    Log.i("FACE_DETECTOR",String.format("Detected %s faces", faceDetections.toArray().length));
 
        if(faceDetections.toArray().length==0){
        	Toast.makeText(this, "No faces detected", Toast.LENGTH_LONG).show();
    	return;
    }
    else if (faceDetections.toArray().length!=1){
    	Toast.makeText(this, "Detected more than 1 face", Toast.LENGTH_LONG).show();
    	return;
    }
    	
    for (Rect rect : faceDetections.toArray()) {      
        Rect rect2 = new Rect(rect.x, rect.y+rect.height/2, rect.width, rect.height/7);
        Mat aux = new Mat();
        inputMat.copyTo(aux);
        aux = aux.submat(rect2);
        inputMat = inputMat.submat(rect);
//
//        faceBorderDetection(aux, 30, 3, 3);        
//        Imgproc.cvtColor(aux, aux, Imgproc.COLOR_RGB2GRAY);
//        Imgproc.threshold(aux, aux, 5, 255, Imgproc.THRESH_BINARY);
//        int kernelSize=3;
//        Imgproc.erode(aux, aux, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernelSize, kernelSize)) );
//        Imgproc.dilate(aux, aux, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernelSize, kernelSize)) );     
//
//        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();   
//	    Mat hierarchy=new Mat();
//        Imgproc.findContours(aux, contours,hierarchy, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);  
//        
////	    for(int i=0; i< contours.size();i++){
////	    	double contourArea=Imgproc.contourArea(contours.get(i));
////	    	//Look for objects with are bigger than minObjectArea pixels 
////	        if (contourArea > (aux.width()*aux.height()/4) )
////	        	Imgproc.drawContours(inputMat, contours, i, new Scalar(255,0,0),1);    
////	        
////	    }
//        
//	    Rect rect3 = rect;
//	    rect3.height=rect.height*1/3;
//	    Rect roi = new Rect(0,aux.height()*2/3-1,aux.width(),aux.height()*1/3-1);
//	    aux = new Mat(aux, roi);
//	    contours = new ArrayList<MatOfPoint>();   
//        Imgproc.findContours(aux, contours,hierarchy, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);        	
//	    for(int i=0; i< contours.size();i++){
//	    	double contourArea=Imgproc.contourArea(contours.get(i));
//	    	//Look for objects with are bigger than minObjectArea pixels 
//	        if (contourArea > (aux.width()*aux.height()/4) ){
//	        	//Imgproc.drawContours(inputMat, contours, i, new Scalar(255,0,0),1);
//	        	Rect BBrect = Imgproc.boundingRect(contours.get(i));
//	            TextView tv = (TextView) findViewById(R.id.textViewFaceDetection);
//	            tv.setText("Face size = "+BBrect.width+"x"+inputMat.height()+"."+"\nRatio = "+(float)inputMat.height()/BBrect.width+".");
//	            Core.rectangle(inputMat, new Point(BBrect.x,inputMat.height()/2+inputMat.height()/14), new Point(BBrect.x+BBrect.width,inputMat.height()/2+BBrect.height/7), new Scalar(255,255,0));
//	            Core.rectangle(inputMat, new Point(inputMat.width()/2-inputMat.width()/20,0), new Point(inputMat.width()/2+inputMat.width()/20,inputMat.height()), new Scalar(255,255,0));
//	        }            
//	    }       
    
    ImageView im = (ImageView) findViewById(R.id.imageView_face);
    Bitmap bmp_out = Bitmap.createBitmap(inputMat.width(), inputMat.height(), Config.ARGB_8888);
    Utils.matToBitmap(inputMat, bmp_out); 
    im.setImageBitmap(bmp_out);
    }
} 
class MiThread extends Thread {


	@Override public void run() {
		String toPath = "/data/data/" + getPackageName()+"/haarcascade_frontalface_alt.xml";  // Your application path
		AssetManager assetManager = getAssets();
		copyAsset(assetManager, "haarcascade_frontalface_alt.xml", toPath);
		Log.i("FOOD_DETECTOR",toPath);
	    CascadeClassifier faceDetector = new CascadeClassifier(toPath); 
	    MatOfRect faceDetections = new MatOfRect();
	    faceDetector.detectMultiScale(inputMat, faceDetections,1.1, 2, 0,new Size(30,30), new Size());
	    Log.i("FACE_DETECTOR",String.format("Detected %s faces", faceDetections.toArray().length));
	 
	        if(faceDetections.toArray().length==0){
	        	//Toast.makeText(this, "No faces detected", Toast.LENGTH_LONG).show();
	        	Log.i("TAG","No faces detected");
	    	return;
	    }
	    else if (faceDetections.toArray().length!=1){
	    	//Toast.makeText(this, "Detected more than 1 face", Toast.LENGTH_LONG).show();
	    	Log.i("TAG","Detected more than 1 face");
	    	return;
	    }
	    else
	    	Log.i("TAG","OK");
	    	
	    for (Rect rect : faceDetections.toArray()) {      
	        Rect rect2 = new Rect(rect.x, rect.y+rect.height/2, rect.width, rect.height/7);
	        Mat aux = new Mat();
	        inputMat.copyTo(aux);
	        aux = aux.submat(rect2);
	        inputMat = inputMat.submat(rect);
	//
	        faceBorderDetection(aux, 30, 3, 3);        
	        Imgproc.cvtColor(aux, aux, Imgproc.COLOR_RGB2GRAY);
	        Imgproc.threshold(aux, aux, 5, 255, Imgproc.THRESH_BINARY);
	        int kernelSize=3;
	        Imgproc.erode(aux, aux, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernelSize, kernelSize)) );
	        Imgproc.dilate(aux, aux, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernelSize, kernelSize)) );     
	
	        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();   
		    Mat hierarchy=new Mat();
	        Imgproc.findContours(aux, contours,hierarchy, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);  
	        
//		    for(int i=0; i< contours.size();i++){
//		    	double contourArea=Imgproc.contourArea(contours.get(i));
//		    	//Look for objects with are bigger than minObjectArea pixels 
//		        if (contourArea > (aux.width()*aux.height()/4) )
//		        	Imgproc.drawContours(inputMat, contours, i, new Scalar(255,0,0),1);    
//		        
//		    }
	        
		    Rect rect3 = rect;
		    rect3.height=rect.height*1/3;
		    Rect roi = new Rect(0,aux.height()*2/3-1,aux.width(),aux.height()*1/3-1);
		    aux = new Mat(aux, roi);
		    contours = new ArrayList<MatOfPoint>();   
	        Imgproc.findContours(aux, contours,hierarchy, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);  
		    for(int i=0; i< contours.size();i++){
		    	double contourArea=Imgproc.contourArea(contours.get(i));
		    	//Look for objects with are bigger than minObjectArea pixels 
		        if (contourArea > (aux.width()*aux.height()/4) ){
		        	//Imgproc.drawContours(inputMat, contours, i, new Scalar(255,0,0),1);
		        	BBrect = Imgproc.boundingRect(contours.get(i));
		            Core.rectangle(inputMat, new Point(BBrect.x,inputMat.height()/2+inputMat.height()/14), new Point(BBrect.x+BBrect.width,inputMat.height()/2+BBrect.height/7), new Scalar(255,255,0));
		            Core.rectangle(inputMat, new Point(inputMat.width()/2-inputMat.width()/20,0), new Point(inputMat.width()/2+inputMat.width()/20,inputMat.height()), new Scalar(255,255,0));
		        }            
		    }       
		    runOnUiThread(new Runnable() {
                public void run() {
                	TextView tv = (TextView) findViewById(R.id.textViewFaceDetection);
                	tv.setText("Face size = "+BBrect.width+"x"+inputMat.height()+"."+"\nRatio = "+(float)inputMat.height()/BBrect.width+".");
	          	    ImageView im = (ImageView) findViewById(R.id.imageView_face);
            	    Bitmap bmp_out = Bitmap.createBitmap(inputMat.width(), inputMat.height(), Config.ARGB_8888);
            	    Utils.matToBitmap(inputMat, bmp_out); 
            	    im.setImageBitmap(bmp_out);
                }
            });

	    }
	}
}

/**
 * 
 * @param in
 * @param out
 * @throws IOException
 */
           
private static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
    InputStream in = null;
    OutputStream out = null;
    try {
          in = assetManager.open(fromAssetPath);
          new File(toPath).createNewFile();
          out = new FileOutputStream(toPath);
          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
          return true;
    } catch(Exception e) {
    	e.printStackTrace();
        return false;
    }
}
/**
 * 
 * @param in
 * @param out
 * @throws IOException
 */
private static void copyFile(InputStream in, OutputStream out) throws IOException {
    byte[] buffer = new byte[1024];
    int read;
    while((read = in.read(buffer)) != -1){
      out.write(buffer, 0, read);
    }
}
    
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
    //Log.i("WhiteBalance"," "+bottomR+" "+topR+" "+bottomG+" "+topG+" "+bottomB+" "+topB);
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
}

/**
 * 
 * @param detectedObject
 * @param colorName
 * @param i
 */
public Mat filterSpectrumBW (Mat detectedObject){
	int SL=40,SH=256, VL=40,VH=256;
	Imgproc.cvtColor(detectedObject, detectedObject, Imgproc.COLOR_RGB2HSV);
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
	
	Log.i("FOOD_INTAKE","Object "+":"+"histogram= "+histogram.dump());
	histogram.put(0, 0, 0);
	//Search for the maximum Hue value
	Core.MinMaxLocResult minMax=Core.minMaxLoc(histogram);
	Log.i("FOOD_INTAKE","Object "+" Max="+  minMax.maxVal+" Loc="+minMax.maxLoc);
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
	}while ( (histogram.get(Hfreq,0)[0]> (minMax.maxVal/10)) &&
			( (histogram.get(Hfreq,0)[0]>(minMax.maxVal/5)) || (histogram.get(Hfreq+1,0)[0]>(histogram.get(Hfreq,0)[0])) ) );            
	int Lfreq = (int)minMax.maxLoc.y;
	do{
		if(Lfreq<=1)
			Lfreq=178;
		else
			Lfreq--;
	}while ( (histogram.get(Lfreq,0)[0]> (minMax.maxVal/10)) &&
			( (histogram.get(Lfreq,0)[0]>(minMax.maxVal/5)) || (histogram.get(Lfreq+1,0)[0]>(histogram.get(Lfreq,0)[0])) ) );
	Log.i("FOOD_INTAKE","Object "+" BW= "+Hfreq+"-"+Lfreq);
	
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
	
	//Mat detectedColorMaskout = new Mat();
	//detectedObject.copyTo(detectedColorMaskout,detectedColorMask);
	return detectedColorMask;
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

public Mat detectSkin(Mat input){
	Mat output = new Mat();
	//Convert to RGB color space again
	int Y_MIN  = 0;
	int Y_MAX  = 255;
	int Cr_MIN = 133;
	int Cr_MAX = 173;
	int Cb_MIN = 77;
	int Cb_MAX = 127;
	Imgproc.cvtColor(input, output, Imgproc.COLOR_RGB2YCrCb);
	Core.inRange(output, new Scalar(Y_MIN,Cr_MIN,Cb_MIN), new Scalar(Y_MAX,Cr_MAX,Cb_MAX), output); //Threshold the image
	return output;
}

/**
 * 
 * @param image
 * @param threshold
 * @param EEsize
 * @param BlurSize
 * @return
 */
public Mat faceBorderDetection (Mat image, int threshold, int EEsize, int BlurSize){	
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
		for (int y=0;y<image.cols()/5;y++){
			double []data = detected_edges.get(x,y);
			boolean Delete = (int)data[0]!=0;
			if(Delete){	
				image.put(x,y,border);
			}
		} 	
	for (int x=0;x<image.rows();x++)
		for (int y=image.cols()-1;y>4*image.cols()/5;y--){
			double []data = detected_edges.get(x,y);
			boolean Delete = (int)data[0]!=0;
			if(Delete){	
				image.put(x,y,border);
			}
		}
	return image;
}

}
