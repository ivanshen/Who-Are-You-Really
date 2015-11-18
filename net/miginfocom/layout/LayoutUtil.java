package net.miginfocom.layout;

import java.beans.Beans;
import java.beans.ExceptionListener;
import java.beans.Introspector;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.IdentityHashMap;
import java.util.TreeSet;
import java.util.WeakHashMap;
import org.jfree.chart.JFreeChart;

public final class LayoutUtil {
    private static volatile WeakHashMap<Object, String> CR_MAP = null;
    private static volatile WeakHashMap<Object, Boolean> DT_MAP = null;
    static final int INF = 2097051;
    public static final int MAX = 2;
    public static final int MIN = 0;
    static final int NOT_SET = -2147471302;
    public static final int PREF = 1;
    private static final IdentityHashMap<Object, Object> SER_MAP;
    private static int eSz;
    private static int globalDebugMillis;
    private static byte[] readBuf;
    private static ByteArrayOutputStream writeOutputStream;

    static class 1 implements ExceptionListener {
        1() {
        }

        public void exceptionThrown(Exception exception) {
            exception.printStackTrace();
        }
    }

    static {
        CR_MAP = null;
        DT_MAP = null;
        eSz = MIN;
        globalDebugMillis = MIN;
        writeOutputStream = null;
        readBuf = null;
        SER_MAP = new IdentityHashMap(MAX);
    }

    private LayoutUtil() {
    }

    static int[] calculateSerial(int[][] iArr, ResizeConstraint[] resizeConstraintArr, Float[] fArr, int i, int i2) {
        int i3;
        int brokenBoundary;
        float[] fArr2 = new float[iArr.length];
        float f = 0.0f;
        for (i3 = MIN; i3 < iArr.length; i3 += PREF) {
            if (iArr[i3] != null) {
                float f2 = iArr[i3][i] != NOT_SET ? (float) iArr[i3][i] : 0.0f;
                brokenBoundary = getBrokenBoundary(f2, iArr[i3][MIN], iArr[i3][MAX]);
                if (brokenBoundary != NOT_SET) {
                    f2 = (float) brokenBoundary;
                }
                f += f2;
                fArr2[i3] = f2;
            }
        }
        i3 = Math.round(f);
        if (!(i3 == i2 || resizeConstraintArr == null)) {
            Object obj = i3 < i2 ? PREF : MIN;
            TreeSet treeSet = new TreeSet();
            for (brokenBoundary = MIN; brokenBoundary < iArr.length; brokenBoundary += PREF) {
                ResizeConstraint resizeConstraint = (ResizeConstraint) getIndexSafe(resizeConstraintArr, brokenBoundary);
                if (resizeConstraint != null) {
                    treeSet.add(Integer.valueOf(obj != null ? resizeConstraint.growPrio : resizeConstraint.shrinkPrio));
                }
            }
            Integer[] numArr = (Integer[]) treeSet.toArray(new Integer[treeSet.size()]);
            int i4 = MIN;
            float f3 = f;
            while (true) {
                int i5 = (obj == null || fArr == null) ? MIN : PREF;
                if (i4 > i5) {
                    break;
                }
                int length = numArr.length - 1;
                while (length >= 0) {
                    int intValue = numArr[length].intValue();
                    Float[] fArr3 = new Float[iArr.length];
                    float f4 = 0.0f;
                    brokenBoundary = MIN;
                    while (brokenBoundary < iArr.length) {
                        if (iArr[brokenBoundary] != null) {
                            ResizeConstraint resizeConstraint2 = (ResizeConstraint) getIndexSafe(resizeConstraintArr, brokenBoundary);
                            if (resizeConstraint2 != null) {
                                if (intValue == (obj != null ? resizeConstraint2.growPrio : resizeConstraint2.shrinkPrio)) {
                                    if (obj != null) {
                                        Float f5;
                                        if (i4 == 0 || resizeConstraint2.grow != null) {
                                            f5 = resizeConstraint2.grow;
                                        } else {
                                            f5 = fArr[brokenBoundary < fArr.length ? brokenBoundary : fArr.length - 1];
                                        }
                                        fArr3[brokenBoundary] = f5;
                                    } else {
                                        fArr3[brokenBoundary] = resizeConstraint2.shrink;
                                    }
                                    if (fArr3[brokenBoundary] != null) {
                                        f4 += fArr3[brokenBoundary].floatValue();
                                    }
                                }
                            }
                        }
                        brokenBoundary += PREF;
                    }
                    if (f4 > 0.0f) {
                        f = f3;
                        Object obj2;
                        do {
                            float f6 = ((float) i2) - f;
                            obj2 = null;
                            f3 = 0.0f;
                            brokenBoundary = MIN;
                            while (brokenBoundary < iArr.length && f4 > 1.0E-4f) {
                                float f7;
                                Float f8 = fArr3[brokenBoundary];
                                if (f8 != null) {
                                    Object obj3;
                                    float f9;
                                    float floatValue = (f8.floatValue() * f6) / f4;
                                    f7 = fArr2[brokenBoundary] + floatValue;
                                    if (iArr[brokenBoundary] != null) {
                                        int brokenBoundary2 = getBrokenBoundary(f7, iArr[brokenBoundary][MIN], iArr[brokenBoundary][MAX]);
                                        if (brokenBoundary2 != NOT_SET) {
                                            fArr3[brokenBoundary] = null;
                                            obj3 = PREF;
                                            f7 = f3 + f8.floatValue();
                                            f3 = (float) brokenBoundary2;
                                            f9 = f3 - fArr2[brokenBoundary];
                                            fArr2[brokenBoundary] = f3;
                                            f += f9;
                                            f3 = f7;
                                            obj2 = obj3;
                                            f7 = f;
                                        }
                                    }
                                    float f10 = f7;
                                    f7 = f3;
                                    f3 = f10;
                                    float f11 = floatValue;
                                    obj3 = obj2;
                                    f9 = f11;
                                    fArr2[brokenBoundary] = f3;
                                    f += f9;
                                    f3 = f7;
                                    obj2 = obj3;
                                    f7 = f;
                                } else {
                                    f7 = f;
                                }
                                brokenBoundary += PREF;
                                f = f7;
                            }
                            f4 -= f3;
                        } while (obj2 != null);
                    } else {
                        f = f3;
                    }
                    length--;
                    f3 = f;
                }
                i4 += PREF;
            }
        }
        return roundSizes(fArr2);
    }

