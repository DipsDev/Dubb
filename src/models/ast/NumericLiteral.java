package models.ast;

import models.ast.types.ASTNode;
import models.ast.types.ASTNodeType;

public class NumericLiteral extends ASTNode {

    private int value;

    public NumericLiteral(int value) {
        super(ASTNodeType.NUMERIC_LITERAL);
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
