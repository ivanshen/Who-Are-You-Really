package gui;

import java.awt.Dimension;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;

public class MainFrame extends JFrame {
    private static Logger logger = null;
    private static final long serialVersionUID = -8026416994513756565L;
    private Stack<JComponent> components;

    static {
        logger = Logger.getLogger(MainFrame.class.getName());
    }

    public MainFrame() {
        this.components = new Stack();
        addContent(new MenuPanel(this));
        setTitle("Survey");
        setSize(800, 600);
        setDefaultCloseOperation(3);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void addContent(JComponent content) {
        if (!this.components.empty()) {
            ((JComponent) this.components.peek()).setVisible(false);
            revalidate();
        }
        add(content);
        this.components.add(content);
        Dimension temp = ((JComponent) this.components.peek()).getSize();
        temp.width += 20;
        temp.height += 16;
        setMinimumSize(temp);
        setSize(temp);
        validate();
        logger.log(Level.INFO, "Added " + content);
    }

    public void removeContent(JComponent content) {
        remove(content);
        this.components.remove(content);
        if (!this.components.empty()) {
            ((JComponent) this.components.peek()).setVisible(true);
            setMinimumSize(((JComponent) this.components.peek()).getSize());
            setSize(getMinimumSize());
            logger.log(Level.INFO, "Under: " + this.components.peek());
        }
        validate();
        logger.log(Level.INFO, "Removed " + content);
    }
}
