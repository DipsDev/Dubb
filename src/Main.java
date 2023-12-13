import models.token.Token;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws ParseException {
        String code =   """
                        declare slimbo = 4
                        declare slimbo2 = 1
                        change slimbo = slimbo * 2 + slimbo2
                        declare slimbo = 3
                        """;

        // Queue<Token> q = new Lexer().tokenize(code);
        // q.forEach(System.out::println);


        Runtime.printOutput(new Parser(code).buildTree());


    }

    // The Task:
    // Calculate 1 + 2

    // Lexer -> Tokenize each character
    // Parser -> make the AST

}