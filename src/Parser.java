import models.ast.*;
import models.ast.functions.Function;
import models.ast.functions.FunctionCall;
import models.ast.functions.ReturnExpression;
import models.ast.interfaces.Variable;
import models.ast.interfaces.VariableInstance;
import models.ast.types.BinaryExpression;
import models.ast.types.BooleanExpression;
import models.ast.types.IfStatement;
import models.ast.types.NumericLiteral;
import models.ast.interfaces.ASTNode;
import models.ast.functions.ModifyVariable;
import models.token.Token;
import models.token.TokenType;

import java.text.ParseException;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Parser {

    private final Queue<Token> tokens;

    public Parser(String sourceCode) {
        this.tokens = new Lexer().tokenize(sourceCode);
    }

    /**
     * Parses variable declaration statement, into AST nodes
     * @param isConstant whether the variable created should be a constant
     * @return the created AST node
     * @throws ParseException
     */
    private Variable<?> parseVariableDeclaration(boolean isConstant) throws ParseException {
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
                return new Variable<>(variable.getValue(), variableStringValue.getValue()).setConstant(isConstant);
            }
            case IDENTIFIER -> {
                Token variableName = this.tokens.remove();
                if (this.tokens.peek().getType() == TokenType.OPEN_PARAN) {
                    return new Variable<>(variable.getValue(), parseFunctionCall(variableName));
                }
                if (this.tokens.peek().getType() == TokenType.BINARY_OPERATOR) {
                    return new Variable<>(variable.getValue(), parseAdditiveExpression(variableName));
                }
                if (this.tokens.peek().getType() == TokenType.BOOLEAN_OPERATOR) {
                    return new Variable<>(variable.getValue(), parseBooleanExpression(variableName));
                }
                throw new Error("Not Implemented, got " + variableName.getValue());
            }
            default -> {
                Token value = this.tokens.remove();
                if (this.tokens.peek().getType() == TokenType.BOOLEAN_OPERATOR) {
                    return new Variable<>(variable.getValue(), parseBooleanExpression(value));
                }
                ASTNode mathExpression = this.parseAdditiveExpression(value); // can return NumberLiteral, or BinaryExpression
                if (mathExpression instanceof BinaryExpression)
                    return new Variable<BinaryExpression>(variable.getValue(), (BinaryExpression) mathExpression);
                return new Variable<BinaryExpression>(variable.getValue(), new BinaryExpression(mathExpression, new NumericLiteral(0), '+')).setConstant(isConstant);
            }
        }

    }

    /**
     * Parses a return statement into an AST node
     * @return the return statement as an AST node
     * @throws ParseException
     */
    private ReturnExpression parseReturnStatement() throws ParseException {
        this.tokens.remove(); // remove the RETURN keyword
        Token returnValue = this.tokens.remove();
        if (returnValue.getType() == TokenType.NUMBER) {
            return new ReturnExpression(parseAdditiveExpression(returnValue));
        }
        if (returnValue.getType() == TokenType.IDENTIFIER) {
            if (this.tokens.peek().getType() == TokenType.OPEN_PARAN) {
                return new ReturnExpression(parseFunctionCall(returnValue));
            }
            return new ReturnExpression(parseAdditiveExpression(returnValue));


        }
        throw new Error("Not implemented, return type of " + returnValue.getType());


    }

    /**
     * Parses a function declaration statement
     * @return the AST node representing the function declaration statement
     * @throws ParseException
     */
    private Function parseFunctionDeclaration() throws ParseException {
        // func name(args) {}
        this.tokens.remove(); // removes the func keyword
        Token nameWithArguments = this.tokens.remove();
        if (nameWithArguments.getType() != TokenType.IDENTIFIER) {
            throw new Error("Unexpected Token " + nameWithArguments.getType());
        }

        Function function = new Function(nameWithArguments.getValue());
        if (this.tokens.remove().getType() != TokenType.OPEN_PARAN) {
            throw new Error("Unexpected Token " + nameWithArguments.getType());
        }
        while (this.tokens.peek().getType() != TokenType.CLOSE_PARAN) {
            function.appendArgument(this.tokens.remove().getValue());
        }
        this.tokens.remove(); // remove the close parenthesis
        if (this.tokens.remove().getType() != TokenType.OPEN_STATEMENT) {
            throw new Error("Unexpected Token " + nameWithArguments.getType());
        }

        // append body to the function
        while (this.tokens.peek().getType() != TokenType.CLOSE_STATEMENT) {
            ASTNode node = this.parseStatements(Optional.of((token -> {
                switch (token.getType()) {
                    case RETURN -> {
                        try {
                            return parseReturnStatement();
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    }
                    default -> {
                        try {
                            throw new ParseException("Couldn't parse correctly, got type " + tokens.peek().getType(), tokens.size());
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            })));
            function.appendBodyStatement(node);
        }
        // remove the close statement
        this.tokens.remove();

        return function;

    }

    /**
     * Parses a modify variable statement, meaning the function should be run every new assignment of a variable
     * @param variableName the variable that is being reassigned
     * @return the AST node representing the modification statement
     * @throws ParseException
     */
    private ModifyVariable<?> parseModifyStatements(Token variableName) throws ParseException {
        if (this.tokens.peek().getType() != TokenType.EQUALS) {
            throw new Error("Expected = after modify statement");
        }
        this.tokens.remove(); // remove the equal sign
        Token variableValue = this.tokens.peek();
        switch (variableValue.getType()) {
            // Add more variable types
            case STRING -> {
                Token variableStringValue = this.tokens.remove();
                return new ModifyVariable<>(variableName.getValue(), variableStringValue.getValue());
            }
            case IDENTIFIER -> {
                Token identifierValue = this.tokens.remove();
                if (this.tokens.peek().getType() == TokenType.OPEN_PARAN) {
                    return new ModifyVariable<>(variableName.getValue(), parseFunctionCall(identifierValue));
                }
                if (this.tokens.peek().getType() == TokenType.BINARY_OPERATOR) {
                    return new ModifyVariable<>(variableName.getValue(), parseAdditiveExpression(identifierValue));
                }
                if (this.tokens.peek().getType() == TokenType.BOOLEAN_OPERATOR) {
                    return new ModifyVariable<>(variableName.getValue(), parseBooleanExpression(identifierValue));
                }
                throw new Error("Not Implemented, got " + identifierValue.getValue());
            }
            default -> {
                Token value = this.tokens.remove();
                if (this.tokens.peek().getType() == TokenType.BOOLEAN_OPERATOR) {
                    return new ModifyVariable<>(variableName.getValue(), parseBooleanExpression(value));
                }
                ASTNode mathExpression = this.parseAdditiveExpression(value); // can return NumberLiteral, or BinaryExpression
                if (mathExpression instanceof BinaryExpression)
                    return new ModifyVariable<>(variableName.getValue(), (BinaryExpression) mathExpression);
                return new ModifyVariable<>(variableName.getValue(), new BinaryExpression(mathExpression, new NumericLiteral(0), '+'));
            }
        }
    }

    /**
     * Parses a function call, which is called anytime an identifier is used with a parentheses. example: a()
     * @param name the function name that is being called
     * @return the AST node representing the function call
     * @throws ParseException
     */
    private FunctionCall parseFunctionCall(Token name) throws ParseException {
        FunctionCall func = new FunctionCall(name.getValue());
        this.tokens.remove(); // remove the open paran
        while (this.tokens.peek().getType() != TokenType.CLOSE_PARAN) {
            // could be either a variable or a number or a string
            switch (this.tokens.peek().getType()) {
                // Add more variable types
                case STRING -> {
                    Token stringArg = this.tokens.remove();
                    func.appendArg(stringArg.getValue());
                }
                case NUMBER, IDENTIFIER -> {
                    Token removed = this.tokens.remove();
                    if (this.tokens.peek().getType() == TokenType.EQUALS) {
                        func.appendArg(parseModifyStatements(removed));
                    } else if (this.tokens.peek().getType() == TokenType.OPEN_PARAN) { // start of function call
                        func.appendArg(parseFunctionCall(removed));
                    } else if (this.tokens.peek().getType() == TokenType.BOOLEAN_OPERATOR) {
                        func.appendArg(parseBooleanExpression(removed));
                    }
                    else {
                        func.appendArg(parseAdditiveExpression(removed));
                    }
                }
                default -> {
                    throw new Error("Not Implemented, got " + this.tokens.peek().getType());
                }
            }
        }
        this.tokens.remove(); // remove the close paran
        this.tokens.remove(); // removes ;
        return func;
    }

    private BooleanExpression parseBooleanExpression(Token left) throws ParseException {
        if (left == null) {
            left = tokens.remove();
        }
        Token operator = tokens.remove();
        Token rightSide = tokens.remove();
        return new BooleanExpression(parseAdditiveExpression(left), parseAdditiveExpression(rightSide), operator.getValue());


    }


    /**
     * Parses the main statements, and redirects to the specific parsers
     * @return the AST node representing the current token type
     * @throws ParseException
     */
    private ASTNode parseStatements(Optional<java.util.function.Function<Token, ASTNode>> callback) throws ParseException {
        Token token = this.tokens.peek();
        switch (token.getType()) {
            case NUMBER, IDENTIFIER -> {
                Token removed = this.tokens.remove();
                if (this.tokens.peek().getType() == TokenType.EQUALS) {
                    return parseModifyStatements(removed);
                } else if (this.tokens.peek().getType() == TokenType.OPEN_PARAN) { // start of function call
                    return parseFunctionCall(removed);
                } else if (this.tokens.peek().getType() == TokenType.BOOLEAN_OPERATOR) {
                    return parseBooleanExpression(removed);
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
            case FUNCTION -> {
                return parseFunctionDeclaration();
            }
            case IF -> {
                return parseIfStatement();
            }
        }
        if (callback.isPresent()) {
            return callback.get().apply(token);
        }
        else {
            throw new Error("Unexpected Type " + token.getType() + " at char " + this.tokens.remove().getValue());
        }
    }

    /**
     * Parses an if statement and returns it
     * @return the parsed if statement
     */
    private IfStatement parseIfStatement() throws ParseException {
        this.tokens.remove(); // removes the if keyword
        BooleanExpression booleanExpression = this.parseBooleanExpression(null);
        if (this.tokens.remove().getType() != TokenType.OPEN_STATEMENT) {
            throw new Error("Couldn't parse code correctly, unknown token " + this.tokens.peek().getType());
        }
        IfStatement ifStatement = new IfStatement(booleanExpression);
        while (this.tokens.peek().getType() != TokenType.CLOSE_STATEMENT) {
            ifStatement.appendBody(this.parseStatements(Optional.empty()));
        }
        this.tokens.remove();
        return ifStatement;

    }

    /**
     * Parses the additive expression (+, -)
     * @param start start from a specific token, or null if to remove one
     * @return the AST node representing the expression
     * @throws ParseException
     */
    private ASTNode parseAdditiveExpression(Token start) throws ParseException {
        ASTNode left = this.parseMultiExpression(start);

        while (this.tokens.peek().getValue().equals("+") || this.tokens.peek().getValue().equals("-")) {
            Token operator = tokens.remove();
            ASTNode right = this.parseMultiExpression(null);
            left = new BinaryExpression(left, right, operator.getValue().charAt(0));
        }
        return left;

    }

    /**
     * Parses the multi expression (*, /)
     * @param start start from a specific token, or null if to remove one
     * @return the AST node representing the expression
     * @throws ParseException
     */
    private ASTNode parseMultiExpression(Token start) throws ParseException {
        ASTNode left = this.parsePrimaryExpression(start);

        while (this.tokens.peek().getValue().equals("*") || this.tokens.peek().getValue().equals("/")) {
            Token operator = tokens.remove();
            ASTNode right = this.parsePrimaryExpression(null);
            left = new BinaryExpression(left, right, operator.getValue().charAt(0));
        }
        return left;
    }

    /**
     * Parses the primary expression(number, variable)
     * @param start start from a specific token, or null if to remove one
     * @return the AST node representing the expression
     * @throws ParseException
     */
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
                // could be a variable usage, or a function call
                assert this.tokens.peek() != null;
                if (this.tokens.peek().getType() != TokenType.OPEN_PARAN) {
                    return new VariableInstance(current.getValue());
                }
                return new BinaryExpression(parseFunctionCall(current), new NumericLiteral(0), '+');
            }
            default -> {
                throw new ParseException("Couldn't parse correctly, got type " + current.getType(), tokens.size());
            }


        }
    }

    /**
     * Builds the AST
     * @return the AST program
     * @throws ParseException
     */
    public Program build() throws ParseException {
        Program program = new Program();

        while (!tokens.isEmpty() && tokens.peek().getType() != TokenType.EOF) {
            assert this.tokens.peek() != null;
            if (tokens.peek().getType() == TokenType.EOL) {
                tokens.remove();
                continue;
            }
            program.append(parseStatements(Optional.empty()));
        }

        return program;

    }
}
