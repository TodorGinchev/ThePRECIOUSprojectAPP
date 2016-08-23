package precious_rule_system.rules.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import precious_rule_system.rules.db.lookup.LookupDataManager;
import precious_rule_system.rules.db.lookup.table.TableLookupData;
import precious_rule_system.rules.settings.RuleSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Semaphore;

import rules.database.DBHandler;
import rules.database.DBHandlerInterface;
import rules.entities.Rule;

/**
 * Created by christopher on 10.08.16.
 */

public class RuleDatabaseManager {

    // Completionhandler definition
    private interface CompletionHandler {
        public void handle(boolean success);
    }

    // store our context & activity
    Context context;

    // Preferences to store time
    SharedPreferences preferences;

    // Keys
    String preferencesKey = "ruleDb.preferences";
    String lastSyncKey = "lastSync";
    String currentVersionKey = "currentDatabaseVersion";
    String ruleDbJsonFile = "ruleDb.json";

    // How often we should sync the rule database
    long timeIntervalBetweenSyncsInMs = RuleSettings.timeIntervalBetweenSyncsInMs;

    // Our database
    DBHandler db;

    // Our lookup database
    LookupDataManager lookupDb;

    private final String TAG = "Rules.Database";

    /**
     * Initialises this singleton with an application context
     * @param context
     */
    public RuleDatabaseManager(Context context) {

        // initialize veriables
        this.context = context;
        this.preferences = context.getSharedPreferences(this.preferencesKey, Context.MODE_PRIVATE);
        this.db = new DBHandler(context, this.getDatabaseVersion());
        this.lookupDb = new LookupDataManager(context);

        // update if needed
        if (this.needsSynchronisation()) {
            Log.i(TAG ,"Local rule database needs updating");
            this.update(null);
        } else {
            Log.i(TAG ,"Local rule database is up-to-date");
        }
    }

    public synchronized DBHandlerInterface getDatabase() {
        return this.db;
    }
    public LookupDataManager getLookupDatabase() {
        return this.lookupDb;
    }

