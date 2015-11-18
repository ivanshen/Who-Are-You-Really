package gui;

import common.Choice;
import common.Question;
import common.Survey;
import common.Util;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class ChoicePanel extends JPanel {
    private static Logger logger;
    private ButtonGroup btnGp;
    private ArrayList<RadioWrapper> buttons;
    private final Question question;
    private final Survey survey;

    private static class RadioWrapper extends JRadioButton {
        private final Choice c;

        public RadioWrapper(Choice c) {
            this.c = c;
            setText(c.getText());
        }

        public Choice getChoice() {
            return this.c;
        }
    }

    static {
        logger = Logger.getLogger(ChoicePanel.class.getName());
    }

    public ChoicePanel(Survey s, Question q) {
        this.question = q;
        this.survey = s;
        this.buttons = new ArrayList();
        initialize();
    }

    private void initialize() {
        setLayout(new BoxLayout(this, 1));
        this.btnGp = new ButtonGroup();
        Iterator it = this.question.getChoices().iterator();
        while (it.hasNext()) {
            RadioWrapper cur = new RadioWrapper((Choice) it.next());
            cur.setFont(Util.CHOICE_FONT);
            cur.setFocusable(false);
            cur.setToolTipText("Click the to select this option");
            this.btnGp.add(cur);
            this.buttons.add(cur);
            add(cur);
            logger.log(Level.INFO, "Added choice: " + cur);
        }
    }

    public Choice getSelectedChoce() {
        Iterator it = this.buttons.iterator();
        while (it.hasNext()) {
            RadioWrapper r = (RadioWrapper) it.next();
            if (r.isSelected()) {
                return r.getChoice();
            }
        }
        return null;
    }

    public void select(int choice) {
        ((RadioWrapper) this.buttons.get(choice)).setSelected(true);
    }
}
