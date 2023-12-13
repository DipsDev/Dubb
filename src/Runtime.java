import models.ast.BinaryExpression;
import models.ast.Program;
import models.ast.types.ASTNode;

import java.util.List;

public class Runtime {

    private static int evalBinaryExpression(BinaryExpression binaryExpression) {
        return binaryExpression.evaluate();
    }

    public static void printOutput(Program program) {
       List<ASTNode> body = program.getBody();
       for (ASTNode nd : body) {
           if (nd instanceof BinaryExpression) {
               System.out.print(evalBinaryExpression((BinaryExpression) nd));
           }
       }

    }
}
