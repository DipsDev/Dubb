package models.runtime;

import models.ast.functions.Function;
import models.ast.functions.FunctionCall;
import models.ast.functions.ModifyVariable;
import models.ast.interfaces.ASTNode;
import models.ast.interfaces.Variable;
import models.ast.interfaces.VariableInstance;
import models.ast.types.BinaryExpression;
import models.ast.types.BooleanExpression;
import models.ast.types.NumericLiteral;

import java.util.HashMap;


/**
 * This class provides a skeletal body for a runtime that requires a store of variables and functions.
 */
public abstract class MemoryStore {
    protected HashMap<String, RuntimeVariable> scopeVariables;

    protected HashMap<String, Executable> functionHashMap;

    public MemoryStore() {
    }

    /**
     * Resets all stores to an empty hashmap, or copies the given hashmaps if specified
     * @param scopeVariables the variables that are currently at scope
     * @param scopeFunctions the functions that are currently at scope
     */
    public void resetStores(HashMap<String, RuntimeVariable> scopeVariables, HashMap<String, Executable> scopeFunctions) {
        if (this.scopeVariables != null && this.functionHashMap != null) {
            this.scopeVariables.clear();
            this.functionHashMap.clear();
            this.functionHashMap.putAll(scopeFunctions);
            this.scopeVariables.putAll(scopeVariables);
        } else {
            this.scopeVariables = new HashMap<>(scopeVariables);
            this.functionHashMap = new HashMap<>(scopeFunctions);
        }
    }

    /**
     * Resets all stores to an empty hashmap, or copies the given hashmaps if specified
     * @param scopeFunctions the functions that are currently at scope
     */
    public void resetStores(HashMap<String, Executable> scopeFunctions) {
        if (this.scopeVariables != null && this.functionHashMap != null) {
            this.scopeVariables.clear();
            this.functionHashMap.clear();
            this.functionHashMap.putAll(scopeFunctions);
        } else {
            this.scopeVariables = new HashMap<>();
            this.functionHashMap = new HashMap<>(scopeFunctions);
        }

    }


