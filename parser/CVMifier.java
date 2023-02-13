package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class CVMifier implements Serializer{

    public void save(String path) {
        int size = Function.funcMap.entrySet().size();
        int inBuiltsSize = Function.inBuiltFunctions.length;
        String[] functionNames = new String[size];
        int i =0;
        for (int j = 0; j < inBuiltsSize; j++) {
            functionNames[j] = Function.inBuiltFunctions[j].getName();
            i++;
        }
        for (Entry<String,Function> entry : Function.funcMap.entrySet()) {
            if(!entry.getValue().isInBuiltFunction()){
                functionNames[i] = entry.getKey();
                i++;
            }
        }
        for(int j = inBuiltsSize; j < functionNames.length; j++) {
            if(functionNames[j].equals("main")) {
                String sbuff = functionNames[inBuiltsSize];
                functionNames[inBuiltsSize]=functionNames[j];
                functionNames[j]=sbuff;
                break;
            }
        }
        for (int j = 0; j < functionNames.length; j++) {
            System.out.println(functionNames[j]);
        }
    }
}
