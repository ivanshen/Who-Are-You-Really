package gui;

import common.Question;
import common.Survey;
import common.Util;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;

public class SurveyPanel extends JPanel implements KeyListener {
    private static Logger logger;
    private JButton btnBackToMenu;
    private JButton btnNext;
    private JButton btnPrevious;
    private JButton btnSubmitAnswers;
    private Question displayedQuestion;
    private MainFrame parent;
    private HashMap<Question, ChoicePanel> questions;
    private Survey survey;
    private JTextArea textArea;

    class 1 implements ActionListener {
        private final /* synthetic */ SurveyPanel val$cur;

        1(SurveyPanel surveyPanel) {
            this.val$cur = surveyPanel;
        }

        public void actionPerformed(ActionEvent e) {
            SurveyPanel.this.parent.removeContent(this.val$cur);
            SurveyPanel.this.dispose();
        }
    }

    class 2 implements ActionListener {
        private final /* synthetic */ SurveyPanel val$cur;

        2(SurveyPanel surveyPanel) {
            this.val$cur = surveyPanel;
        }

        public void actionPerformed(ActionEvent e) {
            Question q = SurveyPanel.this.survey.getNextQuestion();
            if (q != null) {
                this.val$cur.setContent(q);
            } else {
                Util.showError("Already at the last question!");
            }
        }
    }

    class 3 implements ActionListener {
        private final /* synthetic */ SurveyPanel val$cur;

        3(SurveyPanel surveyPanel) {
            this.val$cur = surveyPanel;
        }

        public void actionPerformed(ActionEvent e) {
            Question q = SurveyPanel.this.survey.getPrevQuestion();
            if (q != null) {
                this.val$cur.setContent(q);
            } else {
                Util.showError("Already at the first question!");
            }
        }
    }

    class 4 implements ActionListener {
        private final /* synthetic */ SurveyPanel val$cur;

        4(SurveyPanel surveyPanel) {
            this.val$cur = surveyPanel;
        }

        public void actionPerformed(ActionEvent e) {
            int i;
            ArrayList<Question> questions = SurveyPanel.this.survey.getQuestionArray();
            LinkedList<Integer> missedQuestions = new LinkedList();
            for (i = 0; i < questions.size(); i++) {
                ChoicePanel c = (ChoicePanel) SurveyPanel.this.questions.get(questions.get(i));
                if (c == null) {
                    missedQuestions.add(Integer.valueOf(i + 1));
                } else if (c.getSelectedChoce() == null) {
                    missedQuestions.add(Integer.valueOf(i + 1));
                }
            }
            if (missedQuestions.isEmpty()) {
                for (i = 0; i < questions.size(); i++) {
                    SurveyPanel.this.survey.choose(((ChoicePanel) SurveyPanel.this.questions.get(questions.get(i))).getSelectedChoce());
                }
                SurveyPanel.this.parent.addContent(new ResultPanel(SurveyPanel.this.parent, SurveyPanel.this.survey));
                SurveyPanel.this.parent.removeKeyListener(this.val$cur);
                SurveyPanel.this.parent.removeContent(this.val$cur);
                this.val$cur.dispose();
                return;
            }
            Util.showError("You have missed question(s):\n " + missedQuestions);
        }
    }

    static {
        logger = Logger.getLogger(SurveyPanel.class.getName());
    }

    public SurveyPanel(MainFrame parent, Survey survey) {
        if (survey == null) {
            throw new IllegalArgumentException("Invalid survey!");
        }
        this.parent = parent;
        this.survey = survey;
        this.questions = new HashMap();
        this.displayedQuestion = null;
        initialize();
        setVisible(true);
    }

