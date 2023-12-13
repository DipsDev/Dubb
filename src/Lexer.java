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

    private void addReservedKeywords() {
        this.reservedKeywords.put("var", TokenType.VAR);

    }



    private Queue<Character> convertToQueue(String code) {
        Queue<Character> queue = new LinkedList<>();
        for (int i = 0; i<code.length(); i++) {
            queue.add(code.charAt(i));
        }
        return queue;
    }
    public Queue<Token> tokenize(String stringCode) {
        Queue<Character> code = convertToQueue(stringCode);
        Queue<Token> list = new LinkedList<>();
        while (!code.isEmpty()) {
            char current = code.remove();
            if (current == '+' || current == '-' || current == '/' || current == '*') {
                list.add(new Token(TokenType.BINARY_OPERATOR, Character.toString(current)));
            } else if (current == '=') {
                list.add(new Token(TokenType.EQUALS, Character.toString(current)));
            }
            else if (current == ' ' || current == '\n' || current == '\t') {
                // Ignore blank chars

                // Sign that the line ended
                if (current == '\n') {
                    list.add(new Token(TokenType.EOL, "eol"));
                }
                continue;
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
                    while (!code.isEmpty() && Character.isAlphabetic(code.peek())) {
                        stringVar.append(code.remove());
                    }


                    if (!this.reservedKeywords.containsKey(stringVar.toString())) {
                        list.add(new Token(TokenType.IDENTIFIER, stringVar.toString()));
                    } else {
                        // Add reserved Words
                        list.add(new Token(this.reservedKeywords.get(stringVar.toString()), stringVar.toString()));
                    }




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
