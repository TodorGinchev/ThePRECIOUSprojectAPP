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
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import aalto.comnet.thepreciousproject.R;
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
    public void detectFace(){
    	
//    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
//        System.out.println("\nRunning FaceDetector");
    	String toPath = "/data/data/" + getPackageName()+"/haarcascade_frontalface_alt.xml";  // Your application path
    	AssetManager assetManager = getAssets();
    	copyAsset(assetManager, "haarcascade_frontalface_alt.xml", toPath);
    	Log.i("FOOD_DETECTOR",toPath);
        CascadeClassifier faceDetector = new CascadeClassifier(toPath);
//        Mat image = Highgui
//                .imread(FaceDetector.class.getResource("shekhar.JPG").getPath());
 
        MatOfRect faceDetections = new MatOfRect();
        
//        Parameters:
//        	image Matrix of the type CV_8U containing an image where objects are detected.
//        	objects Vector of rectangles where each rectangle contains the detected object.
//        	rejectLevels a rejectLevels
//        	levelWeights a levelWeights
//        	scaleFactor Parameter specifying how much the image size is reduced at each image scale.
//        	minNeighbors Parameter specifying how many neighbors each candidate rectangle should have to retain it.
//        	flags Parameter with the same meaning for an old cascade as in the function cvHaarDetectObjects. It is not used for a new cascade.
//        	minSize Minimum possible object size. Objects smaller than that are ignored.
//        	maxSize Maximum possible object size. Objects larger than that are ignored.
//        	outputRejectLevels a outputRejectLevels
        faceDetector.detectMultiScale(inputMat, faceDetections,1.1, 2, 0,new Size(30,30), new Size());
        //faceDetector.detectMultiScale(inputMat, faceDetections);
       
 
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
//            Core.rectangle(inputMat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
//                    new Scalar(0, 255, 0));
            
            inputMat = inputMat.submat(rect);
            Vector<Mat> channels = new Vector<Mat>(3);
            Core.split(inputMat, channels); 
            Mat iR = channels.get(0);
            Mat iG = channels.get(1);
//            Mat iB = channels.get(2);
            //Mat iR_iG = new Mat();
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
//        String filename = "ouput.png";
//        System.out.println(String.format("Writing %s", filename));
//        Highgui.imwrite(filename, inputMat);
    }
 

    /**
     * 
     * @param in
     * @param out
     * @throws IOException
     */
    }            
    private static boolean copyAsset(AssetManager assetManager,
            String fromAssetPath, String toPath) {
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

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
          out.write(buffer, 0, read);
        }
    }

}

