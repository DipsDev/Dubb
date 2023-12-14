package models.ast.interfaces;

public class VariableInstance extends ASTNode {
    private String name;


    public VariableInstance(String name) {
        super(ASTNodeType.IDENTIFIER);
        this.name = name;
    }

    @Override
    public String toString() {
        return "VariableInstance{" +
                "name='" + name + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }
}
