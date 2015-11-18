package org.jfree.ui.about;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import org.jfree.util.ResourceBundleWrapper;

public class AboutDialog extends JDialog {
    public static final Dimension PREFERRED_SIZE;
    public static final Border STANDARD_BORDER;
    private String application;
    private List contributors;
    private String copyright;
    private String info;
    private String licence;
    private Image logo;
    private ResourceBundle resources;
    private String version;

    static {
        PREFERRED_SIZE = new Dimension(560, 360);
        STANDARD_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    }

    public AboutDialog(String title, ProjectInfo project) {
        init(title, project.getName(), "Version " + project.getVersion(), project.getInfo(), project.getLogo(), project.getCopyright(), project.getLicenceText(), project.getContributors(), project);
    }

    public AboutDialog(Frame owner, String title, ProjectInfo project) {
        super(owner);
        init(title, project.getName(), "Version " + project.getVersion(), project.getInfo(), project.getLogo(), project.getCopyright(), project.getLicenceText(), project.getContributors(), project);
    }

    public AboutDialog(Dialog owner, String title, ProjectInfo project) {
        super(owner);
        init(title, project.getName(), "Version " + project.getVersion(), project.getInfo(), project.getLogo(), project.getCopyright(), project.getLicenceText(), project.getContributors(), project);
    }

    private void init(String title, String application, String version, String info, Image logo, String copyright, String licence, List contributors, ProjectInfo libraries) {
        setTitle(title);
        this.application = application;
        this.version = version;
        this.copyright = copyright;
        this.info = info;
        this.logo = logo;
        this.contributors = contributors;
        this.licence = licence;
        String baseName = "org.jfree.ui.about.resources.AboutResources";
        this.resources = ResourceBundleWrapper.getBundle("org.jfree.ui.about.resources.AboutResources");
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(STANDARD_BORDER);
        content.add(createTabs(libraries));
        setContentPane(content);
        pack();
    }

    public Dimension getPreferredSize() {
        return PREFERRED_SIZE;
    }

    private JTabbedPane createTabs(ProjectInfo info) {
        JTabbedPane tabs = new JTabbedPane();
        JPanel aboutPanel = createAboutPanel(info);
        aboutPanel.setBorder(STANDARD_BORDER);
        tabs.add(this.resources.getString("about-frame.tab.about"), aboutPanel);
        JPanel systemPanel = new SystemPropertiesPanel();
        systemPanel.setBorder(STANDARD_BORDER);
        tabs.add(this.resources.getString("about-frame.tab.system"), systemPanel);
        return tabs;
    }

    private JPanel createAboutPanel(ProjectInfo info) {
        JPanel about = new JPanel(new BorderLayout());
        JPanel details = new AboutPanel(this.application, this.version, this.copyright, this.info, this.logo);
        boolean includetabs = false;
        JTabbedPane tabs = new JTabbedPane();
        if (this.contributors != null) {
            JPanel contributorsPanel = new ContributorsPanel(this.contributors);
            contributorsPanel.setBorder(STANDARD_BORDER);
            tabs.add(this.resources.getString("about-frame.tab.contributors"), contributorsPanel);
            includetabs = true;
        }
        if (this.licence != null) {
            JPanel licencePanel = createLicencePanel();
            licencePanel.setBorder(STANDARD_BORDER);
            tabs.add(this.resources.getString("about-frame.tab.licence"), licencePanel);
            includetabs = true;
        }
        if (info != null) {
            JPanel librariesPanel = new LibraryPanel(info);
            librariesPanel.setBorder(STANDARD_BORDER);
            tabs.add(this.resources.getString("about-frame.tab.libraries"), librariesPanel);
            includetabs = true;
        }
        about.add(details, "North");
        if (includetabs) {
            about.add(tabs);
        }
        return about;
    }

    private JPanel createLicencePanel() {
        JPanel licencePanel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea(this.licence);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setCaretPosition(0);
        area.setEditable(false);
        licencePanel.add(new JScrollPane(area));
        return licencePanel;
    }
}
