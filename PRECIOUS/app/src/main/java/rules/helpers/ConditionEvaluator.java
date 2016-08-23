package rules.helpers;

import java.util.ArrayList;
import java.util.Collections;

import rules.types.RuleTypes;

/**
 * Created by christopher on 04.07.16.
 */

public class ConditionEvaluator {

    private static Double getNumber(Object n) {
        if(n instanceof Integer) {
            return ((Integer) n).doubleValue();
        } else if (n instanceof Float) {
            return ((Float) n).doubleValue();
        } else if (n instanceof Double) {
            return (double) ((double) n);
        } else {
            return null;
        }
    }

    public static boolean evaluateCondition(RuleTypes.Comparator c, ArrayList<Object> params) throws Exception {

        if(params.size() == 0) {
            throw new Exception("Invalid Parameters list for evaluating condition");
        }

        if (c == RuleTypes.Comparator.NOT) {
            return params.get(0) == null;
        } else {

            if (params.size() < 2) {
                throw new Exception("Invalid number of parameters for evaluating condition");
            }

            if (c == RuleTypes.Comparator.EQUAL) {

                // string case
                if (params.get(0) instanceof String) {
                    if (!(params.get(1) instanceof String)) {
                        return false;
                    } else {
                        return ((String) params.get(0)).equals((String) params.get(1));
                    }
                } else if (params.get(1) instanceof String) {
                    if (!(params.get(0) instanceof String)) {
                        return false;
                    } else {
                        return ((String) params.get(0)).equals((String) params.get(1));
                    }
                }

                Object aNum = ConditionEvaluator.getNumber(params.get(0));
                Object bNum = ConditionEvaluator.getNumber(params.get(1));

                if (aNum != null && bNum != null) {
                    return Double.compare( (Double) aNum, (Double) bNum) == 0;
                }

                return params.get(0) == params.get(1);

            } else if (c == RuleTypes.Comparator.UNEQUAL) {

                // string case
                if (params.get(0) instanceof String) {
                    if (!(params.get(1) instanceof String)) {
                        return true;
                    } else {
                        return !((String) params.get(0)).equals((String) params.get(1));
                    }
                } else if (params.get(1) instanceof String) {
                    if (!(params.get(0) instanceof String)) {
                        return true;
                    } else {
                        return !((String) params.get(0)).equals((String) params.get(1));
                    }
                }

                Object aNum = ConditionEvaluator.getNumber(params.get(0));
                Object bNum = ConditionEvaluator.getNumber(params.get(1));

                if (aNum != null && bNum != null) {
                    return Double.compare( (double) aNum, (double) bNum) != 0;
                }

                return params.get(0) != params.get(1);

            } else {

                double a;
                double b;

                // Integer case
                if(params.get(0) instanceof Integer) {
                    if(params.get(1) instanceof Integer) {
                        a = (double) ((Integer) params.get(0));
                        b = (double) ((Integer) params.get(1));
                    } else if (params.get(1) instanceof Float) {
                        a = (double) ((Integer) params.get(0));
                        b = (double) ((Float) params.get(1));
                    } else if (params.get(1) instanceof Double) {
                        a = (double) ((Integer) params.get(0));
                        b = (double) params.get(1);
                    } else {
                        throw new Exception("One of condition parameters is not numeric");
                    }
                } else if (params.get(0) instanceof Float) {
                    if(params.get(1) instanceof Integer) {
                        a = (double) ((Float) params.get(0));
                        b = (double) ((Integer) params.get(1));
                    } else if (params.get(1) instanceof Float) {
                        a = (double) ((Float) params.get(0));
                        b = (double) ((Float) params.get(1));
                    } else if (params.get(1) instanceof Double) {
                        a = (double) ((Float) params.get(0));
                        b = (double) params.get(1);
                    } else {
                        throw new Exception("One of condition parameters is not numeric");
                    }
                } else if (params.get(0) instanceof Double) {
                    if(params.get(1) instanceof Integer) {
                        a = (double) ((double) params.get(0));
                        b = (double) ((Integer) params.get(1));
                    } else if (params.get(1) instanceof Float) {
                        a = (double) ((double) params.get(0));
                        b = (double) ((Float) params.get(1));
                    } else if (params.get(1) instanceof Double) {
                        a = (double) ((double) params.get(0));
                        b = (double) params.get(1);
                    } else {
                        throw new Exception("One of condition parameters is not numeric");
                    }
                } else {
                    throw new Exception("One of condition parameters is not numeric");
                }

                if (c == RuleTypes.Comparator.GREATER_THAN) {
                    return Double.compare(a,b) > 0;
                } else if (c == RuleTypes.Comparator.GREATER_THAN_EQUAL) {
                    return Double.compare(a,b) > 0 || Double.compare(a,b) == 0;
                } else if (c == RuleTypes.Comparator.LOWER_THAN) {
                    return Double.compare(a,b) < 0;
                } else if (c == RuleTypes.Comparator.LOWER_THAN_EQUAL) {
                    return Double.compare(a,b) < 0 || Double.compare(a,b) == 0;
                }

            }

            throw new Exception("Unknown Comparator used in rule");

        }

    }
}
