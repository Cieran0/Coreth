package parser;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

public class CVMifier implements Serializer{

    private static String[] FunctionNameArray = new String[0];
    private static Integer indexOfMain = 0;

    private static List<String> strings = new ArrayList<String>();
    public static short getStringID(String s) {
        if(!strings.contains(s)) {
            strings.add(s);
            return (short)(strings.size()-1);
        }
        for (short i = 0; i < strings.size(); i++) {
            if(strings.get(i).equals(s)) 
                return i;
        }
        return -1;
    }

    private String byteArrayToString(byte[] bytes) {
        if(bytes.length < 1) return "";
        String s = "";
        for (byte b : bytes) {
            s += b + ",";
        }
        return s.substring(0,s.length()-1);
    }

    public static byte[] combineBytes(byte[] array1, byte[] array2) {
        byte[] newByteArray = new byte[array1.length+array2.length];
        for (int i = 0; i < array1.length; i++) {
            newByteArray[i]=array1[i];
        }
        for (int i = 0; i < array2.length; i++) {
            newByteArray[i+array1.length]=array2[i];
        }
        return newByteArray;
    }

    public static byte[] stringToByteArray(String s) {
        byte[] bytes = new byte[s.length()+1];
        for (int i = 0; i < s.length(); i++) {
            bytes[i]=(byte)s.charAt(i);
        }
        bytes[bytes.length-1] = 0;
        return bytes;
    }

    public static byte[] longToByteArray(long l) {
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(l).array();
    }

    public static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array();
    }

    public static byte[] shortToByteArray(short s) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(s).array();
    }

    public static byte[] byteToByteArray(byte b) {
        return new byte[]{b};
    }

    public void save(String path) {
        byte[] finalData = new byte[0];
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
        FunctionNameArray = Arrays.stream(functionNames).filter(s -> Function.funcMap.get(s).isUsed()).toArray(String[]::new);
        for (int j = 0; j < functionNames.length; j++) {
            if(functionNames[j].equals("main")) {
                indexOfMain = j;
            }
        }

        for (int j = 0; j < FunctionNameArray.length; j++) {
            Function f = Function.funcMap.get(FunctionNameArray[j]);
            if(!f.isUsed()) continue;
            byte[] info = shortToByteArray((short)f.variableNames.size());
            byte[] variable_types = new byte[f.variableNames.size()];
            for (int k = 0; k < f.variableNames.size(); k++) {
                variable_types[k] = (byte)f.localNameVariableTypeMap.get(f.variableNames.get(k)).ordinal();
            }
            info = combineBytes(info, variable_types);
            byte[] tokenData = new byte[0];
            for (List<Token> tokens : f.getTokens()) {
                if(tokens==null)continue;
                for (Token t : tokens) {
                    tokenData = combineBytes(tokenData, t.getData());
                }
            }
            byte[] functionData = combineBytes(info, tokenData);
            functionData = combineBytes((shortToByteArray((short)(functionData.length-2))), functionData);
            System.out.println(functionData.length-4);
            finalData = combineBytes(finalData, functionData);
        }
        byte[] strData = new byte[0];
        for (String string : strings) {
            strData = combineBytes(strData, stringToByteArray(string));
        }
        long strOffset = finalData.length+8; 
        finalData = combineBytes(finalData, strData);
        finalData = combineBytes(longToByteArray(strOffset), finalData);
        writeBytesToFile(finalData, path);
    }

    private void writeBytesToFile(byte[] bytes, String path) {
        try {
            Files.write(new File(path).toPath(), bytes, StandardOpenOption.CREATE);
        } catch (IOException e) {
            System.out.println("Failed To Save File.");
            e.printStackTrace();
        }
    }

    public static short getFunctionID(String name) {
        for (int i = 0; i < Function.inBuiltFunctions.length; i++) {
            if(name.equals(Function.inBuiltFunctions[i].getName()))
                return (short)((-1)*(i+1));
        }
        for (int i = 0; i < FunctionNameArray.length; i++) {
            if(FunctionNameArray[i].equals(name))
                return (short)(i);
        }
        return 0;
    }
}
