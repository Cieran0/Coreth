package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class CVMifier implements Serializer{

    private static List<String> strings = new ArrayList<String>();
    public static int getStringID(String s) {
        if(!strings.contains(s)) {
            strings.add(s);
            return strings.size()-1;
        }
        for (int i = 0; i < strings.size(); i++) {
            if(strings.get(i).equals(s)) 
                return i;
        }
        return -1;
    }

    public void save(String path) {
        int size = Function.funcMap.entrySet().size();
        int mainIndex = Function.inBuiltFunctions.length;
        String[] functionNames = new String[size];
        int i =0;
        for (int j = 0; j < mainIndex; j++) {
            functionNames[j] = Function.inBuiltFunctions[j].getName();
            i++;
        }
        for (Entry<String,Function> entry : Function.funcMap.entrySet()) {
            if(!entry.getValue().isInBuiltFunction()){
                functionNames[i] = entry.getKey();
                i++;
            }
        }
        for(int j = mainIndex; j < functionNames.length; j++) {
            if(functionNames[j].equals("main")) {
                String sbuff = functionNames[mainIndex];
                functionNames[mainIndex]=functionNames[j];
                functionNames[j]=sbuff;
                break;
            }
        }
        for (int j = mainIndex; j < functionNames.length; j++) {
            Function f = Function.funcMap.get(functionNames[j]);
            System.out.print(functionNames[j]);
            System.out.print(" { ");
            System.out.print(f.size() + ", ");
            System.out.print((f.getReturnType() != VariableType.VOID) + ", ");
            System.out.print(f.getExpectedParams().size() + " } { ");
            for (VariableType vt : f.getExpectedParams()) {
                System.out.print(vt + ", ");
            }
            System.out.println("}");
            System.out.print("{ ");
            if(!f.isInBuiltFunction()) {
                for (List<Token> tokens : f.getTokens()) {
                    if(tokens==null)continue;
                    for (Token t : tokens) {
                        System.out.print(t.getData()+", ");
                    }
                }
                System.out.println("}");
            }
        }
    }
}
