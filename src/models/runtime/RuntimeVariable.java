package models.runtime;

public class RuntimeVariable {
    private String name;
    private Object value;

    public RuntimeVariable(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}

