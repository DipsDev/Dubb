import models.token.Token;

import java.text.ParseException;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws ParseException {
        String code =   """
                        func f2(num) {
                            return num * num;
                        }
                        func pow(num) {
                            return num * f2(num);
                        }
                        
                        final var x = pow(2);
                        x + 0;
                        """;

        Queue<Token> q = new Lexer().tokenize(code);
        q.forEach(System.out::println);


        Runtime.getInstance().execute(new Parser(code).buildTree());


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