    static BoundSize derive(BoundSize boundSize, UnitValue unitValue, UnitValue unitValue2, UnitValue unitValue3) {
        if (boundSize == null || boundSize.isUnset()) {
            return new BoundSize(unitValue, unitValue2, unitValue3, null);
        }
        return new BoundSize(unitValue != null ? unitValue : boundSize.getMin(), unitValue2 != null ? unitValue2 : boundSize.getPreferred(), unitValue3 != null ? unitValue3 : boundSize.getMax(), boundSize.getGapPush(), null);
    }

    static boolean equals(Object obj, Object obj2) {
        return obj == obj2 || !(obj == null || obj2 == null || !obj.equals(obj2));
    }

    private static int getBrokenBoundary(float f, int i, int i2) {
        if (i != NOT_SET) {
            if (f < ((float) i)) {
                return i;
            }
        } else if (f < 0.0f) {
            return MIN;
        }
        return (i2 == NOT_SET || f <= ((float) i2)) ? NOT_SET : i2;
    }

    static String getCCString(Object obj) {
        return CR_MAP != null ? (String) CR_MAP.get(obj) : null;
    }

    public static int getDesignTimeEmptySize() {
        return eSz;
    }

    public static int getGlobalDebugMillis() {
        return globalDebugMillis;
    }

    static Object getIndexSafe(Object[] objArr, int i) {
        if (objArr == null) {
            return null;
        }
        if (i >= objArr.length) {
            i = objArr.length - 1;
        }
        return objArr[i];
    }

    static UnitValue getInsets(LC lc, int i, boolean z) {
        UnitValue[] insets = lc.getInsets();
        return (insets == null || insets[i] == null) ? z ? PlatformDefaults.getPanelInsets(i) : UnitValue.ZERO : insets[i];
    }

    public static Object getSerializedObject(Object obj) {
        Object remove;
        synchronized (SER_MAP) {
            remove = SER_MAP.remove(obj);
        }
        return remove;
    }

    public static int getSizeSafe(int[] iArr, int i) {
        return (iArr == null || iArr[i] == NOT_SET) ? i == MAX ? INF : MIN : iArr[i];
    }

    public static String getVersion() {
        return "3.7.4";
    }

    public static boolean isDesignTime(ContainerWrapper containerWrapper) {
        Object obj = null;
        if (DT_MAP == null) {
            return Beans.isDesignTime();
        }
        if (!(containerWrapper == null || DT_MAP.containsKey(containerWrapper.getComponent()))) {
            containerWrapper = null;
        }
        WeakHashMap weakHashMap = DT_MAP;
        if (containerWrapper != null) {
            obj = containerWrapper.getComponent();
        }
        Boolean bool = (Boolean) weakHashMap.get(obj);
        return bool != null && bool.booleanValue();
    }

