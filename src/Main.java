import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException {
        String code =   """
                        final var y = 10;
                        var x = 5 - y;
                        x = x + 9 * y;
                        final var minus_2 = 0 - 2;
                        x * minus_2;
                        """;

        // Queue<Token> q = new Lexer().tokenize(code);
        // q.forEach(System.out::println);


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