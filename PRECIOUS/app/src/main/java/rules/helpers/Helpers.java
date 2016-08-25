package rules.helpers;

import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import rules.data.Data;
import ui.precious.comnet.aalto.precious.PRECIOUS_APP;

import java.util.concurrent.TimeUnit;

/**
 * Created by christopher on 07.06.16.
 */
public class Helpers {

    public interface Predicate<T> { boolean apply(T type); }

    public static <T> List<T> filter(List<T> col, Predicate<T> predicate) {
        List<T> result = new ArrayList<T>();
        for (T element: col) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static <T> List<T> filter(T[] col, Predicate<T> predicate) {
        List<T> result = new ArrayList<T>();
        for (T element: col) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static Object getObjectFromPath(JSONObject json, String path) throws Exception {
        if(path == null) return null;
        String[] keys = path.split("\\.");
        int n = keys.length;
        JSONObject current = json;
        for(int i=0; i<n; i++) {

             if (i<n-1) {
                 current = current.getJSONObject(keys[i]);
             } else {
                 return current.get(keys[i]);
             }
        }
        return null;
    }

    public static Integer getInt(Object n) {
        if(n instanceof Integer) {
            return (Integer) n;
        } else if (n instanceof Float) {
            return ((Float) n).intValue();
        } else if (n instanceof Double) {
            return ((Double) n).intValue();
        } else if (n instanceof String) {
            return Integer.parseInt((String) n);
        } else {
            return null;
        }
    }

    //Wrap generic objects in Data class objects
    public static Data[] wrapData(Object value) {
        Data[] rtn = { new Data(null, null, value) };
        return rtn;
    }

    //Inner static class for testing purposes


}
