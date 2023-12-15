package models.runtime;

import java.util.HashMap;
import java.util.List;

public interface Executable {
    Object execute(List<Object> arguments, HashMap<String, Executable> functionHashMap, HashMap<String, RuntimeVariable> variableHashMap);
}
