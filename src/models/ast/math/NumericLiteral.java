package models.ast.math;

import models.ast.interfaces.ASTNode;
import models.ast.interfaces.ASTNodeType;

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
