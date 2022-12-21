package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Tokenizer {

    public static List<Token> tokenizeLine(String line, Function scope, Integer lineNo) {
        int index = 0;
        List<Token> tokens = new ArrayList<Token>();
        line = extractFunctionCalls(tokens,line,lineNo,scope);
        line = extractStringLiterals(tokens,line,lineNo);
        line = extractNumberLiterals(tokens,line,lineNo);
        extractVariableRefrence(tokens,line,lineNo,scope);
        return tokens;

        /*  TODO: Add Variable Declaration/Assignment back into Tokenizer!
        * 
        *else if(line.contains("=")) {
        *    String[] start = line.substring(0, line.indexOf('=')-1).split(" ");
        *    String type = start[0];
        *    String name = start[1];
        *    String value = line.substring(line.indexOf('=')+1);
        *    String v;
        *    if(type.equals("int")) {
        *        v = value.trim();
        *        tokens.add(Token.new_VariableDeclaration(name,lineNo,line.indexOf(v),scope,Integer.parseInt(v),VariableType.INT));
        *    } else if(type.equals("string")) {
        *        v = value.split("\"")[1];
        *        tokens.add(Token.new_VariableDeclaration(name,lineNo,line.indexOf(v),scope,v,VariableType.STRING));
        *    } else {
        *        Parser.exitWithError(type + " is an invalid type!",92);
        *    }
        *    
        *}
        *
        *else if (line.contains("=")) {
        *    tokens.add(Token.new_VariableAssignment(lineNo, line.indexOf("=")));
        *}
        */
    }

    
    private static String extractNumberLiterals(List<Token> tokens, String line, Integer lineNo) {
        String[] matches = Pattern.compile("-?\\d+")
        .matcher(line)
        .results()
        .map(MatchResult::group)
        .toArray(String[]::new);

        for (String match : matches) {
            tokens.add(Token.new_LiteralNum(lineNo, line.indexOf(match), Integer.parseInt(match)));
            line = line.replace(match, emptyString(match.length()));
        }
        return line;
    }


    private static String extractStringLiterals(List<Token> tokens, String line, Integer lineNo) {
        String[] matches = Pattern.compile("\"(.*?)\"")
        .matcher(line)
        .results()
        .map(MatchResult::group)
        .toArray(String[]::new);

        for (String match : matches) {
            String value = match.substring(1,match.length()-1);
            tokens.add(Token.new_LiteralString(lineNo, line.indexOf(match), value));
            line = line.replace(match, emptyString(match.length()));
        }
        return line;
    }


    private static String extractFunctionCalls(List<Token> tokens, String line, Integer lineNo, Function scope) {
        while(line.matches(".*\\((.*?)\\).*")) {
            int start = line.indexOf("(");
            int end = 0;
            int nameIndex = 0;
            int open = 1;
            for (int i = start+1; i < line.length() && open > 0; i++) {
                if(line.charAt(i) == '(') open++;
                else if(line.charAt(i) == ')') open--;
                end = i;
            }
            String paramString = line.substring(start+1, end);
            List<Token> paramTokens = new ArrayList<Token>();
            paramTokens.addAll(tokenizeLine(paramString,scope,lineNo));
            for (int i = start-1; i >= 0 && !Character.isWhitespace(line.charAt(i)); i--) {
                nameIndex = i;
            }
            String name = line.substring(nameIndex,start);
            tokens.add(Token.new_FunctionCall(name, lineNo, nameIndex, paramTokens));
            String replace = line.substring(nameIndex,end+1);
            line = line.replace(replace, emptyString(replace.length()));
        }
        return line;
    }

    private static String emptyString(int size) {
        return " ".repeat(size);
    }

    private static void extractVariableRefrence(List<Token> tokens, String line, Integer lineNo, Function scope) {
        String[] matches = Pattern.compile("\\w+")
        .matcher(line)
        .results()
        .map(MatchResult::group)
        .toArray(String[]::new);

        for (String match : matches) {
            tokens.add(Token.new_VariableRefrence(match, lineNo, line.indexOf(match), scope));
            line.replace(match, emptyString(match.length()));
        }
    }

    public static List<Token> tokenize(String[] lines, Function scope) {
        List<Token> tokens = new ArrayList<Token>();
        for (int i = 0; i < lines.length; i++) {
            tokens.addAll(tokenizeLine(lines[i],scope,i));   
        }
        return tokens;
    }

}
