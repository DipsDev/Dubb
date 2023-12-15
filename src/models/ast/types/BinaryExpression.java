package models.ast.types;

import models.ast.interfaces.ASTNode;
import models.ast.interfaces.ASTNodeType;



// Currently supports only Integers
public class BinaryExpression extends ASTNode {
    private final ASTNode left;
    private final ASTNode right;

    private final char operator;

    public BinaryExpression(ASTNode left, ASTNode right, char operator) {
        super(ASTNodeType.BINARY_EXPR);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public ASTNode getLeft() {
        return left;
    }

    public ASTNode getRight() {
        return right;
    }

    public char getOperator() {
        return operator;
    }
}
