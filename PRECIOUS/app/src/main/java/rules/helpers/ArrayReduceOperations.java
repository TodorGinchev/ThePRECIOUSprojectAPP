package rules.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by christopher on 10.06.16.
 */
public class ArrayReduceOperations {

    public static Object sum(ArrayList<Object> data) {
        assert(data.size()!=0);
        Object o = data.get(0);

        if(o instanceof Integer) {
            int sum = 0;
            for (Object _o : data) {
                sum += (Integer) _o;
            }
            return sum;
        } else if (o instanceof Float) {
            float sum = 0;
            for (Object _o : data) {
                sum += (Float) _o;
            }
            return sum;
        } else if (o instanceof Double) {
            double sum = 0;
            for (Object _o : data) {
                sum += (Double) _o;
            }
            return sum;
        } else {
            return null;
        }

    }

    public static Object average(ArrayList<Object> data) {
        assert(data.size()!=0);
        Object o = data.get(0);

        if(o instanceof Integer) {
            double sum = 0;
            for (Object _o : data) {
                sum += (Integer) _o;
            }
            return sum/data.size();
        } else if (o instanceof Float) {
            double sum = 0;
            for (Object _o : data) {
                sum += (Float) _o;
            }
            return sum/ (float) data.size();
        } else if (o instanceof Double) {
            double sum = 0;
            for (Object _o : data) {
                sum += (Double) _o;
            }
            return sum/ (double) data.size();
        } else {
            return null;
        }

    }

    public static Object median(ArrayList<Object> data) {
        assert(data.size()!=0);
        int middle = data.size()/2;
        Object o = data.get(0);

        if(o instanceof Integer) {
            ArrayList<Integer> v = new ArrayList<>();
            for (Object _o : data) {
                v.add((int)_o);
            }
            Collections.sort(v);
            if (v.size()%2 == 1) {
                return v.get(middle);
            } else {
                return ((double) (v.get(middle-1) + v.get(middle))) / 2.0;
            }
        } else if (o instanceof Float) {
            ArrayList<Float> v = new ArrayList<>();
            for (Object _o : data) {
                v.add((float)_o);
            }
            Collections.sort(v);
            if (v.size()%2 == 1) {
                return v.get(middle);
            } else {
                return ((double) (v.get(middle-1) + v.get(middle))) / 2.0;
            }
        } else if (o instanceof Double) {
            ArrayList<Double> v = new ArrayList<>();
            for (Object _o : data) {
                v.add((double)_o);
            }
            Collections.sort(v);
            if (v.size()%2 == 1) {
                return v.get(middle);
            } else {
                return ((double) (v.get(middle-1) + v.get(middle))) / 2.0;
            }
        } else {
            return null;
        }

    }


    public static Object min(ArrayList<Object> data) {
        assert(data.size()!=0);
        Object o = data.get(0);

        if(o instanceof Integer) {
            Integer min = null;
            for (Object _o : data) {
                int _ov = (int) _o;
                if(min == null || _ov < min) {
                    min = _ov;
                }
            }
            return min;
        } else if (o instanceof Float) {
            Float min = null;
            for (Object _o : data) {
                float _ov = (float) _o;
                if(min == null || _ov < min) {
                    min = _ov;
                }
            }
            return min;
        } else if (o instanceof Double) {
            Double min = null;
            for (Object _o : data) {
                double _ov = (double) _o;
                if(min == null || _ov < min) {
                    min = _ov;
                }
            }
            return min;
        } else {
            return null;
        }

    }

    public static Object max(ArrayList<Object> data) {
        assert(data.size()!=0);
        Object o = data.get(0);

        if(o instanceof Integer) {
            Integer max = null;
            for (Object _o : data) {
                int _ov = (int) _o;
                if(max == null || _ov > max) {
                    max = _ov;
                }
            }
            return max;
        } else if (o instanceof Float) {
            Float max = null;
            for (Object _o : data) {
                float _ov = (float) _o;
                if(max == null || _ov > max) {
                    max = _ov;
                }
            }
            return max;
        } else if (o instanceof Double) {
            Double max = null;
            for (Object _o : data) {
                double _ov = (double) _o;
                if(max == null || _ov > max) {
                    max = _ov;
                }
            }
            return max;
        } else {
            return null;
        }

    }


}
