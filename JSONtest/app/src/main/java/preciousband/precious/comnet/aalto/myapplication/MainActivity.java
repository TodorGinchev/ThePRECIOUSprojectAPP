package preciousband.precious.comnet.aalto.myapplication;


import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String apiKey = "387de4f0-e79b-11e5-b938-9dea270b774d";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.registation_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendRegistrationRequest();
            }
        });
        findViewById(R.id.another_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getUserInfo();
            }
        });
        //sendRegistrationRequest();
    }

    /**
     {
     "email" : "christopher.helf4@gmail.com",
     "password": "test",
     "device" : {
     "deviceId" : "123",
     "deviceUUID" : "ABC",
     "deviceType" : "Android",
     "deviceToken" : "TOKEN"
     },
     "weight" : 80.1,
     "height" : 180.2,
     "activityClass" : 3,
     "nickname" : "chris123",
     "birthdate" : "Fri Mar 11 2016 15:56:54 GMT+0100 (CET)"
     }
     *
     */
    private  void sendRegistrationRequest(){

        Log.i(TAG, "Sending request");
        String serverURL = "http://precious2.research.netlab.hut.fi:9000/register";
        String email = "test1000@test.bg";
        String password = "mypass";
        int activityClass = 5;
        String nickname = "Walker, Johnnie";
        String birthdate ="19June1440";
        int weight = 12;
        int height = 140;
        sendJson(serverURL, email, password, weight, height, activityClass, nickname, birthdate);
    }


    /**
     * Example from Chris: http://stackoverflow.com/questions/15554296/simple-java-aes-encrypt-decrypt-example
     * [17:22:11] Helf Christopher: 1) URL
     [17:22:12] Helf Christopher: http://precious2.research.netlab.hut.fi:9000/user
     [17:22:15] Helf Christopher: 2) METHOD
     [17:22:18] Helf Christopher: GET
     [17:22:23] Helf Christopher: 3) SET REQUEST HEADERS
     [17:22:31] Helf Christopher: setHeader(„x-precious-apikey“, „YOURAPIKEY“)
     [17:22:38] Helf Christopher: 4) RETREIVE RESPONSE
     [17:22:57] Helf Christopher: 5) CHECK RESPONSE HEADER „x-precious-encryption-iv“

     KEY FOR DECRIPTION : be2b9f48ecaf294c2fc68e2862501bbd

     [17:25:12] Helf Christopher: initVector.getBytes("HEX")
     [17:25:17] Helf Christopher: instead of initVector.getBytes("UTF-8")

     decrypt(„be2b9f48ecaf294c2fc68e2862501bbd“, iv from response header, response string)
     */
    private void getUserInfo(){
        String serverURL = "http://precious2.research.netlab.hut.fi:9000/user";
        getJson(serverURL, apiKey);
    }

    /**
     *
     */
    protected void sendJson(final String serverURL, final String email, final String password,
        final int weight, final int height, final int activityClass, final String nickname,
                            final String birthdate) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost(serverURL);
                    json.put("email", email);
                    json.put("password", password);
                    json.put("weight",weight);
                    json.put("height",height);
                    json.put("activityClass",activityClass);
                    json.put("nickname",nickname);
                    json.put("birthdate",birthdate);

                    StringEntity se = new StringEntity( json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if(response!=null){
                        Log.i(TAG,"REPSPONSE IS:"+EntityUtils.toString(response.getEntity()));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Cannot Estabilish Connection");
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }

    protected void getJson(final String serverURL, final String apiKey) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    HttpGet get = new HttpGet(serverURL);
                    get.setHeader("x-precious-apikey", apiKey);

//                    StringEntity se = new StringEntity( json.toString());
//                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                    post.setEntity(se);
                    response = client.execute(get);

                    /*Checking response */
                    if(response!=null){
                        Log.i(TAG,"REPSPONSE IS:"+EntityUtils.toString(response.getEntity()));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Cannot Estabilish Connection");
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }
}
