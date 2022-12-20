package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import parser.Token.TokenType;

public class Function {
    
    private String name;
    private List<TokenType> expectedParams;
    private List<String> paramNames;
    private List<Token> tokens;
    private boolean isbuiltIn;
    private Consumer<List<Token>> linkedBuiltInFunction;

    public static HashMap<String,Function> funcMap = new HashMap<String,Function>();

    public HashMap<String, Variable> localVarMap = new HashMap<String, Variable>();

    public Function(String name) {
        this.name=name;
        this.tokens=null;
        this.isbuiltIn = false;
        this.expectedParams=new ArrayList<Token.TokenType>();
        this.paramNames=new ArrayList<String>();
        funcMap.put(name, this);
    }

    public Function(String name, Consumer<List<Token>> linkedBuiltInFunction, List<Token.TokenType> expectedParams) {
        this.name=name;
        this.isbuiltIn = true;
        this.linkedBuiltInFunction = linkedBuiltInFunction;
        this.expectedParams = expectedParams;
        funcMap.put(name, this);
    }

    public void expectedParamsFromString(String line) {
        //System.out.println(this.name + "(" + line + ")");
        if(line.isBlank()) return;
        for (String param : line.split(",")) {
            String[] split = param.split(" ");
            String typeString = split[0].trim();
            TokenType type = null;
            String name = split[1].trim();
            this.paramNames.add(name);
            if(typeString.equals("int")) {
                type=TokenType.LITERAL_NUM;
                localVarMap.put(name, new Variable<Integer>(name));
            } else if(typeString.equals("string")) {
                type=TokenType.LITERAL_STRING;
                localVarMap.put(name, new Variable<String>(name));
            } else {
                Parser.exitWithError(type + " is not a valid type", 92);
            }
            this.expectedParams.add(type);
        }
    }

    public String getName() {
        return this.name;
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens=tokens;
    }

    public void checkParams(List<Token> got)  {
        if(expectedParams.size() != got.size())
            Parser.exitWithError(("In function, " + this.name + " expected " + expectedParams.size() + " params got " + got.size()) ,-1);
        
        /**
         * TODO: Implement proper type checking
         * for (int i =0; i < expectedParams.size(); i++) {
         *    if(!got.get(i).getType().equals(expectedParams.get(i)))
         *        Parser.exitWithError(("In function, " + this.name + ", for param "+ i+ " expected " + expectedParams.get(i) + " params got " + got.get(i).getType()) ,-1);
         * }
         */
    }

    public void execute(List<Token> params) {
        if(isbuiltIn) {
            checkParams(params);
            linkedBuiltInFunction.accept(params);
        } else {
            checkParams(params);
            for (int i=0; i < paramNames.size(); i++) {
                switch (expectedParams.get(i)) {
                    case LITERAL_NUM:
                        ((Variable<Integer>)localVarMap.get(paramNames.get(i))).setValue(params.get(i).toNumber());
                        break;
                    case LITERAL_STRING:
                        ((Variable<String>)localVarMap.get(paramNames.get(i))).setValue(params.get(i).toStringLit());
                        break;
                    default:
                        break;
                }
            }

            Simulator.SimulateFunction(this);
        }
    }
}
