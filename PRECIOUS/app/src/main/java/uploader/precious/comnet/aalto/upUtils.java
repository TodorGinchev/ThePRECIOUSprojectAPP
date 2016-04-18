package uploader.precious.comnet.aalto;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import aalto.comnet.thepreciousproject.R;

public class upUtils {


    public static final String PREFS_NAME = "UploaderPreferences";

    public static final String TAG = "upUtils";
//    public static final String apiKey = "6a010f50-e9cd-11e5-955a-83f5900d03c7";
    public static final String SECRET_KEY = "be2b9f48ecaf294c2fc68e2862501bbd";
    public static final String serverURLapi = "https://precious2.research.netlab.hut.fi/api";
    public static final String loginSegment = "/login/";
    public static final String UserSegment = "/user/";
    public static final String RegistrationSegment = "/register/";
    public static String serverURL = serverURLapi.concat(RegistrationSegment);
    public static Context mContext;



    //User info
//    private static int activityClass = 5;
//    private static String nickname = "1006";
//    private static String birthdate = "19June1441";
//    private static int weight = 12;
//    private static int height = 140;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.up_main_activivty);
//        mContext = this;
//        preferences = upUtils.getContext().getSharedPreferences(PREFS_NAME, 0);
//
////
//        //Set OnClick Listener for the buttons
//        findViewById(R.id.registation_button).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                sendRegistrationRequest();
//            }
//        });
//        findViewById(R.id.get_user_info_button).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            String serverURL = serverURLapi.concat(UserSegment);
//            getJson(serverURL, preferences.getString("apiKey","?"));
//            }
//        });
//        findViewById(R.id.store_data_button).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                String serverURL = serverURLapi.concat("/user/data");
//                String iv = "11223344556677889900112233445566";
//                //Send number steps
//                String dataType ="Activity";
//                int dataValue = 5000; //step count
//                long to = System.currentTimeMillis();
//                long from =to-20000;
//                storeData(iv, serverURL, dataType, dataValue, from, to);
//            }
//        });
//        findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                login();
//            }
//        });
//        findViewById(R.id.get_data_button).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            getJson(serverURLapi.concat("/user/data?key=USER_STEPS&from=0"), preferences.getString("apiKey", "?"));
//            }
//        });
//    }

    /**
     *
     */
    private void sendRegistrationRequest() {
        Log.i(TAG, "Sending registration request");
        register();
    }

//    /**
//     *
//     */
//    private void getUserInfo() {
//        String serverURL = serverURLapi.concat(UserSegment);
//        getJson(serverURL, preferences.getString("apiKey","?"));
//    }



