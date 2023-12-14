package models.ast.interfaces;

public class Variable<T> extends ASTNode {

    private final String name;

    private T value;
    private boolean isConstant;

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

    public Variable<T> setConstant(boolean state) {
        this.isConstant = state;
        return this;
    }


    public void setValue(T other) {
        this.value = other;
    }

    public T getValue() {
        return value;
    }

}
