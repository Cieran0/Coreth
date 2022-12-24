package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Function {
    
    private String name;
    private List<VariableType> expectedParams;
    private List<String> paramNames;
    private List<Token> tokens;
    private boolean isbuiltIn;
    private BuiltInFunction linkedBuiltInFunction;
    private VariableType returnType = VariableType.VOID;
    private String content;

    public static HashMap<String,Function> funcMap = new HashMap<String,Function>();

    public HashMap<String, Variable> localVarMap = new HashMap<String, Variable>();
    public HashMap<String, VariableType> localNameVariableTypeMap = new HashMap<String, VariableType>(); 

    public static void setUpBuiltInFunctions() {
        new Function("write", new BuiltInFunction() {
            @Override
            public Token run(List<Token> params) {
                if(params.get(0).getInt() != 0) {
                    Parser.exitWithError("Write's first paramater must be 0, was given " + params.get(0).getInt(),-2);
                }
                System.out.print(params.get(1).getString().replace("\\n","\n"));
                return null;
            }
        },List.of(VariableType.INT, VariableType.STRING));

        new Function("printNumber", new BuiltInFunction() {
            @Override
            public Token run(List<Token> params) {
                System.out.print(params.get(0).getInt());
                return null;
            }
        },List.of(VariableType.INT));
    }

    public Function(String name, String content, String paramString) {
        this.name=name;
        this.tokens=new ArrayList<Token>();
        this.isbuiltIn = false;
        this.expectedParams=new ArrayList<VariableType>();
        this.paramNames=new ArrayList<String>();
        this.content = content;
        this.expectedParamsFromString(paramString);
        funcMap.put(name, this);
    }

    public Function(String name, BuiltInFunction linkedBuiltInFunction, List<VariableType> expectedParams) {
        this.name=name;
        this.isbuiltIn = true;
        this.linkedBuiltInFunction = linkedBuiltInFunction;
        this.expectedParams = expectedParams;
        funcMap.put(name, this);
    }

    public void expectedParamsFromString(String line) {
        //System.out.println(this.name + "(" + line + ")");
        if(line.isBlank()) return;
        int pos = 0;
        for (String param : line.split(",")) {
            String[] split = param.trim().split(" ");
            String typeString = split[0].trim();
            VariableType type = null;
            String name = split[1].trim();
            this.paramNames.add(name);
            if(typeString.equals("int")) {
                type=VariableType.INT;
            } else if(typeString.equals("string")) {
                type=VariableType.STRING;
            } else {
                Parser.exitWithError(typeString + " is not a valid type", 92);
            }
            Token t = Token.new_VariableDeclaration(name,0,0,this,type);
            //System.out.println(tokens);
            tokens.add(pos,t);
            this.expectedParams.add(type);
            pos++;
        }
    }

    public String getName() {
        return this.name;
    }

    public List<Token> getTokens() {
        return this.tokens;
    }

    public VariableType getReturnType() {
        return returnType;
    }

    public boolean isInBuiltFunction() {
        return this.isbuiltIn;
    }

    public void addTokens(List<Token> tokens) {
        this.tokens.addAll(sortTokens(tokens));
    }

    public void addTokensFromContent() {
        this.tokens.addAll(sortTokens(Tokenizer.tokenize(content, this)));
    }

    public static List<Token> sortTokens(List<Token> tokens) {
        Token buff;
        for (int i = 1; i < tokens.size(); i++) {
            int j = i;
            while (j > 0 && !tokens.get(j-1).isBefore(tokens.get(j))){
                buff = tokens.get(j);
                tokens.set(j, tokens.get(j-1));
                tokens.set(j-1, buff);
                j--;
            }
        }
        for(int i = 1; i < tokens.size(); i++) {
            if(Token.MathsTokens.contains(tokens.get(i).getType()) || Token.LogicTokens.contains(tokens.get(i).getType()) ) {
                buff = tokens.get(i);
                tokens.set(i, tokens.get(i-1));
                tokens.set(i-1, buff);
            }
        }
        return tokens;
    }

    public void checkParams(List<Token> got)  {
        if(expectedParams.size() != got.size())
            Parser.exitWithError(("In function, " + this.name + " expected " + expectedParams.size() + " params got " + got.size()) ,-1);
        
        for (int i =0; i < got.size() && i < expectedParams.size(); i++) {
            VariableType gotType = Parser.TokenToVariableType(got.get(i));
           if(!gotType.equals(expectedParams.get(i))) {
               Parser.exitWithError(("In function, " + this.name + ", for param "+ i+ " expected " + expectedParams.get(i) + " params got " + gotType) ,-1);
           }
        }
    }

    public Token execute(List<Token> params) {
        if(isbuiltIn) {
            checkParams(params);
            return linkedBuiltInFunction.run(params);
        } else {
            checkParams(params);
            for (int i=0; i < paramNames.size(); i++) {
                Simulator.SimulateToken(i,this,null);
                switch (expectedParams.get(i)) {
                    case INT:
                        localVarMap.get(paramNames.get(i)).setValue(params.get(i).getInt());
                        break;
                    case STRING:
                        localVarMap.get(paramNames.get(i)).setValue(params.get(i).getString());
                        break;
                    default:
                        break;
                }
            }
            Simulator.SimulateFunction(this,paramNames.size());
        }
        return null;
    }

    public void printTokens() {
        System.out.println("Function: " + name);
        System.out.println("-----------------------");
        for (Token token : tokens) {
            token.printInfo(0);
        }
    }
}
