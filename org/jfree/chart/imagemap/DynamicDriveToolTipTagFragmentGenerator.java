package org.jfree.chart.imagemap;

public class DynamicDriveToolTipTagFragmentGenerator implements ToolTipTagFragmentGenerator {
    protected int style;
    protected String title;

    public DynamicDriveToolTipTagFragmentGenerator() {
        this.title = "";
        this.style = 1;
    }

    public DynamicDriveToolTipTagFragmentGenerator(String title, int style) {
        this.title = "";
        this.style = 1;
        this.title = title;
        this.style = style;
    }

    public String generateToolTipFragment(String toolTipText) {
        return " onMouseOver=\"return stm(['" + ImageMapUtilities.javascriptEscape(this.title) + "','" + ImageMapUtilities.javascriptEscape(toolTipText) + "'],Style[" + this.style + "]);\"" + " onMouseOut=\"return htm();\"";
    }
}
