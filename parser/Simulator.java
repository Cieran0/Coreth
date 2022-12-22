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
                Variable var = tokens.get(index-1).getVariable();
                Token valueToken = SimulateToken(index+1, f);
                switch(var.getType()){
                    case INT:
                        var.setValue(valueToken.getInt());
                        break;
                    case STRING:
                        var.setValue(valueToken.getString());
                        break;
                    default:
                        break;
                }
                break;
            case VARIABLE_DECLARATION:
                t.declareVariable();
                break;
            default:
                break;
        }
        return null;
    }
}
