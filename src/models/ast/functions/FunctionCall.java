package models.ast.functions;

import models.ast.interfaces.ASTNode;
import models.ast.interfaces.ASTNodeType;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall extends ASTNode {
    public String getName() {
        return name;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    private String name;
    private List<Object> arguments;
    public FunctionCall(String name) {
        super(ASTNodeType.FUNCTION);
        this.name = name;
        this.arguments = new ArrayList<>();
    }

    /**
     * Appends an argument value to the function call, which will be mapped by order of appendance
     * @param argument the value of the argument
     */
    public void appendArg(Object argument) {
        this.arguments.add(argument);
    }
}
