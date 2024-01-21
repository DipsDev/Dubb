package models.ast.types;

import models.ast.interfaces.ASTNode;
import models.ast.interfaces.ASTNodeType;

public class StringLiteral extends ASTNode {

    private String value;

    public StringLiteral(String value) {
        super(ASTNodeType.STRING_LITERAL);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
