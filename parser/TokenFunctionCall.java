package parser;

import java.util.List;

public class TokenFunctionCall extends Token{

    private String functionName;
    private List<Token> params;

    public TokenFunctionCall(String functionName, Integer lineNo, Integer charNo, List<Token> params) {
        this.functionName = functionName;
        this.name = functionName;
        this.lineNo = lineNo;
        this.charNo = charNo;
        this.type = TokenType.FUNCTION_CALL;
        this.params = params;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<Token> getParams() {
        return params;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Function: " + this.functionName);
        System.out.println("Params:");
        System.out.println("---------------------");
        for (Token param : params) {
            param.printInfo();
        }
        System.out.println("---------------------");
    }
}
