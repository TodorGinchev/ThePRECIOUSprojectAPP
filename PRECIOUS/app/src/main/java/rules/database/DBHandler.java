package rules.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import rules.entities.Rule;
import rules.entities.trigger.Trigger;
import rules.types.RuleTypes;

/**
 * Created by christopher on 07.06.16.
 */
public class DBHandler extends SQLiteOpenHelper implements DBHandlerInterface {

    // Locks
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    private static final int DB_VERSION = 1;

    // Database file name
    private static final String DB_NAME = "RuleDatabase.db";

    // Table names
    private static final String RULE_TABLE_NAME = "rule_table";
    private static final String TRIGGER_TABLE_NAME = "trigger_table";
    private static final String RULE_EXECUTION_TABLE_NAME = "rule_execution_table";

    // Rule_Table columns
    private static final String KEY_ID = "id";
    private static final String KEY_RULE_ID = "_id";
    private static final String KEY_NAME = "_name";
    private static final String KEY_DESCRIPTION = "_description";
    private static final String KEY_CREATED = "_created";
    private static final String KEY_CONDITIONS = "_conditions";
    private static final String KEY_OPERATION = "_operation";
    private static final String KEY_ACTIONS = "_actions";
    private static final String KEY_PRIORITY = "_priority";
    private static final String KEY_DB_IDX = "_id_idx";

    // Rule_Table operations
    String CREATE_RULE_TABLE = "CREATE TABLE "+RULE_TABLE_NAME+
            " (" +
            KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
            KEY_RULE_ID + " TEXT," +
            KEY_NAME + " TEXT," +
            KEY_DESCRIPTION + " TEXT," +
            KEY_CREATED + " DATETIME," +
            KEY_CONDITIONS + " TEXT," +
            KEY_OPERATION + " TEXT," +
            KEY_ACTIONS + " TEXT," +
            KEY_PRIORITY + " INTEGER" +
            ")";

    String CREATE_RULE_TABLE_INDEX = "CREATE INDEX "+KEY_DB_IDX+" ON "+RULE_TABLE_NAME+" ("+KEY_RULE_ID+")";
    String DROP_RULE_TABLE = "DROP TABLE IF EXISTS "+RULE_TABLE_NAME;

    // Trigger_Table columns
    private static final String KEY_TRIGGER_ID = "id";
    private static final String KEY_TRIGGER_KEY = "_trigger";
    private static final String KEY_TRIGGER_IDX = "_id_idx";

    String CREATE_TRIGGER_TABLE = "CREATE TABLE "+TRIGGER_TABLE_NAME+
            " (" +
            KEY_TRIGGER_ID+" INTEGER PRIMARY KEY," +
            KEY_RULE_ID + " TEXT," +
            KEY_TRIGGER_KEY + " TEXT" +
            ")";

    String CREATE_TRIGGER_TABLE_INDEX = "CREATE INDEX "+KEY_TRIGGER_IDX+" ON "+TRIGGER_TABLE_NAME+" ("+KEY_TRIGGER_KEY+")";
    String DROP_TRIGGER_TABLE = "DROP TABLE IF EXISTS "+TRIGGER_TABLE_NAME;

    //Rule Execution Tracking & Table
    private static final String KEY_RULE_LAST_EXECUTED = "_last_executed";

    String CREATE_RULE_EXECUTION_TABLE = "CREATE TABLE IF NOT EXISTS "+RULE_EXECUTION_TABLE_NAME+
            " (" +
            KEY_RULE_ID+" TEXT PRIMARY KEY," +
            KEY_RULE_LAST_EXECUTED + " DATETIME" +
            ")";
    String DROP_RULE_EXECUTION_TABLE = "DROP TABLE IF EXISTS "+RULE_EXECUTION_TABLE_NAME;

    private final String TAG = "Rules.DBHandler";

    /**
     * Constructor of the DB Handler
     * @param context
     */
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public DBHandler(Context context, int version) {
        super(context, DB_NAME, null, version);
    }

