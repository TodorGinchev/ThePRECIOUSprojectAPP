package sql_db.precious.comnet.aalto;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import ui.precious.comnet.aalto.precious.PRECIOUS_APP;
import ui.precious.comnet.aalto.precious.ui_MainActivity;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";
    //GOALS
    public static final String DATABASE_NAME = "precious.db";

    //For the PA recognition
    public static final String TABLE_NAME_PA = "physicalActivityDayOverview";
    public static final String PA_COLUMN_TIMESTAMP = "timestamp";
    public static final String PA_COLUMN_STILL = "still";
    public static final String PA_COLUMN_WALK = "walk";
    public static final String PA_COLUMN_BICYCLE = "bicycle";
    public static final String PA_COLUMN_VEHICLE = "vehicle";
    public static final String PA_COLUMN_RUN = "run";
    public static final String PA_COLUMN_TILTING = "tilting";
    public static final String PA_COLUMN_STEPSGOAL = "stepsgoal";
    //For the manual PA entry
    public static final String TABLE_NAME_PA_MANUAL = "physicalActivityManualEntry";
    public static final String PA_MAN_COLUMN_TIMESTAMP = "timestamp";
    public static final String PA_MAN_COLUMN_PA_TYPE = "physical_activity_type";
    public static final String PA_MAN_COLUMN_PA_INTENSITY = "physical_activity_intensity";
    public static final String PA_MAN_COLUMN_PA_DURATION = "physical_activity_duration_sec";
    public static final String PA_MAN_COLUMN_PA_STEPS = "physical_activity_steps";
    //For the plan PA entry
    public static final String TABLE_NAME_PA_PLAN = "physicalActivityPlannedEntry";
    public static final String PA_PLAN_COLUMN_TIMESTAMP = "timestamp";
    public static final String PA_PLAN_COLUMN_PA_TYPE = "physical_activity_type";
    public static final String PA_PLAN_COLUMN_PA_INTENSITY = "physical_activity_intensity";
    public static final String PA_PLAN_COLUMN_PA_DURATION = "physical_activity_duration_sec";
    public static final String PA_PLAN_COLUMN_PA_STEPS = "physical_activity_steps";
    //For food intake
    public static final String TABLE_NAME_FOOD = "foodIntake";
    public static final String FOOD_COLUMN_TIMESTAMP = "timestamp";
    public static final String FOOD_COLUMN_TYPE = "type";
    public static final String FOOD_COLUMN_NAME = "foodId";
    public static final String FOOD_COLUMN_AMOUNT = "amount";
    public static final String FOOD_COLUMN_PHOTO_ID = "photoId";
    //For food challenges
    public static final String TABLE_NAME_FOOD_CHALLENGE = "foodChallenge";
    public static final String FOOD_CHALLENGE_COLUMN_TIMESTAMP = "timestamp";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE0 = "value0";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE1 = "value1";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE2 = "value2";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE3 = "value3";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE4 = "value4";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE5 = "value5";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE6 = "value6";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE7 = "value7";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE8 = "value8";
    public static final String FOOD_CHALLENGE_COLUMN_VALUE9 = "value9";
    //App usage
    public static final String TABLE_NAME_APP_USAGE = "appUsage";
    public static final String APP_USAGE_COLUMN_TIMESTAMP = "timestamp";
    public static final String APP_USAGE_COLUMN_SUBAPP = "subapp";
    public static final String APP_USAGE_COLUMN_STATUS = "status";
    //Wearable
    public static final String TABLE_NAME_WEARABLE = "wearable";
    public static final String WEARABLE_COLUMN_TIMESTAMP = "timestamp";
    public static final String WEARABLE_COLUMN_CURRENT_ACUMUL_STEPS = "acumulSteps";
    public static final String WEARABLE_COLUMN_TOTAL_DAILY_STEPS = "dailySteps";
    public static final String WEARABLE_COLUMN_BATTERY_LEVEL = "batLevel";
    public static final String WEARABLE_COLUMN_BATTERY_CYCLES = "batCycles";
    public static final String WEARABLE_COLUMN_BATTERY_STATUS = "batStatus";
    public static final String WEARABLE_COLUMN_BATTERY_LAST_CHARGED = "batLastCharged";

    private static DBHelper sInstance;



