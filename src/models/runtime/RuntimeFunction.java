package models.runtime;

import models.ast.functions.FunctionCall;
import models.ast.functions.ReturnExpression;
import models.ast.interfaces.Variable;
import models.ast.interfaces.VariableInstance;
import models.ast.types.BinaryExpression;
import models.ast.types.NumericLiteral;
import models.ast.interfaces.ASTNode;
import models.ast.functions.ModifyVariable;

import java.util.HashMap;
import java.util.List;


public class RuntimeFunction extends MemoryStore implements Executable {

    private String name;
    private List<String> argumentNames;
    private List<ASTNode> body;



    public RuntimeFunction(String name, List<String> argumentNames, List<ASTNode> body) {
        super();
        this.name = name;
        this.argumentNames = argumentNames;
        this.body = body;
    }

    private Object functionLoop() {
        List<ASTNode> body = this.body;
        for (ASTNode nd : body) {

            if (nd instanceof ReturnExpression re) {
                return this.evaluateObject(re.getValue());
            }
            else {
                this.run(nd);
            }
        }
        return null;
    }


    public Object execute(List<Object> arguments, HashMap<String, Executable> functionHashMap, HashMap<String, RuntimeVariable> variableHashMap) throws Error {

        this.resetStores(variableHashMap, functionHashMap);

        if (this.argumentNames.size() != arguments.size()) {
            throw new Error("Not enough arguments were supplied");
        }
        // Initialize scope variables
        for (int i = 0; i<this.argumentNames.size(); i++) {
            this.scopeVariables.remove(this.argumentNames.get(i));
            applyVariableChanges(this.argumentNames.get(i), arguments.get(i));
        }
        return this.functionLoop();






    }






}
