package parser;

public class Simulator {

    public static void SimulateFunction(Function f) {
        for (Token t : f.getTokens()) {
            t.printInfo();
            if(t.type == TokenType.FUNCTION_CALL) {
                //t.printInfo();
                String fName = t.getName();
                if(Function.funcMap.containsKey(fName)) {
                    Function.funcMap.get(fName).execute(t.getParams());
                } else {
                    System.out.println("No builtIn function called ["+fName+"]!");
                }
            } else {
                if(t.type == TokenType.VARIABLE_DECLARATION) {
                    f.localVarMap.put(t.name,t.getVariable());
                }
            }
        }
    }
}
