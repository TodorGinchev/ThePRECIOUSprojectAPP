package rules.entities.conditions;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import rules.helpers.Tuple;
import rules.types.RuleTypes;

/**
 * Created by christopher on 07.06.16.
 */
public class ConditionHistoricRequirement {

    private boolean isPlaceHolder;
    private RuleTypes.HistoricRequirements placeholder;
    private Date from;
    private Date to;

    public JSONObject toJSON() throws Exception {
        JSONObject json = new JSONObject();
        if (isPlaceHolder) {
            json.put("placeholder", placeholder.toString());
            return json;
        } else {
            json.put("from", from.getTime());
            json.put("to", to.getTime());
            return json;
        }
    }

    public static ConditionHistoricRequirement fromJSON(Object o) throws Exception {

        if(o instanceof String) {

            RuleTypes.HistoricRequirements placeholder = RuleTypes.HistoricRequirements.fromString((String)o);
            if(placeholder == null) {
                throw new Exception("Invalid Historic Requirements Placeholder detected");
            }

            return new ConditionHistoricRequirement(placeholder);

        } else if (o instanceof JSONObject) {

            Object _from = ((JSONObject) o).get("from");
            Object _to = ((JSONObject) o).get("to");
            DateFormat df = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            Date from = null;
            Date to = null;

            if (_from instanceof Integer) {
                from = new Date((Integer) _from);
            } else if (_from instanceof Long) {
                from = new Date((Long) _from);
            }  else if (_from instanceof String) {
                from = df.parse((String)_from);
            }

            if (_to instanceof Integer) {
                to = new Date((Integer) _to);
            } else if (_to instanceof Long) {
                to = new Date((Long) _to);
            } else if (_to instanceof String) {
                to = df.parse((String)_to);
            }

            if (from == null || to == null) {
                throw new Exception("Invalid Historic Requirements Object detected");
            }

            return new ConditionHistoricRequirement(from, to);

        } else {
            throw new Exception("Invalid Historic Requirements Object detected");
        }

    }

    public ConditionHistoricRequirement(Date from, Date to) {
        this.isPlaceHolder = false;
        this.from = from;
        this.to = to;
        this.placeholder = null;
    }

    public ConditionHistoricRequirement(RuleTypes.HistoricRequirements placeholder) {
        this.isPlaceHolder = true;
        this.placeholder = placeholder;
        this.to = null;
        this.from = null;
    }

    public Tuple<Date, Date> getDates() {
        if(!isPlaceHolder) {
            return new Tuple<>(from, to);
        } else {
            return new Tuple<>(getFrom(placeholder), getTo(placeholder));
        }
    }

    private Date getFrom(RuleTypes.HistoricRequirements ph) {

        // Create midnight of today's day
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        int today;

        switch(ph) {
            case TODAY:
                return date.getTime();
            case YESTERDAY:
                date.add(Calendar.DAY_OF_MONTH, -1);
                return date.getTime();
            case THIS_WEEK:
                today = date.get(Calendar.DAY_OF_WEEK);
                date.add(Calendar.DAY_OF_WEEK,-today+Calendar.MONDAY);
                return date.getTime();
            case LAST_WEEK:
                today = date.get(Calendar.DAY_OF_WEEK);
                date.add(Calendar.DAY_OF_WEEK,-today+Calendar.MONDAY);
                date.add(Calendar.DAY_OF_MONTH, -7);
                return date.getTime();
            case THIS_MONTH:
                date.set(Calendar.DAY_OF_MONTH, date.getActualMinimum(Calendar.DAY_OF_MONTH));
                return date.getTime();
            case LAST_MONTH:
                date.set(Calendar.DAY_OF_MONTH, date.getActualMinimum(Calendar.DAY_OF_MONTH));
                date.add(Calendar.MONTH, -1);
                return date.getTime();
            case T12_HRS:
                date = new GregorianCalendar();
                date.add(Calendar.HOUR, -12);
                return date.getTime();
            case T24_HRS:
                date = new GregorianCalendar();
                date.add(Calendar.HOUR, -24);
                return date.getTime();
            case T2_DAYS:
                date = new GregorianCalendar();
                date.add(Calendar.HOUR, -48);
                return date.getTime();
            case T3_DAYS:
                date = new GregorianCalendar();
                date.add(Calendar.HOUR, -72);
                return date.getTime();
            case T4_DAYS:
                date = new GregorianCalendar();
                date.add(Calendar.HOUR, -96);
                return date.getTime();
            case T5_DAYS:
                date = new GregorianCalendar();
                date.add(Calendar.HOUR, -120);
                return date.getTime();
            case T6_DAYS:
                date = new GregorianCalendar();
                date.add(Calendar.HOUR, -144);
                return date.getTime();
            case T7_DAYS:
                date = new GregorianCalendar();
                date.add(Calendar.HOUR, -168);
                return date.getTime();
        }
        return null;
    }

    private Date getTo(RuleTypes.HistoricRequirements ph) {

        // Create midnight of today's day
        Calendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        int today;

        Calendar now = new GregorianCalendar();

        switch(ph) {
            case TODAY:
                return now.getTime();
            case YESTERDAY:
                return date.getTime();
            case THIS_WEEK:
                return now.getTime();
            case LAST_WEEK:
                today = date.get(Calendar.DAY_OF_WEEK);
                date.add(Calendar.DAY_OF_WEEK,-today+Calendar.SUNDAY);
                return date.getTime();
            case THIS_MONTH:
                return now.getTime();
            case LAST_MONTH:
                date.add(Calendar.MONTH, -1);
                date.set(Calendar.DAY_OF_MONTH, date.getActualMaximum(Calendar.DAY_OF_MONTH));
                return date.getTime();
            case T12_HRS:
                return now.getTime();
            case T24_HRS:
                return now.getTime();
            case T2_DAYS:
                return now.getTime();
            case T3_DAYS:
                return now.getTime();
            case T4_DAYS:
                return now.getTime();
            case T5_DAYS:
                return now.getTime();
            case T6_DAYS:
                return now.getTime();
            case T7_DAYS:
                return now.getTime();
        }
        return null;

    }

    @Override
    public boolean equals(Object arg0) {
        if(arg0 instanceof ConditionHistoricRequirement) {
            return this.hashCode() == ((ConditionHistoricRequirement) arg0).hashCode();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        if(isPlaceHolder) {
            return placeholder.toString().hashCode();
        } else {
            int a = from.hashCode();
            int b = to.hashCode();
            return (a + b) * (a + b + 1) / 2 + a;
        }
    }
}
