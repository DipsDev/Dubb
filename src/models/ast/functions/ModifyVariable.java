package models.ast.functions;

import models.ast.interfaces.ASTNode;
import models.ast.interfaces.ASTNodeType;

public class ModifyVariable<T> extends ASTNode {

    private String name;
    private T newValue;
    public ModifyVariable(String name, T newValue) {
        super(ASTNodeType.IDENTIFIER);
        this.name = name;
        this.newValue = newValue;
    }

    public String getName() {
        return name;
    }

    public T getNewValue() {
        return newValue;
    }
}
