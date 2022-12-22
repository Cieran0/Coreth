package parser;

import java.util.List;

public class Simulator {

    public static void SimulateFunction(Function f, int start) {
        List<Token> tokens = f.getTokens();
        for (int i = start; i < tokens.size(); i++) {
            SimulateToken(i, f);
        }
    }

    public static Token SimulateToken(Integer index, Function f) {
        List<Token> tokens = f.getTokens();
        Token t = tokens.get(index);
        Variable var;
        Token nextToken;
        Token previousToken;
        switch(t.getType()) { 
            case FUNCTION_CALL:
                String fName = t.getName();
                if(Function.funcMap.containsKey(fName)) {
                    Function func = Function.funcMap.get(fName);
                    List<Token> params = t.getParams();
                    return func.execute(params);
                } else {
                    Parser.exitWithError("No builtIn function called ["+fName+"]!",10);
                }
                break;
            case LITERAL_NUM:
            case LITERAL_STRING:
            case VARIABLE_REFRENCE:
                return t;
            case VARIABLE_ASSIGNMENT:
                previousToken = tokens.get(index-1);
                var = previousToken.getVariable();
                nextToken = SimulateToken(index+1, f);
                switch(var.getType()){
                    case INT:
                        var.setValue(nextToken.getInt());
                        break;
                    case STRING:
                        var.setValue(nextToken.getString());
                        break;
                    default:
                        break;
                }
                break;
            case VARIABLE_DECLARATION:
                t.declareVariable();
                break;
            case PLUS:
            case MINUS:
            case DIVIDE:
            case MULTIPLY:
            case MODULUS:
                previousToken = SimulateToken(index+1, f);
                nextToken = SimulateToken(index+2, f);
                switch(Parser.TokenToVariableType(previousToken)){
                    case INT:
                        switch(t.getType()) {
                            case PLUS:
                                return Token.new_LiteralNum(0,0,previousToken.getInt() + nextToken.getInt());
                            case MINUS:
                                return Token.new_LiteralNum(0,0,previousToken.getInt() - nextToken.getInt());
                            case DIVIDE:
                                return Token.new_LiteralNum(0,0,previousToken.getInt() / nextToken.getInt());
                            case MULTIPLY:
                                return Token.new_LiteralNum(0,0,previousToken.getInt() * nextToken.getInt());
                            case MODULUS:
                                return Token.new_LiteralNum(0,0,previousToken.getInt() % nextToken.getInt());
                        }
                    case STRING:
                        //var.setValue(valueToken.getString());
                        break;
                    default:
                        break;
                }
                break;
            
            default:
                break;
        }
        return null;
    }
}
