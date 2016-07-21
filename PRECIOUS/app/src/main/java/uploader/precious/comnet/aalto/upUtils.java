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

import java.util.ArrayList;
import java.util.Iterator;

import aalto.comnet.thepreciousproject.R;

public class upUtils {


    public static final String PREFS_NAME = "UploaderPreferences";
    public static final String UP_PREFS_NAME = "UploaderPreferences";

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
                            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                            if (currentapiVersion > 22)
                                sql_db.precious.comnet.aalto.DBHelper.copyLogFile();
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
                            preferences.getString("birthdate", "?") + "_" +
                            preferences.getString("activityClass", "?") + "_" +
                            preferences.getString("nickname", "?") + "_" +
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
                            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                            Log.i(TAG,"VERSION:"+currentapiVersion);
                            if (currentapiVersion > 22)
                                sql_db.precious.comnet.aalto.DBHelper.copyLogFile();
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
    protected static void sendAutomaticPADataToPreciousServer() {

        Log.i(TAG,"sendAutomaticPADataToPreciousServer");
        final String iv = "12345678901234561234567890123456";


        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread


                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    long sendFrom=preferences.getLong("LastStoredTimestampPAauto",0);
                    long sendTo=System.currentTimeMillis();
                    Log.i(TAG, " sendAutomaticPADataToPreciousServer Sending from: " + sendFrom);
//                    ui_MainActivity.dbhelp.getAllPA();//TODO this might be wrong
                    ArrayList<ArrayList<Long>> paData = sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getPAdata(sendFrom, sendTo);

                    for (int i=0; i<paData.size();i++) {
//                        Log.i(TAG, ("Walk data:"+paData.get(i).get(1)) + "");
                        long from = (paData.get(i).get(0));
                        Log.i(TAG,"Sending auto pa dat: "+from);
                        long to = from + 24 * 3600 * 1000 - 1;
                        int still_duration_s = (paData.get(i).get(1)).intValue();
                        int walk_duration_s = (paData.get(i).get(2)).intValue();
                        int bike_duration_s = (paData.get(i).get(3)).intValue();
                        int vehicle_duration_s = (paData.get(i).get(4)).intValue();
                        int run_duration_s = (paData.get(i).get(5)).intValue();
                        int tilt_duration_s = (paData.get(i).get(6)).intValue();
                        int goal_steps = (paData.get(i).get(7)).intValue();
                        String id = Long.toString(from);
                        HttpPost post = new HttpPost(userDataURL);
                        //This is used for the whole data to be send (for one day)
                        JSONObject jsonObjDATA = new JSONObject();
                        JSONArray jsonDataArray = new JSONArray();
                        JSONObject jsonObj = new JSONObject(); //Object data
                        jsonObj.put("key", "AUTO_ACTIVITY");
                        jsonObj.put("from", from);
                        jsonObj.put("to", to);
                        jsonObj.put("id", id);
                        //DEFINE VALUE ARRAY
                        JSONArray jsonValueArray = new JSONArray();
                        //ADD STILL DATA TO ARRAY
                        JSONObject pnObj_Still = new JSONObject();
                        pnObj_Still.put("type", "Still");
                        pnObj_Still.put("duration_sec", still_duration_s);
                        jsonValueArray.put(pnObj_Still);
                        //ADD WALK DATA TO ARRAY
                        JSONObject pnObj_Walk = new JSONObject();
                        pnObj_Walk.put("type", "Walk");
                        pnObj_Walk.put("duration_sec", walk_duration_s);
                        jsonValueArray.put(pnObj_Walk);
                        //ADD BIKE DATA TO ARRAY
                        JSONObject pnObj_Bike = new JSONObject();
                        pnObj_Bike.put("type", "Bike");
                        pnObj_Bike.put("duration_sec", bike_duration_s);
                        jsonValueArray.put(pnObj_Bike);
                        //ADD VEHICLE DATA TO ARRAY
                        JSONObject pnObj_Vehicle = new JSONObject();
                        pnObj_Vehicle.put("type", "Vehicle");
                        pnObj_Vehicle.put("duration_sec", vehicle_duration_s);
                        jsonValueArray.put(pnObj_Vehicle);
                        //ADD RUN DATA TO ARRAY
                        JSONObject pnObj_Run = new JSONObject();
                        pnObj_Run.put("type", "Run");
                        pnObj_Run.put("duration_sec", run_duration_s);
                        jsonValueArray.put(pnObj_Run);
                        //ADD TILT DATA TO ARRAY
                        JSONObject pnObj_Tilt = new JSONObject();
                        pnObj_Tilt.put("type", "Tilt");
                        pnObj_Tilt.put("duration_sec", tilt_duration_s);
                        jsonValueArray.put(pnObj_Tilt);
                        //ADD GOAL DATA TO ARRAY
                        JSONObject pnObj_Goal = new JSONObject();
                        pnObj_Goal.put("type", "Goal");
                        pnObj_Goal.put("steps", goal_steps);
                        jsonValueArray.put(pnObj_Goal);
                        //ADD VALUE ARRAY TO JSON OBJECT
                        jsonObj.put("value", jsonValueArray);
                        //ADD THE DAY TO THE DATA ARRAY
                        jsonDataArray.put(jsonObj);
                        //FORM THE DATA JSON OBJECT
                        jsonObjDATA.put("data", jsonDataArray);
                        Log.i(TAG, "JSON OBJECT= " + jsonObjDATA.toString());

                        StringEntity se = new StringEntity(Encryptor.encrypt(SECRET_KEY, iv, jsonObjDATA.toString()));
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                        SharedPreferences preferences = mContext.getSharedPreferences(PA_SOC_PREFS_NAME, 0);
                        post.addHeader("x-precious-encryption-iv", iv);
                        post.addHeader("x-precious-apikey", preferences.getString("apiKey", "?"));

                        post.setEntity(se);
                        response = client.execute(post);

                    /*Checking response */
                        if (response != null) {
                            if (response.getStatusLine().getStatusCode() == 200) {
                                Header[] headers = response.getAllHeaders();
                                String iv = "";
                                for (int j = 0; j < headers.length; j++) {
                                    if (headers[j].getName().equals("x-precious-encryption-iv"))
                                        iv = headers[j].getValue().toString();
                                }
                                String message = EntityUtils.toString(response.getEntity());
                                message = Encryptor.decrypt(SECRET_KEY, iv, message);
                                Log.i(TAG, "Encrypted message is: " + message);
                                Log.i(TAG,"Compare with"+"[\""+id+"\"]");
                                if (!message.equals("[\""+id+"\"]")) {
                                    Log.e(TAG, "BAD SERVER RESPONSE");
                                    return;
                                }
                                else{
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putLong("LastStoredTimestampPAauto",from+1);
                                    editor.commit();
                                }
                            } else {
                                Log.e(TAG, "Server error response: " + EntityUtils.toString(response.getEntity()));
                            }
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
    protected static void sendManualPADataToPreciousServer() {

        Log.i(TAG,"sendManualPADataToPreciousServer");
        final String iv = "12345678901234561234567890123456";


        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread


                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    long sendFrom=preferences.getLong("LastStoredTimestampPAmanual", 0);
                    long sendTo=System.currentTimeMillis();
                    Log.i(TAG, "sendManualPADataToPreciousServer Sending from: " + sendFrom);
                    ArrayList<ArrayList<Long>> manualPaData =  sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getManPA(sendFrom, sendTo);



                    for (int i=0; i<manualPaData.size();i++) {
//                        Log.i(TAG, ("Walk data:"+paData.get(i).get(1)) + "");
                        long from = (manualPaData.get(i).get(0));
                        Log.i(TAG,"Sending manual pa dat: "+from);
                        long to = from + 24 * 3600 * 1000 - 1;
                        long pa_type = (manualPaData.get(i).get(1));
                        long pa_intensity = (manualPaData.get(i).get(2));
                        long pa_duration = (manualPaData.get(i).get(3));
                        long pa_equiv_steps = (manualPaData.get(i).get(4));

                        String id = Long.toString(from);
                        HttpPost post = new HttpPost(userDataURL);

                        //This is used for the whole data to be send (all the days)
                        JSONObject jsonObjDATA = new JSONObject();
                        JSONArray jsonDataArray = new JSONArray();

                        JSONObject jsonObj = new JSONObject(); //Object data
                        jsonObj.put("key", "MANUAL_ACTIVITY");
                        jsonObj.put("from", from);
                        jsonObj.put("to", from+pa_duration*1000);
                        jsonObj.put("id", id);
                        //DEFINE VALUE ARRAY
                        JSONArray jsonValueArray = new JSONArray();
                        //ADD MANUAL PA DATA TO ARRAY
                        JSONObject pnObj_manualPA = new JSONObject();
                        pnObj_manualPA.put("pa_type", pa_type);
                        pnObj_manualPA.put("pa_intensity", pa_intensity);
                        pnObj_manualPA.put("pa_duration", pa_duration);
                        pnObj_manualPA.put("pa_equiv_steps", pa_equiv_steps);
                        jsonValueArray.put(pnObj_manualPA);
                        //ADD VALUE ARRAY TO JSON OBJECT
                        jsonObj.put("value", jsonValueArray);
                        //ADD THE DAY TO THE DATA ARRAY
                        jsonDataArray.put(jsonObj);
                        //FORM THE DATA JSON OBJECT
                        jsonObjDATA.put("data", jsonDataArray);
                        Log.i(TAG, "JSON OBJECT= " + jsonObjDATA.toString());

                        StringEntity se = new StringEntity(Encryptor.encrypt(SECRET_KEY, iv, jsonObjDATA.toString()));
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                        SharedPreferences preferences = mContext.getSharedPreferences(PA_SOC_PREFS_NAME, 0);
                        post.addHeader("x-precious-encryption-iv", iv);
                        post.addHeader("x-precious-apikey", preferences.getString("apiKey", "?"));

                        post.setEntity(se);
                        response = client.execute(post);

                    /*Checking response */
                        if (response != null) {
                            if (response.getStatusLine().getStatusCode() == 200) {
                                Header[] headers = response.getAllHeaders();
                                String iv = "";
                                for (int j = 0; j < headers.length; j++) {
                                    if (headers[j].getName().equals("x-precious-encryption-iv"))
                                        iv = headers[j].getValue().toString();
                                }
                                String message = EntityUtils.toString(response.getEntity());
                                message = Encryptor.decrypt(SECRET_KEY, iv, message);
                                Log.i(TAG, "Encrypted message is: " + message);
                                Log.i(TAG,"Compare with"+"[\""+id+"\"]");
                                if (!message.equals("[\""+id+"\"]")) {
                                    Log.e(TAG, "BAD SERVER RESPONSE");
                                    return;
                                }
                                else{
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putLong("LastStoredTimestampPAmanual",from+1);
                                    editor.commit();
                                }
                            } else {
                                Log.e(TAG, "Server error response: " + EntityUtils.toString(response.getEntity()));
                            }
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
    protected static void sendFoodDataToPreciousServer() {

        Log.i(TAG,"sendFoodDataToPreciousServer");
        final String iv = "12345678901234561234567890123456";


        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread


                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    long sendFrom=preferences.getLong("LastStoredTimestampFood", 0);
                    long sendTo=System.currentTimeMillis();
                    Log.i(TAG, " sendFoodDataToPreciousServer Sending from: " + sendFrom);
                    ArrayList<ArrayList<Long>> foodData =  sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getFood(sendFrom, sendTo);
                    ArrayList<ArrayList<String>> foodNames =  sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getFoodNames(sendFrom, sendTo);

                    for (int i=0; i<foodData.size();i++) {
//                        Log.i(TAG, ("Walk data:"+paData.get(i).get(1)) + "");
                        long from = (foodData.get(i).get(0));
                        Log.i(TAG,"Sending manual pa dat: "+from);
                        long to = from + 1;
                        long food_type = (foodData.get(i).get(1));
                        long food_amount = (foodData.get(i).get(2));
                        String food_name = (foodNames.get(i).get(0));

                        String id = Long.toString(from);
                        HttpPost post = new HttpPost(userDataURL);

                        //This is used for the whole data to be send (all the days)
                        JSONObject jsonObjDATA = new JSONObject();
                        JSONArray jsonDataArray = new JSONArray();

                        JSONObject jsonObj = new JSONObject(); //Object data
                        jsonObj.put("key", "FOOD_INTAKE");
                        jsonObj.put("from", from);
                        jsonObj.put("to", to);
                        jsonObj.put("id", id);
                        //DEFINE VALUE ARRAY
                        JSONArray jsonValueArray = new JSONArray();
                        //ADD MANUAL PA DATA TO ARRAY
                        JSONObject pnObj_manualPA = new JSONObject();
                        pnObj_manualPA.put("food_type", food_type);
                        pnObj_manualPA.put("food_amount", food_amount);
                        pnObj_manualPA.put("food_name", food_name);
                        jsonValueArray.put(pnObj_manualPA);
                        //ADD VALUE ARRAY TO JSON OBJECT
                        jsonObj.put("value", jsonValueArray);
                        //ADD THE DAY TO THE DATA ARRAY
                        jsonDataArray.put(jsonObj);
                        //FORM THE DATA JSON OBJECT
                        jsonObjDATA.put("data", jsonDataArray);
                        Log.i(TAG, "JSON OBJECT= " + jsonObjDATA.toString());

                        StringEntity se = new StringEntity(Encryptor.encrypt(SECRET_KEY, iv, jsonObjDATA.toString()));
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                        SharedPreferences preferences = mContext.getSharedPreferences(PA_SOC_PREFS_NAME, 0);
                        post.addHeader("x-precious-encryption-iv", iv);
                        post.addHeader("x-precious-apikey", preferences.getString("apiKey", "?"));

                        post.setEntity(se);
                        response = client.execute(post);

                    /*Checking response */
                        if (response != null) {
                            if (response.getStatusLine().getStatusCode() == 200) {
                                Header[] headers = response.getAllHeaders();
                                String iv = "";
                                for (int j = 0; j < headers.length; j++) {
                                    if (headers[j].getName().equals("x-precious-encryption-iv"))
                                        iv = headers[j].getValue().toString();
                                }
                                String message = EntityUtils.toString(response.getEntity());
                                message = Encryptor.decrypt(SECRET_KEY, iv, message);
                                Log.i(TAG, "Encrypted message is: " + message);
                                Log.i(TAG,"Compare with"+"[\""+id+"\"]");
                                if (!message.equals("[\""+id+"\"]")) {
                                    Log.e(TAG, "BAD SERVER RESPONSE");
                                    return;
                                }
                                else{
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putLong("LastStoredTimestampFood",from+1);
                                    editor.commit();
                                }
                            } else {
                                Log.e(TAG, "Server error response: " + EntityUtils.toString(response.getEntity()));
                            }
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
    protected static void sendFoodChallengeDataToPreciousServer() {

        Log.i(TAG,"sendFoodChallengeDataToPreciousServer");
        final String iv = "12345678901234561234567890123456";


        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread


                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    long sendFrom=preferences.getLong("LastStoredTimestampFoodChallenge", 0);
                    long sendTo=System.currentTimeMillis();
                    Log.i(TAG, " sendFoodChallengeDataToPreciousServer Sending from: " + sendFrom);
                    ArrayList<ArrayList<Long>> foodChallengeData =  sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getFoodChallenges(sendFrom, sendTo);



                    for (int i=0; i<foodChallengeData.size();i++) {
//                        Log.i(TAG, ("Walk data:"+paData.get(i).get(1)) + "");
                        long from = (foodChallengeData.get(i).get(0));
                        Log.i(TAG,"Sending manual pa dat: "+from);
                        long to = from + 1;

                        String id = Long.toString(from);
                        HttpPost post = new HttpPost(userDataURL);

                        //This is used for the whole data to be send (all the days)
                        JSONObject jsonObjDATA = new JSONObject();
                        JSONArray jsonDataArray = new JSONArray();

                        JSONObject jsonObj = new JSONObject(); //Object data
                        jsonObj.put("key", "FOOD_CHALLENGE");
                        jsonObj.put("from", from);
                        jsonObj.put("to", to);
                        jsonObj.put("id", id);
                        //DEFINE VALUE ARRAY
                        JSONArray jsonValueArray = new JSONArray();
                        //ADD MANUAL PA DATA TO ARRAY
                        JSONObject pnObj_foodChallenge = new JSONObject();
                        pnObj_foodChallenge.put("challenge_0", foodChallengeData.get(i).get(1));
                        pnObj_foodChallenge.put("challenge_1", foodChallengeData.get(i).get(2));
                        pnObj_foodChallenge.put("challenge_2", foodChallengeData.get(i).get(3));
                        pnObj_foodChallenge.put("challenge_3", foodChallengeData.get(i).get(4));
                        pnObj_foodChallenge.put("challenge_4", foodChallengeData.get(i).get(5));
                        pnObj_foodChallenge.put("challenge_5", foodChallengeData.get(i).get(6));
                        pnObj_foodChallenge.put("challenge_6", foodChallengeData.get(i).get(7));
                        pnObj_foodChallenge.put("challenge_7", foodChallengeData.get(i).get(8));
                        pnObj_foodChallenge.put("challenge_8", foodChallengeData.get(i).get(9));
                        pnObj_foodChallenge.put("challenge_9", foodChallengeData.get(i).get(10));
                        jsonValueArray.put(pnObj_foodChallenge);
                        //ADD VALUE ARRAY TO JSON OBJECT
                        jsonObj.put("value", jsonValueArray);
                        //ADD THE DAY TO THE DATA ARRAY
                        jsonDataArray.put(jsonObj);
                        //FORM THE DATA JSON OBJECT
                        jsonObjDATA.put("data", jsonDataArray);
                        Log.i(TAG, "JSON OBJECT= " + jsonObjDATA.toString());

                        StringEntity se = new StringEntity(Encryptor.encrypt(SECRET_KEY, iv, jsonObjDATA.toString()));
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                        SharedPreferences preferences = mContext.getSharedPreferences(PA_SOC_PREFS_NAME, 0);
                        post.addHeader("x-precious-encryption-iv", iv);
                        post.addHeader("x-precious-apikey", preferences.getString("apiKey", "?"));

                        post.setEntity(se);
                        response = client.execute(post);

                    /*Checking response */
                        if (response != null) {
                            if (response.getStatusLine().getStatusCode() == 200) {
                                Header[] headers = response.getAllHeaders();
                                String iv = "";
                                for (int j = 0; j < headers.length; j++) {
                                    if (headers[j].getName().equals("x-precious-encryption-iv"))
                                        iv = headers[j].getValue().toString();
                                }
                                String message = EntityUtils.toString(response.getEntity());
                                message = Encryptor.decrypt(SECRET_KEY, iv, message);
                                Log.i(TAG, "Encrypted message is: " + message);
                                Log.i(TAG,"Compare with"+"[\""+id+"\"]");
                                if (!message.equals("[\""+id+"\"]")) {
                                    Log.e(TAG, "BAD SERVER RESPONSE");
                                    return;
                                }
                                else{
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putLong("LastStoredTimestampFoodChallenge",from+1);
                                    editor.commit();
                                }
                            } else {
                                Log.e(TAG, "Server error response: " + EntityUtils.toString(response.getEntity()));
                            }
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
    protected static void sendAppUsageDataToPreciousServer() {

        Log.i(TAG,"sendAppUsageDataToPreciousServer");
        final String iv = "12345678901234561234567890123456";


        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread


                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    long sendFrom=preferences.getLong("LastStoredTimestampAppUsage", 0);
                    long sendTo=System.currentTimeMillis();
                    Log.i(TAG, " sendAppUsageDataToPreciousServer Sending from: " + sendFrom);
                    ArrayList<ArrayList<String>> appUsageData =  sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getAppUsage(sendFrom, sendTo);



                    for (int i=0; i<appUsageData.size();i++) {
//                        Log.i(TAG, ("Walk data:"+paData.get(i).get(1)) + "");
                        long from = (Long.parseLong(appUsageData.get(i).get(0)));
                        Log.i(TAG,"Sending manual pa dat: "+from);
                        long to = from + 1;

                        String id = Long.toString(from);
                        HttpPost post = new HttpPost(userDataURL);

                        //This is used for the whole data to be send (all the days)
                        JSONObject jsonObjDATA = new JSONObject();
                        JSONArray jsonDataArray = new JSONArray();

                        JSONObject jsonObj = new JSONObject(); //Object data
                        jsonObj.put("key", "APP_USAGE");
                        jsonObj.put("from", from);
                        jsonObj.put("to", to);
                        jsonObj.put("id", id);
                        //DEFINE VALUE ARRAY
                        JSONArray jsonValueArray = new JSONArray();
                        //ADD MANUAL PA DATA TO ARRAY
                        JSONObject pnObj_appUsage = new JSONObject();
                        pnObj_appUsage.put("subapp", appUsageData.get(i).get(1));
                        pnObj_appUsage.put("status", appUsageData.get(i).get(2));
                        jsonValueArray.put(pnObj_appUsage);
                        //ADD VALUE ARRAY TO JSON OBJECT
                        jsonObj.put("value", jsonValueArray);
                        //ADD THE DAY TO THE DATA ARRAY
                        jsonDataArray.put(jsonObj);
                        //FORM THE DATA JSON OBJECT
                        jsonObjDATA.put("data", jsonDataArray);
                        Log.i(TAG, "JSON OBJECT= " + jsonObjDATA.toString());

                        StringEntity se = new StringEntity(Encryptor.encrypt(SECRET_KEY, iv, jsonObjDATA.toString()));
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                        SharedPreferences preferences = mContext.getSharedPreferences(PA_SOC_PREFS_NAME, 0);
                        post.addHeader("x-precious-encryption-iv", iv);
                        post.addHeader("x-precious-apikey", preferences.getString("apiKey", "?"));

                        post.setEntity(se);
                        response = client.execute(post);

                    /*Checking response */
                        if (response != null) {
                            if (response.getStatusLine().getStatusCode() == 200) {
                                Header[] headers = response.getAllHeaders();
                                String iv = "";
                                for (int j = 0; j < headers.length; j++) {
                                    if (headers[j].getName().equals("x-precious-encryption-iv"))
                                        iv = headers[j].getValue().toString();
                                }
                                String message = EntityUtils.toString(response.getEntity());
                                message = Encryptor.decrypt(SECRET_KEY, iv, message);
                                Log.i(TAG, "Encrypted message is: " + message);
                                Log.i(TAG,"Compare with"+"[\""+id+"\"]");
                                if (!message.equals("[\""+id+"\"]")) {
                                    Log.e(TAG, "BAD SERVER RESPONSE");
                                    return;
                                }
                                else{
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putLong("LastStoredTimestampAppUsage",from+1);
                                    editor.commit();
                                }
                            } else {
                                Log.e(TAG, "Server error response: " + EntityUtils.toString(response.getEntity()));
                            }
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

    /**
     *
     */
    public static void sendGroupIDToPreciousServer(final int groupID) {

        Log.i(TAG,"sendGroupIDToPreciousServer");
        final String iv = "12345678901234561234567890123456";


        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); //For Preparing Message Pool for the child Thread


                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response;
                try {
                    SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME, 0);
                    long sendFrom=preferences.getLong("LastStoredTimestampFoodChallenge", 0);
                    long sendTo=System.currentTimeMillis();
                    Log.i(TAG, " sendFoodChallengeDataToPreciousServer Sending from: " + sendFrom);
                    ArrayList<ArrayList<Long>> foodChallengeData =  sql_db.precious.comnet.aalto.DBHelper.getInstance(mContext).getFoodChallenges(sendFrom, sendTo);



                        HttpPost post = new HttpPost(userDataURL);

                        //This is used for the whole data to be send (all the days)
                        JSONObject jsonObjDATA = new JSONObject();
                        JSONArray jsonDataArray = new JSONArray();
                        String id = Long.toString(System.currentTimeMillis());
                        JSONObject jsonObj = new JSONObject(); //Object data
                        jsonObj.put("key", "GROUP_ID");
                        jsonObj.put("from", System.currentTimeMillis());
                        jsonObj.put("to", System.currentTimeMillis());
                        jsonObj.put("id",id);
                        //DEFINE VALUE ARRAY
                        JSONArray jsonValueArray = new JSONArray();
                        //ADD MANUAL PA DATA TO ARRAY
                        JSONObject pnObj_GroupID = new JSONObject();

                        pnObj_GroupID.put("GROUP_ID", groupID);
                        jsonValueArray.put(pnObj_GroupID);
                        //ADD VALUE ARRAY TO JSON OBJECT
                        jsonObj.put("value", jsonValueArray);
                        //ADD THE DAY TO THE DATA ARRAY
                        jsonDataArray.put(jsonObj);
                        //FORM THE DATA JSON OBJECT
                        jsonObjDATA.put("data", jsonDataArray);
                        Log.i(TAG, "JSON OBJECT= " + jsonObjDATA.toString());

                        StringEntity se = new StringEntity(Encryptor.encrypt(SECRET_KEY, iv, jsonObjDATA.toString()));
                        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
//                        SharedPreferences preferences = mContext.getSharedPreferences(PA_SOC_PREFS_NAME, 0);
                        post.addHeader("x-precious-encryption-iv", iv);
                        post.addHeader("x-precious-apikey", preferences.getString("apiKey", "?"));

                        post.setEntity(se);
                        response = client.execute(post);

                    /*Checking response */
                        if (response != null) {
                            if (response.getStatusLine().getStatusCode() == 200) {
                                Header[] headers = response.getAllHeaders();
                                String iv = "";
                                for (int j = 0; j < headers.length; j++) {
                                    if (headers[j].getName().equals("x-precious-encryption-iv"))
                                        iv = headers[j].getValue().toString();
                                }
                                String message = EntityUtils.toString(response.getEntity());
                                message = Encryptor.decrypt(SECRET_KEY, iv, message);
                                Log.i(TAG, "Encrypted message is: " + message);
                                Log.i(TAG,"Compare with"+"[\""+id+"\"]");
                                if (!message.equals("[\""+id+"\"]")) {
                                    Log.e(TAG, "BAD SERVER RESPONSE");
                                    return;
                                }
                                else{
                                    SharedPreferences preferences_up = mContext.getSharedPreferences(UP_PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = preferences_up.edit();
                                    editor.putBoolean("GroupIDsent",true);
                                    editor.commit();

                                }
                            } else {
                                Log.e(TAG, "Server error response: " + EntityUtils.toString(response.getEntity()));
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


    public static void setContext(Context context){
        mContext=context;
    }

    public static Bitmap getBitmap(){
        return bitmap;
    }



}
