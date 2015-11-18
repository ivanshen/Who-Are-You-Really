package org.jfree.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.jfree.date.SerialDate;

public class DateChooserPanel extends JPanel implements ActionListener {
    private int[] WEEK_DAYS;
    private JButton[] buttons;
    private Calendar chosenDate;
    private Color chosenDateButtonColor;
    private Color chosenMonthButtonColor;
    private Color chosenOtherButtonColor;
    private Font dateFont;
    private int firstDayOfWeek;
    private JComboBox monthSelector;
    private boolean refreshing;
    private JButton todayButton;
    private int yearSelectionRange;
    private JComboBox yearSelector;

    public DateChooserPanel() {
        this(Calendar.getInstance(), false);
    }

    public DateChooserPanel(Calendar calendar, boolean controlPanel) {
        super(new BorderLayout());
        this.yearSelectionRange = 20;
        this.dateFont = new Font("SansSerif", 0, 10);
        this.refreshing = false;
        this.chosenDateButtonColor = UIManager.getColor("textHighlight");
        this.chosenMonthButtonColor = UIManager.getColor("control");
        this.chosenOtherButtonColor = UIManager.getColor("controlShadow");
        this.chosenDate = calendar;
        this.firstDayOfWeek = calendar.getFirstDayOfWeek();
        this.WEEK_DAYS = new int[7];
        for (int i = 0; i < 7; i++) {
            this.WEEK_DAYS[i] = (((this.firstDayOfWeek + i) - 1) % 7) + 1;
        }
        add(constructSelectionPanel(), "North");
        add(getCalendarPanel(), "Center");
        if (controlPanel) {
            add(constructControlPanel(), "South");
        }
        setDate(calendar.getTime());
    }

    public void setDate(Date theDate) {
        this.chosenDate.setTime(theDate);
        this.monthSelector.setSelectedIndex(this.chosenDate.get(2));
        refreshYearSelector();
        refreshButtons();
    }

    public Date getDate() {
        return this.chosenDate.getTime();
    }

    public void actionPerformed(ActionEvent e) {
        int dayOfMonth;
        if (e.getActionCommand().equals("monthSelectionChanged")) {
            JComboBox c = (JComboBox) e.getSource();
            dayOfMonth = this.chosenDate.get(5);
            this.chosenDate.set(5, 1);
            this.chosenDate.set(2, c.getSelectedIndex());
            this.chosenDate.set(5, Math.min(dayOfMonth, this.chosenDate.getActualMaximum(5)));
            refreshButtons();
        } else if (e.getActionCommand().equals("yearSelectionChanged")) {
            if (!this.refreshing) {
                Integer y = (Integer) ((JComboBox) e.getSource()).getSelectedItem();
                dayOfMonth = this.chosenDate.get(5);
                this.chosenDate.set(5, 1);
                this.chosenDate.set(1, y.intValue());
                this.chosenDate.set(5, Math.min(dayOfMonth, this.chosenDate.getActualMaximum(5)));
                refreshYearSelector();
                refreshButtons();
            }
        } else if (e.getActionCommand().equals("todayButtonClicked")) {
            setDate(new Date());
        } else if (e.getActionCommand().equals("dateButtonClicked")) {
            int i = Integer.parseInt(((JButton) e.getSource()).getName());
            Calendar cal = getFirstVisibleDate();
            cal.add(5, i);
            setDate(cal.getTime());
        }
    }

    private JPanel getCalendarPanel() {
        int i;
        JPanel p = new JPanel(new GridLayout(7, 7));
        String[] weekDays = new DateFormatSymbols().getShortWeekdays();
        for (int i2 : this.WEEK_DAYS) {
            p.add(new JLabel(weekDays[i2], 0));
        }
        this.buttons = new JButton[42];
        for (i = 0; i < 42; i++) {
            JButton b = new JButton("");
            b.setMargin(new Insets(1, 1, 1, 1));
            b.setName(Integer.toString(i));
            b.setFont(this.dateFont);
            b.setFocusPainted(false);
            b.setActionCommand("dateButtonClicked");
            b.addActionListener(this);
            this.buttons[i] = b;
            p.add(b);
        }
        return p;
    }

    private Color getButtonColor(Calendar theDate) {
        if (equalDates(theDate, this.chosenDate)) {
            return this.chosenDateButtonColor;
        }
        if (theDate.get(2) == this.chosenDate.get(2)) {
            return this.chosenMonthButtonColor;
        }
        return this.chosenOtherButtonColor;
    }

