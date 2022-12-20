package parser;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public static final Integer PLACEHOLDER_CHARNO = 14;

    public static List<Token> tokenizeLine(String line, Function scope, Integer lineNo) {
        int index = 0;
        List<Token> tokens = new ArrayList<Token>();
        line = line.trim();
        //System.out.println(line);
        if(line.contains("(") && line.contains(")")){
            int start = line.indexOf("(");
            int end = line.lastIndexOf(")");
            String paramString = line.substring(start+1, end);
            //System.out.println(paramString);
            String[] params = paramString.split(",");
            List<Token> paramTokens = new ArrayList<Token>();
            for (String param : params) {
                paramTokens.addAll(tokenizeLine(param,scope,lineNo));
            }
            tokens.add(new TokenFunctionCall(line.substring(0, start),lineNo,PLACEHOLDER_CHARNO, paramTokens));

        } else if(line.contains("=")) {
            String[] start = line.substring(0, line.indexOf('=')-1).split(" ");
            String type = start[0];
            String name = start[1];
            String value = line.substring(line.indexOf('=')+1);
            if(type.equals("int")) {
                
                tokens.add(new TokenVariableDeclaration<Integer>(name,lineNo,PLACEHOLDER_CHARNO,scope,Integer.parseInt(value.trim())));
            } else if(type.equals("string")) {
                tokens.add(new TokenVariableDeclaration<String>(name,lineNo,PLACEHOLDER_CHARNO,scope,value.split("\"")[1]));
            } else {
                Parser.exitWithError(type + " is an invalid type!",92);
            }
        }
        else if(line.matches("-?\\d+")) {
            tokens.add(new TokenLiteralNum(Integer.parseInt(line),lineNo,PLACEHOLDER_CHARNO));
        } else if(line.startsWith("\"") && line.endsWith("\"")) {
            tokens.add(new TokenLiteralString(line.substring(1, line.length()-1),lineNo,PLACEHOLDER_CHARNO));
        } else if(!line.isBlank()) {
            tokens.add(new TokenVariableRefrence<Integer>(line,lineNo,PLACEHOLDER_CHARNO,scope));
        }
        return tokens;
    }

    public static List<Token> tokenize(String[] lines, Function scope) {
        List<Token> tokens = new ArrayList<Token>();
        for (int i = 0; i < lines.length; i++) {
            tokens.addAll(tokenizeLine(lines[i],scope,i));   
        }
        return tokens;
    }

}
