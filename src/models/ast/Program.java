package models.ast;

import models.ast.types.ASTNode;
import models.ast.types.ASTNodeType;

import java.util.ArrayList;
import java.util.List;

public class Program extends ASTNode {
    private final List<ASTNode> body;

    public Program() {
        super(ASTNodeType.PROGRAM);
        this.body = new ArrayList<>();
    }

    public void append(ASTNode node) {
        this.body.add(node);

    }

    public List<ASTNode> getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ASTNode nd : this.body) {
            stringBuilder.append(nd);
        }
        return stringBuilder.toString();
    }
}
