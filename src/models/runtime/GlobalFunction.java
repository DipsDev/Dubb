package models.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class GlobalFunction implements Executable {

    private String name;
    private Function<List<Object>, Object> function;

    public GlobalFunction(String name, Function<List<Object>, Object> function) {
        this.name = name;
        this.function = function;
    }

    @Override
    public Object execute(List<Object> arguments, HashMap<String, Executable> functionHashMap, HashMap<String, RuntimeVariable> variableHashMap) {
        return this.function.apply(arguments);
    }
}
