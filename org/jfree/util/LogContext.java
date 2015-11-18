package org.jfree.util;

import org.jfree.util.Log.SimpleMessage;

public class LogContext {
    private String contextPrefix;

    public LogContext(String contextPrefix) {
        this.contextPrefix = contextPrefix;
    }

    public boolean isDebugEnabled() {
        return Log.isDebugEnabled();
    }

    public boolean isInfoEnabled() {
        return Log.isInfoEnabled();
    }

    public boolean isWarningEnabled() {
        return Log.isWarningEnabled();
    }

    public boolean isErrorEnabled() {
        return Log.isErrorEnabled();
    }

    public void debug(Object message) {
        log(3, message);
    }

    public void debug(Object message, Exception e) {
        log(3, message, e);
    }

    public void info(Object message) {
        log(2, message);
    }

    public void info(Object message, Exception e) {
        log(2, message, e);
    }

    public void warn(Object message) {
        log(1, message);
    }

    public void warn(Object message, Exception e) {
        log(1, message, e);
    }

    public void error(Object message) {
        log(0, message);
    }

    public void error(Object message, Exception e) {
        log(0, message, e);
    }

    public void log(int level, Object message) {
        if (this.contextPrefix != null) {
            Log.getInstance().doLog(level, new SimpleMessage(this.contextPrefix, ":", message));
        } else {
            Log.getInstance().doLog(level, message);
        }
    }

    public void log(int level, Object message, Exception e) {
        if (this.contextPrefix != null) {
            Log.getInstance().doLog(level, new SimpleMessage(this.contextPrefix, ":", message), e);
        } else {
            Log.getInstance().doLog(level, message, e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LogContext)) {
            return false;
        }
        LogContext logContext = (LogContext) o;
        if (this.contextPrefix != null) {
            if (this.contextPrefix.equals(logContext.contextPrefix)) {
                return true;
            }
            return false;
        } else if (logContext.contextPrefix != null) {
            return false;
        } else {
            return true;
        }
    }

    public int hashCode() {
        return this.contextPrefix != null ? this.contextPrefix.hashCode() : 0;
    }
}
