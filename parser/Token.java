package parser;

import java.util.List;

public class Token {

    private TokenType type;
    private String name;
    private Integer lineNo;
    private Integer charNo;
    private String stringValue;
    private Integer intValue;
    private Object objectValue;
    private Function scope;
    private String variableName;
    private VariableType variableType;
    private List<Token> params;

    private Token(TokenType type, String name, Integer lineNo, Integer charNo) {
        this.type = type;
        this.name = name;
        this.lineNo = lineNo;
        this.charNo = charNo;
    }

    //TokenLiteralString
    public static Token new_LiteralString(Integer lineNo, Integer charNo, String value) {
        Token t = new Token(TokenType.LITERAL_STRING,value,lineNo,charNo);
        t.stringValue = value;
        return t;
    }

    //TokenLiteralNum
    public static Token new_LiteralNum(Integer lineNo, Integer charNo, Integer value) {
        Token t = new Token(TokenType.LITERAL_NUM, value.toString(), lineNo, charNo);
        t.intValue = value;
        return t;
    }

    //TokenVariableRefrence
    public static Token new_VariableRefrence(String name, Integer lineNo, Integer charNo, Function scope) {
        Token t = new Token(TokenType.VARIABLE_REFRENCE, name, lineNo, charNo);
        t.variableName = name;
        t.scope = scope;
        t.variableType = t.scope.localNameVariableTypeMap.get(t.name);
        return t;
    }
    
    //public TokenVariableDeclaration(no value)
    public static Token new_VariableDeclaration(String name, Integer lineNo, Integer charNo, Function scope, VariableType variableType) {
        Token t = new Token(TokenType.VARIABLE_DECLARATION, name, lineNo, charNo);
        t.variableName = name;
        t.variableType = variableType;
        t.scope = scope;
        t.scope.localNameVariableTypeMap.put(t.name, t.variableType);
        return t;
    }
    
    //public TokenVariableDeclaration
    public static Token new_VariableDeclaration(String name, Integer lineNo, Integer charNo, Function scope, Object value, VariableType variableType) {
        Token t = new Token(TokenType.VARIABLE_DECLARATION, name, lineNo, charNo);
        t.variableName = name;
        t.variableType = variableType;
        t.scope = scope;
        t.objectValue = value;
        t.scope.localNameVariableTypeMap.put(t.name, t.variableType);
        return t;
    }

    //public TokenFunctionCall
    public static Token new_FunctionCall(String name, Integer lineNo, Integer charNo, List<Token> params) {
        Token t = new Token(TokenType.FUNCTION_CALL, name, lineNo, charNo);
        t.params = params;
        return t;
    }

    public boolean isBefore(Token otherToken) {
        if(this.lineNo < otherToken.getLineNo()) return true;
        if(this.lineNo > otherToken.getLineNo()) return false;
        if(this.charNo < otherToken.getCharNo()) return true;
        return false;
    }

    private Integer getCharNo() {
        return this.charNo;
    }

    private Integer getLineNo() {
        return this.lineNo;
    }

    public String getName() {
        return this.name;
    }

    public TokenType getType() {
        return this.type;
    }

    //Shouldnt always work!
    public void declareVariable() {
        this.scope.localVarMap.put(this.name, new Variable(this.variableName,this.variableType,this.objectValue));
    }

    public Variable getVariable() {
        return Variable.getVar(this.variableName, this.scope);
    }

    public VariableType getVariableType() {
        return this.variableType;
    }

    public Function getScope() {
        return this.scope;
    }

    public Integer getInt() {
        if(this.type == TokenType.LITERAL_NUM) return this.intValue;
        return this.getVariable().getIntValue();
    }

    public List<Token> getParams() {
        return params;
    }

    public String getString() {
        if(this.type == TokenType.LITERAL_STRING) return this.stringValue;
        return this.getVariable().getStringValue();
    }
    //End of shouldnt always work!

    public void printInfo() {
        System.out.println("Name: "  +this.name);
        System.out.println("Type: "  +this.type);
        System.out.println("Line: "  +this.lineNo);
        System.out.println("Char: "  +this.charNo);
    }
}
