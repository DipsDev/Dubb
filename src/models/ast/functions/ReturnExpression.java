package models.ast.functions;

import models.ast.interfaces.ASTNode;
import models.ast.interfaces.ASTNodeType;

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
