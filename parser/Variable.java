package parser;

public class Variable {

    private String name;
    private Object value;
    private VariableType type;
    private int index;
    private boolean alive;

    public Variable(String name, VariableType type) {
        this.name=name;
        this.value=null;
        this.type=type;
        this.alive=true;
    }

    public Variable(String name, VariableType type, Object value) {
        this.name=name;
        this.value=value;
        this.type = type;
        this.alive=true;
    }


    public Object getValue() {
        return this.value;
    }

    public void kill() {
        this.alive=false;
    }

    public boolean isDead() {
        return !alive;
    }

    public void setIndex(int index) {
        this.index =index;
    }

    public int getIndex() {
        return this.index;
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
