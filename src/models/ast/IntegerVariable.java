package models.ast;

import models.ast.interfaces.Variable;

public class IntegerVariable extends Variable<BinaryExpression> {

    public IntegerVariable(String name, BinaryExpression initialValue) {
        super(name, initialValue);
    }



}
