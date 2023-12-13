package models.ast;

import models.ast.interfaces.Variable;

public class StringVariable extends Variable<String> {

    public StringVariable(String name, String value) {
        super(name, value);
    }

}
