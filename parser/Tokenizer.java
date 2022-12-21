package parser;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public static List<Token> tokenizeLine(List<Token> currentTokens, String line, Function scope, Integer lineNo) {
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
                paramTokens.addAll(tokenizeLine(tokens,param,scope,lineNo));
            }
            String functionName = line.substring(0, start);
            tokens.add(Token.new_FunctionCall(functionName,lineNo,line.indexOf(functionName), paramTokens));

        } else if(line.contains("=")) {
            String[] start = line.substring(0, line.indexOf('=')-1).split(" ");
            String type = start[0];
            String name = start[1];
            String value = line.substring(line.indexOf('=')+1);
            String v;
            if(type.equals("int")) {
                v = value.trim();
                tokens.add(Token.new_VariableDeclaration(name,lineNo,line.indexOf(v),scope,Integer.parseInt(v),VariableType.INT));
            } else if(type.equals("string")) {
                v = value.split("\"")[1];
                tokens.add(Token.new_VariableDeclaration(name,lineNo,line.indexOf(v),scope,v,VariableType.STRING));
            } else {
                Parser.exitWithError(type + " is an invalid type!",92);
            }
            
        }
        else if(line.matches("-?\\d+")) {
            tokens.add(Token.new_LiteralNum(lineNo,0,Integer.parseInt(line)));
        } else if(line.startsWith("\"") && line.endsWith("\"")) {
            tokens.add(Token.new_LiteralString(lineNo,0,line.substring(1, line.length()-1)));
        } else if(!line.isBlank()) {
            tokens.add(Token.new_VariableRefrence(line,lineNo,0,scope));
        }
        return tokens;
    }

    public static List<Token> tokenize(String[] lines, Function scope) {
        List<Token> tokens = new ArrayList<Token>();
        for (int i = 0; i < lines.length; i++) {
            tokens.addAll(tokenizeLine(tokens,lines[i],scope,i));   
        }
        return tokens;
    }

}
