import models.token.Token;

import java.text.ParseException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String code = "8 / 4 - 2 * 6";

        Parser parser = new Parser(code);

        try {
            Runtime.printOutput(parser.buildTree());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // The Task:
    // Calculate 1 + 2

    // Lexer -> Tokenize each character
    // Parser -> make the AST

}