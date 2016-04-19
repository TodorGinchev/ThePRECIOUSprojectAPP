package sql_db.precious.comnet.aalto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = "DBHelper";
    public static final String DATABASE_NAME = "precious.db";
    public static final String GOALS_TABLE_NAME = "goals";
    public static final String GOALS_COLUMN_TIMESTAMP = "timestamp";
    public static final String GOALS_COLUMN_VALUE = "value";
    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG,"Db onCreate");
        // TODO Auto-generated method stub
        db.execSQL(
                "create table if not exists "+GOALS_TABLE_NAME +
                        " (timestamp timestamp primary key, value integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG,"DB onUpgrade");
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }

    public boolean insertContact  (long timestamp, int value)
    {
        Log.i(TAG,"DB insertContact");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GOALS_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(GOALS_COLUMN_VALUE, value);
        db.insert(GOALS_TABLE_NAME, null, contentValues);
        return true;
    }

    public Cursor getData(long timestamp){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from goals where timestamp="+timestamp+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, GOALS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (long timestamp, int value)
    {
        Log.i(TAG, "DB updateContact");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(GOALS_COLUMN_TIMESTAMP, timestamp);
        contentValues.put(GOALS_COLUMN_VALUE, value);
        db.update("goals", contentValues, "timestamp = ? ", new String[]{Long.toString(timestamp) } );
        return true;
    }

    public Integer deleteContact (long timestamp)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("SELECT * FROM goals WHERE timestamp >= 0");
//        return db.delete("goals",
//                "timestamp = ? ",
//                new String[] { "*" });
        return 1;

    }

//    public ArrayList<String> getAllCotacts()
//    {
//        ArrayList<String> array_list = new ArrayList<String>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from contacts", null );
//        res.moveToFirst();
//
//        while(res.isAfterLast() == false){
//            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
//            res.moveToNext();
//        }
//        return array_list;
//    }
}
