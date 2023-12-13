package models.ast.types;

public class ASTNode {
    private ASTNodeType type;

    public ASTNode(ASTNodeType type) {
        this.type = type;;
    }

    @Override
    public String toString() {
        return "ASTNode{" +
                "type=" + type +
                '}';
    }
}
