package models.ast.interfaces;

import models.ast.types.ASTNode;
import models.ast.types.ASTNodeType;

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
