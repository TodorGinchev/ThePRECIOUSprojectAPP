package aalto.comnet.thepreciousfacerecognition;

import java.io.File;
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
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import aalto.comnet.thepreciousproject.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
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
private Mat inputMat;
private Rect BBrect;

private FaceDetector.Face[] faces;
private int face_count;
private PointF tmp_point;
private int eyeDist;


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
	inputMat=new Mat();
	BBrect=null;
	faces = null;
	tmp_point= new PointF();
	eyeDist=-1;
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
    		// Set internal configuration to RGB_565
//            BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
//            bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;
//    		bmp = BitmapFactory.decodeFile(imageUri.getPath(),bitmap_options);
    		Bitmap aux = BitmapFactory.decodeFile(imageUri.getPath());
    		bmp = convert(aux, Bitmap.Config.RGB_565);
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
		if(bmp.getWidth()!=640 && bmp.getHeight()!=640){
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
		
//        //convert bitmap format in OpenCV-compatible Mat format
//        Utils.bitmapToMat(bmp, inputMat);    	*
    	FaceDetector face_detector = new FaceDetector(bmp.getWidth(), bmp.getHeight(),10);   
    	faces = new FaceDetector.Face[10];
        // The bitmap must be in 565 format (for now).
    	int count = 0;
    	do{
    		bmp =RotateBitmap(bmp,90);
    		face_detector = new FaceDetector(
        			bmp.getWidth(), bmp.getHeight(),
                    10);  
    		face_count = face_detector.findFaces(bmp, faces);    		
    		count++;
    	} while (face_count!=1 && count<4);
    	//convert bitmap format in OpenCV-compatible Mat format
        Utils.bitmapToMat(bmp, inputMat);
        
        ImageView im = (ImageView) findViewById(R.id.imageView_face);
	    im.setImageBitmap(bmp);
        
        //Ensure that only one face is detected
        if(face_count==0){
        	Toast.makeText(this, "No faces detected", Toast.LENGTH_LONG).show();
        	Log.i("TAG","No faces detected");
    	return;
	    }
	    else if (face_count!=1){
	    	Toast.makeText(this, "Detected more than 1 face", Toast.LENGTH_LONG).show();
	    	Log.i("TAG","Detected more than 1 face");
	    	Paint tmp_paint = new Paint();
	    	bmp = bmp.copy(Bitmap.Config.ARGB_8888, true);
	    	Canvas canvas = new Canvas(bmp);
            for (int i = 0; i < face_count; i++) {
                    FaceDetector.Face face = faces[i];
                    tmp_paint.setColor(Color.RED);
                    tmp_paint.setAlpha(100);
                    face.getMidPoint(tmp_point);
                    canvas.drawCircle(tmp_point.x, tmp_point.y, face.eyesDistance(),
                                    tmp_paint);
            }
            im.setImageBitmap(bmp);

	    	return;
	    }
	    else
	    	Log.i("TAG","OK");
        //Draw faces
        FaceDetector.Face face = faces[0];
        eyeDist = (int)face.eyesDistance();
        face.getMidPoint(tmp_point);
        //rect = new Rect ((int)(tmp_point.x-1.7*eyeDist),(int)(tmp_point.y-2*eyeDist),(int)(tmp_point.x+1.7*eyeDist),(int)(tmp_point.y+2*eyeDist));
        
        int canny_thres = 30;
        boolean detected = false;
        while (detected==false && canny_thres>5){
        	canny_thres = canny_thres-5;
        	detected = detectFaceSize(canny_thres);
        }
    }
  else if (resultCode == RESULT_CANCELED) {
	  	Toast.makeText(this, getString(R.string.picture_not_loaded), Toast.LENGTH_SHORT).show();
   }         
}       

