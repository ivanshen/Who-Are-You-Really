package org.jfree.ui.tabbedui;

import javax.swing.JComponent;

public abstract class DetailEditor extends JComponent {
    private boolean confirmed;
    private Object object;

    public abstract void clear();

    protected abstract void fillObject();

    protected abstract void updateObject(Object obj);

    public void update() {
        if (this.object == null) {
            throw new IllegalStateException();
        }
        updateObject(this.object);
        setConfirmed(false);
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
        this.object = object;
        setConfirmed(false);
        fillObject();
    }

    protected static int parseInt(String text, int def) {
        try {
            def = Integer.parseInt(text);
        } catch (NumberFormatException e) {
        }
        return def;
    }

    public boolean isConfirmed() {
        return this.confirmed;
    }

    protected void setConfirmed(boolean confirmed) {
        boolean oldConfirmed = this.confirmed;
        this.confirmed = confirmed;
        firePropertyChange("confirmed", oldConfirmed, confirmed);
    }
}
