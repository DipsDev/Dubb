package models.runtime;

import models.ast.functions.FunctionCall;
import models.ast.functions.ReturnExpression;
import models.ast.interfaces.Variable;
import models.ast.interfaces.VariableInstance;
import models.ast.math.BinaryExpression;
import models.ast.math.NumericLiteral;
import models.ast.interfaces.ASTNode;
import models.ast.variables.ModifyVariable;

import java.util.HashMap;
import java.util.List;


public class RuntimeFunction {

    private String name;
    private List<String> argumentNames;

    private List<ASTNode> body;

    private HashMap<String, RuntimeVariable> scopeVariables;

    private HashMap<String, RuntimeVariable> givenVariables;

    private HashMap<String, RuntimeFunction> functionHashMap;

    public RuntimeFunction(String name, List<String> argumentNames, List<ASTNode> body) {
        this.name = name;
        this.argumentNames = argumentNames;
        this.body = body;
        this.scopeVariables = new HashMap<>();
        this.functionHashMap = new HashMap<>();
    }

    private void clearVariables() {
        this.scopeVariables.clear();
    }



    private void applyVariableChanges(Variable<?> variable) {
        // check if variable already exists
        if (this.scopeVariables.containsKey(variable.getName())) {
            throw new Error("Variable `" + variable.getName() + "` cannot be instantiated twice");
        }


        // convert Variable to runtime variable

        // if variable value is a number
        if (variable.getValue() instanceof BinaryExpression) {
            RuntimeVariable rtVar = new RuntimeVariable(variable.getName(), traverseBinaryExpression((BinaryExpression) variable.getValue()), variable.isConstant());
            this.scopeVariables.put(variable.getName(), rtVar);
            return;
        }
        if (variable.getValue() instanceof  String) {
            RuntimeVariable rtVar = new RuntimeVariable(variable.getName(), variable.getValue(), variable.isConstant());
            this.scopeVariables.put(variable.getName(), rtVar);
        }
    }

    private void applyVariableChanges(String name, Object value) {
        // check if variable already exists
        if (this.scopeVariables.containsKey(name)) {
            throw new Error("Variable `" + name + "` cannot be instantiated twice");
        }


        // convert Variable to runtime variable

        // if variable value is a number
        if (value instanceof Number) {
            RuntimeVariable rtVar = new RuntimeVariable(name, value, false);
            this.scopeVariables.put(name, rtVar);
            return;
        }
        if (value instanceof String) {
            RuntimeVariable rtVar = new RuntimeVariable(name, value, false);
            this.scopeVariables.put(name, rtVar);
        }
    }




    private double computeBinaryExpression(BinaryExpression be) {
        switch (be.getOperator()) {
            case '+' -> {
                return traverseBinaryExpression(be.getLeft()) + traverseBinaryExpression(be.getRight());
            }
            case '-' -> {
                return traverseBinaryExpression(be.getLeft()) - traverseBinaryExpression(be.getRight());
            }
            case '*' -> {
                return traverseBinaryExpression(be.getLeft()) * traverseBinaryExpression(be.getRight());
            }
            default -> {
                return traverseBinaryExpression(be.getLeft()) / traverseBinaryExpression(be.getRight());
            }
        }

    }

    private double traverseBinaryExpression(ASTNode node) {
        if (node == null) {
            return 0;
        }

        if (node instanceof NumericLiteral nl) {
            return nl.getValue();
        }

        if (node instanceof VariableInstance vi) {
            if (!this.scopeVariables.containsKey(vi.getName())) {
                throw new Error("Unknown variable usage: " + vi.getName());
            }

            // check if the variable is being reused

            RuntimeVariable var = this.scopeVariables.get(vi.getName());
            if (var.getValue() instanceof Number nm) {
                return nm.intValue();
            }
            throw new Error("Not Implemented, got " + var.getValue().getClass());
        }
        if (node instanceof FunctionCall fc) {
            if (!functionHashMap.containsKey(fc.getName())) {
                throw new Error("Unknown function usage: " + fc.getName());
            }
            RuntimeFunction function = functionHashMap.get(fc.getName());
            for (int i = 0; i<fc.getArguments().size(); i++) {
                Object arg = fc.getArguments().get(i);
                fc.getArguments().set(i, evaluateArgument(arg));

            }
            function.execute(fc.getArguments(), functionHashMap, scopeVariables);


        }
        BinaryExpression be = (BinaryExpression) node;
        return computeBinaryExpression(be);
    }

