package models.token;

public class Token {
    private TokenType type;
    private String value;


    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }

    public String getValue() {
        return value;
    }

    public TokenType getType() {
        return type;
    }
}
