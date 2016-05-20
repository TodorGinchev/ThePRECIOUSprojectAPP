package uploader.precious.comnet.aalto;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.Base64;
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
    public static final String serverURLapi = "https://precious.entertain.univie.ac.at/api";
    public static final String loginSegment = "/login/";
    public static final String loginURL = serverURLapi.concat(loginSegment);
    public static final String UserSegment = "/user/";
    public static final String userURL = serverURLapi.concat(UserSegment);
    public static final String UserDataSegment = "/user/data";
    public static final String userDataURL = serverURLapi.concat(UserDataSegment);
    public static final String RegistrationSegment = "/register/";
    public static final String registrationURL = serverURLapi.concat(RegistrationSegment);
    public static Context mContext;
    public static Bitmap bitmap;



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
                    HttpPost post = new HttpPost(loginURL);
                    json.put("email", preferences.getString("email","?"));
                    json.put("password", preferences.getString("password","?"));
                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    Log.i(TAG, "Executing request get to: " + serverURLapi.concat(loginSegment));
                    response = client.execute(post);
                    /*Checking response */
                    if (response != null) {
                        //Statuscode 500, Invalid Login Parameters (if no email or password is provided)
                        //Statuscode 500, Invalid Login Credentials (if user doesn’t exist or password is wrong)
                        if(response.getStatusLine().getStatusCode()==200){
                            String responseString = EntityUtils.toString(response.getEntity());
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isUserLoggedIn", true);
                            editor.apply();
                            Log.i(TAG, "User logged in");
                            upUtils.saveLoginInfo(responseString);
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.logged_in),Toast.LENGTH_LONG).show();
                            Intent i = new Intent(mContext,ui.precious.comnet.aalto.precious.ui_MainActivity.class);
                            mContext.startActivity(i);
                        }
                        else{
                            String responseString = EntityUtils.toString(response.getEntity());
                            Toast.makeText(mContext,responseString,Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Log.i(TAG, "Server response was NULL");
                        Toast.makeText(mContext,"Server connection problem!",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "Cannot Establish Connection");
                    Toast.makeText(mContext,"Server connection problem!",Toast.LENGTH_LONG).show();
                }
                Looper.loop(); //Loop in the message queue
            }
        };
        t.start();
    }

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
                    HttpPost post = new HttpPost(registrationURL);
                    json.put("email", preferences.getString("email","?"));
                    json.put("password", preferences.getString("password","?"));
                    json.put("weight", preferences.getString("weight","?"));
                    json.put("height", preferences.getString("height","?"));
                    json.put("activityClass", preferences.getString("activityClass","?"));
                    json.put("nickname", preferences.getString("nickname","?"));
                    json.put("birthdate", preferences.getString("birthdate","?"));
                    json.put("gender", preferences.getString("gender", "?"));

                    Log.i(TAG, preferences.getString("email", "?") + "_" +
                            preferences.getString("password", "?") + "_" +
                            preferences.getString("weight", "?") + "_" +
                            preferences.getString("height", "?") + "_" +
                            preferences.getString("activityClass", "?") + "_" +
                            preferences.getString("nickname", "?") + "_" +
                            preferences.getString("birthdate", "?") + "_" +
                            preferences.getString("gender", "?"));


                    StringEntity se = new StringEntity(json.toString());
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                    post.setEntity(se);
                    response = client.execute(post);

                    /*Checking response */
                    if (response != null) {
                        if(response.getStatusLine().getStatusCode()==200){
                            String responseString = EntityUtils.toString(response.getEntity());
                            Log.i(TAG, "RESPONSE IS: " + responseString);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("isUserLoggedIn", true);
                            editor.apply();
                            upUtils.saveLoginInfo(responseString);
                            Toast.makeText(mContext,mContext.getResources().getString(R.string.logged_in),Toast.LENGTH_LONG).show();
                            Intent i = new Intent(mContext,ui.precious.comnet.aalto.precious.ui_MainActivity.class);
                            mContext.startActivity(i);
                        }
                        else {
                            String responseString = EntityUtils.toString(response.getEntity());
                            Toast.makeText(mContext,responseString,Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(mContext,"Server connection problem!",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Server connection problem!",Toast.LENGTH_LONG).show();
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
    public static void getBGimage(final String URLparams) {
        //TODO check status code from HTTPS response, if it's 200, go on, otherwise, log
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
//                JSONObject json = new JSONObject();
                SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                try {
                    HttpGet get = new HttpGet(uploader.precious.comnet.aalto.upUtils.userURL.concat(URLparams));
                    Log.i(TAG, "Requesting user info with apiKey=_" + preferences.getString("apiKey","?"));
                    get.setHeader("x-precious-apikey", preferences.getString("apiKey","?"));
                    response = client.execute(get);
                    /*Checking response */
                    if (response != null) {
                        if (response.getStatusLine().getStatusCode() == 200) {
                            Header[] headers = response.getAllHeaders();
                            String iv = "";
                            for (int i = 0; i < headers.length; i++) {
                                if (headers[i].getName().equals("x-precious-encryption-iv"))
                                    iv = headers[i].getValue().toString();
                            }
                            String message = EntityUtils.toString(response.getEntity());

                            Log.i(TAG, "Encrypted message is: " + Encryptor.decrypt(uploader.precious.comnet.aalto.upUtils.SECRET_KEY, iv, message));
                            JSONArray jArray = new JSONArray(Encryptor.decrypt(uploader.precious.comnet.aalto.upUtils.SECRET_KEY, iv, message));
                            if (jArray.length() < 1)
                                Toast.makeText(mContext, R.string.no_fb_data, Toast.LENGTH_LONG).show();
                            else{
                                JSONObject jObject = jArray.getJSONObject(0);
                                Iterator<String> keys = jObject.keys();

                                while (keys.hasNext()) {
                                    String key = keys.next();
                                    if (key.equals("value")) {
                                        String data = jObject.getString(key);
                                        byte[] bytes = Base64.decode(data, Base64.DEFAULT);
                                        Log.i(TAG, "LENGTH=_" + bytes.length);
                                        bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        Intent i = new Intent(mContext, firstbeat.precious.comnet.aalto.fbMainActivity.class);
                                        mContext.startActivity(i);
                                    }
                                }
                        }
                        }
                        else{
                            String responseString = EntityUtils.toString(response.getEntity());
                            Toast.makeText(mContext, responseString, Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(mContext,R.string.connection_problem,Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,R.string.connection_problem,Toast.LENGTH_LONG).show();
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
    public static void getJson(final String URLparams) {
        //TODO check status code from HTTPS response, if it's 200, go on, otherwise, log
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread
                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
//                JSONObject json = new JSONObject();
                SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                try {
                    HttpGet get = new HttpGet(userURL.concat(URLparams));
                    Log.i(TAG, "Requesting user info with apiKey=_" + preferences.getString("apiKey","?"));
                    get.setHeader("x-precious-apikey", preferences.getString("apiKey","?"));
                    response = client.execute(get);
                    /*Checking response */
                    if (response != null) {
                        if (response.getStatusLine().getStatusCode() == 200) {
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
                            String responseString = EntityUtils.toString(response.getEntity());
                            Toast.makeText(mContext,responseString,Toast.LENGTH_LONG).show();
                        }
                    }
                    else{
                        Toast.makeText(mContext,"Server connection problem!",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext,"Server connection problem!",Toast.LENGTH_LONG).show();
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
    protected static void sendDataToPreciousServer(final long from, final long to, final int still_duration_s, final int walk_duration_s, final int bike_duration_s, final int vehicle_duration_s, final int run_duration_s, final int tilt_duration_s, final int goal_steps) {

        final String iv = "12345678901234561234567890123456";

        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    HttpPost post = new HttpPost(userDataURL);

                    //This is used for the whole data to be send (all the days)
                    JSONObject jsonObjDATA = new JSONObject();
                    JSONArray jsonDataArray = new JSONArray();


                    JSONObject jsonObj = new JSONObject(); //Object data
                    jsonObj.put("key", "AUTO_ACTIVITY");
                    jsonObj.put("from", from);
                    jsonObj.put("to", to);
                    jsonObj.put("id", "-1");

                    //DEFINE VALUE ARRAY
                    JSONArray jsonValueArray = new JSONArray();

                    //ADD STILL DATA TO ARRAY
                    JSONObject pnObj_Still = new JSONObject();
                    pnObj_Still.put("type","Still");
                    pnObj_Still.put("duration_sec",still_duration_s);
                    jsonValueArray.put(pnObj_Still);

                    //ADD WALK DATA TO ARRAY
                    JSONObject pnObj_Walk = new JSONObject();
                    pnObj_Walk.put("type","Walk");
                    pnObj_Walk.put("duration_sec",walk_duration_s);
                    jsonValueArray.put(pnObj_Walk);

                    //ADD BIKE DATA TO ARRAY
                    JSONObject pnObj_Bike = new JSONObject();
                    pnObj_Bike.put("type","Bike");
                    pnObj_Bike.put("duration_sec",bike_duration_s);
                    jsonValueArray.put(pnObj_Bike);

                    //ADD VEHICLE DATA TO ARRAY
                    JSONObject pnObj_Vehicle = new JSONObject();
                    pnObj_Vehicle.put("type","Vehicle");
                    pnObj_Vehicle.put("duration_sec",vehicle_duration_s);
                    jsonValueArray.put(pnObj_Vehicle);

                    //ADD RUN DATA TO ARRAY
                    JSONObject pnObj_Run = new JSONObject();
                    pnObj_Run.put("type","Run");
                    pnObj_Run.put("duration_sec",run_duration_s);
                    jsonValueArray.put(pnObj_Run);

                    //ADD TILT DATA TO ARRAY
                    JSONObject pnObj_Tilt = new JSONObject();
                    pnObj_Tilt.put("type","Tilt");
                    pnObj_Tilt.put("duration_sec",tilt_duration_s);
                    jsonValueArray.put(pnObj_Tilt);

                    //ADD GOAL DATA TO ARRAY
                    JSONObject pnObj_Goal = new JSONObject();
                    pnObj_Goal.put("type","Goal");
                    pnObj_Goal.put("steps",goal_steps);
                    jsonValueArray.put(pnObj_Goal);

                    //ADD VALUE ARRAY TO JSON OBJECT
                    jsonObj.put("value", jsonValueArray);


                    //ADD THE DAY TO THE DATA ARRAY
                    jsonDataArray.put(jsonObj);

                    //FORM THE DATA JSON OBJECT
                    jsonObjDATA.put("data",jsonDataArray);



                    Log.i(TAG, "JSON OBJECT= " + jsonObjDATA.toString());

                    StringEntity se = new StringEntity(Encryptor.encrypt(SECRET_KEY, iv, jsonObjDATA.toString()));
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

    public static Bitmap getBitmap(){
        return bitmap;
    }



}
