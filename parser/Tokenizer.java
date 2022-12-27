package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Tokenizer {

    private static String emptyString(int size) {
        return " ".repeat(size);
    }

    public static String tokenizeLine(List<Token> tokens, String line, Function scope) {
        line = extractFunctionCalls(tokens,line,scope);
        line = extractStringLiterals(tokens,line);
        line = extractNumberLiterals(tokens,line);
        line = extractVariableDeclaration(tokens,line,scope);
        line = extractVariableAssigment(tokens,line);
        line = extractMaths(tokens,line);
        line = extractVariableRefrence(tokens,line,scope);
        tokens = Function.sortTokens(List.of(tokens)).get(0);
        return line;
    }

    private static String extractFunctionCalls(List<Token> tokens, String line, Function scope) {
        String query = "(";
        String[] functionNames = Function.funcMap.keySet().toArray(new String[Function.funcMap.keySet().size()]);
        for (int i = 0; i < functionNames.length; i++) {
            if(i != 0) query+="|";
            query += functionNames[i];
        }
        query += ")\\((.*?)\\)";
        for (String match : getMatches(line, query)) {
            int paramStart = match.indexOf('(')+1;
            int paramEnd = match.indexOf(')');
            String name = match.substring(0,paramStart-1);
            String paramString = match.substring(paramStart, paramEnd);
            //System.out.println(paramString);
            List<Token> paramTokens = new ArrayList<Token>();
            List<List<Token>> tokensToSimulate = new ArrayList<List<Token>>(); 
            paramString = tokenizeLine(paramTokens, paramString, scope);
            List<Integer> indexes = new ArrayList<Integer>();
            int index = paramString.indexOf(',');
            while (index >= 0) {
                indexes.add(index);
                index = paramString.indexOf(',', index + 1);
            }
            indexes.add(Integer.MAX_VALUE);
            if(indexes.size() == 1) {
                tokensToSimulate.add(paramTokens);
            } else {
                for (int i = 0; i < indexes.size(); i++) {
                    List<Token> simTokens = new ArrayList<Token>();
                    for (Token t : paramTokens) {
                        if(i == 0) {
                            if(t.getCharNo() < indexes.get(i)) {
                                simTokens.add(t);
                            }
                        }
                        else if(t.getCharNo() > indexes.get(i-1) && t.getCharNo() < indexes.get(i)) {
                            simTokens.add(t);
                        }
                    }
                    tokensToSimulate.add(simTokens);
                }
            }
            tokens.add(Token.new_FunctionCall(name, line.indexOf(match), tokensToSimulate, scope));
            line = replace(line, match);
        }
        return line;
    }

    private static String[] getMatches(String line, String query) {
        String[] matches = Pattern.compile(query,Pattern.DOTALL)
        .matcher(line)
        .results()
        .map(MatchResult::group)
        .toArray(String[]::new);
        return matches;
    } 

    private static String replace(String line, String match) {
        return line.replaceFirst(Pattern.quote(match), emptyString(match.length()));
    }

    private static String extractStringLiterals(List<Token> tokens, String line) {
        for (String match : getMatches(line, "\"(.*?)\"")) {
            String value = match.substring(1,match.length()-1);
            tokens.add(Token.new_LiteralString( line.indexOf(match), value));
            line = replace(line, match);
        }
        return line;
    }

    private static String extractNumberLiterals(List<Token> tokens, String line) {
        for (String match : getMatches(line, "-?\\d+")) {
            tokens.add(Token.new_LiteralNum( line.indexOf(match), Integer.parseInt(match)));
            line = replace(line, match);
        }
        return line;
    }

    private static String extractVariableDeclaration(List<Token> tokens, String line, Function scope) {
        for (String match : getMatches(line,"(string|int)\\s\\w+")) {
            String[] matchSplit = match.split(" ");
            String typeString = matchSplit[0];
            String name = matchSplit[1];
            VariableType type = VariableType.VOID;
            if(typeString.equals("string")) {
                type = VariableType.STRING;
            } else if(typeString.equals("int")) {
                type = VariableType.INT;
            }
            tokens.add(Token.new_VariableDeclaration(name,line.indexOf(match),scope,type));
            line = replace(line, match);
        }
        return line;
    }
    
    private static String extractVariableAssigment(List<Token> tokens, String line) {
        for (String match : getMatches(line,"=")) {
            tokens.add(Token.new_VariableAssignment(line.indexOf(match)));
            line = replace(line, match);
        }
        return line;
    }

    private static String extractMaths(List<Token> tokens, String line) {
        final Character[] mathsFunctions = {'+','-','*','\\','%'};

        for (Character c : mathsFunctions) {
            for (String match : getMatches(line,"\\"+c)) {
                tokens.add(Token.new_Maths(line.indexOf(match),c));
                line = replace(line, match);
            }
        }

        for (String match : getMatches(line, "\\!")) {
            tokens.add(Token.new_Not(line.indexOf(match)));
            line = replace(line, match);
        }

        for (String match : getMatches(line, "\\&\\&")) {
            tokens.add(Token.new_And(line.indexOf(match)));
            line = replace(line, match);
        }
        return line;
    }

    private static String extractVariableRefrence(List<Token> tokens, String line, Function scope) {
        for (String match : getMatches(line, "\\w+")) {
            tokens.add(Token.new_VariableRefrence(match, line.indexOf(match), scope));
            line = replace(line, match);
        }
        return line;
    }

    public static Integer getLineNumber(Integer mindex, String lines) {
        Integer index = lines.indexOf("\n", 0);
        Integer count = 0;
        while(index != -1) {
            if(index > mindex) return count;
            index = lines.indexOf("\n", index+1);
            count++;
        }
        return -1;
    }

    // FIXME: blocks can't have more than one space between ')' and '{'
    public static String extractBlocks(String lines, List<List<Token>> tokenLines,Function scope) {
        String query = "(while|if)\\s*\\((.*?)\\)\\s\\{.*\\}";
        String[] matches = getMatches(lines, query);
        while(matches.length > 0) { 
            String match = matches[0];
            match = matchBrackets(match);
            Integer lineNo = getLineNumber(lines.indexOf(match),lines);
            boolean isWhile = (match.charAt(0) == 'w');
            int paramStart = match.indexOf('(')+1;
            int paramEnd = match.indexOf(')');
            String paramString = match.substring(paramStart, paramEnd);
            List<Token> paramTokens = new ArrayList<Token>();
            paramString = tokenizeLine(paramTokens, paramString, scope);
            int tokensStart = match.indexOf('{')+1;
            int tokensEnd = match.lastIndexOf('}');
            String tokenString = match.substring(tokensStart, tokensEnd);
            List<List<Token>> tokenTokens = tokenize(tokenString, scope);
            List<Token> tokens = tokenLines.get(lineNo);
            tokens.add((
                (isWhile)? 
                    Token.new_While(0,paramTokens,tokenTokens)
                :
                    Token.new_If(0,paramTokens,tokenTokens)
            ));
            lines = replace(lines, match);
            matches = getMatches(lines, query);
        }
        return lines;
    }

    private static String matchBrackets(String match) {
        Integer start = match.indexOf('{')+1;
        Integer end = match.lastIndexOf('}');
        Integer pos = 0;
        Integer open = 1;
        for (int i = start; i <= end && open > 0; i++) {
            if(match.charAt(i) == '{') {open++;}
            else if(match.charAt(i) == '}') {open--;}
            pos = i;
        }
        return match.substring(0,pos+1);
    }

    public static List<List<Token>> tokenize(String rawlines, Function scope) {
        Integer lineCount = rawlines.split("\n").length;
        List<List<Token>> tokenLines = new ArrayList<List<Token>>();
        for (int i = 0; i < lineCount; i++) {
            tokenLines.add(new ArrayList<Token>());
        }
        String[] lines = extractBlocks(rawlines, tokenLines, scope).split("\n");
        for (int i = 0; i < lines.length; i++) {
            tokenizeLine(tokenLines.get(i),lines[i],scope);
        }
        return tokenLines;
    }

}
