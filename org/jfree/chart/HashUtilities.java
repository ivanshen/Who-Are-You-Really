package org.jfree.chart;

import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.util.BooleanList;
import org.jfree.util.PaintList;
import org.jfree.util.StrokeList;

public class HashUtilities {
    public static int hashCodeForPaint(Paint p) {
        if (p == null) {
            return 0;
        }
        if (!(p instanceof GradientPaint)) {
            return p.hashCode();
        }
        GradientPaint gp = (GradientPaint) p;
        return ((((((gp.getColor1().hashCode() + 7141) * 37) + gp.getPoint1().hashCode()) * 37) + gp.getColor2().hashCode()) * 37) + gp.getPoint2().hashCode();
    }

    public static int hashCodeForDoubleArray(double[] a) {
        if (a == null) {
            return 0;
        }
        int result = 193;
        for (double doubleToLongBits : a) {
            long temp = Double.doubleToLongBits(doubleToLongBits);
            result = (result * 29) + ((int) ((temp >>> 32) ^ temp));
        }
        return result;
    }

    public static int hashCode(int pre, boolean b) {
        return (b ? 0 : 1) + (pre * 37);
    }

    public static int hashCode(int pre, int i) {
        return (pre * 37) + i;
    }

    public static int hashCode(int pre, double d) {
        long l = Double.doubleToLongBits(d);
        return (pre * 37) + ((int) ((l >>> 32) ^ l));
    }

    public static int hashCode(int pre, Paint p) {
        return (pre * 37) + hashCodeForPaint(p);
    }

    public static int hashCode(int pre, Stroke s) {
        return (pre * 37) + (s != null ? s.hashCode() : 0);
    }

    public static int hashCode(int pre, String s) {
        return (pre * 37) + (s != null ? s.hashCode() : 0);
    }

    public static int hashCode(int pre, Comparable c) {
        return (pre * 37) + (c != null ? c.hashCode() : 0);
    }

    public static int hashCode(int pre, Object obj) {
        return (pre * 37) + (obj != null ? obj.hashCode() : 0);
    }

    public static int hashCode(int pre, BooleanList list) {
        if (list == null) {
            return pre;
        }
        int size = list.size();
        int result = hashCode(127, size);
        if (size > 0) {
            result = hashCode(result, list.getBoolean(0));
            if (size > 1) {
                result = hashCode(result, list.getBoolean(size - 1));
                if (size > 2) {
                    result = hashCode(result, list.getBoolean(size / 2));
                }
            }
        }
        return (pre * 37) + result;
    }

    public static int hashCode(int pre, PaintList list) {
        if (list == null) {
            return pre;
        }
        int size = list.size();
        int result = hashCode(127, size);
        if (size > 0) {
            result = hashCode(result, list.getPaint(0));
            if (size > 1) {
                result = hashCode(result, list.getPaint(size - 1));
                if (size > 2) {
                    result = hashCode(result, list.getPaint(size / 2));
                }
            }
        }
        return (pre * 37) + result;
    }

    public static int hashCode(int pre, StrokeList list) {
        if (list == null) {
            return pre;
        }
        int size = list.size();
        int result = hashCode(127, size);
        if (size > 0) {
            result = hashCode(result, list.getStroke(0));
            if (size > 1) {
                result = hashCode(result, list.getStroke(size - 1));
                if (size > 2) {
                    result = hashCode(result, list.getStroke(size / 2));
                }
            }
        }
        return (pre * 37) + result;
    }
}
