package models.runtime;

import java.util.HashMap;
import java.util.List;

/**
 *The Executable interface should be implemented by a function which can be called at runtime.
 */
public interface Executable {
    /**
     * Executes the global function, and returns its value
     * @param arguments the arguments values
     * @param functionHashMap the functions available at the current scope
     * @param variableHashMap the variables available at the current scope
     * @return the function return value
     */
    Object execute(List<Object> arguments, HashMap<String, Executable> functionHashMap, HashMap<String, RuntimeVariable> variableHashMap);
}
