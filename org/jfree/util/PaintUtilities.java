package org.jfree.util;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class PaintUtilities {
    private PaintUtilities() {
    }

    public static boolean equal(Paint p1, Paint p2) {
        if (p1 == null) {
            if (p2 == null) {
                return true;
            }
            return false;
        } else if (p2 == null) {
            return false;
        } else {
            boolean result;
            if ((p1 instanceof GradientPaint) && (p2 instanceof GradientPaint)) {
                GradientPaint gp1 = (GradientPaint) p1;
                GradientPaint gp2 = (GradientPaint) p2;
                if (gp1.getColor1().equals(gp2.getColor1()) && gp1.getColor2().equals(gp2.getColor2()) && gp1.getPoint1().equals(gp2.getPoint1()) && gp1.getPoint2().equals(gp2.getPoint2()) && gp1.isCyclic() == gp2.isCyclic() && gp1.getTransparency() == gp1.getTransparency()) {
                    result = true;
                } else {
                    result = false;
                }
            } else {
                result = p1.equals(p2);
            }
            return result;
        }
    }

    public static String colorToString(Color c) {
        int i;
        try {
            Field[] fields = Color.class.getFields();
            for (Field f : fields) {
                if (Modifier.isPublic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers())) {
                    String name = f.getName();
                    Object oColor = f.get(null);
                    if ((oColor instanceof Color) && c.equals(oColor)) {
                        return name;
                    }
                }
            }
        } catch (Exception e) {
        }
        String color = Integer.toHexString(c.getRGB() & 16777215);
        StringBuffer retval = new StringBuffer(7);
        retval.append("#");
        int fillUp = 6 - color.length();
        for (i = 0; i < fillUp; i++) {
            retval.append("0");
        }
        retval.append(color);
        return retval.toString();
    }

    public static Color stringToColor(String value) {
        if (value == null) {
            return Color.black;
        }
        try {
            return Color.decode(value);
        } catch (NumberFormatException e) {
            try {
                return (Color) Color.class.getField(value).get(null);
            } catch (Exception e2) {
                Log.info("No such Color : " + value);
                return Color.black;
            }
        }
    }
}
