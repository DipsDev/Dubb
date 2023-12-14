import models.token.Token;

import java.text.ParseException;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws ParseException {
        String code =   """
                        func f1(a, b) {
                            a + b;
                            return a - b;
                        }
                        func f2(a, c, b) {
                            return f1(a, c - b);
                        }
                        var x = 1 + f2(1,2,3);
                        final var y = x + 1;
                        y + 0;
                        """;

        Queue<Token> q = new Lexer().tokenize(code);
        q.forEach(System.out::println);


        Runtime.execute(new Parser(code).buildTree());


    }

    // The Task:
    // Calculate 1 + 2

    // Lexer -> Tokenize each character
    // Parser -> make the AST


    // Source Code:

    // Functions:
    // func myfunction(param1, param2, param3) {
    //        ...
    // }

    // Variables:
    // var x = 4;
    // final var y = 5;
    // x = 5;

    // Loops:
    // loop(myfunction);

    // If statements:
    // if (condition) then {
    //      ...
    // }
    // else if {
    //      ...
    // }
    // else {
    //      ...
    // }



}