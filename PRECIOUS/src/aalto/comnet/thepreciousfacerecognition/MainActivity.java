package aalto.comnet.thepreciousfacerecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
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
private Mat inputMat=new Mat();;

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
        whiteBalance(inputMat);
    	detectFace();
    	
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
        inputMat = inputMat.submat(rect);
        Vector<Mat> channels = new Vector<Mat>(3);
        Core.split(inputMat, channels); 
        Mat iR = channels.get(0);
        Mat iG = channels.get(1);
        Mat aux = new Mat();
        Core.subtract(iR, iG, aux);
        Scalar mean = Core.mean(aux);
        //Imgproc.threshold(aux, aux, 200, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
        Imgproc.threshold(aux, aux, mean.val[0]/2.5, 255, Imgproc.THRESH_BINARY);

        int kernelSize=3;
        Imgproc.dilate(aux, aux, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernelSize, kernelSize)) );
        Imgproc.erode(aux, aux, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernelSize, kernelSize)) );
       
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();   
	    Mat hierarchy=new Mat();
        Imgproc.findContours(aux, contours,hierarchy, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);        	
	    for(int i=0; i< contours.size();i++){
	    	double contourArea=Imgproc.contourArea(contours.get(i));
	    	//Look for objects with are bigger than minObjectArea pixels 
	        if (contourArea > (aux.width()*aux.height()/4) )
	        	Imgproc.drawContours(inputMat, contours, i, new Scalar(255,0,0),1);    	                    
	    }
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
	        	Rect BBrect = Imgproc.boundingRect(contours.get(i));
	            TextView tv = (TextView) findViewById(R.id.textViewFaceDetection);
	            tv.setText("Face size = "+BBrect.width+"x"+inputMat.height()+"."+"\nRatio = "+(float)inputMat.height()/BBrect.width+".");
	            Core.rectangle(inputMat, new Point(BBrect.x,inputMat.height()*2/3), new Point(BBrect.x+BBrect.width,inputMat.height()*2/3+BBrect.height/3), new Scalar(255,255,0));
	        }            
	    }        
    ImageView im = (ImageView) findViewById(R.id.imageView_face);
    Bitmap bmp_out = Bitmap.createBitmap(inputMat.width(), inputMat.height(), Config.ARGB_8888);
    Utils.matToBitmap(inputMat, bmp_out); 
    im.setImageBitmap(bmp_out);
}

/**
 * 
 * @param in
 * @param out
 * @throws IOException
 */
}            
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

}