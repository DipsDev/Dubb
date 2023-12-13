package models.ast;

import models.ast.interfaces.MathExpression;
import models.ast.types.ASTNode;
import models.ast.types.ASTNodeType;



// Currently supports only Integers
public class BinaryExpression extends ASTNode implements MathExpression {
    private final MathExpression left;
    private final MathExpression right;

    private final char operator;

    public BinaryExpression(MathExpression left, MathExpression right, char operator) {
        super(ASTNodeType.BINARY_EXPR);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public int evaluate() {
            switch (this.operator) {
                case '+' -> {
                    return this.left.evaluate() + this.right.evaluate();
                }
                case '-' -> {
                    return this.left.evaluate() - this.right.evaluate();
                }
                case '/' -> {
                    return this.left.evaluate() / this.right.evaluate();
                }
                case '*' -> {
                    return this.left.evaluate() * this.right.evaluate();
                }
            }
        return 0;
    }
}