    private void initialize() {
        setSize(800, 600);
        setMinimumSize(new Dimension(600, 400));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setLayout(new MigLayout("", "[][grow][][::500px,fill][][][][][grow][][][][]", "[][][26.00][][][73.00][][-19.00][][181.00,grow,baseline][center][center][grow,center][grow,baseline]"));
        this.textArea = new JTextArea();
        this.textArea.setFont(Util.QUESTION_FONT);
        this.textArea.setWrapStyleWord(true);
        this.textArea.setLineWrap(true);
        this.textArea.setToolTipText("This is the question you are supposed to answer.\r\n");
        this.textArea.setEditable(false);
        this.textArea.setBackground(getBackground());
        this.textArea.addKeyListener(this);
        add(this.textArea, "cell 0 1 7 2,width 60%");
        this.btnNext = new JButton("Next");
        this.btnNext.setToolTipText("Go to the next question");
        this.btnNext.addKeyListener(this);
        add(this.btnNext, "flowy,cell 11 12,growx");
        this.btnPrevious = new JButton("Previous");
        this.btnPrevious.setToolTipText("Go to the previous question");
        this.btnPrevious.addKeyListener(this);
        add(this.btnPrevious, "cell 11 12,growx");
        this.btnSubmitAnswers = new JButton("Submit Answers");
        this.btnSubmitAnswers.setToolTipText("Submit your answers");
        this.btnSubmitAnswers.addKeyListener(this);
        add(this.btnSubmitAnswers, "cell 11 12");
        this.btnBackToMenu = new JButton("Back to Menu");
        this.btnBackToMenu.setToolTipText("Go back to the menu");
        this.btnBackToMenu.addKeyListener(this);
        add(this.btnBackToMenu, "cell 11 12,growx");
        setContent(this.survey.getQuestion());
        addActions();
        this.parent.addKeyListener(this);
        addKeyListener(this);
    }

    private void addActions() {
        this.btnBackToMenu.addActionListener(new 1(this));
        this.btnNext.addActionListener(new 2(this));
        this.btnPrevious.addActionListener(new 3(this));
        this.btnSubmitAnswers.addActionListener(new 4(this));
    }

    private void setContent(Question q) {
        if (q != null) {
            this.textArea.setText(new StringBuilder(String.valueOf(String.format("%d/%d: ", new Object[]{Integer.valueOf(this.survey.getIndex() + 1), Integer.valueOf(this.survey.getQuestionSize())}))).append(q.getText()).toString());
            if (this.displayedQuestion != null) {
                remove((Component) this.questions.get(this.displayedQuestion));
                ((ChoicePanel) this.questions.get(this.displayedQuestion)).setVisible(false);
                logger.log(Level.INFO, "Removed panel " + this.questions.get(this.displayedQuestion));
            }
            ChoicePanel curPanel = (ChoicePanel) this.questions.get(q);
            if (curPanel == null) {
                curPanel = new ChoicePanel(this.survey, q);
                this.questions.put(q, curPanel);
            }
            add(curPanel, "cell 0 4 7 5");
            curPanel.setVisible(true);
            curPanel.repaint();
            curPanel.addKeyListener(this);
            revalidate();
            this.parent.setMinimumSize(getPreferredSize());
            this.displayedQuestion = q;
        }
    }

    public void dispose() {
        removeAll();
        this.questions.clear();
        this.displayedQuestion = null;
        this.survey = null;
        this.parent = null;
        setVisible(false);
        setEnabled(false);
    }

    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() > '0' && e.getKeyChar() < '9') {
            int choice = e.getKeyChar() - 48;
            if (choice == 0) {
                choice += 10;
            }
            if (choice <= this.displayedQuestion.getChoices().size()) {
                ((ChoicePanel) this.questions.get(this.displayedQuestion)).select(choice - 1);
            }
        } else if (e.getKeyChar() == '\n') {
            this.btnSubmitAnswers.doClick();
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == 39) {
            this.btnNext.doClick();
        } else if (e.getKeyCode() == 37) {
            this.btnPrevious.doClick();
        } else if (e.getKeyCode() == 10) {
            this.btnSubmitAnswers.doClick();
        }
    }

    public void keyReleased(KeyEvent e) {
    }
}
