package models.runtime;

public class RuntimeVariable {
    private String name;
    private Object value;

    private boolean constant;

    public RuntimeVariable(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public RuntimeVariable(String name, Object value, boolean constant) {
        this.name = name;
        this.value = value;
        this.constant = constant;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isConstant() {
        return constant;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}

