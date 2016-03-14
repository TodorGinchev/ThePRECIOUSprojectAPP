package preciousband.precious.comnet.aalto.myapplication;


import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.apache.http.Header;
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
    public static final String Key = "be2b9f48ecaf294c2fc68e2862501bbd";

    //User info
    private String email = "test1003@test.bg";
    private String password = "mypasswds";
    private int activityClass = 5;
    private String nickname = "1003";
    private String birthdate ="19June1441";
    private int weight = 12;
    private int height = 140;


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

     [10:19:53] Helf Christopher: Encryption: Raw JSON -> Convert to String -> AES Encrypt -> Base64 Encoding
     [10:20:23] Helf Christopher: Decryption: Base64 String -> Decode Base64 -> Decrypt -> UTF8 String -> Parse JSON
     [10:20:43] Helf Christopher: As for Decryption: the IV received in the headers in in HEX
     [10:20:48] Helf Christopher: the key is UTF8

     KEY FOR DECRYPTION : be2b9f48ecaf294c2fc68e2862501bbd
     T76egWy0jfKNjk3a6wMUaM7AhFh7M6nZlWtB/M2XtsmdA+nQi7eduoztR07WY9mR1y6OrPUtnAMSSmyoAPQm2gkZdYrTUr4zWNDvF/0H4iTPJwyN09Fgky7FIlQgA0Iu2MDis+2GqOqEPI4DRrGSiw==
     336C674C79333965735A483334767859416463684B6558754C6252614735554B5A744D52412B42352B722F61596143637043486D443543426E526B434E65473875554F57423746414177664E4247725270774930776C44736A674A74563538714D654E676A53486C71492B6B737668794C69427870324C74536256707A7139576D4A646335616E71373334694A6355525979377158513D3D



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
                JSONObject json = new JSONObject();

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
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
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        response.getEntity().writeTo(baos);
//                        byte[] bytes = baos.toByteArray();
//                        Log.i(TAG,"RESPONSE IS:"+bytesToHex(bytes));
                        Header [] headers = response.getAllHeaders();
                        String iv="";
                        for (int i=0; i<headers.length;i++) {
                            if(headers[i].getName().equals("x-precious-encryption-iv"))
                                iv = headers[i].getValue().toString();
//                            Log.i(TAG, "Name: "+headers[i].getName());
//                            Log.i(TAG, "toString: "+headers[i].toString());
                        }
                        byte [] ivHex = iv.getBytes("UTF-8");
                        String message = EntityUtils.toString(response.getEntity());
                        byte [] messageHex = message.getBytes("UTF-8");
                        Log.i(TAG,"IV IS: "+iv);
                        Log.i(TAG,"MESSAGE TEXT IS: "+message);
                        Log.i(TAG, "MESSAGE HEX IS: " + bytesToHex(messageHex));
                        Log.i(TAG,"KEY TEXT IS: "+Key);
                        Log.i(TAG,"KEY HEX IS: "+bytesToHex(Key.getBytes("UTF-8")));
                        Log.i(TAG,"Encrypted message is: "+Encryptor.decrypt(Key,iv,message));
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
     */
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

}
