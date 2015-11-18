import gui.MainFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jfree.chart.axis.ValueAxis;

public class Launcher {
    private static Logger logger;

    static {
        logger = Logger.getLogger(Launcher.class.getName());
    }

    public static void main(String[] args) {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException e) {
                logger.log(Level.INFO, "You are not using windows, reverting to default look and feel");
                ToolTipManager.sharedInstance().setInitialDelay(ValueAxis.MAXIMUM_TICK_COUNT);
                new MainFrame().setVisible(true);
            } catch (InstantiationException e2) {
                logger.log(Level.INFO, "You are not using windows, reverting to default look and feel");
                ToolTipManager.sharedInstance().setInitialDelay(ValueAxis.MAXIMUM_TICK_COUNT);
                new MainFrame().setVisible(true);
            } catch (IllegalAccessException e3) {
                logger.log(Level.INFO, "You are not using windows, reverting to default look and feel");
                ToolTipManager.sharedInstance().setInitialDelay(ValueAxis.MAXIMUM_TICK_COUNT);
                new MainFrame().setVisible(true);
            } catch (UnsupportedLookAndFeelException e4) {
                logger.log(Level.INFO, "You are not using windows, reverting to default look and feel");
                ToolTipManager.sharedInstance().setInitialDelay(ValueAxis.MAXIMUM_TICK_COUNT);
                new MainFrame().setVisible(true);
            }
        }
        ToolTipManager.sharedInstance().setInitialDelay(ValueAxis.MAXIMUM_TICK_COUNT);
        new MainFrame().setVisible(true);
    }
}