    /**
     * Here we create all tables
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RULE_TABLE);
        db.execSQL(CREATE_TRIGGER_TABLE);
        db.execSQL(CREATE_RULE_EXECUTION_TABLE);
        db.execSQL(CREATE_RULE_TABLE_INDEX);
    }

    /**
     * We drop all old tables upon an update
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_RULE_TABLE);
        db.execSQL(DROP_TRIGGER_TABLE);
        // Removed: we do actually not want to drop the execution table
        //db.execSQL(DROP_RULE_EXECUTION_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_RULE_TABLE);
        db.execSQL(DROP_TRIGGER_TABLE);
        // Removed: we do actually not want to drop the execution table
        //db.execSQL(DROP_RULE_EXECUTION_TABLE);
        onCreate(db);
    }

    /**
     * Adds a new rule to the database, which is parsed from the given JSONObject
     * @param rule - JSONObject to be parsed into a rule
     * @return
     * @throws Exception
     */
    @Override
    public long addRule(JSONObject rule) throws Exception {
        Rule r = Rule.fromJSON(rule);
        return this.addRule(r);
    }

    @Override
    public long addRule(Rule rule) throws Exception {
        w.lock();
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            if (db == null) {
                Log.d(TAG, "Could not get writable database");
                return -1;
            }
            return _addRule(rule, db);
        } finally {
            if (db != null) db.close();
            w.unlock();
        }
    }

    /**
     * Adds a new rule to the database
     * @param rule - The rule to be entered into the database
     * @return
     * @throws Exception
     */

    private long _addRule(Rule rule, SQLiteDatabase db) throws Exception {

        ContentValues row = this.getRow(rule);
        long id = db.insert(RULE_TABLE_NAME, null, row);

        if (id < 0) {
            Log.d(TAG, "addRule: Could not insert rule");
            return id;
        }

        // Insert the triggers
        for (Trigger t: rule.getTriggers()) {
            ContentValues tRow = new ContentValues();
            tRow.put(KEY_RULE_ID, rule.getRuleId());
            tRow.put(KEY_TRIGGER_KEY, t.getKey().toString());
            long triggerId = db.insert(TRIGGER_TABLE_NAME, null, tRow);
            if (triggerId < 0) {
                Log.d(TAG, "addRule: Trigger "+t.getKey().toString()+" could not be added");
                return -1;
            };
        }

        return id;
    }

    /**
     *
     * @param r
     * @return
     * @throws Exception
     */
    private ContentValues getRow(Rule r) throws Exception {
        ContentValues row = new ContentValues();
        row.put(KEY_RULE_ID, r.getRuleId());
        row.put(KEY_NAME, r.getName());
        row.put(KEY_DESCRIPTION, r.getDescription());
        row.put(KEY_CREATED, r.getCreated().getTime());
        row.put(KEY_CONDITIONS, r.getConditionsJSON().toString());
        row.put(KEY_OPERATION, r.getOperation().toJSON().toString());
        row.put(KEY_ACTIONS, r.getActionsJSON().toString());
        row.put(KEY_PRIORITY, r.getPriority());
        return row;
    }

    @Override
    public ArrayList<Rule> getAllRules() {
        SQLiteDatabase db = null;
        r.lock();
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                Log.d(TAG, "Could not get readable database");
                return new ArrayList<Rule>();
            }
            return _getAllRules(db);
        } finally {
            if (db != null) db.close();
            r.unlock();
        }
    }

    /**
     *
     * @return
     */
    public ArrayList<Rule> _getAllRules(SQLiteDatabase db) {

        // Here we store all rules that will be triggered
        ArrayList<Rule> rules = new ArrayList<Rule>();

        try {

            // Initialize our query
            String QUERY = "SELECT * FROM " + RULE_TABLE_NAME + " ORDER BY " + KEY_PRIORITY + " DESC";

            // Execute the query
            Cursor cursor = db.rawQuery(QUERY, null);

            // Iterate through results
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    int id = cursor.getInt(0);
                    String ruleId = cursor.getString(1);
                    String name = cursor.getString(2);
                    String description = cursor.getString(3);
                    long created = cursor.getLong(4);
                    String conditions = cursor.getString(5);
                    String operation = cursor.getString(6);
                    String actions = cursor.getString(7);
                    int priority = cursor.getInt(8);
                    Rule r = Rule.fromSQLite(id, ruleId, name, description, created, conditions, operation, actions, priority);
                    r.setTriggers(this.getTriggersForRule(r, db));
                    rules.add(r);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }

        return rules;

    }

    @Override
    public void removeRule(Rule rule) throws Exception {
        SQLiteDatabase db = null;
        w.lock();
        try {
            db = this.getWritableDatabase();
            if (db == null) {
                Log.d(TAG, "Could not get writable database");
                return;
            }
            _removeRule(rule, db);
        } finally {
            if (db != null) db.close();
            w.unlock();
        }
    }

    /**
     *
     * @param rule
     * @throws Exception
     */
    public void _removeRule(Rule rule, SQLiteDatabase db) throws Exception {

        String ruleId = rule.getRuleId();

        try {

            db.delete(RULE_TABLE_NAME, KEY_RULE_ID + "='" + rule.getRuleId() + "'", null);
            db.delete(TRIGGER_TABLE_NAME, KEY_RULE_ID + "='" + rule.getRuleId() + "'", null);

        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }

    }

    public Rule[] getRulesWithTrigger(RuleTypes.Key key, Map<String, Object> parameters) {
        return this.getAllRulesForTrigger(key).toArray(new Rule[0]);
    }

    /**
     *
     * @param r
     * @param db
     * @return
     */
    synchronized private Trigger[] getTriggersForRule(Rule r, SQLiteDatabase db) {

        if (db == null) {
            return null;
        }

        ArrayList<Trigger> triggers = new ArrayList<Trigger>();

        try {
            // Initialize our query
            String QUERY = "SELECT "+KEY_TRIGGER_KEY+" FROM " + TRIGGER_TABLE_NAME +  " WHERE "+KEY_RULE_ID+" = '"+r.getRuleId()+"'";

            // Execute the query
            Cursor cursor = db.rawQuery(QUERY, null);

            // Iterate through results
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    String trigger = cursor.getString(0);
                    triggers.add(new Trigger(trigger));
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }

        Trigger[] ts = new Trigger[triggers.size()];
        int i = 0;
        for (Trigger t : triggers) {
            ts[i] = t;
            i++;
        }
        return ts;
    }

    /**
     *
     * @param key
     * @return
     */
    synchronized public ArrayList<Rule> getAllRulesForTrigger(RuleTypes.Key key) {
        RuleTypes.Key[] keys = { key };
        return this.getAllRulesForTriggers(keys);
    }

    @Override
    synchronized public ArrayList<Rule> getAllRulesForTriggers(RuleTypes.Key[] keys) {
        SQLiteDatabase db = null;
        r.lock();
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                Log.d(TAG, "Could not get readable database");
                return new ArrayList<Rule>();
            }
            return _getAllRulesForTriggers(keys, db);
        } finally {
            if (db != null) db.close();
            r.unlock();
        }
    }

    /**
     *
     * @param keys
     * @return
     */
    synchronized public ArrayList<Rule> _getAllRulesForTriggers(RuleTypes.Key[] keys, SQLiteDatabase db) {

        // Convert enums to strings
        ArrayList<String> keysStr = new ArrayList<String>();
        for(RuleTypes.Key k : keys) { keysStr.add(k.toString()); }

        // Here we store all rules that will be triggered
        ArrayList<Rule> rules = new ArrayList<Rule>();

        try {

            /**
             * Prototype Query sth. like
             *   SELECT * FROM (
                     SELECT DISTINCT _rule_id from trigger_table
                     WHERE
                        _trigger='a' OR
                        _trigger='b'
                 ) AS T

                 JOIN rule_table USING ( _rule_id )
                 ORDER BY _priority desc
             */

            String QUERY =
                    "SELECT * FROM (" +
                            "SELECT DISTINCT " + KEY_RULE_ID + " FROM " + TRIGGER_TABLE_NAME +
                            " WHERE ";

            // Create or conditions for all keys
            int i = 1;
            for(String key : keysStr) {
                QUERY += KEY_TRIGGER_KEY + " = '" + key + "'";
                if (i++ < keysStr.size()) {
                    QUERY += " OR ";
                }
            }

            // Create the join on the rules table
            QUERY += ") AS T JOIN " + RULE_TABLE_NAME + " USING (" + KEY_RULE_ID + ") ORDER BY " + KEY_PRIORITY + " DESC";

            // Execute the query
            Cursor cursor = db.rawQuery(QUERY, null);

            // Iterate through results
            if(!cursor.isLast())
            {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(1);
                    String ruleId = cursor.getString(0);
                    String name = cursor.getString(2);
                    String description = cursor.getString(3);
                    long created = cursor.getLong(4);
                    String conditions = cursor.getString(5);
                    String operation = cursor.getString(6);
                    String actions = cursor.getString(7);
                    int priority = cursor.getInt(8);
                    Rule r = Rule.fromSQLite(id, ruleId, name, description, created, conditions, operation, actions, priority);
                    r.setTriggers(this.getTriggersForRule(r, db));
                    rules.add(r);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }

        return rules;
    }

    @Override
    public int getRuleCount() {
        SQLiteDatabase db = null;
        r.lock();
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                Log.d(TAG, "Could not get readable database");
                return -1;
            }
            return _getRuleCount(db);
        } finally {
            if (db != null) db.close();
            r.unlock();
        }
    }

    /**
     * Returns the current number of rules in the database
     * @return
     */
    public int _getRuleCount(SQLiteDatabase db) {

        // Here we store all rules that will be triggered
        ArrayList<Rule> rules = new ArrayList<Rule>();

        // the count
        int count = 0;

        try {
            // Simple count query
            String QUERY = "SELECT count(*) FROM " + RULE_TABLE_NAME;

            // Execute the query
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToFirst();
            count= cursor.getInt(0);
            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }

        return count;
    }


    public int getTriggerCount() {
        SQLiteDatabase db = null;
        r.lock();
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                Log.d(TAG, "Could not get readable database");
                return -1;
            }
            return _getTriggerCount(db);
        } finally {
            if (db != null) db.close();
            r.unlock();
        }
    }

    private int _getTriggerCount(SQLiteDatabase db) {

        // the count
        int count = 0;

        try {

            // Simple count query
            String QUERY = "SELECT count(*) FROM " + TRIGGER_TABLE_NAME;

            // Execute the query
            Cursor cursor = db.rawQuery(QUERY, null);

            cursor.moveToFirst();
            count= cursor.getInt(0);
            cursor.close();

        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }

        return count;
    }

    @Override
    public Date getRuleLastExecuted(Rule rule) {
        SQLiteDatabase db = null;
        r.lock();
        try {
            db = this.getReadableDatabase();
            if (db == null) {
                Log.d(TAG, "Could not get readable database");
                return new Date(0);
            }
            return _getRuleLastExecuted(rule,db);
        } finally {
            if (db != null) db.close();
            r.unlock();
        }
    }

    public Date _getRuleLastExecuted(Rule rule, SQLiteDatabase db) {

        String ruleId = rule.getRuleId();

        // the count
        Date date = null;

        try {

            String QUERY = "SELECT "+KEY_RULE_LAST_EXECUTED+" FROM "+RULE_EXECUTION_TABLE_NAME+" WHERE "+KEY_RULE_ID+"='"+ruleId+"'";

            // Execute the query
            Cursor cursor = db.rawQuery(QUERY, null);

            if(cursor == null || !cursor.moveToFirst()) {
                return null;
            }

            if (cursor.isNull(0)) {
                date = null;
            } else {
                date = new Date(cursor.getLong(0));
            }

        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }

        return date;

    }

    @Override
    public void setRuleExecuted(Rule rule, Date date) {

        SQLiteDatabase db = null;
        w.lock();
        try {
            db = this.getWritableDatabase();
            if (db == null) {
                Log.d(TAG, "Could not get writable database");
                return;
            }
            _setRuleExecuted(rule,date, db);
        } finally {
            if (db != null) db.close();
            w.unlock();
        }
    }

    public void _setRuleExecuted(Rule rule, Date date, SQLiteDatabase db) {

        String ruleId = rule.getRuleId();

        try {

            ContentValues cv = new ContentValues();
            cv.put(KEY_RULE_ID, ruleId);
            cv.put(KEY_RULE_LAST_EXECUTED, date.getTime());
            long res = db.insertWithOnConflict(RULE_EXECUTION_TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);

        } catch (Exception e) {
            Log.e(TAG, "Error " + e.toString());
        }

    }

}
