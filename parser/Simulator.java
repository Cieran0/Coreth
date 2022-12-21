package parser;

import java.util.List;

public class Simulator {

    public static void SimulateFunction(Function f, int start) {
        List<Token> tokens = f.getTokens();
        for (int i = start; i < tokens.size(); i++) {
            SimulateToken(i, f);
        }
    }

    public static void SimulateToken(Integer index, Function f) {
        List<Token> tokens = f.getTokens();
        Token t = tokens.get(index);
        if(t.getType() == TokenType.FUNCTION_CALL) {
            String fName = t.getName();
            if(Function.funcMap.containsKey(fName)) {
                Function func = Function.funcMap.get(fName);
                List<Token> params = t.getParams();
                func.execute(params);
            } else {
                System.out.println("No builtIn function called ["+fName+"]!");
            }
        } else if(t.getType() == TokenType.VARIABLE_DECLARATION) {
            t.declareVariable();
        } else if(t.getType() == TokenType.VARIABLE_ASSIGNMENT) {
            Variable var = tokens.get(index-1).getVariable();
            Token valueToken = tokens.get(index+1);
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
        }
    }
}
