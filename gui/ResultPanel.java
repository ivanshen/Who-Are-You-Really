package gui;

import common.Result;
import common.Survey;
import common.Type;
import common.Util;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class ResultPanel extends JPanel {
    private static final int MAX_CHART_ELEMENTS = 7;
    private static Logger logger;
    private JPanel barChart;
    private MainFrame parent;
    private JPanel pieChart;
    private List<Result> results;
    private Survey survey;
    private JTable table;

    class 1 implements Comparator<Type> {
        1() {
        }

        public int compare(Type a, Type b) {
            return (int) (b.getPoints() - a.getPoints());
        }
    }

    class 2 implements ActionListener {
        2() {
        }

        public void actionPerformed(ActionEvent e) {
            File file = Util.chooseFile("Save", Util.createFileFilter("HTML file (.html)", "^.*\\.html$"));
            if (!file.getAbsolutePath().endsWith(".html")) {
                file = new File(file.getAbsolutePath() + ".html");
            }
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e2) {
                    Util.showError("Error creating file " + file.getAbsolutePath());
                    e2.printStackTrace();
                    return;
                }
            }
            try {
                int i;
                PrintWriter fout = new PrintWriter(file);
                fout.println("<!DOCTYPE html>");
                fout.println("<head>");
                fout.println("<strong>" + ResultPanel.this.survey.getTitle() + "</strong>");
                fout.println("</head>");
                fout.println("<body>");
                fout.println("<p>");
                fout.println("Results:<br>");
                fout.println("<ol type=\"1\">");
                for (i = 0; i < ResultPanel.this.results.size(); i++) {
                    fout.println("<li>");
                    fout.printf("%s", new Object[]{((Result) ResultPanel.this.results.get(i)).getText()});
                    fout.println("</li>");
                }
                fout.println("</ol>");
                fout.println("</p>");
                fout.println("<p>");
                fout.println("Points breakdown:<br>");
                fout.println("<ul>");
                for (i = 0; i < ResultPanel.this.survey.getTypes().size(); i++) {
                    fout.println("<li>");
                    fout.printf("%s: %4.2f", new Object[]{((Type) ResultPanel.this.survey.getTypes().get(i)).getText(), Double.valueOf(((Type) ResultPanel.this.survey.getTypes().get(i)).getPoints())});
                    fout.println("</li>");
                }
                fout.println("</ul>");
                fout.println("</p>");
                fout.println("<br><br><br>");
                String website = ResultPanel.this.survey.getWebsite();
                if (!(website.startsWith("http://") || website.startsWith("https://"))) {
                    website = "http://" + website;
                }
                fout.printf("For more information, click <a href=\"%s\">here</a>\n", new Object[]{website});
                fout.println("</body>");
                if (fout != null) {
                    fout.close();
                }
                ResultPanel.logger.log(Level.INFO, "Written to " + file.getAbsolutePath());
                Util.open(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }

    class 3 implements ActionListener {
        3() {
        }

        public void actionPerformed(ActionEvent e) {
            ResultPanel.this.parent.removeContent(ResultPanel.this);
            ResultPanel.this.parent.invalidate();
            ResultPanel.this.parent.validate();
            ResultPanel.this.parent.setResizable(true);
            ResultPanel.logger.log(Level.INFO, "Removed");
            ResultPanel.this.dispose();
        }
    }

    class 4 implements ActionListener {
        4() {
        }

        public void actionPerformed(ActionEvent e) {
            String website = ResultPanel.this.survey.getWebsite();
            if (!(website.startsWith("http://") || website.startsWith("https://"))) {
                website = "http://" + website;
            }
            try {
                Util.openWebpage(new URL(website));
            } catch (MalformedURLException e1) {
                Util.open(new File(website));
                e1.printStackTrace();
            }
        }
    }

    static {
        logger = Logger.getLogger(ResultPanel.class.getName());
    }

    public ResultPanel(MainFrame parent, Survey s) {
        this.survey = s;
        this.results = s.getResults();
        this.parent = parent;
        initialize();
        setVisible(true);
    }

    private void initialize() {
        int i;
        setPreferredSize(new Dimension(800, 600));
        setMinimumSize(getPreferredSize());
        setSize(getPreferredSize());
        ArrayList<Type> arrayList = new ArrayList(this.survey.getTypes());
        Collections.sort(arrayList, new 1());
        int chartSize = Math.min(arrayList.size(), 8);
        String[] names = new String[chartSize];
        double[] nums = new double[chartSize];
        if (arrayList.size() > chartSize) {
            for (i = 0; i < MAX_CHART_ELEMENTS; i++) {
                names[i] = ((Type) arrayList.get(i)).getText();
                nums[i] = ((Type) arrayList.get(i)).getPoints();
            }
            names[chartSize - 1] = "Other";
            nums[chartSize - 1] = 0.0d;
            for (i = chartSize - 1; i < arrayList.size(); i++) {
                int i2 = chartSize - 1;
                nums[i2] = nums[i2] + ((Type) arrayList.get(i)).getPoints();
            }
        } else {
            for (i = 0; i < names.length; i++) {
                names[i] = ((Type) arrayList.get(i)).getText();
                nums[i] = ((Type) arrayList.get(i)).getPoints();
            }
        }
        this.pieChart = PieChart.createPieChart(this.survey.getTitle(), "Results", names, nums);
        this.pieChart.setBorder(BorderFactory.createBevelBorder(1));
        JScrollPane jScrollPane = new JScrollPane(this.pieChart);
        jScrollPane.setToolTipText("Right click for more options");
        this.barChart = BarChart.createBarChart(nums, names, this.survey.getTitle(), "Categories", "Points", "Chart");
        this.barChart.setBorder(BorderFactory.createBevelBorder(1));
        JScrollPane barScrollPane = new JScrollPane(this.barChart);
        barScrollPane.setToolTipText("Right click for more options");
        setBorder(new EmptyBorder(20, 20, 20, 20));
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("Results: \n");
        for (i = 0; i < this.results.size(); i++) {
            StringBuilder stringBuilder = resultBuilder;
            stringBuilder.append(new StringBuilder(String.valueOf(String.format("%d: ", new Object[]{Integer.valueOf(i + 1)}))).append(((Result) this.results.get(i)).getText()).append("\n").toString());
        }
        setLayout(new BorderLayout(0, 0));
        JPanel contentPanel = new JPanel();
        add(contentPanel, "Center");
        contentPanel.setLayout(new BoxLayout(contentPanel, 0));
        JPanel textPanel = new JPanel();
        contentPanel.add(textPanel);
        textPanel.setLayout(new BoxLayout(textPanel, 1));
        JTextArea textArea = new JTextArea();
        textArea.setToolTipText("This is the result of the survey based on your decisions");
        textArea.setFont(new Font("Baskerville Old Face", 0, 20));
        textArea.setEditable(false);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setBackground(getBackground().brighter());
        textArea.setText(resultBuilder.toString());
        textArea.setBorder(BorderFactory.createBevelBorder(1));
        jScrollPane = new JScrollPane(textArea);
        jScrollPane.setToolTipText("This is the result of the survey based on your decisions");
        jScrollPane.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        textPanel.add(jScrollPane);
        textPanel.add(Box.createVerticalStrut(20));
        String[] columnNames = new String[]{"Category", "Points", "Percentage"};
        Object[][] tableData = (Object[][]) Array.newInstance(Object.class, new int[]{arrayList.size(), 3});
        double totalPoints = 0.0d;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            totalPoints += ((Type) it.next()).getPoints();
        }
        for (i = 0; i < arrayList.size(); i++) {
            tableData[i][0] = ((Type) arrayList.get(i)).getText();
            tableData[i][1] = String.format("%.2f", new Object[]{Double.valueOf(curType.getPoints())});
            tableData[i][2] = String.format("%05.2f%%", new Object[]{Double.valueOf((curType.getPoints() / totalPoints) * 100.0d)});
        }
        this.table = new JTable(tableData, columnNames);
        this.table.setFont(new Font("Baskerville Old Face", 0, 17));
        this.table.setToolTipText("This is the detailed breakdown of the points you scored in the different categories of this survey");
        this.table.setRowHeight((int) (((double) this.table.getFontMetrics(this.table.getFont()).getHeight()) * 1.602d));
        this.table.setBorder(BorderFactory.createBevelBorder(1));
        this.table.setEnabled(false);
        jScrollPane = new JScrollPane(this.table);
        jScrollPane.setToolTipText("This is the detailed breakdown of the points you scored in the different categories of this survey");
        textPanel.add(jScrollPane);
        contentPanel.add(Box.createHorizontalStrut(20));
        JPanel graphPanel = new JPanel();
        contentPanel.add(graphPanel);
        graphPanel.setLayout(new BoxLayout(graphPanel, 1));
        graphPanel.add(barScrollPane);
        graphPanel.add(Box.createVerticalStrut(20));
        graphPanel.add(jScrollPane);
        JPanel buttonPanel = new JPanel();
        ((FlowLayout) buttonPanel.getLayout()).setHgap(10);
        buttonPanel.setSize(800, 50);
        add(buttonPanel, "South");
        JButton btnSave = new JButton("Save");
        btnSave.setToolTipText("Click to save the results into a webpage");
        btnSave.addActionListener(new 2());
        buttonPanel.add(btnSave);
        JButton btnBackToMenu = new JButton("Back to Menu");
        btnBackToMenu.setToolTipText("Click to go back to the menu");
        btnBackToMenu.addActionListener(new 3());
        buttonPanel.add(btnBackToMenu);
        JButton btnMoreInfo = new JButton("More Info");
        btnMoreInfo.setToolTipText("Click to open a link in the browser for more information");
        btnMoreInfo.addActionListener(new 4());
        buttonPanel.add(btnMoreInfo);
    }

    public void dispose() {
        setVisible(false);
        setEnabled(false);
        this.results = null;
        this.survey = null;
        this.parent = null;
    }
}