//    private HashMap hp;

    public static synchronized DBHelper getInstance() {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(PRECIOUS_APP.getAppContext().getApplicationContext());
        }
        return sInstance;
    }

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        copyLogFile();
        createTablesIfNotExist(db);
    }

    private void createTablesIfNotExist(SQLiteDatabase db){
        Log.i(TAG, "Db createTablesIfNotExist");
        //Create PA table
        db.execSQL(
                "create table if not exists " + TABLE_NAME_PA +
                        " (" + PA_MAN_COLUMN_TIMESTAMP + " timestamp primary key, " + PA_COLUMN_STILL + " integer, "
                        + PA_COLUMN_WALK + " integer, " + PA_COLUMN_BICYCLE + " integer, "
                        + PA_COLUMN_VEHICLE + " integer, " + PA_COLUMN_RUN + " integer, " + PA_COLUMN_TILTING + " integer, " + PA_COLUMN_STEPSGOAL + " integer)"
        );

        //Create manual pa table
        db.execSQL(
                "create table if not exists " + TABLE_NAME_PA_MANUAL +
                        " (" + PA_COLUMN_TIMESTAMP + " timestamp primary key, " + PA_MAN_COLUMN_PA_TYPE + " integer, "
                        + PA_MAN_COLUMN_PA_INTENSITY + " integer, " + PA_MAN_COLUMN_PA_STEPS + " integer, "
                        + PA_MAN_COLUMN_PA_DURATION + " integer)"
        );
        //Create manual pa table
        db.execSQL(
                "create table if not exists " + TABLE_NAME_PA_PLAN +
                        " (" + PA_COLUMN_TIMESTAMP + " timestamp primary key, " + PA_PLAN_COLUMN_PA_TYPE + " integer, "
                        + PA_PLAN_COLUMN_PA_INTENSITY + " integer, " + PA_PLAN_COLUMN_PA_STEPS + " integer, "
                        + PA_PLAN_COLUMN_PA_DURATION + " integer)"
        );

        //Create food intake table
        db.execSQL(
                "create table if not exists " + TABLE_NAME_FOOD +
                        " (" + FOOD_COLUMN_TIMESTAMP + " timestamp primary key, " + FOOD_COLUMN_TYPE + " integer, "
                        + FOOD_COLUMN_NAME + " varchar(32), " + FOOD_COLUMN_AMOUNT + " integer, "
                        + FOOD_COLUMN_PHOTO_ID + " integer)"
        );

        //Create food challenge table
        db.execSQL(
                "create table if not exists " + TABLE_NAME_FOOD_CHALLENGE +
                        " (" +FOOD_CHALLENGE_COLUMN_TIMESTAMP + " timestamp PRIMARY KEY, " + FOOD_CHALLENGE_COLUMN_VALUE0 + " integer, "+ FOOD_CHALLENGE_COLUMN_VALUE1 + " integer, "+ FOOD_CHALLENGE_COLUMN_VALUE2 + " integer, "+ FOOD_CHALLENGE_COLUMN_VALUE3 + " integer, "+ FOOD_CHALLENGE_COLUMN_VALUE4 + " integer, "+ FOOD_CHALLENGE_COLUMN_VALUE5 + " integer, "+ FOOD_CHALLENGE_COLUMN_VALUE6 + " integer, "+ FOOD_CHALLENGE_COLUMN_VALUE7 + " integer, "+ FOOD_CHALLENGE_COLUMN_VALUE8 + " integer, "+ FOOD_CHALLENGE_COLUMN_VALUE9 + " integer)"
        );

        //Create app usage table
        db.execSQL(
                "create table if not exists " + TABLE_NAME_APP_USAGE +
                        " (" +APP_USAGE_COLUMN_TIMESTAMP + " timestamp PRIMARY KEY, " + APP_USAGE_COLUMN_SUBAPP + " varchar(32), "+ APP_USAGE_COLUMN_STATUS + " varchar(32) )"
        );

        //Create wearable table
        db.execSQL(
                "create table if not exists " + TABLE_NAME_WEARABLE +
                        " (" +WEARABLE_COLUMN_TIMESTAMP + " timestamp PRIMARY KEY, " + WEARABLE_COLUMN_CURRENT_ACUMUL_STEPS + " integer, " + WEARABLE_COLUMN_TOTAL_DAILY_STEPS + " integer, "+ WEARABLE_COLUMN_BATTERY_LEVEL + " integer, "+ WEARABLE_COLUMN_BATTERY_CYCLES+ " integer, "+ WEARABLE_COLUMN_BATTERY_STATUS + " varchar(32), "+ WEARABLE_COLUMN_BATTERY_LAST_CHARGED + " varchar(32) )"
        );

        Log.i(TAG, "Db created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "DB onUpgrade");
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PA_MANUAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PA_PLAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FOOD_CHALLENGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_APP_USAGE);
        onCreate(db);
    }

    /**
     *
     */
    public void dropAllTables() {
        Log.i(TAG, "DB onUpgrade");
        // TODO Auto-generated method stub
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PA_MANUAL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PA_PLAN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FOOD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FOOD_CHALLENGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_APP_USAGE);
        onCreate(db);
    }

    /**
     * GOALS
     */
    public boolean insertGoal  (long timestamp, int value)
    {
        Log.i(TAG,"DB insertGoal");
        SQLiteDatabase db = this.getWritableDatabase();
        //Check if entry already exists
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA+" where timestamp="+timestamp+"", null );
        if(res.getCount()>0){
            
            updateGoal(timestamp,value);
        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PA_COLUMN_TIMESTAMP, timestamp);
            contentValues.put(PA_COLUMN_STEPSGOAL, value);
            try {
                db.insert(TABLE_NAME_PA, null, contentValues);
            } catch (Exception e) {
                
                Log.e(TAG, " ", e);
            }
            
        }
        return true;
    }

    public boolean updateGoal (long timestamp, int value)
    {
        Log.i(TAG, "DB updateGoal");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PA_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(PA_COLUMN_STEPSGOAL, value);
        try{
            db.update(TABLE_NAME_PA, contentValues, PA_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
        }
        catch (Exception e){
            Log.e(TAG," ",e);
        }
        
        return true;
    }

    public Integer getGoalData(long timestamp){
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA+" where timestamp="+timestamp+"", null );
            res.moveToFirst();
            int result = res.getInt(res.getColumnIndex(PA_COLUMN_STEPSGOAL));
            res.close();
            
            return result;
        }catch (Exception e) {
//            Log.e(TAG, " ", e);
            return -1;
        }
    }

