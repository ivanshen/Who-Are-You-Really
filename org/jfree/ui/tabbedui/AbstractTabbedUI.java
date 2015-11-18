package org.jfree.ui.tabbedui;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jfree.util.Log;

public abstract class AbstractTabbedUI extends JComponent {
    public static final String GLOBAL_MENU_PROPERTY = "globalMenu";
    public static final String JMENUBAR_PROPERTY = "jMenuBar";
    private Action closeAction;
    private JComponent currentToolbar;
    private boolean globalMenu;
    private JMenuBar jMenuBar;
    private ArrayList rootEditors;
    private int selectedRootEditor;
    private JTabbedPane tabbedPane;
    private JPanel toolbarContainer;

    protected class ExitAction extends AbstractAction {
        public ExitAction() {
            putValue("Name", "Exit");
        }

        public void actionPerformed(ActionEvent e) {
            AbstractTabbedUI.this.attempExit();
        }
    }

    private class TabChangeHandler implements ChangeListener {
        private final JTabbedPane pane;

        public TabChangeHandler(JTabbedPane pane) {
            this.pane = pane;
        }

        public void stateChanged(ChangeEvent e) {
            AbstractTabbedUI.this.setSelectedEditor(this.pane.getSelectedIndex());
        }
    }

    private class TabEnableChangeListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent evt) {
            if (!evt.getPropertyName().equals("enabled")) {
                Log.debug("PropertyName");
            } else if (evt.getSource() instanceof RootEditor) {
                AbstractTabbedUI.this.updateRootEditorEnabled((RootEditor) evt.getSource());
            } else {
                Log.debug("Source");
            }
        }
    }

    protected abstract void attempExit();

    protected abstract JMenu[] getPostfixMenus();

    protected abstract JMenu[] getPrefixMenus();

    public AbstractTabbedUI() {
        this.selectedRootEditor = -1;
        this.toolbarContainer = new JPanel();
        this.toolbarContainer.setLayout(new BorderLayout());
        this.tabbedPane = new JTabbedPane(3);
        this.tabbedPane.addChangeListener(new TabChangeHandler(this.tabbedPane));
        this.rootEditors = new ArrayList();
        setLayout(new BorderLayout());
        add(this.toolbarContainer, "North");
        add(this.tabbedPane, "Center");
        this.closeAction = createCloseAction();
    }

    protected JTabbedPane getTabbedPane() {
        return this.tabbedPane;
    }

    public boolean isGlobalMenu() {
        return this.globalMenu;
    }

    public void setGlobalMenu(boolean globalMenu) {
        this.globalMenu = globalMenu;
        if (isGlobalMenu()) {
            setJMenuBar(updateGlobalMenubar());
        } else if (getRootEditorCount() > 0) {
            setJMenuBar(createEditorMenubar(getRootEditor(getSelectedEditor())));
        }
    }

    public JMenuBar getJMenuBar() {
        return this.jMenuBar;
    }

    protected void setJMenuBar(JMenuBar menuBar) {
        JMenuBar oldMenuBar = this.jMenuBar;
        this.jMenuBar = menuBar;
        firePropertyChange(JMENUBAR_PROPERTY, oldMenuBar, menuBar);
    }

    protected Action createCloseAction() {
        return new ExitAction();
    }

    public Action getCloseAction() {
        return this.closeAction;
    }

    private void addMenus(JMenuBar menuBar, JMenu[] customMenus) {
        for (JMenu add : customMenus) {
            menuBar.add(add);
        }
    }

    private JMenuBar updateGlobalMenubar() {
        JMenuBar menuBar = getJMenuBar();
        if (menuBar == null) {
            menuBar = new JMenuBar();
        } else {
            menuBar.removeAll();
        }
        addMenus(menuBar, getPrefixMenus());
        for (int i = 0; i < this.rootEditors.size(); i++) {
            addMenus(menuBar, ((RootEditor) this.rootEditors.get(i)).getMenus());
        }
        addMenus(menuBar, getPostfixMenus());
        return menuBar;
    }

    private JMenuBar createEditorMenubar(RootEditor root) {
        JMenuBar menuBar = getJMenuBar();
        if (menuBar == null) {
            menuBar = new JMenuBar();
        } else {
            menuBar.removeAll();
        }
        addMenus(menuBar, getPrefixMenus());
        if (isGlobalMenu()) {
            for (int i = 0; i < this.rootEditors.size(); i++) {
                addMenus(menuBar, ((RootEditor) this.rootEditors.get(i)).getMenus());
            }
        } else {
            addMenus(menuBar, root.getMenus());
        }
        addMenus(menuBar, getPostfixMenus());
        return menuBar;
    }

    public void addRootEditor(RootEditor rootPanel) {
        this.rootEditors.add(rootPanel);
        this.tabbedPane.add(rootPanel.getEditorName(), rootPanel.getMainPanel());
        rootPanel.addPropertyChangeListener("enabled", new TabEnableChangeListener());
        updateRootEditorEnabled(rootPanel);
        if (getRootEditorCount() == 1) {
            setSelectedEditor(0);
        } else if (isGlobalMenu()) {
            setJMenuBar(updateGlobalMenubar());
        }
    }

    public int getRootEditorCount() {
        return this.rootEditors.size();
    }

    public RootEditor getRootEditor(int pos) {
        return (RootEditor) this.rootEditors.get(pos);
    }

    public int getSelectedEditor() {
        return this.selectedRootEditor;
    }

    public void setSelectedEditor(int selectedEditor) {
        if (this.selectedRootEditor != selectedEditor) {
            int i;
            boolean shouldBeActive;
            RootEditor container;
            this.selectedRootEditor = selectedEditor;
            for (i = 0; i < this.rootEditors.size(); i++) {
                if (i == selectedEditor) {
                    shouldBeActive = true;
                } else {
                    shouldBeActive = false;
                }
                container = (RootEditor) this.rootEditors.get(i);
                if (container.isActive() && !shouldBeActive) {
                    container.setActive(false);
                }
            }
            if (this.currentToolbar != null) {
                closeToolbar();
                this.toolbarContainer.removeAll();
                this.currentToolbar = null;
            }
            for (i = 0; i < this.rootEditors.size(); i++) {
                if (i == selectedEditor) {
                    shouldBeActive = true;
                } else {
                    shouldBeActive = false;
                }
                container = (RootEditor) this.rootEditors.get(i);
                if (!container.isActive() && shouldBeActive) {
                    container.setActive(true);
                    setJMenuBar(createEditorMenubar(container));
                    this.currentToolbar = container.getToolbar();
                    if (this.currentToolbar != null) {
                        this.toolbarContainer.add(this.currentToolbar, "Center");
                        this.toolbarContainer.setVisible(true);
                        this.currentToolbar.setVisible(true);
                    } else {
                        this.toolbarContainer.setVisible(false);
                    }
                    getJMenuBar().repaint();
                }
            }
        }
    }

    private void closeToolbar() {
        if (this.currentToolbar != null) {
            if (this.currentToolbar.getParent() != this.toolbarContainer) {
                Window w = SwingUtilities.windowForComponent(this.currentToolbar);
                if (w != null) {
                    w.setVisible(false);
                    w.dispose();
                }
            }
            this.currentToolbar.setVisible(false);
        }
    }

    protected void updateRootEditorEnabled(RootEditor editor) {
        boolean enabled = editor.isEnabled();
        for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
            if (this.tabbedPane.getComponentAt(i) == editor.getMainPanel()) {
                this.tabbedPane.setEnabledAt(i, enabled);
                return;
            }
        }
    }
}
