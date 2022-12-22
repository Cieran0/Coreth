package parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class Tokenizer {

    private static String emptyString(int size) {
        return " ".repeat(size);
    }

    // TODO: restructor how tokens are handled! maybe idk? right to left rather than left to right?

    public static List<Token> tokenizeLine(String line, Function scope, Integer lineNo) {
        List<Token> tokens = new ArrayList<Token>();
        line = extractIfs(tokens,line,lineNo,scope);
        line = extractFunctionCalls(tokens,line,lineNo,scope);
        line = extractStringLiterals(tokens,line,lineNo);
        line = extractNumberLiterals(tokens,line,lineNo);
        line = extractVariableDeclaration(tokens,line,lineNo,scope);
        line = extractVariableAssigment(tokens,line,lineNo);
        line = extractMaths(tokens,line,lineNo);
        extractVariableRefrence(tokens,line,lineNo,scope);
        return tokens;
    }

    private static String extractIfs(List<Token> tokens, String line, Integer lineNo, Function scope) {
        String query = "(if)\\s*\\((.*?)\\)\\s\\{.*\\}";
        for (String match : getMatches(line, query)) { 
            System.out.println(match);
            line = line.replace(match, emptyString(match.length()));
            int paramStart = match.indexOf('(')+1;
            int paramEnd = match.indexOf(')');
            String paramString = match.substring(paramStart, paramEnd);
            List<Token> paramTokens = new ArrayList<Token>();
            paramTokens.addAll(Function.sortTokens(tokenizeLine(paramString,scope,lineNo)));
            int tokensStart = match.indexOf('{')+1;
            int tokensEnd = match.lastIndexOf('}');
            String tokenString = match.substring(tokensStart, tokensEnd);
            List<Token> tokenTokens = new ArrayList<Token>();
            tokenTokens.addAll(Function.sortTokens(tokenizeLine(tokenString,scope,lineNo)));
            tokens.add(Token.new_If(lineNo, lineNo, paramTokens,tokenTokens));
            line.replace(match, emptyString(match.length()));
        }
        return line;
    }

    private static String extractFunctionCalls(List<Token> tokens, String line, Integer lineNo, Function scope) {
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
            List<Token> paramTokens = new ArrayList<Token>();
            paramTokens.addAll(Function.sortTokens(tokenizeLine(paramString,scope,lineNo)));
            tokens.add(Token.new_FunctionCall(name, lineNo, line.indexOf(match), paramTokens));
            line = line.replace(match, emptyString(match.length()));
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

    private static String extractMaths(List<Token> tokens, String line, Integer lineNo) {
        final Character[] mathsFunctions = {'+','-','*','\\','%'};

        for (Character c : mathsFunctions) {
            for (String match : getMatches(line,"\\"+c)) {
                tokens.add(Token.new_Maths(lineNo,line.indexOf(match),c));
                line = line.replace(match, emptyString(match.length()));
            }
        }
        return line;
    }

    private static void extractVariableRefrence(List<Token> tokens, String line, Integer lineNo, Function scope) {
        int index = 0;
        for (String match : getMatches(line, "\\w+")) {
            tokens.add(Token.new_VariableRefrence(match, lineNo, line.indexOf(match,index), scope));
            line.replace(match, emptyString(match.length()));
            index = line.indexOf(match,index)+1;
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
