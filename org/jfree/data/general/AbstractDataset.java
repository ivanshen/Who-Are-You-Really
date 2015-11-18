package org.jfree.data.general;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectInputValidation;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.EventListener;
import javax.swing.event.EventListenerList;
import org.jfree.chart.util.ParamChecks;

public abstract class AbstractDataset implements Dataset, Cloneable, Serializable, ObjectInputValidation {
    private static final long serialVersionUID = 1918768939869230744L;
    private DatasetGroup group;
    private transient EventListenerList listenerList;
    private boolean notify;

    protected AbstractDataset() {
        this.group = new DatasetGroup();
        this.listenerList = new EventListenerList();
        this.notify = true;
    }

    public DatasetGroup getGroup() {
        return this.group;
    }

    public void setGroup(DatasetGroup group) {
        ParamChecks.nullNotPermitted(group, "group");
        this.group = group;
    }

    public boolean getNotify() {
        return this.notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
        if (notify) {
            fireDatasetChanged();
        }
    }

    public void addChangeListener(DatasetChangeListener listener) {
        this.listenerList.add(DatasetChangeListener.class, listener);
    }

    public void removeChangeListener(DatasetChangeListener listener) {
        this.listenerList.remove(DatasetChangeListener.class, listener);
    }

    public boolean hasListener(EventListener listener) {
        return Arrays.asList(this.listenerList.getListenerList()).contains(listener);
    }

    protected void fireDatasetChanged() {
        if (this.notify) {
            notifyListeners(new DatasetChangeEvent(this, this));
        }
    }

    protected void notifyListeners(DatasetChangeEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == DatasetChangeListener.class) {
                ((DatasetChangeListener) listeners[i + 1]).datasetChanged(event);
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        AbstractDataset clone = (AbstractDataset) super.clone();
        clone.listenerList = new EventListenerList();
        return clone;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.listenerList = new EventListenerList();
        stream.registerValidation(this, 10);
    }

    public void validateObject() throws InvalidObjectException {
        fireDatasetChanged();
    }
}
