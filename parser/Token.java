package parser;

import java.util.List;
import java.util.Set;

public class Token {

    private TokenType type;
    private String name;
    private Integer charNo;
    private Object value;
    private Function scope;
    private String variableName;
    private VariableType variableType;
    private List<List<Token>> params;
    private List<List<Token>> blockTokens;

    private Token(TokenType type, String name,  Integer charNo) {
        this.type = type;
        this.name = name;
        this.charNo = charNo;
    }

    public static Token new_LiteralString( Integer charNo, String value) {
        Token t = new Token(TokenType.LITERAL_STRING,value,charNo);
        t.value = value;
        return t;
    }

    public static Token new_LiteralNum( Integer charNo, Integer value) {
        Token t = new Token(TokenType.LITERAL_NUM, value.toString(),  charNo);
        t.value = value;
        return t;
    }

    public static Token new_VariableRefrence(String name,  Integer charNo, Function scope) {
        Token t = new Token(TokenType.VARIABLE_REFRENCE, name,  charNo);
        t.variableName = name;
        t.scope = scope;
        t.variableType = t.scope.localNameVariableTypeMap.get(t.name);
        return t;
    }

    public static Token new_VariableDeclaration(String name,  Integer charNo, Function scope, VariableType variableType) {
        Token t = new Token(TokenType.VARIABLE_DECLARATION, name,  charNo);
        t.variableName = name;
        t.variableType = variableType;
        t.scope = scope;
        t.scope.localNameVariableTypeMap.put(t.name, t.variableType);
        return t;
    }

    public static Token new_FunctionCall(String name,  Integer charNo, List<List<Token>> params, Function scope) {
        Token t = new Token(TokenType.FUNCTION_CALL, name,  charNo);
        t.params = params;
        return t;
    }

    public static Token new_VariableAssignment( Integer charNo) {
        Token t = new Token(TokenType.VARIABLE_ASSIGNMENT, "Variable Assignment",  charNo);
        return t;
    }

    public static final Set<TokenType> MathsTokens = Set.of(TokenType.PLUS,TokenType.MINUS,TokenType.MULTIPLY,TokenType.DIVIDE,TokenType.MODULUS);        

    public static Token new_Maths( Integer charNo, Character function) {
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
                break;
            case '%':
                type = TokenType.MODULUS;
                break;
            default:
                Parser.exitWithError(function + " is not a valid maths function", 0);
                break;
        }
        Token t = new Token(type, function.toString(),  charNo);
        return t;
    }

    public static Token new_If( Integer charNo, List<Token> params, List<List<Token>> ifTokens) {
        Token t = new Token(TokenType.IF, "IF",  charNo);
        t.params=List.of(params);
        t.blockTokens = ifTokens;
        return t;
    }

    public static Token new_While( Integer charNo, List<Token> params, List<List<Token>> whileTokens) {
        Token t = new Token(TokenType.WHILE, "WHILE",  charNo);
        t.params=List.of(params);
        t.blockTokens = whileTokens;
        return t;
    }

    public static Token new_Not( Integer charNo) {
        Token t = new Token(TokenType.NOT, "!",  charNo);
        return t;
    }

    public static final Set<TokenType> LogicTokens = Set.of(TokenType.AND, TokenType.OR, TokenType.ISFACTOR, TokenType.EQUAL, TokenType.NOTEQUAL, TokenType.GREATER, TokenType.LESSER, TokenType.NOTLESSER, TokenType.NOTGREATER);

    public static Token new_And( Integer charNo) {
        Token t = new Token(TokenType.AND, "&&",  charNo);
        return t;
    }

    public static Token new_Or( Integer charNo) {
        Token t = new Token(TokenType.OR, "||",  charNo);
        return t;
    }

    public static Token new_Comparison(Integer charNo, String comparison) { 
        TokenType type = null;
        if(comparison.equals("==")) {
            type = TokenType.EQUAL;
        }
        else if(comparison.equals("!=")) {
            type = TokenType.NOTEQUAL;
        }
        else if(comparison.equals(">")) {
            type = TokenType.GREATER;
        }
        else if(comparison.equals("<")) {
            type = TokenType.LESSER;
        }
        else if(comparison.equals(">=")) {
            type = TokenType.NOTLESSER;
        }
        else if(comparison.equals("<=")) {
            type = TokenType.NOTGREATER;
        } 
        else if (comparison.equals("*?=")) {
            type = TokenType.ISFACTOR;
        }
        else {
            Parser.exitWithError(comparison + " is not a valid comparison function", 0);
        }
        Token t = new Token(type, comparison,  charNo);
        return t;
    }

    public static Token new_Return(Integer charNo) {
        Token t = new Token(TokenType.RETURN, "return", charNo);
        return t;
    }

    public static Token new_NULLToken() {
        Token t = new Token(TokenType.NULL, "Empty Token", -1);
        return t;
    }

    public boolean isBefore(Token otherToken) {
        if(this.charNo < otherToken.getCharNo()) return true;
        return false;
    }

    public Integer getCharNo() {
        return this.charNo;
    }


    public String getName() {
        return this.name;
    }

    public TokenType getType() {
        return this.type;
    }

    public List<List<Token>> getBlockTokens() {
        return blockTokens;
    }

    //Shouldnt always work!
    public void declareVariable() {
        this.scope.localVarMap.put(this.name, new Variable(this.variableName,this.variableType,this.value));
    }

    public Variable getVariable() {
        return Variable.getVar(this.variableName, this.scope);
    }

    public VariableType getVariableType() {
        if(this.type == TokenType.VARIABLE_REFRENCE) return Variable.getVar(this.variableName, this.scope).getType();
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

    public List<List<Token>> getParams() {
        return params;
    }

    public String getString() {
        if(this.type == TokenType.LITERAL_STRING) return (String)this.value;
        return this.getVariable().getStringValue();
    }
    //End of shouldnt always work!

    private void print(Integer indent, String name, Object value) {
        System.out.println(" ".repeat(indent) + name + ": "  +value);
    }

    public void printInfo(Integer indent) {
        print(indent,"Name",name);
        print(indent,"Type",type);
        print(indent,"Char",charNo);
        switch (type) {
            case IF:
            case WHILE:
            print(indent, "Block Tokens", "");
            //for (Token t : blockTokens) {
            //    t.printInfo(indent+4);
            //};
            case FUNCTION_CALL:
            print(indent, "Params", "");
            for (List<Token> tk : params) {
                for (Token t : tk) {
                    t.printInfo(indent+4);
                }
            };
            break;
            default:
            break;
        }
        if(indent == 0) System.out.println("-----------------------");
    }
}