//    public int numberOfRows(){
//        SQLiteDatabase db = this.getReadableDatabase();
//        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME_GOALS);
//        return numRows;
//    }



//    public Integer deleteContact (long timestamp)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("SELECT * FROM goals WHERE timestamp >= 0");
////        return db.delete("goals",
////                "timestamp = ? ",
////                new String[] { "*" });
//        return 1;
//
//    }


    /**
     * PA
     */
    public boolean insertPA  (long timestamp, int still, int walk, int bicycle, int vehicle, int run, int tilting, int stepsgoal)
    {
        Log.i(TAG,"DB insertPA");
        SQLiteDatabase db = this.getWritableDatabase();
        //Check if entry already exists
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA+" where timestamp="+timestamp+"", null );
        if(res.getCount()>0){
            
            updatePA(timestamp, still, walk, bicycle, vehicle, run, tilting);
        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PA_COLUMN_TIMESTAMP, timestamp);
            contentValues.put(PA_COLUMN_STILL, still);
            contentValues.put(PA_COLUMN_WALK, walk);
            contentValues.put(PA_COLUMN_BICYCLE, bicycle);
            contentValues.put(PA_COLUMN_VEHICLE, vehicle);
            contentValues.put(PA_COLUMN_RUN, run);
            contentValues.put(PA_COLUMN_TILTING, tilting);
            contentValues.put(PA_COLUMN_STEPSGOAL, stepsgoal);
            try {
                db.insert(TABLE_NAME_PA, null, contentValues);
            }
            catch (Exception e){
                // Log.e(TAG," ");
            }
            
        }

        return true;
    }

    public boolean updatePA (long timestamp, int still, int walk, int bicycle, int vehicle, int run, int tilting)
    {
        Log.i(TAG, "DB updatePA");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PA_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(PA_COLUMN_STILL, still);
        contentValues.put(PA_COLUMN_WALK, walk);
        contentValues.put(PA_COLUMN_BICYCLE, bicycle);
        contentValues.put(PA_COLUMN_VEHICLE, vehicle);
        contentValues.put(PA_COLUMN_RUN, run);
        contentValues.put(PA_COLUMN_TILTING, tilting);
        try{
            db.update(TABLE_NAME_PA, contentValues, PA_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
        }
        catch (Exception e){
            Log.e(TAG," ",e);
        }
        
        return true;
    }

    public ArrayList<ArrayList<Long>> getAllPA()
    {
        ArrayList<ArrayList<Long>> paData = new ArrayList<>();
        ArrayList<Long> aux;

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            aux = new ArrayList<>();
            aux.add(res.getLong(res.getColumnIndex(PA_COLUMN_TIMESTAMP)));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_STILL))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_WALK))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_BICYCLE))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_VEHICLE))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_RUN))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_TILTING))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_STEPSGOAL))));
            paData.add(aux);
            res.moveToNext();
        }
        res.close();
        
        return paData;
    }

    public ArrayList<ArrayList<Long>> getPAdata(long from, long to)
    {
        ArrayList<ArrayList<Long>> paData = new ArrayList<>();
        ArrayList<Long> aux;

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA +" WHERE "+PA_COLUMN_TIMESTAMP+" BETWEEN "+from+ " AND "+ to, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            aux = new ArrayList<>();
            aux.add(res.getLong(res.getColumnIndex(PA_COLUMN_TIMESTAMP)));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_STILL))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_WALK))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_BICYCLE))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_VEHICLE))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_RUN))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_TILTING))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_COLUMN_STEPSGOAL))));
            paData.add(aux);
            res.moveToNext();
        }
        res.close();
        
        return paData;
    }


    public void copyLogFile(){
        Log.i(TAG,"copyLogFile called");
        //Read File
        Vector<String> LogVector = new Vector<String>();

        boolean hasPermission=true;
        try{
            hasPermission = (ContextCompat.checkSelfPermission(ui_MainActivity.mContext.getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }catch (Exception e){
            Log.e(TAG," ",e);
        }
        try {
            if(!hasPermission)
                return;
            File ext_storage = Environment.getExternalStorageDirectory();
            String extPath = ext_storage.getPath();
            File folder = new File(extPath+"/precious");
            boolean success = false;
            if(!folder.exists())
                success = folder.mkdir();
            if(folder.exists() || success){
                File file = new File (folder, "ViewerLogFile.txt");
                if(file.exists())
                    file.delete();
                file.createNewFile();
                FileInputStream f = new FileInputStream(file);
                BufferedReader entrada = new BufferedReader(  new InputStreamReader(f));
                String line="";
                while ((line = entrada.readLine()) != null) {
                    LogVector.add(line);
                }
                f.close();
                file.delete();
                //Copy file
                File file_server = new File (folder, "Log2File.txt");
                copyFile(file_server,file);

                //Copy remaining info
                FileOutputStream fout = new FileOutputStream(file, true);
                for(int i=0;i<LogVector.size()-1;i++)
                    fout.write( (LogVector.get(i).concat("\n")).getBytes());
            }
        } catch (Exception e){
            Log.e(TAG,"Error:",e);
        }
    }
    public static void copyFile(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    /**
     * MANUAL PA
     */
    public boolean insertManualPA  (long timestamp, int type, int intensity, int duration, int steps)
    {
        Log.i(TAG,"DB insertManualPA");
        SQLiteDatabase db = this.getWritableDatabase();
        //Check if entry already exists
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA_MANUAL+" where timestamp="+timestamp+"", null );
        if(res.getCount()>0){
            
            updateManualPA(timestamp, type, intensity, duration, steps);
        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PA_MAN_COLUMN_TIMESTAMP, timestamp);
            contentValues.put(PA_MAN_COLUMN_PA_TYPE, type);
            contentValues.put(PA_MAN_COLUMN_PA_INTENSITY, intensity);
            contentValues.put(PA_MAN_COLUMN_PA_DURATION, duration);
            contentValues.put(PA_MAN_COLUMN_PA_STEPS, steps);
            try {
                createTablesIfNotExist(db);
                db.insert(TABLE_NAME_PA_MANUAL, null, contentValues);
                Log.i(TAG, "Manual activity inserted");
            } catch (Exception e) {
                Log.e(TAG, " ", e);
            }
            
        }
        return true;
    }

    public boolean updateManualPA (long timestamp, int type, int intensity, int duration, int steps)
    {
        Log.i(TAG, "DB updatePA");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PA_MAN_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(PA_MAN_COLUMN_PA_TYPE, type);
        contentValues.put(PA_MAN_COLUMN_PA_INTENSITY, intensity);
        contentValues.put(PA_MAN_COLUMN_PA_DURATION, duration);
        contentValues.put(PA_MAN_COLUMN_PA_STEPS, steps);
        try{
            db.update(TABLE_NAME_PA_MANUAL, contentValues, PA_MAN_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
        }
        catch (Exception e){
            Log.e(TAG, " ", e);
        }
        
        return true;
    }

    public boolean deleteManualPA (long timestamp)
    {
        Log.i(TAG, "removeManualPA");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PA_MAN_COLUMN_TIMESTAMP, timestamp);
        try{
            db.delete(TABLE_NAME_PA_MANUAL, PA_MAN_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
        }
        catch (Exception e){
            Log.e(TAG," ",e);
        }
        
        return true;
    }

    public ArrayList<ArrayList<Long>> getManPA(long from, long to)
    {
        ArrayList<ArrayList<Long>> paData = new ArrayList<>();
        ArrayList<Long> aux;

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA_MANUAL +" WHERE "+PA_MAN_COLUMN_TIMESTAMP+" BETWEEN "+from+ " AND "+ to, null );
//        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA_MANUAL, null );
        res.moveToFirst();
//        Log.i(TAG, "timestamp FIRST= " + res.getColumnIndex(PA_MAN_COLUMN_TIMESTAMP));
        while(!res.isAfterLast()){
            aux = new ArrayList<>();
//            Log.i(TAG,"timestamp= "+res.getLong(res.getColumnIndex(PA_MAN_COLUMN_TIMESTAMP)));
            aux.add(res.getLong(res.getColumnIndex(PA_MAN_COLUMN_TIMESTAMP)));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_MAN_COLUMN_PA_TYPE))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_MAN_COLUMN_PA_INTENSITY))));
            aux.add(res.getLong(res.getColumnIndex(PA_MAN_COLUMN_PA_DURATION)));
            aux.add(res.getLong(res.getColumnIndex(PA_MAN_COLUMN_PA_STEPS)));
            paData.add(aux);
            res.moveToNext();
        }
        res.close();
        
        return paData;
    }


    /**
     * PLANNED PA
     */
    public boolean insertPlannedPA  (long timestamp, int type, int intensity, int duration, int steps)
    {
        Log.i(TAG,"DB insertPlannedPA");
        SQLiteDatabase db = this.getWritableDatabase();
        //Check if entry already exists
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA_PLAN+" where timestamp="+timestamp+"", null );
        if(res.getCount()>0){
            
            updatePlannedPA(timestamp, type, intensity, duration, steps);
        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(PA_PLAN_COLUMN_TIMESTAMP, timestamp);
            contentValues.put(PA_PLAN_COLUMN_PA_TYPE, type);
            contentValues.put(PA_PLAN_COLUMN_PA_INTENSITY, intensity);
            contentValues.put(PA_PLAN_COLUMN_PA_DURATION, duration);
            contentValues.put(PA_PLAN_COLUMN_PA_STEPS, steps);
            try {
                createTablesIfNotExist(db);
                db.insert(TABLE_NAME_PA_PLAN, null, contentValues);
                Log.i(TAG, "Planned activity inserted");
            } catch (Exception e) {
                Log.e(TAG, " ", e);
            }
            
        }
        return true;
    }

    public boolean updatePlannedPA (long timestamp, int type, int intensity, int duration, int steps)
    {
        Log.i(TAG, "DB updatePlannedPA");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PA_PLAN_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(PA_PLAN_COLUMN_PA_TYPE, type);
        contentValues.put(PA_PLAN_COLUMN_PA_INTENSITY, intensity);
        contentValues.put(PA_PLAN_COLUMN_PA_DURATION, duration);
        contentValues.put(PA_PLAN_COLUMN_PA_STEPS, steps);
        try{
            db.update(TABLE_NAME_PA_PLAN, contentValues, PA_PLAN_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
        }
        catch (Exception e){
            Log.e(TAG, " ", e);
        }
        
        return true;
    }

    public boolean deletePlannedPA (long timestamp)
    {
        Log.i(TAG, "deletePlannedPA");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PA_PLAN_COLUMN_TIMESTAMP, timestamp);
        try{
            db.delete(TABLE_NAME_PA_PLAN, PA_PLAN_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
        }
        catch (Exception e){
            Log.e(TAG," ",e);
        }
        
        return true;
    }

    public ArrayList<ArrayList<Long>> getPlannedPA(long from, long to)
    {
        ArrayList<ArrayList<Long>> paData = new ArrayList<>();
        ArrayList<Long> aux;

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA_PLAN +" WHERE "+PA_PLAN_COLUMN_TIMESTAMP+" BETWEEN "+from+ " AND "+ to, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            aux = new ArrayList<>();
            aux.add(res.getLong(res.getColumnIndex(PA_PLAN_COLUMN_TIMESTAMP)));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_PLAN_COLUMN_PA_TYPE))));
            aux.add((long)(res.getInt(res.getColumnIndex(PA_PLAN_COLUMN_PA_INTENSITY))));
            aux.add(res.getLong(res.getColumnIndex(PA_PLAN_COLUMN_PA_DURATION)));
            aux.add(res.getLong(res.getColumnIndex(PA_PLAN_COLUMN_PA_STEPS)));
            paData.add(aux);
            res.moveToNext();
        }
        res.close();
        
        return paData;
    }


    /*
     * FOOD
     */
    public boolean insertFood  (long timestamp, int type, String foodName, int amount, int photoId)
    {
        Log.i(TAG,"DB insertFood");
        SQLiteDatabase db = this.getWritableDatabase();
        //Check if entry already exists
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_FOOD+" where timestamp="+timestamp+"", null );
        if(res.getCount()>0){
            
            updateFood(timestamp, type, foodName, amount, photoId);
        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FOOD_COLUMN_TIMESTAMP, timestamp);
            contentValues.put(FOOD_COLUMN_TYPE, type);
            contentValues.put(FOOD_COLUMN_NAME, foodName);
            contentValues.put(FOOD_COLUMN_AMOUNT, amount);
            contentValues.put(FOOD_COLUMN_PHOTO_ID, photoId);
            try {
                createTablesIfNotExist(db);
                db.insert(TABLE_NAME_FOOD, null, contentValues);
            } catch (Exception e) {
                Log.e(TAG, " ", e);
            }
            
        }
        return true;
    }

    public boolean updateFood (long timestamp, int type, String foodName, int amount, int photoId)
    {
        Log.i(TAG, "DB updatePA");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(FOOD_COLUMN_TYPE, type);
        contentValues.put(FOOD_COLUMN_NAME, foodName);
        contentValues.put(FOOD_COLUMN_AMOUNT, amount);
        contentValues.put(FOOD_COLUMN_PHOTO_ID, photoId);
        try{
            db.update(TABLE_NAME_FOOD, contentValues, FOOD_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
        }
        catch (Exception e){
            Log.e(TAG, " ", e);
        }
        
        return true;
    }

    public boolean deleteFood (long from, long to,long type, String foodName, long amount)
    {
        Log.i(TAG, "removeFood");
        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(TABLE_NAME_FOOD, timestamp);
//        db.rawQuery("select * from " + TABLE_NAME_PA_MANUAL + " WHERE " + PA_MAN_COLUMN_TIMESTAMP + " BETWEEN " + from + " AND " + to, null);
        try{
            db.delete(TABLE_NAME_FOOD, FOOD_COLUMN_TIMESTAMP + " BETWEEN "+from+" AND "+to+" AND " + FOOD_COLUMN_TYPE + " = ? AND " +FOOD_COLUMN_NAME + " = ?"+" AND "+FOOD_COLUMN_AMOUNT+" = ? ", new String[]{Long.toString(type),foodName,Long.toString(amount)});
        }
        catch (Exception e){
            Log.e(TAG," ",e);
        }
        
        return true;
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public ArrayList<ArrayList<Long>> getFood(long from, long to)
    {
        ArrayList<ArrayList<Long>> paData = new ArrayList<>();
        ArrayList<Long> aux;

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TABLE_NAME_FOOD + " WHERE " + FOOD_COLUMN_TIMESTAMP + " BETWEEN " + from + " AND " + to, null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            aux = new ArrayList<>();
            aux.add(res.getLong(res.getColumnIndex(FOOD_COLUMN_TIMESTAMP)));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_COLUMN_TYPE))));
