package parser;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

    public byte[] combineBytes(byte[] array1, byte[] array2) {
        byte[] newByteArray = new byte[array1.length+array2.length];
        for (int i = 0; i < array1.length; i++) {
            newByteArray[i]=array1[i];
        }
        for (int i = 0; i < array2.length; i++) {
            newByteArray[i+array1.length]=array2[i];
        }
        return newByteArray;
    }

    public byte[] longToByteArray(long l) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(l).array();
    }

    public byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array();
    }

    public byte[] shortToByteArray(short s) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(s).array();
    }

    public byte[] byteToByteArray(byte b) {
        return new byte[]{b};
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
