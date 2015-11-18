package common;

public class Type implements Comparable<Type> {
    int index;
    double points;
    String text;

    public Type(String text, int index) {
        this.text = text;
        this.index = index;
        this.points = 0.0d;
    }

    public String toString() {
        return this.text;
    }

    public int compareTo(Type o) {
        return this.index - o.index;
    }

    public String getText() {
        return this.text;
    }

    public double getPoints() {
        return this.points;
    }
}
