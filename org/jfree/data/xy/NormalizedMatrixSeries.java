package org.jfree.data.xy;

public class NormalizedMatrixSeries extends MatrixSeries {
    public static final double DEFAULT_SCALE_FACTOR = 1.0d;
    private double m_scaleFactor;
    private double m_totalSum;

    public NormalizedMatrixSeries(String name, int rows, int columns) {
        super(name, rows, columns);
        this.m_scaleFactor = DEFAULT_SCALE_FACTOR;
        this.m_totalSum = Double.MIN_VALUE;
    }

    public Number getItem(int itemIndex) {
        return new Double((get(getItemRow(itemIndex), getItemColumn(itemIndex)) * this.m_scaleFactor) / this.m_totalSum);
    }

    public void setScaleFactor(double factor) {
        this.m_scaleFactor = factor;
    }

    public double getScaleFactor() {
        return this.m_scaleFactor;
    }

    public void update(int i, int j, double mij) {
        this.m_totalSum -= get(i, j);
        this.m_totalSum += mij;
        super.update(i, j, mij);
    }

    public void zeroAll() {
        this.m_totalSum = 0.0d;
        super.zeroAll();
    }
}
