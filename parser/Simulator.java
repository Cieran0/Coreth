package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Simulator {

    public static void SimulateFunction(Function f, int start) {
        List<Token> tokens = f.getTokens();
        for (int i = start; i < tokens.size(); i++) {
            SimulateToken(i, f,null);
        }
    }

    public static List<Token> SimulateParams(Function f, List<List<Token>> tokenLists) {
        List<Token> params = new ArrayList<Token>();
        for (List<Token> tokens : tokenLists) {
            params.add(SimulateToken(0, f, Optional.of(tokens)));
        }
        return params;
    }

    public static void SimulateBlock(Function f, List<Token> block) {
        for (int i = 0; i < block.size(); i++) {
            SimulateToken(i, f,Optional.of(block));
        }
    }

    public static Token SimulateToken(Integer index, Function f, Optional<List<Token>> givenTokens) {
        List<Token> tokens = f.getTokens();
        if(givenTokens != null) {
            if(givenTokens.isPresent()) tokens = givenTokens.get();
        }
        Token t = tokens.get(index);
        Variable var;
        Token nextToken;
        Token previousToken;
        switch(t.getType()) { 
            case FUNCTION_CALL:
                String fName = t.getName();
                if(Function.funcMap.containsKey(fName)) {
                    Function func = Function.funcMap.get(fName);
                    List<Token> params = SimulateParams(f,t.getParams());
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
                nextToken = SimulateToken(index+1, f,Optional.of(tokens));
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
                previousToken = SimulateToken(index+1, f,Optional.of(tokens));
                nextToken = SimulateToken(index+2, f,Optional.of(tokens));

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
                            default:
                                break;
                        }
                    case STRING:
                        //var.setValue(valueToken.getString());
                        break;
                    default:
                        break;
                }
                break;
            case IF:
                Token condition = SimulateToken(0, f,Optional.of(SimulateParams(f, t.getParams())));
                if(condition.getInt() != 0 ) {
                    SimulateBlock(f, t.getBlockTokens());
                }
                break;
            case NOT:
                nextToken = tokens.get(index+1);
                int value = 0;
                if(Parser.TokenToVariableType(nextToken) == VariableType.STRING) {
                    value = 0;
                } else if(Parser.TokenToVariableType(nextToken) == VariableType.INT){
                    if(nextToken.getInt() == 0) value = 1;
                    else value = 0; 
                } else {
                    value = 1;
                }
                return Token.new_LiteralNum(index, index, value);
            default:
                break;
        }
        return Token.new_NULLToken();
    }
}
