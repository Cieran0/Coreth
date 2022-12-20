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
        lines = ExtractFunction(lines);
        
        System.out.println("Functions "+functions.size()+":");
        for (Function f : functions.values()) {
            if(f.getName().equals("main")) mainFound = true;
            System.out.println(f.getName());
        }
        if(!mainFound)
            exitWithError("main function not found", 1);
        Simulator.SimulateFunction(functions.get("main"));
        
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
        f.setTokens(Tokenizer.tokenize(sub.split("\n"),f));
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

}

