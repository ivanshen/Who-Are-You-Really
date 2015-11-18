package org.jfree.ui.tabbedui;

import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JMenu;

public interface RootEditor {
    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void addPropertyChangeListener(String str, PropertyChangeListener propertyChangeListener);

    String getEditorName();

    JComponent getMainPanel();

    JMenu[] getMenus();

    JComponent getToolbar();

    boolean isActive();

    boolean isEnabled();

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);

    void removePropertyChangeListener(String str, PropertyChangeListener propertyChangeListener);

    void setActive(boolean z);
}
