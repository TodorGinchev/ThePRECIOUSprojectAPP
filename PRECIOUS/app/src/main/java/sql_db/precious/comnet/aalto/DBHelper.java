package sql_db.precious.comnet.aalto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
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
import java.util.Vector;

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
//    private HashMap hp;

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
        db.execSQL(
                "create table if not exists " + TABLE_NAME_PA +
                        " (" + PA_MAN_COLUMN_TIMESTAMP + " timestamp primary key, " + PA_COLUMN_STILL + " integer, "
                        + PA_COLUMN_WALK + " integer, " + PA_COLUMN_BICYCLE + " integer, "
                        + PA_COLUMN_VEHICLE + " integer, " + PA_COLUMN_RUN + " integer, " + PA_COLUMN_TILTING + " integer, " +                  PA_COLUMN_STEPSGOAL + " integer)"
        );

        Log.i(TAG, "Db onCreate");

        db.execSQL(
                "create table if not exists " + TABLE_NAME_PA_MANUAL +
                        " (" + PA_COLUMN_TIMESTAMP + " timestamp primary key, " + PA_MAN_COLUMN_PA_TYPE + " integer, "
                        + PA_MAN_COLUMN_PA_INTENSITY + " integer, " + PA_MAN_COLUMN_PA_STEPS + " integer, "
                        + PA_MAN_COLUMN_PA_DURATION + " integer)"
        );
        Log.i(TAG, TABLE_NAME_PA + " Created");

        Log.i(TAG, "Db onCreated");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "DB onUpgrade");
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PA_MANUAL);
        onCreate(db);
    }

    /**
     * GOALS
     */
    public boolean insertGoal  (long timestamp, int value)
    {
        Log.i(TAG,"DB insertGoal");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PA_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(PA_COLUMN_STEPSGOAL, value);
        try {
            db.insert(TABLE_NAME_PA, null, contentValues);
        }
        catch (Exception e){
            Log.e(TAG," ",e);
        }
        db.close();
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
        db.close();
        return true;
    }

    public Integer getGoalData(long timestamp){

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from "+TABLE_NAME_PA+" where timestamp="+timestamp+"", null );
            res.moveToFirst();
            int result = res.getInt(res.getColumnIndex(PA_COLUMN_STEPSGOAL));
            res.close();
            db.close();
            return result;
        }catch (Exception e) {
            Log.e(TAG, "", e);
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
            Log.e(TAG," ",e);
        }
        db.close();
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
        db.close();
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
        db.close();
        return paData;
    }

    private void copyLogFile(){
        //Read File
        Vector<String> LogVector = new Vector<String>();
        try {
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
    public void copyFile(File src, File dst) throws IOException {
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
        ContentValues contentValues = new ContentValues();
        contentValues.put(PA_MAN_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(PA_MAN_COLUMN_PA_TYPE, type);
        contentValues.put(PA_MAN_COLUMN_PA_INTENSITY, intensity);
        contentValues.put(PA_MAN_COLUMN_PA_DURATION, duration);
        contentValues.put(PA_MAN_COLUMN_PA_STEPS, steps);
        try {
            createTablesIfNotExist(db);
            db.insert(TABLE_NAME_PA_MANUAL, null, contentValues);
        }
        catch (Exception e){
                Log.e(TAG, " ",e);
        }
        db.close();
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
        db.close();
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
        db.close();
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
        db.close();
        return paData;
    }
}



