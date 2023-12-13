import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException {
        String code =   """
                        var x = 5;
                        x + 5;
                        x = 7;
                        x + 5;
                        
                        """;

        // Queue<Token> q = new Lexer().tokenize(code);
        // q.forEach(System.out::println);


        Runtime.printOutput(new Parser(code).buildTree());


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



}