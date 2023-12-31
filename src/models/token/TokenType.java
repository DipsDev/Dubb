package models.token;

import java.util.regex.Pattern;

public enum TokenType {

    NUMBER,
    EQUALS,

    IDENTIFIER,

    STRING,

    CONST,
    FUNCTION,

    OPEN_STATEMENT, // {
    CLOSE_STATEMENT, // }

    OPEN_PARAN, // (
    CLOSE_PARAN, // )

    RETURN,
    IF,

    VAR,

    EOL,

    EOF,

    BOOLEAN_OPERATOR,

    BINARY_OPERATOR;

}