    private Object evaluateArgument(Object arg) {
        if (arg instanceof String) {
            return arg;
        }
        else if (arg instanceof BinaryExpression binE) {
            return traverseBinaryExpression(binE);
        }
        else if (arg instanceof NumericLiteral) {
            return ((NumericLiteral) arg).getValue();
        }
        else if (arg instanceof VariableInstance vi) {
            if (!scopeVariables.containsKey(vi.getName())) {
                throw new Error("Unknown variable usage: " + vi.getName());
            }

            // check if the variable is being reused

            RuntimeVariable var = scopeVariables.get(vi.getName());
            if (var.getValue() instanceof Number) {
                return ((Number) var.getValue()).doubleValue();
            }
            else {
                throw new Error("Cannot add strings");
            }
        }
        else if (arg instanceof FunctionCall functionCall) {
            if (!functionHashMap.containsKey(functionCall.getName())) {
                throw new Error("Unknown function usage: " + functionCall.getName());
            }
            RuntimeFunction function = functionHashMap.get(functionCall.getName());
            for (int i = 0; i<functionCall.getArguments().size(); i++) {
                Object args1 = functionCall.getArguments().get(i);
                functionCall.getArguments().set(i, evaluateArgument(args1));
            }
            return function.execute(functionCall.getArguments(), functionHashMap, givenVariables);
        }
        throw new Error("Not Implemented, got " + arg.getClass());

    }

    private Object executeProgram() {
        List<ASTNode> body = this.body;
        for (ASTNode nd : body) {
            if (nd instanceof BinaryExpression) {
                System.out.println(traverseBinaryExpression(nd));
            }
            else if (nd instanceof Variable<?>) {
                applyVariableChanges((Variable<?>) nd);
            }
            else if (nd instanceof ReturnExpression re) {
                return evaluateArgument(re.getValue());
            }

            else if (nd instanceof FunctionCall functionCall) {
                if (!functionHashMap.containsKey(functionCall.getName())) {
                    throw new Error("Unknown function usage: " + functionCall.getName());
                }
                RuntimeFunction function = functionHashMap.get(functionCall.getName());
                for (int i = 0; i<functionCall.getArguments().size(); i++) {
                    Object arg = functionCall.getArguments().get(i);
                    functionCall.getArguments().set(i, evaluateArgument(arg));
                }
                return function.execute(functionCall.getArguments(), functionHashMap, scopeVariables);
            }
            else if (nd instanceof ModifyVariable<?> modifyVariable) {
                if (!this.scopeVariables.containsKey(modifyVariable.getName())) {
                    throw new Error("Unknown variable usage: " + modifyVariable.getName());
                }
                RuntimeVariable var = this.scopeVariables.get(modifyVariable.getName());
                if (var.isConstant()) {
                    throw new Error("Variable assignment to constant variable");
                }
                if (modifyVariable.getNewValue() instanceof String) {
                    var.setValue(modifyVariable.getNewValue());
                }
                else if (modifyVariable.getNewValue() instanceof BinaryExpression binE) {
                    var.setValue(traverseBinaryExpression(binE));
                }
                else {
                    throw new RuntimeException("Unknown type");
                }
            }


        }
        return null;
    }


    public Object execute(List<Object> arguments, HashMap<String, RuntimeFunction> functionHashMap, HashMap<String, RuntimeVariable> variableHashMap) throws Error {
        this.clearVariables();


        this.functionHashMap = (HashMap<String, RuntimeFunction>) functionHashMap.clone();
        this.scopeVariables = (HashMap<String, RuntimeVariable>) variableHashMap.clone();
        this.givenVariables = (HashMap<String, RuntimeVariable>) variableHashMap.clone();


        if (this.argumentNames.size() != arguments.size()) {
            throw new Error("Not enough arguments were supplied");
        }
        // Initialize scope variables
        for (int i = 0; i<this.argumentNames.size(); i++) {
            applyVariableChanges(this.argumentNames.get(i), arguments.get(i));
        }
        return this.executeProgram();






    }






}
