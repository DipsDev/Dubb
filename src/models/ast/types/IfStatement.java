package models.ast.types;

import models.ast.interfaces.ASTNode;
import models.ast.interfaces.ASTNodeType;

import java.util.ArrayList;
import java.util.List;

public class IfStatement extends ASTNode {

    private List<ASTNode> body;
    private BooleanExpression value;
    public IfStatement(BooleanExpression value) {
        super(ASTNodeType.IDENTIFIER);
        this.value = value;
        this.body = new ArrayList<>();
    }
    public void appendBody(ASTNode b) {
        this.body.add(b);
    }

    public List<ASTNode> getBody() {
        return body;
    }

    public BooleanExpression getValue() {
        return value;
    }
}
