package models.ast.variables;

import models.ast.interfaces.Variable;

public class UnknownVariable extends Variable<Object> {

    public UnknownVariable(String name, Object initialValue) {
        super(name, initialValue);
    }
}