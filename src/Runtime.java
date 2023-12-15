import models.ast.*;
import models.ast.interfaces.ASTNode;
import models.runtime.MemoryStore;

import java.util.List;

public class Runtime extends MemoryStore {
    private static Runtime __instance;

    public static Runtime getInstance() {
        if (__instance == null) {
            __instance = new Runtime();
        }
        return __instance;
    }

    public void execute(Program program) {
       List<ASTNode> body = program.getBody();
       this.resetStores();
       for (ASTNode nd : body) {
                this.run(nd);
           }
       }

    }

