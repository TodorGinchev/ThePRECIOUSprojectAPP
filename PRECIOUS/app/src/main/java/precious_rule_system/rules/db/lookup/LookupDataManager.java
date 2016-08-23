package precious_rule_system.rules.db.lookup;

import android.content.Context;

import precious_rule_system.rules.db.lookup.table.TableLookupData;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by christopher on 14.08.16.
 */

public class LookupDataManager {

    public LookupDataManager(Context context) {
    }

    public LookupData getLookupData(String _id) {
        Realm realm = Realm.getDefaultInstance();
        RealmQuery<TableLookupData> query = realm.where(TableLookupData.class);
        query.equalTo("_id", _id);
        RealmResults<TableLookupData> result = query.findAll();
        if (result.size() == 0) return null;
        return result.get(0);
    }

    public void deleteAll() {
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<TableLookupData> data = realm.where(TableLookupData.class).findAll();
        realm.beginTransaction();
        data.deleteAllFromRealm();
        realm.commitTransaction();
    }

    public boolean insertLookupData(TableLookupData data) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        TableLookupData realmUser = realm.copyToRealm(data);
        realm.commitTransaction();
        return true;
    }

}
