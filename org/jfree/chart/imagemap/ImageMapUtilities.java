package org.jfree.chart.imagemap;

import java.io.IOException;
import java.io.PrintWriter;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.util.ParamChecks;
import org.jfree.util.StringUtils;

public class ImageMapUtilities {
    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info) throws IOException {
        writeImageMap(writer, name, info, new StandardToolTipTagFragmentGenerator(), new StandardURLTagFragmentGenerator());
    }

    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info, boolean useOverLibForToolTips) throws IOException {
        ToolTipTagFragmentGenerator toolTipTagFragmentGenerator;
        if (useOverLibForToolTips) {
            toolTipTagFragmentGenerator = new OverLIBToolTipTagFragmentGenerator();
        } else {
            toolTipTagFragmentGenerator = new StandardToolTipTagFragmentGenerator();
        }
        writeImageMap(writer, name, info, toolTipTagFragmentGenerator, new StandardURLTagFragmentGenerator());
    }

    public static void writeImageMap(PrintWriter writer, String name, ChartRenderingInfo info, ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, URLTagFragmentGenerator urlTagFragmentGenerator) throws IOException {
        writer.println(getImageMap(name, info, toolTipTagFragmentGenerator, urlTagFragmentGenerator));
    }

    public static String getImageMap(String name, ChartRenderingInfo info) {
        return getImageMap(name, info, new StandardToolTipTagFragmentGenerator(), new StandardURLTagFragmentGenerator());
    }

    public static String getImageMap(String name, ChartRenderingInfo info, ToolTipTagFragmentGenerator toolTipTagFragmentGenerator, URLTagFragmentGenerator urlTagFragmentGenerator) {
        StringBuilder sb = new StringBuilder();
        sb.append("<map id=\"").append(htmlEscape(name));
        sb.append("\" name=\"").append(htmlEscape(name)).append("\">");
        sb.append(StringUtils.getLineSeparator());
        EntityCollection entities = info.getEntityCollection();
        if (entities != null) {
            for (int i = entities.getEntityCount() - 1; i >= 0; i--) {
                ChartEntity entity = entities.getEntity(i);
                if (entity.getToolTipText() != null || entity.getURLText() != null) {
                    String area = entity.getImageMapAreaTag(toolTipTagFragmentGenerator, urlTagFragmentGenerator);
                    if (area.length() > 0) {
                        sb.append(area);
                        sb.append(StringUtils.getLineSeparator());
                    }
                }
            }
        }
        sb.append("</map>");
        return sb.toString();
    }

    public static String htmlEscape(String input) {
        ParamChecks.nullNotPermitted(input, "input");
        StringBuilder result = new StringBuilder();
        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == '&') {
                result.append("&amp;");
            } else if (c == '\"') {
                result.append("&quot;");
            } else if (c == '<') {
                result.append("&lt;");
            } else if (c == '>') {
                result.append("&gt;");
            } else if (c == '\'') {
                result.append("&#39;");
            } else if (c == '\\') {
                result.append("&#092;");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String javascriptEscape(String input) {
        ParamChecks.nullNotPermitted(input, "input");
        StringBuilder result = new StringBuilder();
        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            if (c == '\"') {
                result.append("\\\"");
            } else if (c == '\'') {
                result.append("\\'");
            } else if (c == '\\') {
                result.append("\\\\");
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