    /**
     * Starts the update process, stays synchronous however so it can be done within a job
     * uses a semaphore
     */
    public synchronized void update() {

        Log.i(TAG ,"Updating local rule database");
        final Semaphore available = new Semaphore(0, true);
        final RuleDatabaseManager self = this;
        CompletionHandler handler = new CompletionHandler() {
            @Override
            public void handle(boolean success) {
                if (!success) {
                    Log.i(TAG ,"Could not retreive new rules database from internet");
                    return;
                } else {
                    Log.i(TAG ,"Successfully retreived new db from backend");
                    // update the local database
                    boolean result = self.updateLocalDatabase();
                }
                available.release();
            }
        };
        retreiveDatabaseFromServer(handler);
        try {
            available.acquire();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Starts the update process, i.e. retreives new infos from the backend,
     * and then updates the local database
     */
    private synchronized void update(final CompletionHandler successHandler) {
        Log.i(TAG ,"Updating local rule database");
        final RuleDatabaseManager self = this;
        // first get the completion handler for the retreival of the new database
        CompletionHandler handler = new CompletionHandler() {
            @Override
            public void handle(boolean success) {
                if (!success) {
                    Log.i(TAG ,"Could not retreive new rules database from internet");
                    return;
                } else {
                    Log.i(TAG ,"Successfully retreived new db from backend");
                    // update the local database
                    boolean result = self.updateLocalDatabase();
                    if (successHandler != null) {
                        successHandler.handle(result);
                    }
                }
            }
        };
        // retreive the things from the server
        retreiveDatabaseFromServer(handler);
    }

    /**
     * Updates the local database from the json file
     */
    private boolean updateLocalDatabase() {

        // get all rules from the json file
        JSONObject json = retreiveRulesFromJson();

        // check
        if (json == null) {
            Log.i(TAG ,"Could not read ruledb json file");
            return false;
        }

        JSONArray jsonRules = null;
        JSONArray jsonData = null;
        int dbVersion = 0;

        try {
            jsonRules = json.getJSONArray("rules");
            jsonData = json.getJSONArray("data");
            dbVersion = json.getInt("version");
        } catch (Exception ex) {
            Log.i(TAG ,"Could not read ruledb json file");
            return false;
        }

        int newVersion = this.getDatabaseVersion();

        if (dbVersion == newVersion) {
            Log.i(TAG ,"Local Rule-Database already up-to-date");
            return true;
        } else {
            newVersion = dbVersion;
            this.setDatabaseVersion(newVersion);
        }

        // parse data
        ArrayList<TableLookupData> data = new ArrayList<TableLookupData>();
        try {
            int l = jsonData.length();
            for(int i=0; i<l; i++) {
                JSONObject jsonDataObj = jsonData.getJSONObject(i);
                TableLookupData d = TableLookupData.getFromJSON(jsonDataObj);
                if (d == null) {
                    Log.i(TAG ,"Invalid data object detected");
                    return false;
                }
                data.add(d);
            }
        } catch (Exception ex) {
            Log.i(TAG ,"Could not parse data from local file");
            ex.printStackTrace();
            return false;
        }

        // parse rules
        ArrayList<Rule> rules = new ArrayList<Rule>();
        try {
            int l = jsonRules.length();
            for(int i=0; i<l; i++) {
                JSONObject jsonRule = jsonRules.getJSONObject(i);
                rules.add(Rule.fromJSON(jsonRule));
            }
        } catch (Exception ex) {
            Log.i(TAG ,"Could not parse rules from local file");
            ex.printStackTrace();
            return false;
        }

        // close the current database
        this.db.close();

        // initialize the new one, this drops and recreates all tables
        this.db = new DBHandler(context, newVersion);

        // add the rules to the database
        try {
            for (Rule r: rules) {
                this.db.addRule(r);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.i(TAG ,"Could not add rules to local database");
            return false;
        }

        Log.i(TAG ,"Added " + rules.size() + " Rules to the database");

        // delete the lookup data
        this.lookupDb.deleteAll();

        // insert the new data
        for (TableLookupData d: data) {
            this.lookupDb.insertLookupData(d);
        }

        Log.i(TAG ,"Added " + data.size() + " LookupDataObjects to the database");

        this.updateLastSynchronisation(new Date());
        return true;
    }

    /**
     * Retreives all rules from the JSON array
     * @return
     */
    private JSONObject retreiveRulesFromJson() {

        // Get the string
        String json = null;
        try {
            InputStream is = this.context.getAssets().open(ruleDbJsonFile);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        // Should be a json array
        try {
            return new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retreives the new database from the server and replaces the json file
     * Not implemented in backend yet, hence we always use the current json file atm
     * @param handler
     */
    private void retreiveDatabaseFromServer(CompletionHandler handler) {
        //TODO : Download database, unzip, replace json file
        handler.handle(true);
    }

    /**
     * Checks whether synchronisation is needed
     * @return
     */
    private boolean needsSynchronisation() {
        long now = new Date().getTime();
        long lastSync = this.getLastSynchronisation().getTime();
        if (now - lastSync > timeIntervalBetweenSyncsInMs) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the last synchronisation date
     * @param date - Date to set
     */
    private void updateLastSynchronisation(Date date) {
        synchronized (preferences) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(this.lastSyncKey, date.getTime());
            editor.commit();
        }
    }

    /**
     * Retreives the last synchronisation date
     * @return Date
     */
    private Date getLastSynchronisation() {
        synchronized (preferences) {
            long lastDate = preferences.getLong(this.lastSyncKey, 0);
            return new Date(lastDate);
        }
    }

    /**
     * Increases the database version that is used and returns the latest
     * version
     * @return Int
     */
    private int setDatabaseVersion(int nextVersion) {
        synchronized (preferences) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(this.currentVersionKey, nextVersion);
            editor.commit();
            return nextVersion;
        }
    }

    /**
     * Returns the current database version
     * @return
     */
    private int getDatabaseVersion() {
        synchronized (preferences) {
            return preferences.getInt(this.currentVersionKey,1);
        }
    }
}
