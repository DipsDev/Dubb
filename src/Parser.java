import models.ast.BinaryExpression;
import models.ast.NumericLiteral;
import models.ast.interfaces.MathExpression;
import models.ast.types.ASTNode;
import models.ast.Program;
import models.token.Token;
import models.token.TokenType;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Queue;
import java.util.function.BinaryOperator;

public class Parser {

    private Queue<Token> tokens;

    public Parser(String sourceCode) {
        this.tokens = new Lexer().tokenize(sourceCode);
    }

    private ASTNode parseAdditiveExpression() throws ParseException {
        ASTNode left = this.parseMultiExpression();

        while (this.tokens.peek().getValue().equals("+") || this.tokens.peek().getValue().equals("-")) {
            Token operator = tokens.remove();
            ASTNode right = this.parseMultiExpression();
            left = new BinaryExpression((MathExpression) left, (MathExpression) right, operator.getValue().charAt(0));
        }
        return left;

    }

    private ASTNode parseMultiExpression() throws ParseException {
        ASTNode left = this.parsePrimaryExpression();

        while (this.tokens.peek().getValue().equals("*") || this.tokens.peek().getValue().equals("/")) {
            Token operator = tokens.remove();
            ASTNode right = this.parsePrimaryExpression();
            left = new BinaryExpression((MathExpression) left, (MathExpression) right, operator.getValue().charAt(0));
        }
        return left;
    }

    private ASTNode parsePrimaryExpression() throws ParseException {
        Token current = tokens.remove();

        switch (current.getType()) {
            case NUMBER -> {
                return new NumericLiteral(Integer.parseInt(current.getValue()));
            }
            default -> {
                throw new ParseException("Couldn't parse correctly, got type " + current.getType(), tokens.size());
            }


        }
    }
    public Program buildTree() throws ParseException {
        Program program = new Program();

        while (!tokens.isEmpty() && tokens.peek().getType() != TokenType.EOF) {
            program.append(parseAdditiveExpression());
        }

        return program;

    }
}
