package models.ast;

import models.ast.interfaces.MathExpression;
import models.ast.types.ASTNode;
import models.ast.types.ASTNodeType;

public class NumericLiteral extends ASTNode implements MathExpression {

    private int value;

    public NumericLiteral(int value) {
        super(ASTNodeType.NUMERIC_LITERAL);
        this.value = value;
    }

    @Override
    public int evaluate() {
        return this.value;
    }
}
