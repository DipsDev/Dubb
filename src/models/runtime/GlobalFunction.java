package models.runtime;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

public class GlobalFunction implements Executable {

    private String name;

    private int expectedArguments;

    private Function<List<Object>, Object> function;

    public GlobalFunction(String name, int expectedArguments) {
        this.name = name;
        this.expectedArguments = expectedArguments;
    }

    public String getName() {
        return name;
    }

    public GlobalFunction run(Function<List<Object>, Object> function) {
        this.function = function;
        return this;
    }

    public Function<List<Object>, Object> getFunction() {
        return function;
    }


    @Override
    public Object execute(List<Object> arguments, HashMap<String, Executable> functionHashMap, HashMap<String, RuntimeVariable> variableHashMap) {
        if (this.expectedArguments > 0 && this.expectedArguments != arguments.size()) {
            throw new Error("Not enough arguments were supplied");
        }
        return this.function.apply(arguments);
    }
}
