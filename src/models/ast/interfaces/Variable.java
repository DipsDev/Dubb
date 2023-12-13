package models.ast.interfaces;

import models.ast.types.ASTNode;
import models.ast.types.ASTNodeType;

public abstract class Variable<T> extends ASTNode {

    private final String name;

    private T value;
    private final boolean isConstant;

    public Variable(String name, T initialValue) {
        super(ASTNodeType.IDENTIFIER);
        this.name = name;
        this.isConstant = false;
        this.value = initialValue;
    }

    public String getName() {
        return name;
    }

    public boolean isConstant() {
        return isConstant;
    }


    public void setValue(T other) {
        this.value = other;
    }

    public T getValue() {
        return value;
    }

}
