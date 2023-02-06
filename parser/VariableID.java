package parser;

import java.util.ArrayList;

public class VariableID {
    private Function scope;
    private String name;
    private static ArrayList<VariableID> listOfVariableIDs = new ArrayList<VariableID>();

    public VariableID(Function scope, String name){
        this.scope = scope;
        this.name = name;
    }

    public static Integer addNewVariableIDToList(Function scope, String name) {
        listOfVariableIDs.add(new VariableID(scope, name));
        return listOfVariableIDs.size()-1;
    }

    public static Integer getVariableID(Function scope, String name) {
        for (int index = 0; index < listOfVariableIDs.size(); index++) {
            VariableID current =listOfVariableIDs.get(index); 
            if(current.name.equals(name) && current.scope.getName() == scope.getName()){
                return index;
            }
        }
        return null;
    }

}
