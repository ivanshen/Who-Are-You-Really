package org.jfree.chart.plot;

import java.util.Collections;
import org.jfree.chart.axis.DateAxis;

public class PieLabelDistributor extends AbstractPieLabelDistributor {
    private double minGap;

    public PieLabelDistributor(int labelCount) {
        this.minGap = 4.0d;
    }

    public void distributeLabels(double minY, double height) {
        sort();
        if (isOverlap()) {
            adjustDownwards(minY, height);
        }
        if (isOverlap()) {
            adjustUpwards(minY, height);
        }
        if (isOverlap()) {
            spreadEvenly(minY, height);
        }
    }

    private boolean isOverlap() {
        double y = 0.0d;
        for (int i = 0; i < this.labels.size(); i++) {
            PieLabelRecord plr = getPieLabelRecord(i);
            if (y > plr.getLowerY()) {
                return true;
            }
            y = plr.getUpperY();
        }
        return false;
    }

    protected void adjustInwards() {
        int lower = 0;
        for (int upper = this.labels.size() - 1; upper > lower; upper--) {
            if (lower < upper - 1) {
                PieLabelRecord r0 = getPieLabelRecord(lower);
                PieLabelRecord r1 = getPieLabelRecord(lower + 1);
                if (r1.getLowerY() < r0.getUpperY()) {
                    r1.setAllocatedY(r1.getAllocatedY() + ((r0.getUpperY() - r1.getLowerY()) + this.minGap));
                }
            }
            PieLabelRecord r2 = getPieLabelRecord(upper - 1);
            PieLabelRecord r3 = getPieLabelRecord(upper);
            if (r2.getUpperY() > r3.getLowerY()) {
                r3.setAllocatedY(r3.getAllocatedY() + ((r2.getUpperY() - r3.getLowerY()) + this.minGap));
            }
            lower++;
        }
    }

    protected void adjustDownwards(double minY, double height) {
        for (int i = 0; i < this.labels.size() - 1; i++) {
            PieLabelRecord record0 = getPieLabelRecord(i);
            PieLabelRecord record1 = getPieLabelRecord(i + 1);
            if (record1.getLowerY() < record0.getUpperY()) {
                record1.setAllocatedY(Math.min((minY + height) - (record1.getLabelHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS), (record0.getUpperY() + this.minGap) + (record1.getLabelHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)));
            }
        }
    }

    protected void adjustUpwards(double minY, double height) {
        for (int i = this.labels.size() - 1; i > 0; i--) {
            PieLabelRecord record0 = getPieLabelRecord(i);
            PieLabelRecord record1 = getPieLabelRecord(i - 1);
            if (record1.getUpperY() > record0.getLowerY()) {
                record1.setAllocatedY(Math.max((record1.getLabelHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + minY, (record0.getLowerY() - this.minGap) - (record1.getLabelHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS)));
            }
        }
    }

    protected void spreadEvenly(double minY, double height) {
        int i;
        double y = minY;
        double sumOfLabelHeights = 0.0d;
        for (i = 0; i < this.labels.size(); i++) {
            sumOfLabelHeights += getPieLabelRecord(i).getLabelHeight();
        }
        double gap = height - sumOfLabelHeights;
        if (this.labels.size() > 1) {
            gap /= (double) (this.labels.size() - 1);
        }
        for (i = 0; i < this.labels.size(); i++) {
            PieLabelRecord record = getPieLabelRecord(i);
            y += record.getLabelHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS;
            record.setAllocatedY(y);
            y = ((record.getLabelHeight() / DateAxis.DEFAULT_AUTO_RANGE_MINIMUM_SIZE_IN_MILLISECONDS) + y) + gap;
        }
    }

    public void sort() {
        Collections.sort(this.labels);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.labels.size(); i++) {
            result.append(getPieLabelRecord(i).toString()).append("\n");
        }
        return result.toString();
    }
}
