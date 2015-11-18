package tests;

import common.Survey;
import io.Parser;
import java.io.File;
import java.io.IOException;

public class ParserTest {
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        for (Survey s : Parser.readAll(new File("./assets"))) {
            System.out.println("-------------------------------------------\n" + s);
        }
    }
}
