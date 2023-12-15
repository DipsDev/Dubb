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

    public void addGlobalFunctions() {
        GlobalFunction print = new GlobalFunction("print", (arguments) -> {
            System.out.println(arguments);
            return null;
        });
        globalFunctions.put("print", print);
    }

    public void execute(Program program) {
       List<ASTNode> body = program.getBody();
       this.addGlobalFunctions();
       this.resetStores(globalFunctions);
       for (ASTNode nd : body) {
                this.run(nd);
           }
       }

    }

