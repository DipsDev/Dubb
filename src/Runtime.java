import models.ast.*;
import models.ast.interfaces.Variable;
import models.ast.interfaces.VariableInstance;
import models.ast.types.ASTNode;
import models.runtime.RuntimeVariable;

import javax.management.RuntimeErrorException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Runtime {
    private static final HashMap<String, RuntimeVariable> hashMap = new HashMap<>();

    private static void applyVariableChanges(Variable<?> variable) {
        // check if variable already exists
        if (hashMap.containsKey(variable.getName())) {
            throw new Error("Variable `" + variable.getName() + "` cannot be instantiated twice");
        }


        // convert Variable to runtime variable

        // if variable value is a number
        if (variable.getValue() instanceof BinaryExpression) {
            RuntimeVariable rtVar = new RuntimeVariable(variable.getName(), traverseBinaryExpression((BinaryExpression) variable.getValue()), variable.isConstant());
            hashMap.put(variable.getName(), rtVar);
            return;
        }
        if (variable.getValue() instanceof  String) {
            RuntimeVariable rtVar = new RuntimeVariable(variable.getName(), variable.getValue(), variable.isConstant());
            hashMap.put(variable.getName(), rtVar);
        }


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

    private static double traverseBinaryExpression(ASTNode node) {
        if (node == null) {
            return 0;
        }

        if (node instanceof NumericLiteral nl) {
            return nl.getValue();
        }

        if (node instanceof VariableInstance vi) {
            if (!hashMap.containsKey(vi.getName())) {
                throw new Error("Unknown variable usage: " + vi.getName());
            }

            // check if the variable is being reused

            RuntimeVariable var = hashMap.get(vi.getName());
            if (var.getValue() instanceof Number) {
                return (double) var.getValue();
            }
            throw new Error("Cannot add strings");
        }
        BinaryExpression be = (BinaryExpression) node;
        return computeBinaryExpression(be);
    }

    public static void printOutput(Program program) {
       List<ASTNode> body = program.getBody();
       for (ASTNode nd : body) {
           if (nd instanceof BinaryExpression) {
               System.out.println(traverseBinaryExpression(nd));
           }
           if (nd instanceof Variable<?>) {
               applyVariableChanges((Variable<?>) nd);
           }
           if (nd instanceof ModifyVariable<?> modifyVariable) {
               if (!hashMap.containsKey(modifyVariable.getName())) {
                   throw new Error("Unknown variable usage: " + modifyVariable.getName());
               }
               RuntimeVariable var = hashMap.get(modifyVariable.getName());
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