    private boolean equalDates(Calendar c1, Calendar c2) {
        if (c1.get(5) == c2.get(5) && c1.get(2) == c2.get(2) && c1.get(1) == c2.get(1)) {
            return true;
        }
        return false;
    }

    private Calendar getFirstVisibleDate() {
        Calendar c = Calendar.getInstance();
        c.set(this.chosenDate.get(1), this.chosenDate.get(2), 1);
        c.add(5, -1);
        while (c.get(7) != getFirstDayOfWeek()) {
            c.add(5, -1);
        }
        return c;
    }

    private int getFirstDayOfWeek() {
        return this.firstDayOfWeek;
    }

    private void refreshButtons() {
        Calendar c = getFirstVisibleDate();
        for (int i = 0; i < 42; i++) {
            JButton b = this.buttons[i];
            b.setText(Integer.toString(c.get(5)));
            b.setBackground(getButtonColor(c));
            c.add(5, 1);
        }
    }

    private void refreshYearSelector() {
        if (!this.refreshing) {
            this.refreshing = true;
            this.yearSelector.removeAllItems();
            Integer[] years = getYears(this.chosenDate.get(1));
            for (Object addItem : years) {
                this.yearSelector.addItem(addItem);
            }
            this.yearSelector.setSelectedItem(new Integer(this.chosenDate.get(1)));
            this.refreshing = false;
        }
    }

    private Integer[] getYears(int chosenYear) {
        int size = (this.yearSelectionRange * 2) + 1;
        int start = chosenYear - this.yearSelectionRange;
        Integer[] years = new Integer[size];
        for (int i = 0; i < size; i++) {
            years[i] = new Integer(i + start);
        }
        return years;
    }

    private JPanel constructSelectionPanel() {
        JPanel p = new JPanel();
        int minMonth = this.chosenDate.getMinimum(2);
        String[] months = new String[((this.chosenDate.getMaximum(2) - minMonth) + 1)];
        System.arraycopy(SerialDate.getMonths(), minMonth, months, 0, months.length);
        this.monthSelector = new JComboBox(months);
        this.monthSelector.addActionListener(this);
        this.monthSelector.setActionCommand("monthSelectionChanged");
        p.add(this.monthSelector);
        this.yearSelector = new JComboBox(getYears(0));
        this.yearSelector.addActionListener(this);
        this.yearSelector.setActionCommand("yearSelectionChanged");
        p.add(this.yearSelector);
        return p;
    }

    private JPanel constructControlPanel() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        this.todayButton = new JButton("Today");
        this.todayButton.addActionListener(this);
        this.todayButton.setActionCommand("todayButtonClicked");
        p.add(this.todayButton);
        return p;
    }

    public Color getChosenDateButtonColor() {
        return this.chosenDateButtonColor;
    }

    public void setChosenDateButtonColor(Color chosenDateButtonColor) {
        if (chosenDateButtonColor == null) {
            throw new NullPointerException("UIColor must not be null.");
        }
        Color oldValue = this.chosenDateButtonColor;
        this.chosenDateButtonColor = chosenDateButtonColor;
        refreshButtons();
        firePropertyChange("chosenDateButtonColor", oldValue, chosenDateButtonColor);
    }

    public Color getChosenMonthButtonColor() {
        return this.chosenMonthButtonColor;
    }

    public void setChosenMonthButtonColor(Color chosenMonthButtonColor) {
        if (chosenMonthButtonColor == null) {
            throw new NullPointerException("UIColor must not be null.");
        }
        Color oldValue = this.chosenMonthButtonColor;
        this.chosenMonthButtonColor = chosenMonthButtonColor;
        refreshButtons();
        firePropertyChange("chosenMonthButtonColor", oldValue, chosenMonthButtonColor);
    }

    public Color getChosenOtherButtonColor() {
        return this.chosenOtherButtonColor;
    }

    public void setChosenOtherButtonColor(Color chosenOtherButtonColor) {
        if (chosenOtherButtonColor == null) {
            throw new NullPointerException("UIColor must not be null.");
        }
        Color oldValue = this.chosenOtherButtonColor;
        this.chosenOtherButtonColor = chosenOtherButtonColor;
        refreshButtons();
        firePropertyChange("chosenOtherButtonColor", oldValue, chosenOtherButtonColor);
    }

    public int getYearSelectionRange() {
        return this.yearSelectionRange;
    }

    public void setYearSelectionRange(int yearSelectionRange) {
        int oldYearSelectionRange = this.yearSelectionRange;
        this.yearSelectionRange = yearSelectionRange;
        refreshYearSelector();
        firePropertyChange("yearSelectionRange", oldYearSelectionRange, yearSelectionRange);
    }
}
