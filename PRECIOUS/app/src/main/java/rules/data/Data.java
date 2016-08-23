package rules.data;

import java.util.Date;

/**
 * A simple data object class in order to wrap retrieved data into a processable
 * structure via date objects, value can be everything from objects, to arrays,
 * to numbers or strings etc.
 * Created by christopher on 09.06.16.
 */
public class Data {

    // start Date of data
    public Date from;
    // end Date of data, note: if Data is not a timeinterval but a timestamp, from equals to
    public Date to;
    // the associated value with the data
    public Object value;

    public Data(Date from, Date to, Object value) {
        this.from = from;
        this.to = to;
        this.value = value;
    }
}