private boolean detectFaceSize(int canny_thres){

			Mat inputMatCopy = new Mat();
			inputMat.copyTo(inputMatCopy);
			int rectx =(int)(tmp_point.x-1.7*eyeDist);
			rectx = (rectx<0)? 0:rectx;
			int recty = (int)(tmp_point.y-1.7*eyeDist);
			recty = (recty<0)? 0 : recty;
			int rectwidth = (int)(2*1.7*eyeDist);
			rectwidth = ((rectwidth+rectx)>inputMatCopy.width()-1)? inputMatCopy.width()-1-rectx : rectwidth;
			int rectheight = (int)(2*2*eyeDist);
			rectheight = ((rectheight+recty)>inputMatCopy.height()-1)? inputMatCopy.height()-1-recty : rectheight;
			Rect rect = new Rect (rectx,recty,rectwidth,rectheight);
			
	        //Rect rect2 = new Rect(rect.x, rect.y+rect.height/2+rect.height/7, rect.width, rect.height/7);
	        int rect2x =(int)(tmp_point.x-1.7*eyeDist);
	        rect2x = (rect2x<0)? 0:rect2x;
	        int rect2width = (int)(2*1.7*eyeDist);
	        rect2width = ( (rect2width+rect2x)>inputMatCopy.width()-1)? inputMatCopy.width()-1-rect2x : rect2width;	        
			Rect rect2 = new Rect (rect2x,(int)(tmp_point.y+eyeDist/2),rect2width,(int)(eyeDist/3));
			
			
			Mat aux = new Mat();
	        inputMatCopy.copyTo(aux);
	        try{
	        	aux = aux.submat(rect2);
	        }catch (Exception e){
	        	Toast.makeText(this, "No faces detected", Toast.LENGTH_LONG).show();
	        	return false;
	        }
			Log.i("TAG",rect.toString());
			Core.rectangle(inputMatCopy, new Point(tmp_point.x-2*eyeDist/3,tmp_point.y-eyeDist/7), new Point(tmp_point.x+2*eyeDist/3,tmp_point.y+eyeDist/7), new Scalar(255,255,0));
	        Log.i("TAG",inputMatCopy.width()+"x"+inputMatCopy.height()+";"+rect.x+","+rect.y+";"+rect.width+"x"+rect.height);
			inputMatCopy = inputMatCopy.submat(rect);

			
			//Imgproc.cvtColor(aux, aux, Imgproc.COLOR_RGB2GRAY);
	        //faceBorderDetection(aux, 30, 3, 3); 
//			ImageView im = (ImageView) findViewById(R.id.imageView_face);
//    	    Bitmap bmp_out = Bitmap.createBitmap(aux.width(), aux.height(), Config.ARGB_8888);
//    	    Utils.matToBitmap(aux, bmp_out); 
//    	    im.setImageBitmap(bmp_out);
    	    
			aux=faceBorderDetection(aux, canny_thres, 3, 3);
			
//			ImageView im = (ImageView) findViewById(R.id.imageView_face);
//    	    Bitmap bmp_out = Bitmap.createBitmap(aux.width(), aux.height(), Config.ARGB_8888);
//    	    Utils.matToBitmap(aux, bmp_out); 
//    	    im.setImageBitmap(bmp_out);
			//TODO
			
	        Imgproc.cvtColor(aux, aux, Imgproc.COLOR_RGB2GRAY);
	        Imgproc.threshold(aux, aux, 5, 255, Imgproc.THRESH_BINARY);
	        int kernelSize=3;
	        Imgproc.erode(aux, aux, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernelSize, kernelSize)) );
	        Imgproc.dilate(aux, aux, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(kernelSize, kernelSize)) );     
	
	        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();   
		    Mat hierarchy=new Mat();
	        Imgproc.findContours(aux, contours,hierarchy, Imgproc.RETR_EXTERNAL,Imgproc.CHAIN_APPROX_NONE);  

	        
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
		        	//Imgproc.drawContours(inputMatCopy, contours, i, new Scalar(255,0,0),1);
		        	BBrect = Imgproc.boundingRect(contours.get(i));
		            Core.rectangle(inputMatCopy, new Point(BBrect.x,tmp_point.y+eyeDist/2-recty), new Point(BBrect.x+BBrect.width,(int)(tmp_point.y+eyeDist/2-recty+eyeDist/3)), new Scalar(255,255,0));
//		            Core.rectangle(inputMatCopy, new Point(inputMatCopy.width()/2-inputMatCopy.width()/20,0), new Point(inputMatCopy.width()/2+inputMatCopy.width()/20,inputMatCopy.height()), new Scalar(255,255,0));
		            if(BBrect.x<10 || inputMatCopy.width()-(BBrect.x+BBrect.width) < 10){
		            	Log.i("TAG","left corner "+BBrect.x);
		            	Log.i("TAG","right corner: "+inputMatCopy.width()+" "+(BBrect.x+BBrect.width) );
	            		return false;
		            }
		        }            
		    }
		    try{
	        	TextView tv = (TextView) findViewById(R.id.textViewFaceDetection);
	        	tv.setText("Eye distance is "+eyeDist+"px.\nFace width is "+BBrect.width+"px.\nRatio is "+((double)BBrect.width/(double)eyeDist)+".");
		    }catch (Exception e){
		    	Log.e("TAG","",e);
		    }
		    ImageView im = (ImageView) findViewById(R.id.imageView_face);
    	    Bitmap bmp_out = Bitmap.createBitmap(inputMatCopy.width(), inputMatCopy.height(), Config.ARGB_8888);
    	    Utils.matToBitmap(inputMatCopy, bmp_out); 
    	    im.setImageBitmap(bmp_out);
    	    return true;
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
	
	Log.i("FACE_DETECTION","Object "+":"+"histogram= "+histogram.dump());
	histogram.put(0, 0, 0);
	//Search for the maximum Hue value
	Core.MinMaxLocResult minMax=Core.minMaxLoc(histogram);
	Log.i("FACE_DETECTION","Object "+" Max="+  minMax.maxVal+" Loc="+minMax.maxLoc);
	//            Log.i("FACE_DETECTION","Object "+i+" His Size="+ histogram.size());
	//            double[] aaaaa = histogram.get(27,0);
	//            Log.i("FACE_DETECTION","Object "+i+" His val="+ aaaaa[0]);
	
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
	Log.i("FACE_DETECTION","Object "+" BW= "+Hfreq+"-"+Lfreq);
	
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
	//Split image in R, G and B channels
	Vector<Mat> channels = new Vector<Mat>(3);			
	Core.split(image, channels); 

    Mat iG = channels.get(1);

	int ratio = 3;	
	/// Reduce noise with a kernel 3x3	
	Mat detected_edges=iG.clone();
	
	if(BlurSize>0)
		Imgproc.blur( iG, detected_edges, new Size(BlurSize,BlurSize) );

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

public static Bitmap RotateBitmap(Bitmap source, float angle){
      Matrix matrix = new Matrix();
      matrix.postRotate(angle);
      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
}

private Bitmap convert(Bitmap bitmap, Bitmap.Config config) {
    Bitmap convertedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
    Canvas canvas = new Canvas(convertedBitmap);
    Paint paint = new Paint();
    paint.setColor(Color.BLACK);
    canvas.drawBitmap(bitmap, 0, 0, paint);
    return convertedBitmap;
}

public void saveInfo(View view){
	 Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
	 finish();
}

}
