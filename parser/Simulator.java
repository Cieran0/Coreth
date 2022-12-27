package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Simulator {

    public static void SimulateFunction(Function f, int start) {
        List<List<Token>> lines = f.getTokens();
        for (List<Token> line : lines) {
            SimulateLine(line, f);
        }
    }

    public static List<Token> SimulateParams(Function f, List<List<Token>> tokenLists) {
        List<Token> params = new ArrayList<Token>();
        for (List<Token> line : tokenLists) {
            params.add(SimulateToken(line,0,f));
        }
        return params;
    }

    public static void SimulateBlock(Function f, List<List<Token>> block) {
        for (List<Token> line : block) {
            SimulateLine(line, f);
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
                            case AND:
                                return Token.new_LiteralNum(0,intFromBool(BooleanFromToken(previousToken) && BooleanFromToken(nextToken)));
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
                nextToken = tokens.get(index+1);
                return Token.new_LiteralNum(index, intFromBool(!BooleanFromToken(nextToken)));
            default:
                break;
        }
        return Token.new_NULLToken();
    }

    public static void SimulateLine(List<Token> line, Function scope) {
        //System.out.println("EE");
        //for (Token token : line) {
        //    token.printInfo(0);
        //}
        if(line.size() < 1) return;
        SimulateToken(line, 0, scope);
    }
}
