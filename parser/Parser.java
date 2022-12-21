package parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Parser {

    public static HashMap<String,Function> functions = new HashMap<String,Function>(); 

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

    public static void main(String[] args) {
        boolean mainFound = false;
        String path = (args.length > 0)? args[0] : "test.coreth";
        BuiltInFunctions.setUpMap();

        String lines = readLines(path);
        lines = ExtractFunction(lines);
        
        Function mainFunction = functions.get("main");
        if(mainFunction != null) mainFound = true;
        if(!mainFound) {
            exitWithError("main function not found", 1);
        }
        Simulator.SimulateFunction(mainFunction,0);
    }

    public static String ExtractFunction(String lines) {
        if(!lines.contains("{")) return lines;
        String newLines = lines;
        int open = 1;
        int start = lines.indexOf('{')+1;
        int end = 0;
        String name = lines.substring(0, lines.indexOf('(')).trim();
        String params = lines.substring(lines.indexOf('(')+1, lines.indexOf(')'));
        for(int i = start; i < lines.length() && open > 0; i++) {
            if(lines.charAt(i) == '{') open++;
            else if(lines.charAt(i) == '}') open--;
            end = i;
        }
        Function f = new Function(name);
        String content = newLines.substring(lines.indexOf(name), end+1);
        newLines = lines.replace(content, "");

        String sub = content.substring(start, content.length()-1);

        f.expectedParamsFromString(params);
        f.addTokens(Tokenizer.tokenize(sub.split("\n"),f));
        //System.out.println(name);
        functions.put(name, f);
        if(lines == newLines) return lines;
        newLines = ExtractFunction(newLines);
        return newLines;
    }

    public static void exitWithError(String msg, int errorNo) {
        System.out.println("Error: " + msg);
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
            case LITERAL_NUM:
                return VariableType.INT;
            case LITERAL_STRING:
                return VariableType.STRING;
            default:
                return VariableType.NULL;
        }
    }

}

