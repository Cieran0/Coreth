package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Parser { 

    public static Integer line = 0;

    public static final Boolean printTokens = false;
    public static final Boolean dumpMemoryOnExit = false;
    public static final Boolean createJSONFile = true;
    public static Serializer serializer = new CVMifier();

    public static HashMap<String,VariableType> stringVariableTypeMap = new HashMap<String,VariableType>() {{
        put("void",VariableType.VOID);
        put("string",VariableType.STRING);
        put("int",VariableType.INT);
        put("pointer",VariableType.POINTER);

    }};

    public static String readLines(String path) {
        try {
            List<String> sarry = Files.readAllLines(Paths.get(path));
            sarry = sarry.stream().filter((s) -> !s.startsWith("//")).collect(Collectors.toList());
            String s = "";
            for (String line : sarry) {
                s+=line+"\n";
            }
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public static void markUsedFunctions(Function main) {
        main.setUsed();
        for (List<Token> tokenList : main.getTokens()) {
            for (Token token : tokenList) {
                if(token.getType() == TokenType.FUNCTION_CALL) {
                    Function func = Function.funcMap.get(token.getName());
                    if(!func.isInBuiltFunction() && !func.isUsed()) {
                        markUsedFunctions(func);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String path = (args.length > 0)? args[0] : "test.coreth";
        Syscalls.SetUpMap();
        Function.setUpBuiltInFunctions();

        String lines = readLines(path);
        for (String line : lines.split("\n")) {
            if(!line.contains("#include")) continue;
            String filePath = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));
            String fileLines = readLines(filePath);
            lines = lines.replace(line+"\n", fileLines);
        }

        lines = ExtractFunction(lines);
        
        Function mainFunction = Function.funcMap.get("main");
        for (Function function : Function.funcMap.values()) {
            if(!function.isInBuiltFunction()) {
                function.addTokensFromContent();
                if(!function.hasReturnAtEnd()) {
                    exitWithError("Function [" + function.getName() + "] is missing return on last line",11);
                }
            }
        }
        if(mainFunction == null) {
            exitWithError("main function not found", 1);
        }
        mainFunction.execute(List.of());
        if(dumpMemoryOnExit) {
            Memory.dump();
        }
        markUsedFunctions(mainFunction);
        serializer.save("test.cvm");
        
    }

    public static String ExtractFunction(String lines) {
        if(!lines.contains("{")) return lines;
        String newLines = lines;
        int open = 1;
        int start = lines.indexOf('{')+1;
        int end = 0;
        String[] nameAndType = lines.substring(0, lines.indexOf('(')).trim().split(" ");
        String name = nameAndType[1];
        VariableType returnType = stringVariableTypeMap.get(nameAndType[0]);
        String params = lines.substring(lines.indexOf('(')+1, lines.indexOf(')'));
        for(int i = start; i < lines.length() && open > 0; i++) {
            if(lines.charAt(i) == '{') open++;
            else if(lines.charAt(i) == '}') open--;
            end = i;
        }
        String content = newLines.substring(lines.indexOf(nameAndType[0]), end+1);
        String sub = content.substring(content.indexOf("{")+1, content.lastIndexOf("}"));
        new Function(name,sub,params,returnType);
        newLines = lines.replace(content, " ".repeat(content.length()));
        if(lines == newLines) return lines;
        newLines = ExtractFunction(newLines);
        return newLines;
    }

    public static void exitWithError(String msg, int errorNo) {
        System.out.println("Error: " + msg);
        //System.out.println("Line: " + line);
        Thread.dumpStack();
        System.exit(errorNo);
    }

    public static VariableType TokenToVariableType(Token token) {
        if(token.getType() == TokenType.VARIABLE_REFRENCE) {
            VariableType vt = token.getVariableType();
            if(vt == null) {
                exitWithError("Variable " + token.getName() + " is not defined!", -99);
            }
            return vt;
        }
        if(token.getType() == TokenType.FUNCTION_CALL) {
            return Function.funcMap.get(token.getName()).getReturnType();
        }
        switch (token.getType()) {
            case CONSTANT_INTEGER:
            case INTEGER:
                return VariableType.INT;
            case CONSTANT_STRING:
            case STRING:
                return VariableType.STRING;
            default:
                return VariableType.VOID;
        }
    }

}

