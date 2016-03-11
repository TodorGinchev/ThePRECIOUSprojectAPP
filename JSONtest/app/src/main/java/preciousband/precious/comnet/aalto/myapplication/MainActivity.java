package preciousband.precious.comnet.aalto.myapplication;


import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpEntity;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.registation_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendRegistrationRequest();
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
        sendJson (serverURL,email,password,weight,height,activityClass,nickname,birthdate);
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

    /**
     *
     * @param url
     * @return
     */
    public String requestContent(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        String result = null;
        HttpGet httpget = new HttpGet(url);
        HttpResponse response = null;
        InputStream instream = null;

        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                instream = entity.getContent();
                result = convertStreamToString(instream);
            }

        } catch (Exception e) {
            // manage exceptions
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (Exception exc) {

                }
            }
        }

        return result;
    }

    /**
     *
     * @param is
     * @return
     */
    public String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }
}
