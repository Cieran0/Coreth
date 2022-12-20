package parser;

import java.util.List;

public class Token {

    protected TokenType type;
    protected String name;
    protected Integer lineNo;
    protected Integer charNo;
    protected String stringValue;
    protected Integer intValue;
    protected Function scope;
    protected Variable variable;
    protected VariableType variableType;
    protected List<Token> params;

    private void setMain(TokenType type, String name, Integer lineNo, Integer charNo) {
        this.type = type;
        this.name = name;
        this.lineNo = lineNo;
        this.charNo = charNo;
    }

    //TokenLiteralString
    public Token(TokenType type, Integer lineNo, Integer charNo, String value) {
        setMain(type, value, lineNo, charNo);
        assert this.type == TokenType.LITERAL_STRING;
        this.stringValue = value;
    }

    //TokenLiteralNum
    public Token(TokenType type, Integer lineNo, Integer charNo, Integer value) {
        setMain(type, value.toString(), lineNo, charNo);
        assert this.type == TokenType.LITERAL_NUM;
        this.intValue = value;
    }

    //TokenVariableRefrence
    public Token(TokenType type, String name, Integer lineNo, Integer charNo, Function scope) {
        setMain(type, name, lineNo, charNo);
        assert this.type == TokenType.VARIABLE_REFRENCE;
        this.scope = scope;
        this.variable = Variable.getVar(name, scope);
    }
    
    //public TokenVariableDeclaration
    public Token(TokenType type, String name, Integer lineNo, Integer charNo, Function scope, VariableType variableType) {
        setMain(type, name, lineNo, charNo);
        assert this.type == TokenType.VARIABLE_DECLARATION;
        this.variable = new Variable(name,variableType);
        scope.localVarMap.put(this.name, this.variable);

    }

    //public TokenVariableDeclaration
    public Token(TokenType type, String name, Integer lineNo, Integer charNo, Function scope, Object value, VariableType variableType) {
        setMain(type, name, lineNo, charNo);
        assert this.type == TokenType.VARIABLE_DECLARATION;
        this.variable = new Variable(name,variableType,value);
        scope.localVarMap.put(this.name, this.variable);
    }

    //public TokenFunctionCall
    public Token(TokenType type, String name, Integer lineNo, Integer charNo, List<Token> params) {
        setMain(type, name, lineNo, charNo);
        assert this.type == TokenType.FUNCTION_CALL;
        this.params = params;
    }

    public String getName() {
        return this.name;
    }

    public TokenType getType() {
        return this.type;
    }

    //Shouldnt always work!
    public Variable getVariable() {
        return this.variable;
    }

    public Integer getInt() {
        if(this.type == TokenType.LITERAL_NUM) return this.intValue;
        return this.variable.getIntValue();
    }

    public List<Token> getParams() {
        return params;
    }

    public String getString() {
        if(this.type == TokenType.LITERAL_STRING) return this.stringValue;
        return this.variable.getStringValue();
    }
    //End of shouldnt always work!

    public void printInfo() {
        System.out.println("Name: "  +this.name);
        System.out.println("Type: "  +this.type);
        System.out.println("Line: "  +this.lineNo);
        System.out.println("Char: "  +this.charNo);
    }
}
