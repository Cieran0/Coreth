package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Function {
    
    private String name;
    private List<VariableType> expectedParams;
    private List<String> paramNames;
    private List<List<Token>> tokens;
    private boolean isbuiltIn;
    private BuiltInFunction linkedBuiltInFunction;
    private VariableType returnType = VariableType.VOID;
    private String content;
    public static List<String> stringPointers = new ArrayList<String>();
    
    public static HashMap<String,Function> funcMap = new HashMap<String,Function>();

    public HashMap<String, Variable> localVarMap = new HashMap<String, Variable>();
    public HashMap<String, VariableType> localNameVariableTypeMap = new HashMap<String, VariableType>(); 

    public static Scanner sc = new Scanner(System.in);

    public static void setUpBuiltInFunctions() {

        new Function("printNumber", new BuiltInFunction() {
            @Override
            public Token run(List<Token> params) {
                System.out.print(params.get(0).getInt());
                return Token.new_NULLToken();
            }
        },VariableType.VOID,List.of(VariableType.INT));

        new Function("readLine", new BuiltInFunction() {
            @Override
            public Token run(List<Token> params) {
                return Token.new_LiteralString(-1, sc.nextLine());
            }
        },VariableType.STRING,List.of());

        new Function("pointerFromString", new BuiltInFunction() {
            @Override
            public Token run(List<Token> params) {
                int ptr = stringPointers.indexOf(params.get(0).getString());
                if(ptr < 0) {
                    ptr = stringPointers.size();
                    stringPointers.add(params.get(0).getString());
                }
                return Token.new_LiteralNum(-1, ptr);
            }
        }, VariableType.INT, List.of(VariableType.STRING));


        new Function("stringFromPointer", new BuiltInFunction() {
            @Override
            public Token run(List<Token> params) {
                return Token.new_LiteralString(-1, stringPointers.get(params.get(0).getInt()));
            }
        }, VariableType.STRING, List.of(VariableType.INT));

        new Function("strLen", new BuiltInFunction() {
            @Override
            public Token run(List<Token> params) {
                return Token.new_LiteralNum(-1, params.get(0).getString().length());
            }
        }, VariableType.INT, List.of(VariableType.STRING));

        new Function("syscallTwo", Syscalls.syscall, VariableType.INT,List.of(VariableType.INT,VariableType.INT));
        new Function("syscallThree", Syscalls.syscall, VariableType.INT,List.of(VariableType.INT,VariableType.INT,VariableType.INT));
        new Function("syscallFour", Syscalls.syscall, VariableType.INT,List.of(VariableType.INT,VariableType.INT,VariableType.INT,VariableType.INT));
        new Function("syscallFive", Syscalls.syscall, VariableType.INT,List.of(VariableType.INT,VariableType.INT,VariableType.INT,VariableType.INT,VariableType.INT));
        new Function("syscallSix", Syscalls.syscall, VariableType.INT,List.of(VariableType.INT,VariableType.INT,VariableType.INT,VariableType.INT,VariableType.INT,VariableType.INT));
    }

    public Function(String name, String content, String paramString, VariableType returnType) {
        this.name=name;
        this.returnType= returnType;
        this.tokens=new ArrayList<List<Token>>();
        this.isbuiltIn = false;
        this.expectedParams=new ArrayList<VariableType>();
        this.paramNames=new ArrayList<String>();
        this.content = content;
        this.expectedParamsFromString(paramString);
        funcMap.put(name, this);
    }

    public Function(String name, BuiltInFunction linkedBuiltInFunction, VariableType returnType ,List<VariableType> expectedParams) {
        this.name=name;
        this.returnType = returnType;
        this.isbuiltIn = true;
        this.linkedBuiltInFunction = linkedBuiltInFunction;
        this.expectedParams = expectedParams;
        funcMap.put(name, this);
    }

    public void expectedParamsFromString(String line) {
        if(line.isBlank()) return;
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
            localVarMap.put(name, new Variable(name, type));
            this.expectedParams.add(type);
        }
    }

    public String getName() {
        return this.name;
    }

    public List<List<Token>> getTokens() {
        return this.tokens;
    }

    public VariableType getReturnType() {
        return returnType;
    }

    public boolean isInBuiltFunction() {
        return this.isbuiltIn;
    }

    public void addTokens(List<List<Token>> tokens) {
        this.tokens.addAll(sortTokens(tokens));
    }

    public void addTokensFromContent() {
        this.tokens.addAll(sortTokens(Tokenizer.tokenize(content, this)));
    }

    public static List<List<Token>> sortTokens(List<List<Token>> tokenLineList) {
        for (List<Token> tokens : tokenLineList) {
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
        }
        return tokenLineList;
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
        } 

        checkParams(params);
        for (int i=0; i < paramNames.size(); i++) {
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
        if(Parser.printTokens) {
            printTokens();
        }
        return Simulator.SimulateFunction(this,paramNames.size());
    }

    public void printTokens() {
        System.out.println("Function: " + name);
        System.out.println("-----------------------");
        for(int i =0; i < tokens.size(); i++) {
            if(tokens.get(i).size() == 0) continue;
            System.out.println("Line " + i);
            System.out.println("#####################");
            for (Token t : tokens.get(i)) {
                t.printInfo(0);
            }
            System.out.println("#####################");
        }
    }
}
