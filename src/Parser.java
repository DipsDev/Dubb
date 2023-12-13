import models.ast.*;
import models.ast.interfaces.VariableInstance;
import models.ast.types.ASTNode;
import models.token.Token;
import models.token.TokenType;

import java.text.ParseException;
import java.util.Queue;

public class Parser {

    private final Queue<Token> tokens;

    public Parser(String sourceCode) {
        this.tokens = new Lexer().tokenize(sourceCode);
    }

    // var x = 4
    private ASTNode parseVariableDeclaration(boolean isConstant) throws ParseException {
        this.tokens.remove(); // var
        Token variable = this.tokens.remove();
        if (this.tokens.peek().getType() != TokenType.EQUALS) {
            throw new Error("Variables must start with an alphabetic character");
        }
        // Remove the equals sign
        this.tokens.remove();
        Token variableValue = this.tokens.peek();
        switch (variableValue.getType()) {
            // Add more variable types
            case STRING -> {
                Token variableStringValue = this.tokens.remove();
                return new StringVariable(variable.getValue(), variableStringValue.getValue()).setConstant(isConstant);
            }
            default -> {
                ASTNode mathExpression = this.parseAdditiveExpression(null); // can return NumberLiteral, or BinaryExpression
                if (mathExpression instanceof BinaryExpression)
                    return new IntegerVariable(variable.getValue(), (BinaryExpression) mathExpression);
                return new IntegerVariable(variable.getValue(), new BinaryExpression((NumericLiteral) mathExpression, new NumericLiteral(0), '+')).setConstant(isConstant);
            }
        }

    }

    // usage: modify x = 3
    private ASTNode parseModifyStatements(Token variableName) throws ParseException {
        if (this.tokens.peek().getType() != TokenType.EQUALS) {
            throw new Error("Expected = after modify");
        }
        this.tokens.remove(); // remove the equal sign
        Token variableValue = this.tokens.peek();
        switch (variableValue.getType()) {
            // Add more variable types
            case STRING -> {
                Token variableStringValue = this.tokens.remove();
                return new ModifyVariable<>(variableName.getValue(), variableStringValue.getValue());
            }
            default -> {
                ASTNode mathExpression = this.parseAdditiveExpression(null); // can return NumberLiteral, or BinaryExpression
                if (mathExpression instanceof BinaryExpression)
                    return new ModifyVariable<>(variableName.getValue(), (BinaryExpression) mathExpression);
                return new ModifyVariable<>(variableName.getValue(), new BinaryExpression((NumericLiteral) mathExpression, new NumericLiteral(0), '+'));
            }
        }
    }


    private ASTNode parseStatements() throws ParseException {
        Token token = this.tokens.peek();
        switch (token.getType()) {
            case NUMBER, IDENTIFIER -> {
                Token removed = this.tokens.remove();
                if (this.tokens.peek().getType() == TokenType.EQUALS) {
                    return parseModifyStatements(removed);
                }
                return parseAdditiveExpression(removed);
            }
            case VAR -> {
                // Variable declaration
                return parseVariableDeclaration(false);
            }
            case CONST -> {
                this.tokens.remove(); // remove the const keywords
                return parseVariableDeclaration(true);
            }
        }
        throw new Error("Unexpected Type " + token.getType());
    }

    private ASTNode parseAdditiveExpression(Token start) throws ParseException {
        ASTNode left = this.parseMultiExpression(start);

        while (this.tokens.peek().getValue().equals("+") || this.tokens.peek().getValue().equals("-")) {
            Token operator = tokens.remove();
            ASTNode right = this.parseMultiExpression(null);
            left = new BinaryExpression(left, right, operator.getValue().charAt(0));
        }
        return left;

    }

    private ASTNode parseMultiExpression(Token start) throws ParseException {
        ASTNode left = this.parsePrimaryExpression(start);

        while (this.tokens.peek().getValue().equals("*") || this.tokens.peek().getValue().equals("/")) {
            Token operator = tokens.remove();
            ASTNode right = this.parsePrimaryExpression(null);
            left = new BinaryExpression(left, right, operator.getValue().charAt(0));
        }
        return left;
    }

    private ASTNode parsePrimaryExpression(Token start) throws ParseException {
        Token current = start;
        if (current == null) {
            current = tokens.remove();
        }

        switch (current.getType()) {
            case NUMBER -> {
                return new NumericLiteral(Integer.parseInt(current.getValue()));
            }
            case IDENTIFIER -> {
                return new VariableInstance(current.getValue());
            }
            default -> {
                throw new ParseException("Couldn't parse correctly, got type " + current.getType(), tokens.size());
            }


        }
    }
    public Program buildTree() throws ParseException {
        Program program = new Program();

        while (!tokens.isEmpty() && tokens.peek().getType() != TokenType.EOF) {
            if (tokens.peek().getType() == TokenType.EOL) {
                tokens.remove();
                continue;
            }
            program.append(parseStatements());
        }

        return program;

    }
}
