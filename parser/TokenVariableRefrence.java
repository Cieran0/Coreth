package parser;

public class TokenVariableRefrence<T> extends Token{

    private Variable<T> value;
    private T vType = null;

    public TokenVariableRefrence(String name, Integer lineNo, Integer charNo, Function scope) {
        this.value = Variable.getVar(name, scope, vType);
        this.name = name;
        this.type = TokenType.VARIABLE_REFRENCE;
    }

    public T getValue() {
        return this.value.getValue();
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Value: " + this.value);
    }
}
