package common;

import common.Result.Requirement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class Survey {
    private static final Pattern LINE_DELIM;
    private static final Pattern TOKEN_DELIM;
    private static Logger logger;
    private int index;
    private boolean initialized;
    private String name;
    private ArrayList<Question> questions;
    private ArrayList<Result> results;
    private ArrayList<Type> types;
    private String website;

    static {
        logger = Logger.getLogger(Survey.class.getName());
        TOKEN_DELIM = Pattern.compile("(?:(?:\\s+)|(?:#[^\\n]*)){1,}");
        LINE_DELIM = Pattern.compile("(?:(?:\\s*\\n)|(?:#[^\\n]*\\n)){1,}");
    }

    private Survey() {
        this.questions = new ArrayList();
        this.results = new ArrayList();
        this.types = new ArrayList();
        this.index = 0;
        this.initialized = false;
    }

    private void initialize() {
        if (!this.initialized) {
            shuffle();
            Collections.sort(this.types);
            Collections.sort(this.results);
        }
    }

    public void choose(Choice choice) {
        Iterator it = choice.getValues().iterator();
        while (it.hasNext()) {
            ValType v = (ValType) it.next();
            Type type = (Type) this.types.get(v.type - 1);
            type.points += v.value;
        }
    }

    public Question getNextQuestion() {
        if (this.index + 1 >= this.questions.size()) {
            return null;
        }
        this.index++;
        return (Question) this.questions.get(this.index);
    }

    public Question getPrevQuestion() {
        if (this.index <= 0) {
            return null;
        }
        this.index--;
        return (Question) this.questions.get(this.index);
    }

    public Question getQuestion() {
        return (Question) this.questions.get(this.index);
    }

    public String getTitle() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void shuffle() {
        Iterator it = this.questions.iterator();
        while (it.hasNext()) {
            ((Question) it.next()).shuffle();
        }
        Collections.shuffle(this.questions);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.name);
        sb.append('\n');
        sb.append(this.questions.size());
        sb.append('\n');
        Iterator it = this.questions.iterator();
        while (it.hasNext()) {
            sb.append((Question) it.next());
        }
        sb.append(this.results.size());
        sb.append('\n');
        it = this.results.iterator();
        while (it.hasNext()) {
            sb.append((Result) it.next());
        }
        sb.append(this.types.size());
        sb.append('\n');
        it = this.types.iterator();
        while (it.hasNext()) {
            sb.append(((Type) it.next()) + "\n");
        }
        sb.append(this.website);
        sb.append('\n');
        return sb.toString();
    }

    public static Survey parse(String str) {
        try {
            int i;
            Survey res = new Survey();
            Scanner in = new Scanner(str);
            String token = null;
            in.useDelimiter(LINE_DELIM);
            res.name = in.next();
            in.useDelimiter(TOKEN_DELIM);
            int num = in.nextInt();
            for (i = 0; i < num; i++) {
                in.skip("[^\\n]*\\n");
                in.useDelimiter(LINE_DELIM);
                token = in.next();
                Question cur = new Question(token);
                in.useDelimiter(TOKEN_DELIM);
                int nChoices = in.nextInt();
                for (int j = 0; j < nChoices; j++) {
                    in.skip("[^\\n]*\\n");
                    in.useDelimiter(LINE_DELIM);
                    token = in.next();
                    Choice curChoice = new Choice(token);
                    in.useDelimiter(TOKEN_DELIM);
                    while (in.hasNextInt()) {
                        token = in.next();
                        if (!in.hasNextDouble()) {
                            break;
                        }
                        int type = Integer.parseInt(token);
                        double points = in.nextDouble();
                        curChoice.values.add(new ValType(type, points));
                    }
                    cur.choices.add(curChoice);
                }
                res.questions.add(cur);
            }
            in.useDelimiter(TOKEN_DELIM);
            if (token == null) {
                token = in.next();
            }
            num = Integer.parseInt(token);
            for (i = 0; i < num; i++) {
                in.skip("[^\\n]*\\n");
                in.useDelimiter(LINE_DELIM);
                token = in.next();
                Result cur2 = new Result(token);
                in.useDelimiter(TOKEN_DELIM);
                while (in.hasNextInt()) {
                    Requirement r = new Requirement();
                    token = in.next();
                    if (!in.hasNextDouble()) {
                        break;
                    }
                    r.type = Integer.parseInt(token);
                    if (r.type != 0) {
                        r.min = in.nextDouble();
                        r.max = in.nextDouble();
                    } else {
                        r.target = in.nextInt();
                    }
                    cur2.reqs.add(r);
                }
                res.results.add(cur2);
            }
            if (token == null) {
                token = in.next();
            }
            num = Integer.parseInt(token);
            for (i = 1; i <= num; i++) {
                in.skip("[^\\n]*\\n");
                in.useDelimiter(LINE_DELIM);
                Type cur3 = new Type(in.next().trim(), i);
                res.types.add(cur3);
            }
            in.skip("\\s*");
            res.website = in.next();
            in.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getIndex() {
        return this.index;
    }

    public int getQuestionSize() {
        return this.questions.size();
    }

    public ArrayList<Question> getQuestionArray() {
        return this.questions;
    }

    public ArrayList<Result> getResults() {
        initialize();
        ArrayList<Result> res = new ArrayList();
        Iterator it = this.types.iterator();
        while (it.hasNext()) {
            Type t = (Type) it.next();
            logger.log(Level.INFO, t.text + ": " + t.points);
        }
        it = this.results.iterator();
        while (it.hasNext()) {
            Result r = (Result) it.next();
            if (r.check(this.types)) {
                res.add(r);
            }
        }
        return res;
    }

    public ArrayList<Type> getTypes() {
        return this.types;
    }

    public String getWebsite() {
        return this.website;
    }
}
