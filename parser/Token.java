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
    private Integer id;

    private Token(TokenType type, String name,  Integer charNo) {
        this.type = type;
        this.name = name;
        this.charNo = charNo;
    }

    public static Token new_ConstantString( Integer charNo, String value) {
        Token t = new Token(TokenType.CONSTANT_STRING,value,charNo);
        t.value = value;
        return t;
    }

    public static Token new_ConstantInteger( Integer charNo, Integer value) {
        Token t = new Token(TokenType.CONSTANT_INTEGER, value.toString(),  charNo);
        t.value = value;
        return t;
    }

    public static Token new_String(String value) {
        Token t = new Token(TokenType.STRING,value,-1);
        t.value = value;
        return t;
    }

    public static Token new_Integer(Integer value) {
        Token t = new Token(TokenType.INTEGER, value.toString(),  -1);
        t.value = value;
        return t;
    }

    public static Token new_Pointer(Integer value) {
        Token t = new Token(TokenType.POINTER, value.toString(),  -1);
        t.value = value;
        return t;
    }


    public static Token new_VariableRefrence(String name,  Integer charNo, Function scope) {
        Token t = new Token(TokenType.VARIABLE_REFRENCE, name,  charNo);
        t.variableName = name;
        t.scope = scope;
        t.variableType = t.scope.localNameVariableTypeMap.get(t.name);
        t.id = t.scope.getVariableNameIndex(name);
        return t;
    }

    public static Token new_VariableRefrence(Variable variable, Function scope) {
        String name = variable.getName();
        Token t = new Token(TokenType.VARIABLE_REFRENCE, name, -1);
        t.variableName = name;
        t.scope = scope;
        t.variableType = t.scope.localNameVariableTypeMap.get(t.name);
        t.id = t.scope.getVariableNameIndex(name);
        return t;
    }

    public static Token new_VariableDeclaration(String name,  Integer charNo, Function scope, VariableType variableType) {
        Token t = new Token(TokenType.VARIABLE_DECLARATION, name,  charNo);
        t.variableName = name;
        t.variableType = variableType;
        t.scope = scope;
        t.scope.localNameVariableTypeMap.put(t.name, t.variableType);
        t.id = t.scope.addNewVariableNameToList(name);
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
            //TODO: not really math but parsed as maths to save time writing code.
            //###################################################################    
            case '$':
                type = TokenType.DEREFERENCE;
                break;
            case '~':
                type = TokenType.REFERENCE;
                break;
            //####################################################################
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

    public static final Set<TokenType> LogicTokens = Set.of(TokenType.AND, TokenType.OR, TokenType.IS_FACTOR, TokenType.EQUAL, TokenType.NOT_EQUAL, TokenType.GREATER, TokenType.LESSER, TokenType.NOT_LESSER, TokenType.NOT_GREATER);

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
            type = TokenType.NOT_EQUAL;
        }
        else if(comparison.equals(">")) {
            type = TokenType.GREATER;
        }
        else if(comparison.equals("<")) {
            type = TokenType.LESSER;
        }
        else if(comparison.equals(">=")) {
            type = TokenType.NOT_LESSER;
        }
        else if(comparison.equals("<=")) {
            type = TokenType.NOT_GREATER;
        } 
        else if (comparison.equals("*?=")) {
            type = TokenType.IS_FACTOR;
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

    public Integer getID() {
        return this.id;
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
        this.scope.declareVariable(new Variable(this.variableName,this.variableType,this.value));
    }

    public Variable getVariable() {
        if(this.type == TokenType.POINTER) 
            return Memory.getVariable((Integer)this.value);
        return Memory.getVariable( scope.getPointer(name));
    }

    public VariableType getVariableType() {
        if(this.type == TokenType.VARIABLE_REFRENCE) return this.scope.getVariable(this.variableName).getType();
        return this.variableType;
    }

    public Function getScope() {
        return this.scope;
    }

    public Integer getInt() {
        if(this.type == TokenType.CONSTANT_INTEGER || this.type == TokenType.INTEGER || this.type == TokenType.POINTER) return (Integer)this.value;
        if(this.type == TokenType.VARIABLE_REFRENCE) return this.getVariable().getIntValue();
        Parser.exitWithError("Expected type " + TokenType.INTEGER + " got type " + this.type, 0);
        return 0;
    }

    public List<List<Token>> getParams() {
        return params;
    }

    public String getString() {
        if(this.type == TokenType.CONSTANT_STRING || this.type == TokenType.STRING) return (String)this.value;
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
            for (List<Token> tokens: blockTokens) {
                for (Token t : tokens) {
                    t.printInfo(indent+4);
                }
            }
            case FUNCTION_CALL:
            print(indent, "Params", "");
            for (List<Token> tk : params) {
                for (Token t : tk) {
                    t.printInfo(indent+4);
                }
            }
            break;
            default:
            break;
        }
        if(indent == 0) System.out.println("-----------------------");
    }

    public static int sizeOfType(TokenType type) {
        switch(type) {
            case CONSTANT_INTEGER:
            case INTEGER:
            case POINTER:
                return 9;
            case CONSTANT_STRING:
            case STRING:
                return 3;
            case VARIABLE_DECLARATION:
            case VARIABLE_REFRENCE:
                return 3;
            default:
                break;
        }
        return 1;
    }

    public int size() {
        int count = sizeOfType(type);
        switch (type) {
            case IF:
            case WHILE:
            for (List<Token> tokens: blockTokens) {
                for (Token t : tokens) {
                    count +=t.size();
                }
            }
            case FUNCTION_CALL:
            for (List<Token> tk : params) {
                for (Token t : tk) {
                    count +=t.size();
                }
            }
            break;
            default:
            break;
        }
        return count;
    }
}
