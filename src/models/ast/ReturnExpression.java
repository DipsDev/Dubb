package models.ast;

import models.ast.types.ASTNode;
import models.ast.types.ASTNodeType;

public class ReturnExpression  extends ASTNode {
    private ASTNode value;

    public ASTNode getValue() {
        return value;
    }

    public ReturnExpression(ASTNode value) {
        super(ASTNodeType.RETURN);

        this.value = value;

    }
}
