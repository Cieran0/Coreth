package parser;

public class TokenVariableDeclaration<T> extends Token{

    private Variable<T> value;

    public TokenVariableDeclaration(String name, Integer lineNo, Integer charNo, Function scope) {
        this.value = new Variable<T>(name);
        this.name = name;
        this.lineNo = lineNo;
        this.charNo = charNo;
        this.type = TokenType.VARIABLE_DECLARATION;
        scope.localVarMap.put(this.name, this.value);
    }

    public TokenVariableDeclaration(String name, Integer lineNo, Integer charNo, Function scope, T value) {
        this.value = new Variable<T>(name,value);
        this.name = name;
        this.lineNo = lineNo;
        this.charNo = charNo;
        this.type = TokenType.VARIABLE_DECLARATION;
        scope.localVarMap.put(this.name, this.value);
    }

    public Variable<T> getVariable() {
        return this.value;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Value: " + this.value);
    }
}
