package parser;

import parser.Token.TokenType;

public class Simulator {

    public static void SimulateFunction(Function f) {
        for (Token t : f.getTokens()) {
            t.printInfo();
            if(t.type == TokenType.FUNCTION_CALL) {
                //t.printInfo();
                TokenFunctionCall functionCall = (TokenFunctionCall)t;
                String fName = functionCall.getFunctionName();
                if(Function.funcMap.containsKey(fName)) {
                    Function.funcMap.get(fName).execute(functionCall.getParams());
                } else {
                    System.out.println("No builtIn function called ["+fName+"]!");
                }
            } else {
                if(t.type == TokenType.VARIABLE_DECLARATION) {
                    f.localVarMap.put(t.name,((TokenVariableDeclaration)t).getVariable());
                }
            }
        }
    }
}
