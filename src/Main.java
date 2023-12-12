import models.token.Token;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String code = "var x = 3";

        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.tokenize(code);
        tokens.forEach(System.out::println);
    }

    // The Task:
    // Calculate 1 + 2

    // Lexer -> Tokenize each character
    // Parser -> make the AST

}