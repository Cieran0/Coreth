package parser;

import java.util.List;

public class Simulator {

    public static void SimulateFunction(Function f, int start) {
        List<Token> tokens = f.getTokens();
        for (int i = start; i < tokens.size(); i++) {
            SimulateToken(tokens.get(i), f);
        }
    }

    public static void SimulateToken(Token t, Function f) {
        if(t.getType() == TokenType.FUNCTION_CALL) {
            //t.printInfo();
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
        }
    }
}
