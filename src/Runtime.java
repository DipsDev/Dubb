import models.ast.*;
import models.ast.interfaces.Variable;
import models.ast.interfaces.VariableInstance;
import models.ast.types.ASTNode;
import models.runtime.RuntimeFunction;
import models.runtime.RuntimeVariable;

import java.util.HashMap;
import java.util.List;

public class Runtime {
    private static final HashMap<String, RuntimeVariable> VARIABLE_HASH_MAP = new HashMap<>();
    private static final HashMap<String, RuntimeFunction>  FUNCTION_HASH_MAP = new HashMap<>();

    private static void applyVariableChanges(Variable<?> variable) {
        // check if variable already exists
        if (VARIABLE_HASH_MAP.containsKey(variable.getName())) {
            throw new Error("Variable `" + variable.getName() + "` cannot be instantiated twice");
        }


        // convert Variable to runtime variable

        // if variable value is a number
        if (variable.getValue() instanceof BinaryExpression) {
            RuntimeVariable rtVar = new RuntimeVariable(variable.getName(), traverseBinaryExpression((BinaryExpression) variable.getValue()), variable.isConstant());
            VARIABLE_HASH_MAP.put(variable.getName(), rtVar);
            return;
        }
        if (variable.getValue() instanceof  String) {
            RuntimeVariable rtVar = new RuntimeVariable(variable.getName(), variable.getValue(), variable.isConstant());
            VARIABLE_HASH_MAP.put(variable.getName(), rtVar);
        }
    }


    private static void applyFunctionChanges(Function variable) {
        // check if function already exists
        if (FUNCTION_HASH_MAP.containsKey(variable.getName())) {
            throw new Error("Function `" + variable.getName() + "` cannot be instantiated twice");
        }


        // convert Function to runtime function
        RuntimeFunction runtimeFunction = new RuntimeFunction(variable.getName(), variable.getArguments(), variable.getBody());
        FUNCTION_HASH_MAP.put(variable.getName(), runtimeFunction);


    }



    private static double computeBinaryExpression(BinaryExpression be) {
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

    private static Object evaluateArgument(Object arg) {
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
            if (!VARIABLE_HASH_MAP.containsKey(vi.getName())) {
                throw new Error("Unknown variable usage: " + vi.getName());
            }

            // check if the variable is being reused

            RuntimeVariable var = VARIABLE_HASH_MAP.get(vi.getName());
            if (var.getValue() instanceof Number) {
                return ((Number) var.getValue()).doubleValue();
            }
            else {
                throw new Error("Cannot add strings");
            }
        }
        throw new Error("Not Implemented");

    }

    private static double traverseBinaryExpression(ASTNode node) {
        if (node == null) {
            return 0;
        }

        if (node instanceof NumericLiteral nl) {
            return nl.getValue();
        }

        if (node instanceof VariableInstance vi) {
            if (!VARIABLE_HASH_MAP.containsKey(vi.getName())) {
                throw new Error("Unknown variable usage: " + vi.getName());
            }

            // check if the variable is being reused

            RuntimeVariable var = VARIABLE_HASH_MAP.get(vi.getName());
            if (var.getValue() instanceof Number) {
                return ((Number) var.getValue()).doubleValue();
            }
            throw new Error("Cannot add strings");
        }
        if (node instanceof FunctionCall fc) {
            if (!FUNCTION_HASH_MAP.containsKey(fc.getName())) {
                throw new Error("Unknown function usage: " + fc.getName());
            }
            RuntimeFunction function = FUNCTION_HASH_MAP.get(fc.getName());
            for (int i = 0; i<fc.getArguments().size(); i++) {
                Object arg = fc.getArguments().get(i);
                fc.getArguments().set(i, evaluateArgument(arg));

            }
            return (double) function.execute(fc.getArguments(), FUNCTION_HASH_MAP, VARIABLE_HASH_MAP);

        }
        BinaryExpression be = (BinaryExpression) node;
        return computeBinaryExpression(be);

    }

    public static void execute(Program program) {
       List<ASTNode> body = program.getBody();
       for (ASTNode nd : body) {
           if (nd instanceof BinaryExpression) {
               System.out.println(traverseBinaryExpression(nd));
           }
           else if (nd instanceof Function) {
               applyFunctionChanges((Function) nd);
           }
           else if (nd instanceof FunctionCall functionCall) {
               if (!FUNCTION_HASH_MAP.containsKey(functionCall.getName())) {
                   throw new Error("Unknown function usage: " + functionCall.getName());
               }
               RuntimeFunction function = FUNCTION_HASH_MAP.get(functionCall.getName());
               for (int i = 0; i<functionCall.getArguments().size(); i++) {
                   Object arg = functionCall.getArguments().get(i);
                   if (arg instanceof String) {
                       functionCall.getArguments().set(i, arg);
                   }
                   else if (arg instanceof BinaryExpression binE) {
                       functionCall.getArguments().set(i, traverseBinaryExpression(binE));
                   }
                   else if (arg instanceof NumericLiteral) {
                       functionCall.getArguments().set(i, ((NumericLiteral) arg).getValue());
                   }
                   else if (arg instanceof VariableInstance vi) {
                       if (!VARIABLE_HASH_MAP.containsKey(vi.getName())) {
                           throw new Error("Unknown variable usage: " + vi.getName());
                       }

                       // check if the variable is being reused

                       RuntimeVariable var = VARIABLE_HASH_MAP.get(vi.getName());
                       if (var.getValue() instanceof Number) {
                           functionCall.getArguments().set(i, ((Number) var.getValue()).doubleValue());
                       }
                       else {
                           throw new Error("Cannot add strings");
                       }
                   }

               }
               function.execute(functionCall.getArguments(), FUNCTION_HASH_MAP, VARIABLE_HASH_MAP);
           }


           else if (nd instanceof Variable<?>) {
               applyVariableChanges((Variable<?>) nd);
           }
           else if (nd instanceof ModifyVariable<?> modifyVariable) {
               if (!VARIABLE_HASH_MAP.containsKey(modifyVariable.getName())) {
                   throw new Error("Unknown variable usage: " + modifyVariable.getName());
               }
               RuntimeVariable var = VARIABLE_HASH_MAP.get(modifyVariable.getName());
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
       }

    }

