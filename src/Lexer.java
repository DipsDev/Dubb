import models.token.Token;
import models.token.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Lexer {
    private final HashMap<String, TokenType> reservedKeywords;


    public Lexer() {
        reservedKeywords = new HashMap<>();
        this.addReservedKeywords();
    }

    /**
     * Registers the reserved keywords
     */
    private void addReservedKeywords() {
        this.reservedKeywords.put("var", TokenType.VAR);
        this.reservedKeywords.put("final", TokenType.CONST);
        this.reservedKeywords.put(";", TokenType.EOL);
        this.reservedKeywords.put("func", TokenType.FUNCTION);
        this.reservedKeywords.put("return", TokenType.RETURN);

    }


    /**
     * Converts a sourcecode to a queue, which can be tokenized by the lexer
     * @param code the source code to be represented as a queue
     * @return the source code as queue
     */
    private Queue<Character> convertToQueue(String code) {
        Queue<Character> queue = new LinkedList<>();
        for (int i = 0; i<code.length(); i++) {
            queue.add(code.charAt(i));
        }
        return queue;
    }

    /**
     * Tokenizes a source code, and returns its tokenized queue
     * @param stringCode the source code to be tokenized
     * @return the tokenized source code, as a queue
     */
    public Queue<Token> tokenize(String stringCode) {
        Queue<Character> code = convertToQueue(stringCode);
        Queue<Token> list = new LinkedList<>();
        while (!code.isEmpty()) {
            char current = code.remove();
            char next = code.peek() == null ? (char) 0 : code.peek();
            if (current == '>' || current == '<' || (current == '!' && next == '=') || (current == '=' && next == '=')) {
                if (next == '=') {
                    code.remove();
                    list.add(new Token(TokenType.BOOLEAN_OPERATOR, "" + current + next));
                } else {
                    list.add(new Token(TokenType.BOOLEAN_OPERATOR, Character.toString(current)));
                }
            }
            else if (current == '+' || current == '-' || current == '/' || current == '*') {
                list.add(new Token(TokenType.BINARY_OPERATOR, Character.toString(current)));
            } else if (current == '=') {
                list.add(new Token(TokenType.EQUALS, Character.toString(current)));
            }
            else if (current == ' ' || current == '\n' || current == '\t' || current == ',') {
                // Ignore blank chars
                continue;
            }
            else if (current == '{') {
                list.add(new Token(TokenType.OPEN_STATEMENT, "{"));
            }
            else if (current == '}') {
                list.add(new Token(TokenType.CLOSE_STATEMENT, "}"));
            }
            else if (current == '(') {
                list.add(new Token(TokenType.OPEN_PARAN, "("));
            }
            else if (current == ')') {
                list.add(new Token(TokenType.CLOSE_PARAN, ")"));
            }
            else if (current == ';') {
                list.add(new Token(TokenType.EOL, ";"));
            }
            else if (current == '"') {
                // Start of a string
                StringBuilder stringVal = new StringBuilder();
                while (!code.isEmpty() && code.peek() != '"') {
                    stringVal.append(code.remove());
                }
                // Remove the last "
                code.remove();
                list.add(new Token(TokenType.STRING, stringVal.toString()));
            }
            else {
                // Handle multicharacters
                if (Character.isDigit(current)) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(current);
                    while (!code.isEmpty() && Character.isDigit(code.peek())) {
                        builder.append(code.remove());
                    }

                    list.add(new Token(TokenType.NUMBER, builder.toString()));
                }
                else if (Character.isAlphabetic(current)) {
                    StringBuilder stringVar = new StringBuilder();
                    stringVar.append(current);
                    while (!code.isEmpty() && (Character.isAlphabetic(code.peek()) || Character.isDigit(code.peek()))) {
                        stringVar.append(code.remove());
                    }


                    // Add reserved Words
                    list.add(new Token(this.reservedKeywords.getOrDefault(stringVar.toString(), TokenType.IDENTIFIER), stringVar.toString()));




                }
                else {
                    throw new RuntimeException("Unrecognized character at " + current);
                }

            }
        }
        list.add(new Token(TokenType.EOF, "eof"));
        return list;
    }
}
