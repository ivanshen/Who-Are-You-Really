package org.jfree.ui.about;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.jfree.util.ResourceBundleWrapper;

public class SystemPropertiesFrame extends JFrame implements ActionListener {
    private static final String CLOSE_COMMAND = "CLOSE";
    private static final String COPY_COMMAND = "COPY";
    private SystemPropertiesPanel panel;

    public SystemPropertiesFrame(boolean menu) {
        String baseName = "org.jfree.ui.about.resources.AboutResources";
        ResourceBundle resources = ResourceBundleWrapper.getBundle("org.jfree.ui.about.resources.AboutResources");
        setTitle(resources.getString("system-frame.title"));
        setDefaultCloseOperation(2);
        if (menu) {
            setJMenuBar(createMenuBar(resources));
        }
        JPanel content = new JPanel(new BorderLayout());
        this.panel = new SystemPropertiesPanel();
        this.panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        content.add(this.panel, "Center");
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        Character mnemonic = (Character) resources.getObject("system-frame.button.close.mnemonic");
        JButton closeButton = new JButton(resources.getString("system-frame.button.close"));
        closeButton.setMnemonic(mnemonic.charValue());
        closeButton.setActionCommand(CLOSE_COMMAND);
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton, "East");
        content.add(buttonPanel, "South");
        setContentPane(content);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(CLOSE_COMMAND)) {
            dispose();
        } else if (command.equals(COPY_COMMAND)) {
            this.panel.copySystemPropertiesToClipboard();
        }
    }

    private JMenuBar createMenuBar(ResourceBundle resources) {
        JMenuBar menuBar = new JMenuBar();
        Character mnemonic = (Character) resources.getObject("system-frame.menu.file.mnemonic");
        JMenu fileMenu = new JMenu(resources.getString("system-frame.menu.file"), true);
        fileMenu.setMnemonic(mnemonic.charValue());
        JMenuItem closeItem = new JMenuItem(resources.getString("system-frame.menu.file.close"), ((Character) resources.getObject("system-frame.menu.file.close.mnemonic")).charValue());
        closeItem.setActionCommand(CLOSE_COMMAND);
        closeItem.addActionListener(this);
        fileMenu.add(closeItem);
        mnemonic = (Character) resources.getObject("system-frame.menu.edit.mnemonic");
        JMenu editMenu = new JMenu(resources.getString("system-frame.menu.edit"));
        editMenu.setMnemonic(mnemonic.charValue());
        JMenuItem copyItem = new JMenuItem(resources.getString("system-frame.menu.edit.copy"), ((Character) resources.getObject("system-frame.menu.edit.copy.mnemonic")).charValue());
        copyItem.setActionCommand(COPY_COMMAND);
        copyItem.addActionListener(this);
        editMenu.add(copyItem);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        return menuBar;
    }
}
