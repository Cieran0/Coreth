package parser;

public class Variable<T> {

    private String name;
    private T value;

    public Variable(String name) {
        this.name=name;
        this.value=null;
    }

    public Variable(String name, T value) {
        this.name=name;
        this.value=value;
    }

    public static <T> Variable<T> getVar(String name, Function scope, T type) {
        if(!scope.localVarMap.containsKey(name)) {
            System.out.println("variables in function " + scope.getName() + ":");
            for (String key : scope.localVarMap.keySet()) {
                System.out.println(key);
            }
            Parser.exitWithError("variable " + name + " not defined!", 17);
        }
        return scope.localVarMap.get(name);
    }


    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "| Variable | name: " + name + " | value: " + value + " |";
    }
}   