    /**
     * Evaluates a value of an object, could be of type Boolean, String or Number.
     * @param arg the object to be evaluated
     * @return the value of the given object
     */
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
        else if (arg instanceof BooleanExpression boolE) {
            return evaluateBooleanExpression(boolE);

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
            Executable function = functionHashMap.get(functionCall.getName());
            functionCall.getArguments().replaceAll(this::evaluateObject);
            return function.execute(functionCall.getArguments(), functionHashMap, scopeVariables);
        }
        throw new Error("Not Implemented, got " + arg.getClass() + " " + arg.toString());

    }

    /**
     * Evaluates a boolean expression, and returns its value
     * @param node the boolean expression node to be evaluated
     * @return the value of the given boolean expression
     */
    private boolean evaluateBooleanExpression(ASTNode node) {
        if (node == null) {
            return false;
        }

        if (node instanceof NumericLiteral) {
            return true;
        }

        if (node instanceof VariableInstance vi) {
            if (!this.scopeVariables.containsKey(vi.getName())) {
                throw new Error("Unknown variable usage: " + vi.getName());
            }

            // check if the variable is being reused

            RuntimeVariable var = this.scopeVariables.get(vi.getName());
            if (var.getValue() instanceof Number nm) {
                return nm.doubleValue() != 0;
            }
            throw new Error("Not Implemented, got " + var.getValue().getClass());
        }
        if (node instanceof FunctionCall fc) {
            if (!functionHashMap.containsKey(fc.getName())) {
                throw new Error("Unknown function usage: " + fc.getName());
            }
            Executable function = functionHashMap.get(fc.getName());
            fc.getArguments().replaceAll(this::evaluateObject);
            Object functionReturnValue = function.execute(fc.getArguments(), functionHashMap, scopeVariables);
            if (!(functionReturnValue instanceof Number)) {
                throw new Error("Cannot add types other than numbers");
            }
            return ((Number) functionReturnValue).doubleValue() != 0;

        }
        if (node instanceof BinaryExpression) {
            return computeBinaryExpression((BinaryExpression) node) != 0;
        }
        return computeBooleanExpression((BooleanExpression) node);

    }


    /**
     * Computes the next boolean traversal
     * @param node the boolean expression to be traversed on
     * @return the value of the boolean expression
     */
    private boolean computeBooleanExpression(BooleanExpression node) {
        switch (node.getOperator()) {
            case "<=" -> {
                return evaluateBinaryExpression(node.getLeft()) <= evaluateBinaryExpression(node.getRight());
            }
            case ">=" -> {
                return evaluateBinaryExpression(node.getLeft()) >= evaluateBinaryExpression(node.getRight());
            }
            case "!=" -> {
                return evaluateBinaryExpression(node.getLeft()) != evaluateBinaryExpression(node.getRight());
            }
            case ">" -> {
                return evaluateBinaryExpression(node.getLeft()) > evaluateBinaryExpression(node.getRight());
            }
            case "<" -> {
                return evaluateBinaryExpression(node.getLeft()) < evaluateBinaryExpression(node.getRight());
            }
            default -> {
                return evaluateBinaryExpression(node.getLeft()) == evaluateBinaryExpression(node.getRight());
            }
        }


    }


    /**
     * Creates a new runtime variable
     * @param variable the variable to be changed, with its new value
     */
    protected void applyVariableChanges(Variable<?> variable) {
        // check if variable already exists
        if (this.scopeVariables.containsKey(variable.getName())) {
            throw new Error("Variable `" + variable.getName() + "` cannot be instantiated twice");
        }


        // convert Variable to runtime variable
        RuntimeVariable runtimeVariable = new RuntimeVariable(variable.getName(), this.evaluateObject(variable.getValue()), variable.isConstant());
       this.scopeVariables.put(variable.getName(), runtimeVariable);
    }

    /**
     * Creates a new runtime variable
     * @param variableName the variable name to be created
     * @param value the variable value to be set
     */
    protected void applyVariableChanges(String variableName, Object value) {
        // check if variable already exists
        if (this.scopeVariables.containsKey(variableName)) {
            throw new Error("Variable `" + variableName + "` cannot be instantiated twice");
        }


        // convert Variable to runtime variable
        RuntimeVariable runtimeVariable = new RuntimeVariable(variableName, this.evaluateObject(value));
        this.scopeVariables.put(variableName, runtimeVariable);
    }


    /**
     * Computes the next binary traversal
     * @param be the binary expression to be traversed on
     * @return the value of the binary expression
     */
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

    /**
     * Evaluates a binary expression, and returns its value
     * @param node the binary expression node to be evaluated
     * @return the value of the given binary expression
     */
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
            Executable function = functionHashMap.get(fc.getName());
            fc.getArguments().replaceAll(this::evaluateObject);
            Object functionReturnValue = function.execute(fc.getArguments(), functionHashMap, scopeVariables);
            if (!(functionReturnValue instanceof Number)) {
                throw new Error("Cannot add types other than numbers");
            }
            return ((Number) functionReturnValue).doubleValue();

        }
        return computeBinaryExpression((BinaryExpression) node);
    }

    /**
     * Created a new runtime function
     * @param function the function to be created
     */
    protected void applyFunctionChanges(Function function) {
        // check if function already exists
        if (this.functionHashMap.containsKey(function.getName())) {
            throw new Error("Function `" + function.getName() + "` cannot be instantiated twice");
        }


        // convert Function to runtime function
        RuntimeFunction runtimeFunction = new RuntimeFunction(function.getName(), function.getArguments(), function.getBody());
        functionHashMap.put(function.getName(), runtimeFunction);
    }

    /**
     * Computes a function call of a runtime or global function
     * @param functionCall the function call to be computed
     * @return the value of the returned function call
     */
    protected Object resolveFunctionCall(FunctionCall functionCall) {
        if (!functionHashMap.containsKey(functionCall.getName())) {
            throw new Error("Unknown function usage: " + functionCall.getName());
        }

        Executable function = functionHashMap.get(functionCall.getName());
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
            else if (arg instanceof BooleanExpression be) {
                functionCall.getArguments().set(i, evaluateBooleanExpression(be));
            }
            else if (arg instanceof FunctionCall call) {
                functionCall.getArguments().set(i, this.resolveFunctionCall(call));
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
                else if (var.getValue() instanceof Boolean) {
                    functionCall.getArguments().set(i, var.getValue());
                }
                else {
                    throw new Error("Variable type isn't supported, got type " + var.getValue().getClass());
                }
            }

        }
        return function.execute(functionCall.getArguments(), functionHashMap, scopeVariables);
    }


    /**
     * Runs the store main loop and handles changes
     * @param nd current node to traverse on
     */
    protected void run(ASTNode nd) {
        if (nd instanceof Function) {
            applyFunctionChanges((Function) nd);
        }
        else if (nd instanceof FunctionCall functionCall) {
            this.resolveFunctionCall(functionCall);
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



