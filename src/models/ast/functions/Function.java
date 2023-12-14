package models.ast.functions;

import models.ast.interfaces.ASTNode;
import models.ast.interfaces.ASTNodeType;

import java.util.ArrayList;
import java.util.List;

public class Function extends ASTNode {

    private String name;
    private final List<String> arguments;

    private final List<ASTNode> body;


    public String getName() {
        return name;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    public Function(String name) {
        super(ASTNodeType.FUNCTION);
        this.name = name;
        this.arguments = new ArrayList<>();
        this.body = new ArrayList<>();
    }

    public void appendArgument(String argument) {
        this.arguments.add(argument);
    }
    public void appendBodyStatement(ASTNode node) {
        this.body.add(node);
    }
}
