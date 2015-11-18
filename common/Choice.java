package common;

import java.util.Iterator;
import java.util.LinkedList;

public class Choice {
    String text;
    LinkedList<ValType> values;

    static class ValType {
        int type;
        double value;

        public ValType(int type, double value) {
            this.type = type;
            this.value = value;
        }

        public ValType() {
            this.type = 0;
            this.value = 0.0d;
        }
    }

    public Choice(String text) {
        this.text = text;
        this.values = new LinkedList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.text);
        sb.append('\n');
        Iterator it = this.values.iterator();
        while (it.hasNext()) {
            ValType v = (ValType) it.next();
            sb.append(v.type + " " + v.value + " ");
        }
        sb.append('\n');
        return sb.toString();
    }

    public String getText() {
        return this.text;
    }

    public LinkedList<ValType> getValues() {
        return this.values;
    }
}
