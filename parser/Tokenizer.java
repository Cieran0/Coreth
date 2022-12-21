package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Tokenizer {

    private static String emptyString(int size) {
        return " ".repeat(size);
    }

    public static List<Token> tokenizeLine(String line, Function scope, Integer lineNo) {
        List<Token> tokens = new ArrayList<Token>();
        line = extractFunctionCalls(tokens,line,lineNo,scope);
        line = extractStringLiterals(tokens,line,lineNo);
        line = extractNumberLiterals(tokens,line,lineNo);
        line = extractVariableDeclaration(tokens,line,lineNo,scope);
        line = extractVariableAssigment(tokens,line,lineNo);
        extractVariableRefrence(tokens,line,lineNo,scope);
        return tokens;
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
            paramTokens.addAll(Function.sortTokens(tokenizeLine(paramString,scope,lineNo)));
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

    private static String[] getMatches(String line, String query) {
        String[] matches = Pattern.compile(query)
        .matcher(line)
        .results()
        .map(MatchResult::group)
        .toArray(String[]::new);
        return matches;
    } 

    private static String extractStringLiterals(List<Token> tokens, String line, Integer lineNo) {
        for (String match : getMatches(line, "\"(.*?)\"")) {
            String value = match.substring(1,match.length()-1);
            tokens.add(Token.new_LiteralString(lineNo, line.indexOf(match), value));
            line = line.replace(match, emptyString(match.length()));
        }
        return line;
    }

    private static String extractNumberLiterals(List<Token> tokens, String line, Integer lineNo) {
        for (String match : getMatches(line, "-?\\d+")) {
            tokens.add(Token.new_LiteralNum(lineNo, line.indexOf(match), Integer.parseInt(match)));
            line = line.replace(match, emptyString(match.length()));
        }
        return line;
    }

    private static String extractVariableDeclaration(List<Token> tokens, String line, Integer lineNo, Function scope) {
        for (String match : getMatches(line,"(string|int).\\w+")) {
            String[] matchSplit = match.split(" ");
            String typeString = matchSplit[0];
            String name = matchSplit[1];
            VariableType type = VariableType.NULL;
            if(typeString.equals("string")) {
                type = VariableType.STRING;
            } else if(typeString.equals("int")) {
                type = VariableType.INT;
            }
            tokens.add(Token.new_VariableDeclaration(name,lineNo,line.indexOf(match),scope,type));
            line = line.replace(match, emptyString(match.length()));
        }
        return line;
    }
    
    private static String extractVariableAssigment(List<Token> tokens, String line, Integer lineNo) {
        for (String match : getMatches(line,"=")) {
            tokens.add(Token.new_VariableAssignment(lineNo,line.indexOf(match)));
            line = line.replace(match, emptyString(match.length()));
        }
        return line;
    }

    private static void extractVariableRefrence(List<Token> tokens, String line, Integer lineNo, Function scope) {
        for (String match : getMatches(line, "\\w+")) {
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