    /**
     *
     */
    public static void  register() {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                JSONObject json = new JSONObject();

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    //Get user data and put it into request
                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    HttpPost post = new HttpPost(serverURL);
                    json.put("email", preferences.getString("email","?"));
                    json.put("password", preferences.getString("password","?"));
                    json.put("weight", preferences.getString("weight","?"));
                    json.put("height", preferences.getString("height","?"));
                    json.put("activityClass", preferences.getString("activityClass","?"));
                    json.put("nickname", preferences.getString("nickname","?"));
                    json.put("birthdate", preferences.getString("birthdate","?"));
                    json.put("gender", preferences.getString("gender","?"));

                    Log.i(TAG,preferences.getString("email","?")+"_"+
                            preferences.getString("password","?")+"_"+
                            preferences.getString("weight","?")+"_"+
                            preferences.getString("height","?")+"_"+
                            preferences.getString("activityClass","?")+"_"+
                            preferences.getString("nickname","?")+"_"+
                            preferences.getString("birthdate","?")+"_"+
                            preferences.getString("gender","?"));


                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if (response != null) {
                        String responseString = EntityUtils.toString(response.getEntity());
                        if(responseString.equals("Invalid Registration Parameters")
                                ||responseString.equals("User already exists"))
                            Toast.makeText(mContext,responseString,Toast.LENGTH_LONG).show();
                        else {
                            Log.i(TAG, "RESPONSE IS: " + responseString);
                            //TODO finish this
                            upUtils.saveLoginInfo(responseString);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Cannot Establish Connection");
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }

    /**
     * @param serverURL
     * @param apiKey
     */
    protected void getJson(final String serverURL, final String apiKey) {
        //TODO check status code from HTTPS response, if it's 200, go on, otherwise, log
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
//                JSONObject json = new JSONObject();

                try {
                    HttpGet get = new HttpGet(serverURL);
                    Log.i(TAG, "Requesting user info with apiKey=_" + apiKey);
                    get.setHeader("x-precious-apikey", apiKey);
                    response = client.execute(get);
                    /*Checking response */
                    if (response != null) {
                        Header[] headers = response.getAllHeaders();
                        String iv = "";
                        for (int i = 0; i < headers.length; i++) {
                            if (headers[i].getName().equals("x-precious-encryption-iv"))
                                iv = headers[i].getValue().toString();
                        }
                        String message = EntityUtils.toString(response.getEntity());
                        Log.i(TAG, "Encrypted message is: " + Encryptor.decrypt(SECRET_KEY, iv, message));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Cannot Estabilish Connection");
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }

    /**
     * {
     * "data": [{
     * "key" : "USER_GPS",
     * "id": 36,
     * "value": {
     * "lat" : 40,
     * "lon" : 50
     * },
     * "from" : "2016-03-01",
     * "to" : "2016-03-02"
     * },
     * {
     * "key" : "USER_STEPS",
     * "id": 37,
     * "value": 3000,
     * "from" : "2016-03-01",
     * "to" : "2016-03-03"
     * }
     * ]
     * }
     */
    protected void sendJson2(final String iv, final String serverURL, final String jsKey, final int jsId,
                             final int jsSteps, final long jsFrom, final long jsTo) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                JSONObject jsonObj = new JSONObject();

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    HttpPost post = new HttpPost(serverURL);

                    JSONObject pnObj_GPS = new JSONObject();
                    JSONObject pnObj_Steps = new JSONObject();
                    JSONArray jsonArr = new JSONArray();

                    pnObj_GPS.put("key", "USER_GPS");
                    pnObj_GPS.put("id", 36);
                    //JSONArray jsonArr2 = new JSONArray();
                    JSONObject pnObj_gps_aux = new JSONObject();
                    pnObj_gps_aux.put("lat", 50);
                    pnObj_gps_aux.put("lon", 40);
                    //jsonArr2.put(pnObj_gps_aux);
                    pnObj_GPS.put("value", pnObj_gps_aux);
                    pnObj_GPS.put("from", jsFrom);
                    pnObj_GPS.put("to", jsTo);

                    pnObj_Steps.put("value", jsSteps);
                    pnObj_Steps.put("from", jsFrom);
                    pnObj_Steps.put("to", jsTo);
                    jsonArr.put(pnObj_GPS);

                    pnObj_Steps.put("key", jsKey);
                    pnObj_Steps.put("id", jsId);
                    pnObj_Steps.put("value", jsSteps);
                    pnObj_Steps.put("from", jsFrom);
                    pnObj_Steps.put("to", jsTo);
                    jsonArr.put(pnObj_Steps);

                    jsonObj.put("data", jsonArr);

                    Log.i(TAG, "JSON OBJECT= " + jsonObj.toString());


                    StringEntity se = new StringEntity(Encryptor.encrypt(SECRET_KEY, iv, jsonObj.toString()));
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    post.addHeader("x-precious-encryption-iv", iv);
                    post.addHeader("x-precious-apikey", preferences.getString("apiKey","?"));

                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if (response != null) {
                        Header[] headers = response.getAllHeaders();
                        String iv = "";
                        for (int i = 0; i < headers.length; i++) {
                            if (headers[i].getName().equals("x-precious-encryption-iv"))
                                iv = headers[i].getValue().toString();
                        }
                        String message = EntityUtils.toString(response.getEntity());
                        Log.i(TAG, "Encrypted message is: " + Encryptor.decrypt(SECRET_KEY, iv, message));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Cannot Establish Connection");
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }


    /**
     *
     */
    protected void storeData(final String iv, final String serverURL, final String jsKey,
                             final int jsValue, final long jsFrom, final long jsTo) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    HttpPost post = new HttpPost(serverURL);

                    JSONObject jsonObj = new JSONObject(); //Object data

                    //Define array
                    JSONArray jsonArr = new JSONArray();
                    //Define key object
                    JSONObject pnObj_Steps = new JSONObject();
                    pnObj_Steps.put("key", jsKey);
                    pnObj_Steps.put("value", jsValue);
                    pnObj_Steps.put("from", jsFrom);
                    pnObj_Steps.put("to", jsTo);
                    pnObj_Steps.put("id", -1);

                    jsonArr.put(pnObj_Steps);

                    jsonObj.put("data", jsonArr);

                    Log.i(TAG, "JSON OBJECT= " + jsonObj.toString());

                    StringEntity se = new StringEntity(Encryptor.encrypt(SECRET_KEY, iv, jsonObj.toString()));
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    post.addHeader("x-precious-encryption-iv", iv);
                    post.addHeader("x-precious-apikey", preferences.getString("apiKey","?"));

                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if (response != null) {
                        if(response.getStatusLine().getStatusCode()==200) {
                            Header[] headers = response.getAllHeaders();
                            String iv = "";
                            for (int i = 0; i < headers.length; i++) {
                                if (headers[i].getName().equals("x-precious-encryption-iv"))
                                    iv = headers[i].getValue().toString();
                            }
                            String message = EntityUtils.toString(response.getEntity());
                            Log.i(TAG, "Encrypted message is: " + Encryptor.decrypt(SECRET_KEY, iv, message));
                        }
                        else{
                            Log.e(TAG,"Server error response: "+EntityUtils.toString(response.getEntity()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Cannot Establish Connection");
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }

    /**
     *
     */
    public static void login() {
        Thread t = new Thread() {

            public void run() {

                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);//Timeout Limit
                HttpResponse response;
                JSONObject json = new JSONObject();

                try {
                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    HttpPost post = new HttpPost(serverURLapi.concat(loginSegment));
                    json.put("email", preferences.getString("email","?"));
                    json.put("password", preferences.getString("password","?"));
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    Log.i(TAG, "Executing request get to: " + serverURLapi.concat(loginSegment));
                    response = client.execute(post);
                    /*Checking response */
                    if (response != null) {
//                        Header [] headers = response.getAllHeaders();
//                        String iv="";
//                        for (int i=0; i<headers.length;i++) {
//                            if(headers[i].getName().equals("x-precious-encryption-iv"))
//                                iv = headers[i].getValue().toString();
//                        }
//                        String message = EntityUtils.toString(response.getEntity());
//                        Log.i(TAG, "Login response is: " + EntityUtils.toString(response.getEntity()));
                        String responseString = EntityUtils.toString(response.getEntity());
                        if(responseString.equals("Invalid Login Credentials")
                                || responseString.equals("Invalid Login Parameters")) {
                            Toast.makeText(mContext,responseString,Toast.LENGTH_LONG).show();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isUserLoggedIn", false);
                            editor.apply();
                            Log.i(TAG, "User not logged in");
                        }
                        else{
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isUserLoggedIn", true);
                            editor.apply();
                            Log.i(TAG, "User logged in");
                            upUtils.saveLoginInfo(responseString);
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.logged_in),Toast.LENGTH_LONG).show();
                            Intent i = new Intent(mContext,ui.precious.comnet.aalto.precious.ui_MainActivity.class);
                            mContext.startActivity(i);
                        }
                        Log.i(TAG,"Server response: "+responseString);
                    }
                    else{
                        Log.i(TAG,"Server response was NULL");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Cannot Establish Connection");
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }

    public static void saveLoginInfo(String response){
        SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            JSONObject jObject = new JSONObject(response);
            Iterator<String> keys = jObject.keys();

            while( keys.hasNext() ) {
                String key = keys.next();
                String value = jObject.getString(key);
                Log.i(TAG,"key=_"+key+"_value=_"+value);
                editor.putString(key,value);
            }
        } catch (Exception e){
            Log.e(TAG,"",e);
        }
        editor.apply();
    }

    public static void setContext(Context context){
        mContext=context;
    }

}
