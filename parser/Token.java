package parser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Token {

    private TokenType type;
    private String name;
    private Integer lineNo;
    private Integer charNo;
    private Object value;
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

    public static Token new_LiteralString(Integer lineNo, Integer charNo, String value) {
        Token t = new Token(TokenType.LITERAL_STRING,value,lineNo,charNo);
        t.value = value;
        return t;
    }

    public static Token new_LiteralNum(Integer lineNo, Integer charNo, Integer value) {
        Token t = new Token(TokenType.LITERAL_NUM, value.toString(), lineNo, charNo);
        t.value = value;
        return t;
    }

    public static Token new_VariableRefrence(String name, Integer lineNo, Integer charNo, Function scope) {
        Token t = new Token(TokenType.VARIABLE_REFRENCE, name, lineNo, charNo);
        t.variableName = name;
        t.scope = scope;
        t.variableType = t.scope.localNameVariableTypeMap.get(t.name);
        return t;
    }

    public static Token new_VariableDeclaration(String name, Integer lineNo, Integer charNo, Function scope, VariableType variableType) {
        Token t = new Token(TokenType.VARIABLE_DECLARATION, name, lineNo, charNo);
        t.variableName = name;
        t.variableType = variableType;
        t.scope = scope;
        t.scope.localNameVariableTypeMap.put(t.name, t.variableType);
        return t;
    }

    public static Token new_FunctionCall(String name, Integer lineNo, Integer charNo, List<Token> params) {
        Token t = new Token(TokenType.FUNCTION_CALL, name, lineNo, charNo);
        t.params = params;
        return t;
    }

    public static Token new_VariableAssignment(Integer lineNo, Integer charNo) {
        Token t = new Token(TokenType.VARIABLE_ASSIGNMENT, "Variable Assignment", lineNo, charNo);
        return t;
    }

    public static final Set<TokenType> MathsTokens = Set.of(TokenType.PLUS,TokenType.MINUS,TokenType.MULTIPLY,TokenType.DIVIDE,TokenType.MODULUS);
        

    public static Token new_Maths(Integer lineNo, Integer charNo, Character function) {
        TokenType type = null;
        switch (function) {
            case '+':
                type = TokenType.PLUS;
                break;
            case '-':
                type = TokenType.MINUS;
                break;
            case '*':
                type = TokenType.MULTIPLY;
                break;
            case '\\':
                type = TokenType.DIVIDE;
            case '%':
                type = TokenType.MODULUS;
            default:
                Parser.exitWithError(function + " is not a valid maths function", 0);
                break;
        }
        Token t = new Token(type, function.toString(), lineNo, charNo);
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
        this.scope.localVarMap.put(this.name, new Variable(this.variableName,this.variableType,this.value));
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
        if(this.type == TokenType.LITERAL_NUM) return (Integer)this.value;
        if(this.type == TokenType.VARIABLE_REFRENCE) return this.getVariable().getIntValue();
        Parser.exitWithError("Expected type " + TokenType.LITERAL_NUM + " got type " + this.type, 0);
        return 0;
    }

    public List<Token> getParams() {
        return params;
    }

    public String getString() {
        if(this.type == TokenType.LITERAL_STRING) return (String)this.value;
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
