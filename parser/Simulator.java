package parser;

import java.util.ArrayList;
import java.util.List;

public class Simulator {

    private static boolean exitFunction = false;

    public static Token SimulateLine(List<Token> line, Function scope) {
        if(line.size() < 1) return Token.new_NULLToken();
        return SimulateToken(line, 0, scope);
    }

    public static Token SimulateFunction(Function f, int start) {
        exitFunction = false;
        List<List<Token>> lines = f.getTokens();
        Token returnToken = null;
        for (List<Token> line : lines) {
            returnToken = SimulateLine(line,f);
            if(exitFunction) break;
        }
        return returnToken;
    }

    public static List<Token> SimulateParams(Function f, List<List<Token>> tokenLists) {
        List<Token> params = new ArrayList<Token>();
        for (List<Token> line : tokenLists) {
            if(line.size() < 1) continue;
            params.add(SimulateToken(line,0,f));
        }
        return params;
    }

    public static void SimulateBlock(Function f, List<List<Token>> block) {
        for (List<Token> line : block) {
            SimulateLine(line, f);
            if(exitFunction) break;
            Parser.line++;
        }
    }

    public static Integer intFromBool(boolean bool) {
        return bool? 1 : 0;
    }

    public static boolean BooleanFromToken(Token t) {
        if(Parser.TokenToVariableType(t) == VariableType.STRING) {
            return true;
        } else if(Parser.TokenToVariableType(t) == VariableType.INT){
            if(t.getInt() == 0) return false;
            return true;
        }
        return false;
    }

    public static Token SimulateToken(List<Token> tokens, Integer index, Function scope) {
        Token t = tokens.get(index);
        Variable var;
        Token nextToken;
        Token previousToken;
        switch(t.getType()) { 
            case FUNCTION_CALL:
                String fName = t.getName();
                if(Function.funcMap.containsKey(fName)) {
                    Function func = Function.funcMap.get(fName);
                    List<Token> params = SimulateParams(scope,t.getParams());
                    return func.execute(params);
                } else {
                    Parser.exitWithError("No builtIn function called ["+fName+"]!",10);
                }
                break;
            case LITERAL_NUM:
            case LITERAL_STRING:
                return t;
            case VARIABLE_ASSIGNMENT:
                break;
            case VARIABLE_DECLARATION:
                t.declareVariable();
            case VARIABLE_REFRENCE:
                if(tokens.size() <= index+1) return t;
                nextToken = tokens.get(index+1);
                if(nextToken.getType() == TokenType.VARIABLE_ASSIGNMENT) {
                    var = t.getVariable();
                    nextToken = SimulateToken(tokens, index+2, scope);
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
                } else {
                    return t;
                }
                return t;
            case PLUS:
            case MINUS:
            case DIVIDE:
            case MULTIPLY:
            case MODULUS:
            case AND:
            case OR:
            case ISFACTOR:
            case EQUAL:
            case NOTEQUAL:
            case GREATER:
            case LESSER:
            case NOTLESSER:
            case NOTGREATER:
                previousToken = SimulateToken(tokens,index+1,scope);
                nextToken = SimulateToken(tokens,index+2,scope);

                switch(Parser.TokenToVariableType(previousToken)){
                    case INT:
                        switch(t.getType()) {
                            case PLUS:
                                return Token.new_LiteralNum(0,previousToken.getInt() + nextToken.getInt());
                            case MINUS:
                                return Token.new_LiteralNum(0,previousToken.getInt() - nextToken.getInt());
                            case DIVIDE:
                                return Token.new_LiteralNum(0,previousToken.getInt() / nextToken.getInt());
                            case MULTIPLY:
                                return Token.new_LiteralNum(0,previousToken.getInt() * nextToken.getInt());
                            case MODULUS:
                                return Token.new_LiteralNum(0,previousToken.getInt() % nextToken.getInt());
                            case ISFACTOR:
                                return Token.new_LiteralNum(0,intFromBool(nextToken.getInt() % previousToken.getInt() == 0));
                            case AND:
                                return Token.new_LiteralNum(0,intFromBool(BooleanFromToken(previousToken) && BooleanFromToken(nextToken)));
                            case OR:
                                return Token.new_LiteralNum(0,intFromBool(BooleanFromToken(previousToken) || BooleanFromToken(nextToken)));
                            case EQUAL:
                                return Token.new_LiteralNum(0,intFromBool(previousToken.getInt() == nextToken.getInt()));
                            case NOTEQUAL:
                                return Token.new_LiteralNum(0,intFromBool(previousToken.getInt() != nextToken.getInt()));
                            case GREATER:
                                return Token.new_LiteralNum(0,intFromBool(previousToken.getInt() > nextToken.getInt()));
                            case LESSER:
                                return Token.new_LiteralNum(0,intFromBool(previousToken.getInt() < nextToken.getInt()));
                            case NOTLESSER:
                                return Token.new_LiteralNum(0,intFromBool(previousToken.getInt() >= nextToken.getInt()));
                            case NOTGREATER:
                                return Token.new_LiteralNum(0,intFromBool(previousToken.getInt() <= nextToken.getInt()));
                            default:
                                break;
                        }
                        break;
                    case STRING:
                        switch(t.getType()) {
                            case PLUS:
                                return Token.new_LiteralString(0, previousToken.getString() + nextToken.getString());
                            case EQUAL:
                                return Token.new_LiteralNum(0,intFromBool(previousToken.getString().equals(nextToken.getString())));
                            default:
                                break;
                        }
                    default:
                        break;
                }
                Parser.exitWithError("Invalid Operation on type " + Parser.TokenToVariableType(previousToken), -77);
                break;
            case IF:
                nextToken = SimulateParams(scope, t.getParams()).get(0);
                if(nextToken.getInt() != 0 ) {
                    SimulateBlock(scope, t.getBlockTokens());
                }
                break;
            case WHILE:
                nextToken = SimulateParams(scope, t.getParams()).get(0);
                while(nextToken.getInt() != 0 ) {
                    SimulateBlock(scope, t.getBlockTokens());
                    nextToken = SimulateParams(scope, t.getParams()).get(0);
                }
                break;
            case NOT:
                nextToken = SimulateToken(tokens,index+1,scope);
                return Token.new_LiteralNum(index, intFromBool(!BooleanFromToken(nextToken)));
            case RETURN:
                exitFunction = true;
                if(scope.getReturnType() == VariableType.VOID) return Token.new_NULLToken();
                return SimulateToken(tokens,index+1,scope);
            default:
                break;
        }
        return Token.new_NULLToken();
    }
}
