import models.token.Token;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws ParseException {
        String code =   """
                        declare x = 4
                        declare y = 1
                        change x = y * 2 + x
                        change y = x - y
                        y + y * x
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