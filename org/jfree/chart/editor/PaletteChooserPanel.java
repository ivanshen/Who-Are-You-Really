package org.jfree.chart.editor;

import java.awt.BorderLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.jfree.chart.plot.ColorPalette;
import org.jfree.chart.plot.RainbowPalette;

class PaletteChooserPanel extends JPanel {
    private JComboBox selector;

    public PaletteChooserPanel(PaletteSample current, PaletteSample[] available) {
        setLayout(new BorderLayout());
        this.selector = new JComboBox(available);
        this.selector.setSelectedItem(current);
        this.selector.setRenderer(new PaletteSample(new RainbowPalette()));
        add(this.selector);
    }

    public ColorPalette getSelectedPalette() {
        return ((PaletteSample) this.selector.getSelectedItem()).getPalette();
    }
}
