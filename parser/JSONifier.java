package parser;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONifier implements Serializer {
    public void save(String path) {
        try {
            FileWriter myWriter = new FileWriter(path);
            myWriter.write(createJSON());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Failed to write to file!");
            e.printStackTrace();
        }
    }
    
    private String createJSON() {
        String data = "{\n\"functions\": [";
        for (Function function : Function.funcMap.values()) {
            if(function.isInBuiltFunction() || !function.isUsed()) continue;
            data += JSONfromFunction(function, 1);
        }
        data = data.substring(0,data.length()-1);
        data += "\n]";
        data += "\n}";
        return data;
    }

    private String JSONfromFunction(Function function, int indent) {
        String data = "\n{\n\"name\": \"" + function.getName() + "\",\n";
        data += "\"returnType\": \""+function.getReturnType()+"\",\n";
        if(function.getExpectedParams().size() > 0) {
            data += "\"params\": [";
            for(int i = 0; i < function.getParamNames().size(); i++) {
                data+="\n\t{\n";
                data+="\t\t\"name\": \"" + function.getParamNames().get(i)+"\",\n";
                data+="\t\t\"variableType\": \"" + function.getExpectedParams().get(i)+"\",\n";
                data += "\t\t\"variableID\": " + function.getVariableNameIndex(function.getParamNames().get(i));
                data += "\n\t},";
            }
            data = data.substring(0,data.length()-1);
            data += "\n],\n";
        }
        if(function.getExpectedParams().size() > 0) {
            data += "\"variables\": [";
            List<String> variableNamesList = new ArrayList<String>();
            List<VariableType> variableTypeList = new ArrayList<VariableType>();
            for (String name : function.getParamNames()) {
                variableNamesList.add(name);
            }
            for(String name : function.localNameVariableTypeMap.keySet()) {
                variableNamesList.add(name);
            }

            for (VariableType type : function.getExpectedParams()) {
                variableTypeList.add(type);
            }
            for(VariableType type : function.localNameVariableTypeMap.values()) {
                variableTypeList.add(type);
            }

            int size = variableNamesList.size();
            for(int i = 0; i < size; i++) {
                data+="\n\t{\n";
                data+="\t\t\"name\": \"" + variableNamesList.get(i)+"\",\n";
                data+="\t\t\"variableType\": \"" + variableTypeList.get(i)+"\",\n";
                data += "\t\t\"variableID\": " + function.getVariableNameIndex(variableNamesList.get(i));
                data += "\n\t},";
            }
            data = data.substring(0,data.length()-1);
            data += "\n],\n";
        }
        data += "\"tokens\": [";
        for (List<Token> tokenList : function.getTokens()) {
            for (Token token : tokenList) {
                data += "\n\t{";
                data += JSONfromToken(token, indent+1);
                data += "\n\t},";
            }
        }
        data = data.substring(0,data.length()-1);
        data += "\n]},\n";
        return data.substring(0,data.length()-1).replace("\n", "\n" + "\t".repeat(indent));
    }

    private String JSONfromToken(Token token, int indent) {
        String data = "\n\"name\": \"" + token.getName().replace("\n", "\\n") + "\",\n";
        data += "\"type\": \"" + token.getType() + "\",\n";
        switch(token.getType()) {
            case CONSTANT_INTEGER:
            case INTEGER:
                data += "\"value\": " + token.getInt() + "\n";
                break;
            case CONSTANT_STRING:
            case STRING:

                data += "\"value\": " + "\"" + token.getString().replace("\n", "\\n") + "\"\n";
                break;
                case VARIABLE_DECLARATION:
                data += "\"variableName\": " + "\"" + token.getName() + "\",\n";
                data += "\"variableID\": " +  token.getID() + ",\n";
                data += "\"variableType\": " + "\"" + token.getVariableType() + "\"\n";
                break;
                case VARIABLE_REFRENCE:
                data += "\"variableName\": " + "\"" + token.getName() + "\",\n";
                data += "\"variableID\": " + token.getID() + "\n";
                break;
            case IF:
            case WHILE:
                data += "\"blockTokens\": [";
                for (List<Token> tokenList : token.getBlockTokens()) {
                    for (Token blockToken : tokenList) {
                        data += "\n\t{";
                        data += JSONfromToken(blockToken, indent+1);
                        data += "\n\t},";
                    }
                }
                data = data.substring(0,data.length()-1);
                data += "\n],\n";
            case FUNCTION_CALL:
                data += "\"params\": [";
                for (Token param : token.getParams().get(0)) {
                    data += "\n\t{";
                    data += JSONfromToken(param, indent+1);
                    data += "\n\t},";
                }
                data = data.substring(0,data.length()-1);
                data += "\n]\n";
                break;
            default:
                data = data.substring(0,data.length()-1);
                break;
        }       
        return data.substring(0,data.length()-1).replace("\n", "\n" + "\t".repeat(indent));
    }
}
