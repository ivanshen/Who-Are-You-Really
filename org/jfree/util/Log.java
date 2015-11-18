package org.jfree.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Log {
    private static Log singleton;
    private int debuglevel;
    private HashMap logContexts;
    private LogTarget[] logTargets;

    public static class SimpleMessage {
        private String message;
        private Object[] param;

        public SimpleMessage(String message, Object param1) {
            this.message = message;
            this.param = new Object[]{param1};
        }

        public SimpleMessage(String message, Object param1, Object param2) {
            this.message = message;
            this.param = new Object[]{param1, param2};
        }

        public SimpleMessage(String message, Object param1, Object param2, Object param3) {
            this.message = message;
            this.param = new Object[]{param1, param2, param3};
        }

        public SimpleMessage(String message, Object param1, Object param2, Object param3, Object param4) {
            this.message = message;
            this.param = new Object[]{param1, param2, param3, param4};
        }

        public SimpleMessage(String message, Object[] param) {
            this.message = message;
            this.param = param;
        }

        public String toString() {
            StringBuffer b = new StringBuffer();
            b.append(this.message);
            if (this.param != null) {
                for (Object append : this.param) {
                    b.append(append);
                }
            }
            return b.toString();
        }
    }

    protected Log() {
        this.logContexts = new HashMap();
        this.logTargets = new LogTarget[0];
        this.debuglevel = 100;
    }

    public static synchronized Log getInstance() {
        Log log;
        synchronized (Log.class) {
            if (singleton == null) {
                singleton = new Log();
            }
            log = singleton;
        }
        return log;
    }

    protected static synchronized void defineLog(Log log) {
        synchronized (Log.class) {
            singleton = log;
        }
    }

    public int getDebuglevel() {
        return this.debuglevel;
    }

    protected void setDebuglevel(int debuglevel) {
        this.debuglevel = debuglevel;
    }

    public synchronized void addTarget(LogTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        LogTarget[] data = new LogTarget[(this.logTargets.length + 1)];
        System.arraycopy(this.logTargets, 0, data, 0, this.logTargets.length);
        data[this.logTargets.length] = target;
        this.logTargets = data;
    }

    public synchronized void removeTarget(LogTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        ArrayList l = new ArrayList();
        l.addAll(Arrays.asList(this.logTargets));
        l.remove(target);
        this.logTargets = (LogTarget[]) l.toArray(new LogTarget[l.size()]);
    }

    public LogTarget[] getTargets() {
        return (LogTarget[]) this.logTargets.clone();
    }

    public synchronized void replaceTargets(LogTarget target) {
        if (target == null) {
            throw new NullPointerException();
        }
        this.logTargets = new LogTarget[]{target};
    }

    public static void debug(Object message) {
        log(3, message);
    }

    public static void debug(Object message, Exception e) {
        log(3, message, e);
    }

    public static void info(Object message) {
        log(2, message);
    }

    public static void info(Object message, Exception e) {
        log(2, message, e);
    }

    public static void warn(Object message) {
        log(1, message);
    }

    public static void warn(Object message, Exception e) {
        log(1, message, e);
    }

    public static void error(Object message) {
        log(0, message);
    }

    public static void error(Object message, Exception e) {
        log(0, message, e);
    }

    protected void doLog(int level, Object message) {
        if (level > 3) {
            level = 3;
        }
        if (level <= this.debuglevel) {
            for (LogTarget t : this.logTargets) {
                t.log(level, message);
            }
        }
    }

    public static void log(int level, Object message) {
        getInstance().doLog(level, message);
    }

    public static void log(int level, Object message, Exception e) {
        getInstance().doLog(level, message, e);
    }

    protected void doLog(int level, Object message, Exception e) {
        if (level > 3) {
            level = 3;
        }
        if (level <= this.debuglevel) {
            for (LogTarget t : this.logTargets) {
                t.log(level, message, e);
            }
        }
    }

    public void init() {
    }

    public static boolean isDebugEnabled() {
        return getInstance().getDebuglevel() >= 3;
    }

    public static boolean isInfoEnabled() {
        return getInstance().getDebuglevel() >= 2;
    }

    public static boolean isWarningEnabled() {
        return getInstance().getDebuglevel() >= 1;
    }

    public static boolean isErrorEnabled() {
        return getInstance().getDebuglevel() >= 0;
    }

    public static LogContext createContext(Class context) {
        return createContext(context.getName());
    }

    public static LogContext createContext(String context) {
        return getInstance().internalCreateContext(context);
    }

    protected LogContext internalCreateContext(String context) {
        LogContext ctx;
        synchronized (this) {
            ctx = (LogContext) this.logContexts.get(context);
            if (ctx == null) {
                ctx = new LogContext(context);
                this.logContexts.put(context, ctx);
            }
        }
        return ctx;
    }
}
