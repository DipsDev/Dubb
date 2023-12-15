package models.runtime;

import models.ast.functions.Function;
import models.ast.functions.FunctionCall;
import models.ast.functions.ModifyVariable;
import models.ast.interfaces.ASTNode;
import models.ast.interfaces.Variable;
import models.ast.interfaces.VariableInstance;
import models.ast.types.BinaryExpression;
import models.ast.types.NumericLiteral;

import java.util.HashMap;
import java.util.List;

public abstract class MemoryStore {
    protected HashMap<String, RuntimeVariable> scopeVariables;

    protected HashMap<String, RuntimeFunction> functionHashMap;

    public MemoryStore() {
    }

    public void resetStores(HashMap<String, RuntimeVariable> scopeVariables, HashMap<String, RuntimeFunction> scopeFunctions) {
        if (this.scopeVariables != null) {
            this.scopeVariables.clear();
            this.functionHashMap.clear();
            this.functionHashMap.putAll(scopeFunctions);
            this.scopeVariables.putAll(scopeVariables);
        } else {
            this.scopeVariables = new HashMap<>(scopeVariables);
            this.functionHashMap = new HashMap<>(scopeFunctions);
        }
    }

    public void resetStores() {
        if (this.scopeVariables != null) {
            this.scopeVariables.clear();
            this.functionHashMap.clear();
        } else {
            this.scopeVariables = new HashMap<>();
            this.functionHashMap = new HashMap<>();
        }


    }

    // The function evaluates an object to it's value
    protected Object evaluateObject(Object arg) {
        if (arg instanceof String) {
            return arg;
        }
        else if (arg instanceof Number) {
            return ((Number) arg).doubleValue();
        }
        else if (arg instanceof BinaryExpression binE) {
            return evaluateBinaryExpression(binE);
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
                functionCall.getArguments().set(i, evaluateObject(args1));
            }
            return function.execute(functionCall.getArguments(), functionHashMap, scopeVariables);
        }
        throw new Error("Not Implemented, got " + arg.getClass() + " " + arg.toString());

    }



    protected void applyVariableChanges(Variable<?> variable) {
        // check if variable already exists
        if (this.scopeVariables.containsKey(variable.getName())) {
            throw new Error("Variable `" + variable.getName() + "` cannot be instantiated twice");
        }


        // convert Variable to runtime variable
        RuntimeVariable runtimeVariable = new RuntimeVariable(variable.getName(), this.evaluateObject(variable.getValue()));
       this.scopeVariables.put(variable.getName(), runtimeVariable);
    }

    protected void applyVariableChanges(String variableName, Object value) {
        // check if variable already exists
        if (this.scopeVariables.containsKey(variableName)) {
            throw new Error("Variable `" + variableName + "` cannot be instantiated twice");
        }


        // convert Variable to runtime variable
        RuntimeVariable runtimeVariable = new RuntimeVariable(variableName, this.evaluateObject(value));
        this.scopeVariables.put(variableName, runtimeVariable);
    }


    private double computeBinaryExpression(BinaryExpression be) {
        switch (be.getOperator()) {
            case '+' -> {
                return evaluateBinaryExpression(be.getLeft()) + evaluateBinaryExpression(be.getRight());
            }
            case '-' -> {
                return evaluateBinaryExpression(be.getLeft()) - evaluateBinaryExpression(be.getRight());
            }
            case '*' -> {
                return evaluateBinaryExpression(be.getLeft()) * evaluateBinaryExpression(be.getRight());
            }
            default -> {
                return evaluateBinaryExpression(be.getLeft()) / evaluateBinaryExpression(be.getRight());
            }
        }

    }

    private double evaluateBinaryExpression(ASTNode node) {
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
                fc.getArguments().set(i, evaluateObject(arg));

            }
            Object functionReturnValue = function.execute(fc.getArguments(), functionHashMap, scopeVariables);
            if (!(functionReturnValue instanceof Number)) {
                throw new Error("Cannot add types other than numbers");
            }
            return ((Number) functionReturnValue).doubleValue();

        }
        return computeBinaryExpression((BinaryExpression) node);
    }

    protected void applyFunctionChanges(Function variable) {
        // check if function already exists
        if (this.functionHashMap.containsKey(variable.getName())) {
            throw new Error("Function `" + variable.getName() + "` cannot be instantiated twice");
        }


        // convert Function to runtime function
        RuntimeFunction runtimeFunction = new RuntimeFunction(variable.getName(), variable.getArguments(), variable.getBody());
        functionHashMap.put(variable.getName(), runtimeFunction);


    }


    protected void run(ASTNode nd) {
        if (nd instanceof BinaryExpression) {
            System.out.println(evaluateBinaryExpression(nd));
        }
        else if (nd instanceof Function) {
            applyFunctionChanges((Function) nd);
        }
        else if (nd instanceof FunctionCall functionCall) {
            if (!functionHashMap.containsKey(functionCall.getName())) {
                throw new Error("Unknown function usage: " + functionCall.getName());
            }
            RuntimeFunction function = functionHashMap.get(functionCall.getName());
            for (int i = 0; i<functionCall.getArguments().size(); i++) {
                Object arg = functionCall.getArguments().get(i);
                if (arg instanceof String) {
                    functionCall.getArguments().set(i, arg);
                }
                else if (arg instanceof BinaryExpression binE) {
                    functionCall.getArguments().set(i, evaluateBinaryExpression(binE));
                }
                else if (arg instanceof NumericLiteral) {
                    functionCall.getArguments().set(i, ((NumericLiteral) arg).getValue());
                }
                else if (arg instanceof VariableInstance vi) {
                    if (!scopeVariables.containsKey(vi.getName())) {
                        throw new Error("Unknown variable usage: " + vi.getName());
                    }

                    // check if the variable is being reused

                    RuntimeVariable var = scopeVariables.get(vi.getName());
                    if (var.getValue() instanceof Number) {
                        functionCall.getArguments().set(i, ((Number) var.getValue()).doubleValue());
                    }
                    else {
                        throw new Error("Variable type isn't supported");
                    }
                }

            }
            function.execute(functionCall.getArguments(), functionHashMap, scopeVariables);
        }


        else if (nd instanceof Variable<?>) {
            applyVariableChanges((Variable<?>) nd);
        }
        else if (nd instanceof ModifyVariable<?> modifyVariable) {
            if (!scopeVariables.containsKey(modifyVariable.getName())) {
                throw new Error("Unknown variable usage: " + modifyVariable.getName());
            }
            RuntimeVariable var = scopeVariables.get(modifyVariable.getName());
            if (var.isConstant()) {
                throw new Error("Variable assignment to constant variable");
            }
            if (modifyVariable.getNewValue() instanceof String) {
                var.setValue(modifyVariable.getNewValue());
            }
            else if (modifyVariable.getNewValue() instanceof BinaryExpression binE) {
                var.setValue(evaluateBinaryExpression(binE));
            }
            else {
                throw new RuntimeException("Unknown type");
            }
        }


        }
    }


