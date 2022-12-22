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

    public static String tokenizeLine(List<Token> tokens, String line, Function scope, Integer lineNo) {
        line = extractIfs(tokens,line,lineNo,scope);
        line = extractFunctionCalls(tokens,line,lineNo,scope);
        line = extractStringLiterals(tokens,line,lineNo);
        line = extractNumberLiterals(tokens,line,lineNo);
        line = extractVariableDeclaration(tokens,line,lineNo,scope);
        line = extractVariableAssigment(tokens,line,lineNo);
        line = extractMaths(tokens,line,lineNo);
        line = extractVariableRefrence(tokens,line,lineNo,scope);
        tokens = Function.sortTokens(tokens);
        return line;
    }

    private static String extractIfs(List<Token> tokens, String line, Integer lineNo, Function scope) {
        String query = "(if)\\s*\\((.*?)\\)\\s\\{.*\\}";
        for (String match : getMatches(line, query)) { 
            int paramStart = match.indexOf('(')+1;
            int paramEnd = match.indexOf(')');
            String paramString = match.substring(paramStart, paramEnd);
            List<Token> paramTokens = new ArrayList<Token>();
            tokenizeLine(tokens, paramString, scope, lineNo);
            int tokensStart = match.indexOf('{')+1;
            int tokensEnd = match.lastIndexOf('}');
            String tokenString = match.substring(tokensStart, tokensEnd);
            List<Token> tokenTokens = new ArrayList<Token>();
            tokenizeLine(tokenTokens,tokenString,scope,lineNo);
            tokens.add(Token.new_If(lineNo, lineNo, paramTokens,tokenTokens));
            line = replace(line, match);
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
            //System.out.println(paramString);
            List<Token> paramTokens = new ArrayList<Token>();
            List<List<Token>> tokensToSimulate = new ArrayList<List<Token>>(); 
            paramString = tokenizeLine(paramTokens, paramString, scope, lineNo);
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
            tokens.add(Token.new_FunctionCall(name, lineNo, line.indexOf(match), tokensToSimulate, scope));
            line = replace(line, match);
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

    private static String replace(String line, String match) {
        return line.replaceFirst(Pattern.quote(match), emptyString(match.length()));
    }

    private static String extractStringLiterals(List<Token> tokens, String line, Integer lineNo) {
        for (String match : getMatches(line, "\"(.*?)\"")) {
            String value = match.substring(1,match.length()-1);
            tokens.add(Token.new_LiteralString(lineNo, line.indexOf(match), value));
            line = replace(line, match);
        }
        return line;
    }

    private static String extractNumberLiterals(List<Token> tokens, String line, Integer lineNo) {
        for (String match : getMatches(line, "-?\\d+")) {
            tokens.add(Token.new_LiteralNum(lineNo, line.indexOf(match), Integer.parseInt(match)));
            line = replace(line, match);
        }
        return line;
    }

    private static String extractVariableDeclaration(List<Token> tokens, String line, Integer lineNo, Function scope) {
        for (String match : getMatches(line,"(string|int).\\w+")) {
            String[] matchSplit = match.split(" ");
            String typeString = matchSplit[0];
            String name = matchSplit[1];
            VariableType type = VariableType.VOID;
            if(typeString.equals("string")) {
                type = VariableType.STRING;
            } else if(typeString.equals("int")) {
                type = VariableType.INT;
            }
            tokens.add(Token.new_VariableDeclaration(name,lineNo,line.indexOf(match),scope,type));
            line = replace(line, match);
        }
        return line;
    }
    
    private static String extractVariableAssigment(List<Token> tokens, String line, Integer lineNo) {
        for (String match : getMatches(line,"=")) {
            tokens.add(Token.new_VariableAssignment(lineNo,line.indexOf(match)));
            line = replace(line, match);
        }
        return line;
    }

    private static String extractMaths(List<Token> tokens, String line, Integer lineNo) {
        final Character[] mathsFunctions = {'+','-','*','\\','%'};

        for (Character c : mathsFunctions) {
            for (String match : getMatches(line,"\\"+c)) {
                tokens.add(Token.new_Maths(lineNo,line.indexOf(match),c));
                line = replace(line, match);
            }
        }

        for (String match : getMatches(line, "\\!")) {
            tokens.add(Token.new_Not(lineNo,line.indexOf(match)));
            line = replace(line, match);
        }
        return line;
    }

    private static String extractVariableRefrence(List<Token> tokens, String line, Integer lineNo, Function scope) {
        int index = 0;
        for (String match : getMatches(line, "\\w+")) {
            tokens.add(Token.new_VariableRefrence(match, lineNo, line.indexOf(match,index), scope));
            line = replace(line, match);
            index = line.indexOf(match,index)+1;
        }
        return line;
    }

    public static List<Token> tokenize(String[] lines, Function scope) {
        List<Token> tokens = new ArrayList<Token>();
        for (int i = 0; i < lines.length; i++) {
            tokenizeLine(tokens,lines[i],scope,i);
        }
        return tokens;
    }

}
