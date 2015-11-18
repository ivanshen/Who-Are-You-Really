package common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.util.LineBreakIterator;
import org.jfree.util.LogTarget;

public class Result implements Comparable<Result> {
    private static Logger logger;
    LinkedList<Requirement> reqs;
    String text;

    public static class Requirement implements Comparable<Requirement> {
        double max;
        double min;
        int target;
        int type;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.type);
            sb.append('\n');
            if (this.type == 0) {
                sb.append(this.target);
            } else {
                sb.append(this.min + " " + this.max);
            }
            return sb.toString();
        }

        public int compareTo(Requirement o) {
            if (this.type != o.type) {
                return this.type - o.type;
            }
            if (this.min != o.min) {
                return (int) (this.min - o.min);
            }
            return (int) (this.max - o.max);
        }
    }

    static {
        logger = Logger.getLogger(Result.class.getName());
    }

    public Result(String text) {
        this.text = text;
        this.reqs = new LinkedList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.text);
        sb.append('\n');
        Iterator it = this.reqs.iterator();
        while (it.hasNext()) {
            sb.append((Requirement) it.next());
            sb.append('\n');
        }
        sb.append('\n');
        return sb.toString();
    }

    public boolean check(ArrayList<Type> scores) {
        logger.log(Level.INFO, "Checking " + this.text + ": ");
        Iterator it = this.reqs.iterator();
        while (it.hasNext()) {
            Requirement r = (Requirement) it.next();
            Logger logger;
            Level level;
            Object[] objArr;
            double points;
            switch (r.type) {
                case LineBreakIterator.DONE /*-1*/:
                    if (((Type) scores.get(((int) r.min) - 1)).points <= ((Type) scores.get(((int) r.max) - 1)).points) {
                        logger = logger;
                        level = Level.INFO;
                        objArr = new Object[4];
                        ArrayList<Type> arrayList = scores;
                        objArr[0] = ((Type) arrayList.get(((int) r.min) - 1)).text;
                        arrayList = scores;
                        objArr[1] = Double.valueOf(((Type) arrayList.get(((int) r.min) - 1)).points);
                        arrayList = scores;
                        objArr[2] = ((Type) arrayList.get(((int) r.max) - 1)).text;
                        arrayList = scores;
                        objArr[3] = Double.valueOf(((Type) arrayList.get(((int) r.max) - 1)).points);
                        logger.log(level, String.format("%s:%f < %s:%f", objArr));
                        break;
                    }
                    return false;
                case LogTarget.ERROR /*0*/:
                    points = ((Type) scores.get(r.target - 1)).points;
                    Iterator it2 = scores.iterator();
                    while (it2.hasNext()) {
                        Type s = (Type) it2.next();
                        if (s.index != r.target && s.points > points) {
                            return false;
                        }
                    }
                    logger = logger;
                    level = Level.INFO;
                    objArr = new Object[2];
                    objArr[0] = ((Type) scores.get(r.target - 1)).text;
                    objArr[1] = Double.valueOf(((Type) scores.get(r.target - 1)).points);
                    logger.log(level, String.format("%s:%f is max", objArr));
                    break;
                default:
                    points = ((Type) scores.get(r.type - 1)).points;
                    if (points >= r.min && points <= r.max) {
                        logger = logger;
                        level = Level.INFO;
                        objArr = new Object[4];
                        objArr[0] = Double.valueOf(r.min);
                        objArr[1] = ((Type) scores.get(r.type - 1)).text;
                        objArr[2] = Double.valueOf(((Type) scores.get(r.type - 1)).points);
                        objArr[3] = Double.valueOf(r.max);
                        logger.log(level, String.format("%f < %s:%f < %f", objArr));
                        break;
                    }
                    return false;
            }
        }
        return true;
    }

    public int compareTo(Result o) {
        if (this.reqs.size() != o.reqs.size()) {
            return this.reqs.size() - o.reqs.size();
        }
        for (int i = 0; i < this.reqs.size(); i++) {
            if (((Requirement) this.reqs.get(i)).compareTo((Requirement) o.reqs.get(i)) != 0) {
                return ((Requirement) this.reqs.get(i)).compareTo((Requirement) o.reqs.get(i));
            }
        }
        return 0;
    }

    public String getText() {
        return this.text;
    }
}
