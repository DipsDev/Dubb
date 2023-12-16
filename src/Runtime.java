import models.ast.*;
import models.ast.interfaces.ASTNode;
import models.runtime.Executable;
import models.runtime.GlobalFunction;
import models.runtime.MemoryStore;

import java.util.HashMap;
import java.util.List;

public class Runtime extends MemoryStore {
    private static Runtime __instance;

    private static HashMap<String, Executable> globalFunctions = new HashMap<>();

    public static Runtime getInstance() {
        if (__instance == null) {
            __instance = new Runtime();
        }
        return __instance;
    }

    /**
     *  Adds a global function to the runtime
     * @param func the function to be added
     */
    private void addGlobalFunction(GlobalFunction func) {
        globalFunctions.put(func.getName(), func);
    }

    /**
     * Adds all global functions declared in the function itself
     */
    public void addGlobalFunctions() {
        GlobalFunction print = new GlobalFunction("print", -1).run((arguments) -> {
            arguments.forEach((d) -> {
                System.out.print(d + " ");
            });
            return null;
        });
        addGlobalFunction(print);

        GlobalFunction pow = new GlobalFunction("pow", 2).run((arguments) -> {
            if (!(arguments.get(0) instanceof Number) || !(arguments.get(1) instanceof Number)) {
                throw new Error("Pow function receives only number values");
            }
            return Math.pow(((Number) arguments.get(0)).doubleValue(), ((Number) arguments.get(1)).doubleValue());
        });
        addGlobalFunction(pow);
    }

    /**
     * Executes the program created by the parser
     * @param program the program to be run, created by the parser
     */
    public void execute(Program program) {
       List<ASTNode> body = program.getBody();
       this.addGlobalFunctions();
       this.resetStores(globalFunctions);
       for (ASTNode nd : body) {
                this.run(nd);
           }
       }

    }