    public static boolean isLeftToRight(LC lc, ContainerWrapper containerWrapper) {
        return (lc == null || lc.getLeftToRight() == null) ? containerWrapper == null || containerWrapper.isLeftToRight() : lc.getLeftToRight().booleanValue();
    }

    static void putCCString(Object obj, String str) {
        if (str != null && obj != null && isDesignTime(null)) {
            if (CR_MAP == null) {
                CR_MAP = new WeakHashMap(64);
            }
            CR_MAP.put(obj, str);
        }
    }

    public static synchronized Object readAsXML(ObjectInput objectInput) throws IOException {
        Object obj = null;
        synchronized (LayoutUtil.class) {
            ClassLoader contextClassLoader;
            if (readBuf == null) {
                readBuf = new byte[16384];
            }
            Thread currentThread = Thread.currentThread();
            try {
                contextClassLoader = currentThread.getContextClassLoader();
                try {
                    currentThread.setContextClassLoader(LayoutUtil.class.getClassLoader());
                } catch (SecurityException e) {
                }
            } catch (SecurityException e2) {
                contextClassLoader = null;
            }
            try {
                int readInt = objectInput.readInt();
                if (readInt > readBuf.length) {
                    readBuf = new byte[readInt];
                }
                objectInput.readFully(readBuf, MIN, readInt);
                obj = new XMLDecoder(new ByteArrayInputStream(readBuf, MIN, readInt)).readObject();
            } catch (EOFException e3) {
            }
            if (contextClassLoader != null) {
                currentThread.setContextClassLoader(contextClassLoader);
            }
        }
        return obj;
    }

    static int[] roundSizes(float[] fArr) {
        int[] iArr = new int[fArr.length];
        float f = 0.0f;
        for (int i = MIN; i < iArr.length; i += PREF) {
            int i2 = (int) (f + JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA);
            f += fArr[i];
            iArr[i] = ((int) (f + JFreeChart.DEFAULT_BACKGROUND_IMAGE_ALPHA)) - i2;
        }
        return iArr;
    }

    static synchronized void setDelegate(Class cls, PersistenceDelegate persistenceDelegate) {
        synchronized (LayoutUtil.class) {
            try {
                Introspector.getBeanInfo(cls, 3).getBeanDescriptor().setValue("persistenceDelegate", persistenceDelegate);
            } catch (Exception e) {
            }
        }
    }

    public static void setDesignTime(ContainerWrapper containerWrapper, boolean z) {
        if (DT_MAP == null) {
            DT_MAP = new WeakHashMap();
        }
        DT_MAP.put(containerWrapper != null ? containerWrapper.getComponent() : null, Boolean.valueOf(z));
    }

    public static void setDesignTimeEmptySize(int i) {
        eSz = i;
    }

    public static void setGlobalDebugMillis(int i) {
        globalDebugMillis = i;
    }

    public static void setSerializedObject(Object obj, Object obj2) {
        synchronized (SER_MAP) {
            SER_MAP.put(obj, obj2);
        }
    }

    static int sum(int[] iArr) {
        return sum(iArr, MIN, iArr.length);
    }

    static int sum(int[] iArr, int i, int i2) {
        int i3 = MIN;
        int i4 = i + i2;
        while (i < i4) {
            i3 += iArr[i];
            i += PREF;
        }
        return i3;
    }

    static void throwCC() {
        throw new IllegalStateException("setStoreConstraintData(true) must be set for strings to be saved.");
    }

    public static synchronized void writeAsXML(ObjectOutput objectOutput, Object obj) throws IOException {
        synchronized (LayoutUtil.class) {
            if (writeOutputStream == null) {
                writeOutputStream = new ByteArrayOutputStream(16384);
            }
            writeOutputStream.reset();
            writeXMLObject(writeOutputStream, obj, new 1());
            byte[] toByteArray = writeOutputStream.toByteArray();
            objectOutput.writeInt(toByteArray.length);
            objectOutput.write(toByteArray);
        }
    }

    static void writeXMLObject(OutputStream outputStream, Object obj, ExceptionListener exceptionListener) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(LayoutUtil.class.getClassLoader());
        XMLEncoder xMLEncoder = new XMLEncoder(outputStream);
        if (exceptionListener != null) {
            xMLEncoder.setExceptionListener(exceptionListener);
        }
        xMLEncoder.writeObject(obj);
        xMLEncoder.close();
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
}
