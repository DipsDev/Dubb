import models.ast.BinaryExpression;
import models.ast.IntegerVariable;
import models.ast.NumericLiteral;
import models.ast.Program;
import models.ast.interfaces.Variable;
import models.ast.interfaces.VariableInstance;
import models.ast.types.ASTNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Runtime {
    private static final HashMap<String, Variable<?>> hashMap = new HashMap<>();

    private static void initVariable(Variable<?> variable) {
        hashMap.put(variable.getName(), variable);
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
            Variable<?> var = hashMap.get(vi.getName());
            if (var.getValue() instanceof BinaryExpression be) {
                return traverseBinaryExpression(be.getLeft()) + traverseBinaryExpression(be.getRight());
            }
            throw new Error("Cannot add strings");
        }
        BinaryExpression expression = (BinaryExpression) node;
        switch (expression.getOperator()) {
            case '+':
                return traverseBinaryExpression(expression.getLeft()) + traverseBinaryExpression(expression.getRight());
        }
        return 0;
    }

    public static void printOutput(Program program) {
       List<ASTNode> body = program.getBody();
       for (ASTNode nd : body) {
           System.out.println(nd);
           if (nd instanceof BinaryExpression) {
               System.out.println(traverseBinaryExpression(nd));
           }
           if (nd instanceof Variable<?>) {

               initVariable((Variable<?>) nd);
               Variable<?> x = hashMap.get("x");
               if (x instanceof IntegerVariable tv) {
                   System.out.println(tv.getValue());

               }

           }
       }

    }
}
