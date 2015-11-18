package gui;

import common.Survey;
import common.Util;
import io.Parser;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

public class MenuPanel extends JPanel {
    private JButton btnExit;
    private JButton btnHelp;
    private JButton btnLoad;
    private JButton btnStart;
    private JPanel buttonPanel;
    private JLabel lblTitle;
    private MainFrame parent;

    class 1 implements ActionListener {

        final class SurveyWrapper {
            public String name;
            public Survey survey;

            public SurveyWrapper(Survey survey) {
                this.name = survey.getTitle();
                this.survey = survey;
            }

            public String toString() {
                return this.name;
            }
        }

        1() {
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Survey[] surveys = Parser.readAll(new File("./assets"));
                SurveyWrapper[] wrapped = new SurveyWrapper[surveys.length];
                for (int i = 0; i < surveys.length; i++) {
                    wrapped[i] = new SurveyWrapper(surveys[i]);
                }
                SurveyWrapper selected = (SurveyWrapper) JOptionPane.showInputDialog(MenuPanel.this.parent, "Select a survey:", "Choose a Survey", -1, null, wrapped, wrapped[0]);
                if (selected != null) {
                    MenuPanel.this.parent.addContent(new SurveyPanel(MenuPanel.this.parent, selected.survey));
                }
            } catch (IOException ex) {
                Util.showError("Could not read the surveys. Make sure there are surveys in the " + new File("./assets").getAbsolutePath() + " folder (in the same folder as this program.");
                ex.printStackTrace();
            }
        }
    }

    class 2 implements ActionListener {
        2() {
        }

        public void actionPerformed(ActionEvent e) {
            File f = Util.chooseFile("Load", Util.TXT_FILTER);
            if (f != null) {
                try {
                    MenuPanel.this.parent.addContent(new SurveyPanel(MenuPanel.this.parent, Parser.readSurvey(f)));
                } catch (IOException e1) {
                    Util.showError("Could not read file " + f.getAbsolutePath());
                    e1.printStackTrace();
                } catch (IllegalArgumentException e2) {
                }
            }
        }
    }

    class 3 implements ActionListener {
        3() {
        }

        public void actionPerformed(ActionEvent e) {
        }
    }

    class 4 implements ActionListener {
        4() {
        }

        public void actionPerformed(ActionEvent e) {
            if (Util.showConfirm("Are you sure you want to exit?")) {
                System.exit(0);
            }
        }
    }

    public MenuPanel(MainFrame parent) {
        this.parent = parent;
        initialize();
        addListeners();
    }

    private void initialize() {
        setLayout(new BorderLayout(0, 0));
        this.lblTitle = new JLabel("Who Are You, Really?");
        this.lblTitle.setBorder(new EmptyBorder(100, 10, 10, 10));
        this.lblTitle.setFont(new Font("Tekton Pro Ext", 0, 41));
        this.lblTitle.setHorizontalAlignment(0);
        add(this.lblTitle, "North");
        this.buttonPanel = new JPanel();
        add(this.buttonPanel, "Center");
        SpringLayout sl_buttonPanel = new SpringLayout();
        this.buttonPanel.setLayout(sl_buttonPanel);
        this.btnStart = new JButton("Start");
        sl_buttonPanel.putConstraint("East", this.btnStart, -349, "East", this.buttonPanel);
        this.btnStart.setToolTipText("Start a survey");
        sl_buttonPanel.putConstraint("North", this.btnStart, 61, "North", this.buttonPanel);
        sl_buttonPanel.putConstraint("West", this.btnStart, 355, "West", this.buttonPanel);
        this.btnStart.setFont(new Font("Times New Roman", 0, 25));
        this.buttonPanel.add(this.btnStart);
        this.btnLoad = new JButton("Load");
        sl_buttonPanel.putConstraint("North", this.btnLoad, 32, "South", this.btnStart);
        sl_buttonPanel.putConstraint("East", this.btnLoad, 0, "East", this.btnStart);
        this.btnLoad.setToolTipText("Load a survey from a file");
        sl_buttonPanel.putConstraint("West", this.btnLoad, 355, "West", this.buttonPanel);
        this.btnLoad.setFont(new Font("Times New Roman", 0, 25));
        this.buttonPanel.add(this.btnLoad);
        this.btnHelp = new JButton("Help");
        sl_buttonPanel.putConstraint("North", this.btnHelp, 40, "South", this.btnLoad);
        sl_buttonPanel.putConstraint("West", this.btnHelp, 355, "West", this.buttonPanel);
        sl_buttonPanel.putConstraint("East", this.btnHelp, 0, "East", this.btnStart);
        this.btnHelp.setToolTipText("Show help");
        this.btnHelp.setFont(new Font("Times New Roman", 0, 25));
        this.buttonPanel.add(this.btnHelp);
        this.btnExit = new JButton("Exit");
        sl_buttonPanel.putConstraint("North", this.btnExit, 41, "South", this.btnHelp);
        sl_buttonPanel.putConstraint("West", this.btnExit, 355, "West", this.buttonPanel);
        sl_buttonPanel.putConstraint("East", this.btnExit, 0, "East", this.btnStart);
        this.btnExit.setToolTipText("Exit the program");
        this.btnExit.setFont(new Font("Times New Roman", 0, 25));
        this.buttonPanel.add(this.btnExit);
        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(getMinimumSize());
        setSize(getPreferredSize());
    }

    private void addListeners() {
        MenuPanel menu = this;
        this.btnStart.addActionListener(new 1());
        this.btnLoad.addActionListener(new 2());
        this.btnHelp.addActionListener(new 3());
        this.btnExit.addActionListener(new 4());
    }
}
