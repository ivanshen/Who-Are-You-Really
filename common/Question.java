package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Question {
    ArrayList<Choice> choices;
    String text;

    public Question(String text) {
        this.text = text;
        this.choices = new ArrayList();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.text);
        sb.append('\n');
        sb.append(this.choices.size());
        sb.append('\n');
        Iterator it = this.choices.iterator();
        while (it.hasNext()) {
            sb.append((Choice) it.next());
        }
        sb.append('\n');
        return sb.toString();
    }

    public void shuffle() {
        Collections.shuffle(this.choices);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<Choice> getChoices() {
        return this.choices;
    }

    public void setChoices(ArrayList<Choice> choices) {
        this.choices = choices;
    }

    public int hashCode() {
        return this.text.hashCode() & this.choices.hashCode();
    }
}
