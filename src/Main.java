import models.token.Token;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws ParseException {
        String code =   """
                        declare x = 4
                        change x = x * 3 + 2
                        x + 1
                        """;

        Queue<Token> q = new Lexer().tokenize(code);
        q.forEach(System.out::println);


        Runtime.printOutput(new Parser(code).buildTree());


    }

    // The Task:
    // Calculate 1 + 2

    // Lexer -> Tokenize each character
    // Parser -> make the AST

}