//            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_COLUMN_NAME))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_COLUMN_AMOUNT))));
//            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_COLUMN_PHOTO_ID))));
            paData.add(aux);
            res.moveToNext();
        }
        res.close();
        

        return paData;
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public ArrayList<ArrayList<String>> getFoodNames(long from, long to)
    {
        ArrayList<ArrayList<String>> paData = new ArrayList<>();
        ArrayList<String> aux;

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_FOOD +" WHERE "+FOOD_COLUMN_TIMESTAMP+" BETWEEN "+from+ " AND "+ to, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            aux = new ArrayList<>();
            aux.add((res.getString(res.getColumnIndex(FOOD_COLUMN_NAME))));
            paData.add(aux);
            res.moveToNext();
        }
        res.close();
        
        return paData;
    }


    /*
     * FOOD CHALLENGE
     */
    public boolean insertFoodChallenge  (long timestamp, int type, int value)
    {
        Log.i(TAG,"DB insertFoodChallenge");
        SQLiteDatabase db = this.getWritableDatabase();
        //Check if entry already exists
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_FOOD_CHALLENGE+" where timestamp="+timestamp+"", null);
        if(res.getCount()>0){
            
            updateFoodChallenge(timestamp, type, value);
        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(FOOD_CHALLENGE_COLUMN_TIMESTAMP, timestamp);
            switch (type) {
                case 0:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE0, value);
                    break;
                case 1:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE1, value);
                    break;
                case 2:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE2, value);
                    break;
                case 3:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE3, value);
                    break;
                case 4:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE4, value);
                    break;
                case 5:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE5, value);
                    break;
                case 6:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE6, value);
                    break;
                case 7:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE7, value);
                    break;
                case 8:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE8, value);
                    break;
                case 9:
                    contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE9, value);
                    break;
                default:
                    break;
            }
            try {
                createTablesIfNotExist(db);
                db.insert(TABLE_NAME_FOOD_CHALLENGE, null, contentValues);
            } catch (Exception e) {
                Log.e(TAG, " ", e);
            }
            
        }
        return true;
    }

    public boolean updateFoodChallenge (long timestamp, int type, int value)
    {
        Log.i(TAG, "DB updateFoodChallenge");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_CHALLENGE_COLUMN_TIMESTAMP, timestamp);
        switch (type){
            case 0  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE0, value);  break;
            case 1  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE1, value);  break;
            case 2  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE2, value);  break;
            case 3  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE3, value);  break;
            case 4  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE4, value);  break;
            case 5  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE5, value);  break;
            case 6  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE6, value);  break;
            case 7  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE7, value);  break;
            case 8  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE8, value);  break;
            case 9  :   contentValues.put(FOOD_CHALLENGE_COLUMN_VALUE9, value);  break;
            default: break;
        }
        try{
            db.update(TABLE_NAME_FOOD_CHALLENGE, contentValues, FOOD_CHALLENGE_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
        }
        catch (Exception e){
            Log.e(TAG, " ", e);
        }
        
        return true;
    }

    /**
     *
     * @param from
     * @param to
     * @return
     */
    public ArrayList<ArrayList<Long>> getFoodChallenges(long from, long to)
    {
        ArrayList<ArrayList<Long>> paData = new ArrayList<>();
        ArrayList<Long> aux;

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TABLE_NAME_FOOD_CHALLENGE + " WHERE " + FOOD_CHALLENGE_COLUMN_TIMESTAMP + " BETWEEN " + from + " AND " + to, null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            aux = new ArrayList<>();
            aux.add(res.getLong(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_TIMESTAMP)));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE0))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE1))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE2))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE3))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE4))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE5))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE6))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE7))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE8))));
            aux.add((long)(res.getInt(res.getColumnIndex(FOOD_CHALLENGE_COLUMN_VALUE9))));

            paData.add(aux);
            res.moveToNext();
        }
        res.close();
        
        return paData;
    }



    /**
     * APP USAGE
     *     public static final String TABLE_NAME_APP_USAGE = "appUsage";
     public static final String APP_USAGE_COLUMN_TIMESTAMP = "timestamp";
     public static final String APP_USAGE_COLUMN_SUBAPP = "subapp";
     public static final String APP_USAGE_COLUMN_STATUS = "status";
     */
    public boolean insertAppUsage  (long timestamp, String subapp, String status)
    {
        Log.i(TAG,"DB insertAppUsage");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(APP_USAGE_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(APP_USAGE_COLUMN_SUBAPP, subapp);
        contentValues.put(APP_USAGE_COLUMN_STATUS, status);
        try {
            db.insert(TABLE_NAME_APP_USAGE, null, contentValues);
        }
        catch (Exception e){
            Log.e(TAG," ",e);
        }
        
        return true;
    }

//    public boolean updateAppUsage (long timestamp, String subapp, String status)
//    {
//        Log.i(TAG, "DB updateGoal");
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(APP_USAGE_COLUMN_TIMESTAMP, timestamp);
//        contentValues.put(APP_USAGE_COLUMN_SUBAPP, subapp);
//        contentValues.put(APP_USAGE_COLUMN_STATUS, status);
//        try{
//            db.update(TABLE_NAME_APP_USAGE, contentValues, APP_USAGE_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
//        }
//        catch (Exception e){
//            Log.e(TAG," ",e);
//        }
//        
//        return true;
//    }

    public ArrayList<ArrayList<String>> getAppUsage(long from, long to)
    {
        ArrayList<ArrayList<String>> paData = new ArrayList<>();
        ArrayList<String> aux;

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TABLE_NAME_APP_USAGE + " WHERE " + APP_USAGE_COLUMN_TIMESTAMP + " BETWEEN " + from + " AND " + to, null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            aux = new ArrayList<>();
            aux.add(Long.toString(res.getLong(res.getColumnIndex(APP_USAGE_COLUMN_TIMESTAMP))));
            aux.add((res.getString(res.getColumnIndex(APP_USAGE_COLUMN_SUBAPP))));
            aux.add((res.getString(res.getColumnIndex(APP_USAGE_COLUMN_STATUS))));
            paData.add(aux);
            res.moveToNext();
        }
        res.close();
        
        return paData;
    }

    /**
     * WEARABLE
     */
    public boolean insertWearableCurrentSteps  (long timestamp, int steps)
    {
        Log.i(TAG,"DB insertWearableCurrentSteps");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEARABLE_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(WEARABLE_COLUMN_CURRENT_ACUMUL_STEPS, steps);
        try {
            createTablesIfNotExist(db);
            db.insert(TABLE_NAME_WEARABLE, null, contentValues);
        }
        catch (Exception e){
            Log.e(TAG," ",e);
        }
        
        return true;
    }

    public boolean insertWearableDailySteps  (long timestamp, int steps)
    {
        Log.i(TAG,"DB insertWearableDailySteps");
        SQLiteDatabase db = this.getWritableDatabase();
        //Check if entry already exists
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_WEARABLE+" where timestamp="+timestamp+"", null );
        if(res.getCount()>0){
            
            updateWearableDailySteps(timestamp, steps);
        }
        else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(WEARABLE_COLUMN_TIMESTAMP, timestamp);
            contentValues.put(WEARABLE_COLUMN_TOTAL_DAILY_STEPS, steps);
            try {
                db.insert(TABLE_NAME_WEARABLE, null, contentValues);
            } catch (Exception e) {
//            Log.e(TAG," ",e);
            }
            
        }
        return true;
    }

    public boolean updateWearableDailySteps  (long timestamp, int steps)
    {
        Log.i(TAG,"DB updateWearableDailySteps");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEARABLE_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(WEARABLE_COLUMN_TOTAL_DAILY_STEPS, steps);
        try {
            db.update(TABLE_NAME_WEARABLE, contentValues, WEARABLE_COLUMN_TIMESTAMP + " = ? ", new String[]{Long.toString(timestamp)});
        }
        catch (Exception e){
            Log.e(TAG," ",e);
        }
        
        return true;
    }

    public ArrayList<ArrayList<Long>> getWearableDailySteps(long from, long to){
        ArrayList<ArrayList<Long>> data = new ArrayList<>();
//        Log.i(TAG,"getWearableDailySteps");
        try {
            SQLiteDatabase db = this.getReadableDatabase();
//            Cursor res =  db.rawQuery( "select "+WEARABLE_COLUMN_TOTAL_DAILY_STEPS+","+ WEARABLE_COLUMN_TIMESTAMP+" from "+TABLE_NAME_WEARABLE+" where "+WEARABLE_COLUMN_TIMESTAMP+" BETWEEN "+from+ " AND "+ to, null );
            Cursor res =  db.rawQuery( "select "+WEARABLE_COLUMN_TOTAL_DAILY_STEPS+","+ WEARABLE_COLUMN_TIMESTAMP+" from "+TABLE_NAME_WEARABLE+" where "+WEARABLE_COLUMN_TOTAL_DAILY_STEPS+" > 0 and "+WEARABLE_COLUMN_TIMESTAMP+" between "+from+" and "+to, null );
            res.moveToFirst();
            ArrayList<Long> aux;
            while(!res.isAfterLast()){
                aux = new ArrayList<>();
                aux.add(res.getLong(res.getColumnIndex(WEARABLE_COLUMN_TIMESTAMP)));
                aux.add((long)(res.getInt(res.getColumnIndex(WEARABLE_COLUMN_TOTAL_DAILY_STEPS))));
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(res.getLong(res.getColumnIndex(WEARABLE_COLUMN_TIMESTAMP)));
                String date = DateFormat.format("yyyy-MM-dd HH:mm", cal).toString();
                Log.i(TAG,"Steps: "+res.getInt(res.getColumnIndex(WEARABLE_COLUMN_TOTAL_DAILY_STEPS))+" Date: "+date);
                data.add(aux);
                res.moveToNext();
            }
            res.close();
            
            return data;
        }catch (Exception e) {
            Log.e(TAG, " ", e);
            return null;
        }
    }


    public boolean insertWearableBatteryLevel  (long timestamp, int level)
    {
        Log.i(TAG,"DB insertWearableBatteryLevel");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WEARABLE_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(WEARABLE_COLUMN_BATTERY_LEVEL, level);
        try {
            createTablesIfNotExist(db);
            db.insert(TABLE_NAME_WEARABLE, null, contentValues);
        }
        catch (Exception e){

            Log.e(TAG," ",e);
        }
        
        return true;
    }

    public ArrayList<Long> getWearableInformation(){
        ArrayList<Long> result = new ArrayList<>();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            //get batter info
            Cursor res =  db.rawQuery( "select * from "+ TABLE_NAME_WEARABLE+" WHERE "+WEARABLE_COLUMN_BATTERY_LEVEL+" > "+0+" ORDER BY "+ WEARABLE_COLUMN_TIMESTAMP +" DESC LIMIT 1", null );
            res.moveToFirst();
            int batLevel = res.getInt(res.getColumnIndex(WEARABLE_COLUMN_BATTERY_LEVEL));
//            Log.i(TAG,"Battery level SQL = "+res.getInt(res.getColumnIndex(WEARABLE_COLUMN_BATTERY_LEVEL)));
            result.add((long)batLevel);

            //Get steps info
            res =  db.rawQuery( "select "+WEARABLE_COLUMN_CURRENT_ACUMUL_STEPS+", "+WEARABLE_COLUMN_TIMESTAMP+ " from "+ TABLE_NAME_WEARABLE+" WHERE "+WEARABLE_COLUMN_CURRENT_ACUMUL_STEPS+" > "+0+" ORDER BY "+ WEARABLE_COLUMN_TIMESTAMP +" DESC LIMIT 1", null );
            res.moveToFirst();
            int currentSteps = res.getInt(res.getColumnIndex(WEARABLE_COLUMN_CURRENT_ACUMUL_STEPS));
            result.add((long)currentSteps);
            long timestamp = res.getLong(res.getColumnIndex(WEARABLE_COLUMN_TIMESTAMP));
            result.add(timestamp);
            res.close();
            
            return result;
        }catch (Exception e) {
//            Log.e(TAG, " ", e);
            result.add((long)-1);
            result.add((long)-1);
            result.add((long)-1);
            return null;
        }
    }
}

