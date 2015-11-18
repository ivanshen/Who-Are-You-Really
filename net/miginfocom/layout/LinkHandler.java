package net.miginfocom.layout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public final class LinkHandler {
    public static final int HEIGHT = 3;
    private static final ArrayList<WeakReference<Object>> LAYOUTS;
    private static final ArrayList<HashMap<String, int[]>> VALUES;
    private static final ArrayList<HashMap<String, int[]>> VALUES_TEMP;
    public static final int WIDTH = 2;
    public static final int X = 0;
    public static final int X2 = 4;
    public static final int Y = 1;
    public static final int Y2 = 5;

    static {
        LAYOUTS = new ArrayList(X2);
        VALUES = new ArrayList(X2);
        VALUES_TEMP = new ArrayList(X2);
    }

    private LinkHandler() {
    }

    public static synchronized boolean clearBounds(Object obj, String str) {
        boolean z;
        synchronized (LinkHandler.class) {
            int size = LAYOUTS.size() - 1;
            while (size >= 0) {
                if (((WeakReference) LAYOUTS.get(size)).get() == obj) {
                    z = ((HashMap) VALUES.get(size)).remove(str) != null;
                } else {
                    size--;
                }
            }
            z = false;
        }
        return z;
    }

    static synchronized void clearTemporaryBounds(Object obj) {
        synchronized (LinkHandler.class) {
            for (int size = LAYOUTS.size() - 1; size >= 0; size--) {
                if (((WeakReference) LAYOUTS.get(size)).get() == obj) {
                    ((HashMap) VALUES_TEMP.get(size)).clear();
                    break;
                }
            }
        }
    }

    public static synchronized void clearWeakReferencesNow() {
        synchronized (LinkHandler.class) {
            LAYOUTS.clear();
        }
    }

    public static synchronized Integer getValue(Object obj, String str, int i) {
        Integer num;
        synchronized (LinkHandler.class) {
            Object obj2 = Y;
            num = null;
            for (int size = LAYOUTS.size() - 1; size >= 0; size--) {
                Object obj3 = ((WeakReference) LAYOUTS.get(size)).get();
                if (num == null && obj3 == obj) {
                    Integer valueOf;
                    int[] iArr = (int[]) ((HashMap) VALUES_TEMP.get(size)).get(str);
                    if (obj2 == null || iArr == null || iArr[i] == -2147471302) {
                        iArr = (int[]) ((HashMap) VALUES.get(size)).get(str);
                        valueOf = (iArr == null || iArr[i] == -2147471302) ? null : Integer.valueOf(iArr[i]);
                    } else {
                        valueOf = Integer.valueOf(iArr[i]);
                    }
                    obj2 = null;
                    num = valueOf;
                }
                if (obj3 == null) {
                    LAYOUTS.remove(size);
                    VALUES.remove(size);
                    VALUES_TEMP.remove(size);
                }
            }
        }
        return num;
    }

    public static synchronized boolean setBounds(Object obj, String str, int i, int i2, int i3, int i4) {
        boolean bounds;
        synchronized (LinkHandler.class) {
            bounds = setBounds(obj, str, i, i2, i3, i4, false, false);
        }
        return bounds;
    }

    static synchronized boolean setBounds(Object obj, String str, int i, int i2, int i3, int i4, boolean z, boolean z2) {
        boolean z3 = true;
        synchronized (LinkHandler.class) {
            int size = LAYOUTS.size() - 1;
            while (size >= 0) {
                if (((WeakReference) LAYOUTS.get(size)).get() == obj) {
                    HashMap hashMap = (HashMap) (z ? VALUES_TEMP : VALUES).get(size);
                    int[] iArr = (int[]) hashMap.get(str);
                    if (iArr != null && iArr[X] == i && iArr[Y] == i2 && iArr[WIDTH] == i3 && iArr[HEIGHT] == i4) {
                        z3 = false;
                    } else if (iArr == null || !z2) {
                        hashMap.put(str, new int[]{i, i2, i3, i4, i + i3, i2 + i4});
                    } else {
                        boolean z4;
                        int i5;
                        if (i != -2147471302) {
                            if (iArr[X] == -2147471302 || i < iArr[X]) {
                                iArr[X] = i;
                                iArr[WIDTH] = iArr[X2] - i;
                                z4 = true;
                            } else {
                                z4 = X;
                            }
                            if (i3 != -2147471302) {
                                i5 = i + i3;
                                if (iArr[X2] == -2147471302 || i5 > iArr[X2]) {
                                    iArr[X2] = i5;
                                    iArr[WIDTH] = i5 - iArr[X];
                                    z4 = true;
                                }
                            }
                        } else {
                            z4 = X;
                        }
                        if (i2 != -2147471302) {
                            if (iArr[Y] == -2147471302 || i2 < iArr[Y]) {
                                iArr[Y] = i2;
                                iArr[HEIGHT] = iArr[Y2] - i2;
                                z4 = true;
                            }
                            if (i4 != -2147471302) {
                                i5 = i2 + i4;
                                if (iArr[Y2] == -2147471302 || i5 > iArr[Y2]) {
                                    iArr[Y2] = i5;
                                    iArr[HEIGHT] = i5 - iArr[Y];
                                    z4 = true;
                                }
                            }
                        }
                        z3 = z4;
                    }
                } else {
                    size--;
                }
            }
            LAYOUTS.add(new WeakReference(obj));
            Object obj2 = new int[]{i, i2, i3, i4, i + i3, i2 + i4};
            HashMap hashMap2 = new HashMap(X2);
            if (z) {
                hashMap2.put(str, obj2);
            }
            VALUES_TEMP.add(hashMap2);
            hashMap2 = new HashMap(X2);
            if (!z) {
                hashMap2.put(str, obj2);
            }
            VALUES.add(hashMap2);
        }
        return z3;
    }
}
