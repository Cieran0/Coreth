package parser;

public class Variable {

    private String name;
    private Object value;
    private VariableType type;

    public Variable(String name, VariableType type) {
        this.name=name;
        this.value=null;
        this.type=type;
    }

    public Variable(String name, VariableType type, Object value) {
        this.name=name;
        this.value=value;
        this.type = type;
    }

    public static Variable getVar(String name, Function scope) {
        if(!scope.localVarMap.containsKey(name)) {
            System.out.println("variables in function " + scope.getName() + ":");
            for (String key : scope.localVarMap.keySet()) {
                System.out.println(key);
            }
            Parser.exitWithError("variable " + name + " not defined!", 17);
        }
        return scope.localVarMap.get(name);
    }


    public Object getValue() {
        return this.value;
    }

    public String getStringValue(){

        return ((String)this.value);
    }

    public Integer getIntValue(){
        return ((Integer)this.value);
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "| Variable | name: " + name + " | value: " + value + " |";
    }

    public VariableType getType() {
        return this.type;
    }
}   
