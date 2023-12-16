package models.ast.types;

import models.ast.interfaces.ASTNode;
import models.ast.interfaces.ASTNodeType;

public class BooleanExpression extends ASTNode {

    private ASTNode left;
    private ASTNode right;
    private String operator;
    public BooleanExpression(ASTNode left, ASTNode right, String operator) {
        super(ASTNodeType.BOOLEAN_EXPR);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }



    public ASTNode getRight() {
        return right;
    }

    public ASTNode getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }
}
