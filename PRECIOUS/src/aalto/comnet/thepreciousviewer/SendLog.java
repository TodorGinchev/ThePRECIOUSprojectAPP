package aalto.comnet.thepreciousviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;

public class SendLog extends Service{
	
	//Default IP and Socket number of the server    
    private static final String SERVER_DEST ="precious.research.netlab.hut.fi";
    private static final int SOCKET_DEST = 5885;
    
    private static SharedPreferences prefs;
    private static SharedPreferences Default_prefs;
    
    
    @Override
    public void onCreate() {
	    prefs =this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
	    Default_prefs = PreferenceManager.getDefaultSharedPreferences(this);
	    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.
                Builder().permitNetwork().build());
    }
	
    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
    	Log.i("SendLog","on startd command");
    	
    	new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
            	try {
	           		 File ext_storage = Environment.getExternalStorageDirectory();
	           		 String extPath = ext_storage.getPath();
	           		 File folder = new File(extPath+"/precious");
	           		 boolean success = false;
	           		 if(!folder.exists())
	           			 success = folder.mkdir();
	           		 if(folder.exists() || success){
	           			 //Send steps info
	       	      	     File file = new File (folder, "ServerActivity.txt");
	       	      	     if(!file.exists())
	            	     		file.createNewFile();
	       	      	     FileInputStream f = new FileInputStream(file);
	       	      	     BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
	       	      	     String line="";
	       	      	     String serverReply = "OK";
	       	    		 while ((line = entrada.readLine()) != null) {
	       	    	  		 serverReply = sendTCP("A;"+line);
	       	    			 if(serverReply.equals("notOK"))
	       	    					 break;
	       	    		 }
	       	    		 //If there was an error in the connection, send everything again
	       	    		 if(serverReply.equals("notOK"))
	       	    			 f.close();
	       	    		 //If everything was sent, delete file
	       	    		 else{
	       	    			 f.close();
	       	    			 file.delete();
	       	    		 }
	       	    		 f.close();
	       	    		 
	       	    		 //Send activity info
	       	    		 file = new File (folder, "serverSteps.txt");
	       	    		 if(!file.exists())
	       	     	     		file.createNewFile();
	       	      	     f = new FileInputStream(file);
	       	      	     entrada = new BufferedReader(  new InputStreamReader(f));
	       	      	     line="";
	       	    		 while ((line = entrada.readLine()) != null) {
	       	    			 serverReply = sendTCP("S;"+line);
	       	    			 if(serverReply.equals("notOK")){
	           					 break;
	       	    			 }
	       	    		 }
	       	    		 //If there was an error in the connection, send everything again
	       	    		 if(serverReply.equals("notOK"))
	       	    			 f.close();
	       	    		 //If everything was sent, delete file
	       	    		 else{
	       	    			 f.close();
	       	    			 file.delete();
	       	    		 }
			       	    f.close();
	           		 }
       		    } catch (Exception e){
		       	    	Log.e("getLog","Fichero serverSteps.txt o ServerActivity.txt no existe",e);
	       		 }              	
                return null;
            }
        }.execute();
    	 
    	onDestroy();
    	return START_NOT_STICKY;
    }

	 public void onDestroy() {
	 }
	 
	 @Override
     public IBinder onBind(Intent intencion) {
           return null;
     }


	public String sendTCP (String stringToSend){
		
		String respuesta ="_";
		//get user ID and add it at the start of the string
		String userID = prefs.getString("user_id", "-1");
		stringToSend = userID.concat(";"+stringToSend);
		//stringToSend = userID;
		  try {
			  	String ipDest = Default_prefs.getString("IP",SERVER_DEST);
			  	String socketDestString = Default_prefs.getString("PORT", String.valueOf(SOCKET_DEST));
			  	int socketDest = Integer.valueOf(socketDestString);
			  	Log.i("INTERNET",ipDest+ " " + socketDestString);
			  	
			  	Socket sk = new Socket();
		        sk.connect(new InetSocketAddress(ipDest, socketDest), 30000); //timeout 30000ms
		        
		        BufferedReader entrada = new BufferedReader(
		                              new InputStreamReader(sk.getInputStream()));
		        PrintWriter salida = new PrintWriter(
		                              new OutputStreamWriter(sk.getOutputStream()),true);
		        
		        salida.println(stringToSend);
		        //respuesta = entrada.readLine(); //TODO
		        Log.i("INTERNET",respuesta+" "+stringToSend);
		        sk.close();
	  } catch (Exception e) {
	        Log.e("INTERNET","No connection to the server "+stringToSend,e);
	        return "notOK";
	  }
		return respuesta;
	}
	
	
	
	
}