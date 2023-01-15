package parser;

import java.util.HashMap;
import java.util.List;

public class Syscalls {

    public static HashMap<Integer,BuiltInFunction> syscallsMap = new HashMap<Integer,BuiltInFunction>() {

        @Override
        public BuiltInFunction get(Object key) {
            BuiltInFunction value = super.get(key);
            return (value == null)? null : value;
        };

    };

    public static void SetUpMap() {
        syscallsMap.put(0, read);
        syscallsMap.put(1, write);
        syscallsMap.put(2, open);
        syscallsMap.put(3, close);
    }

    public static BuiltInFunction syscall = new BuiltInFunction() {
        @Override
        public Token run(List<Token> params) {
            return syscallsMap.get(params.get(0).getInt()).run(params);
        }
    };

    private static BuiltInFunction syscallNotSimulatedYet = new BuiltInFunction() {
        @Override
        public Token run(java.util.List<Token> params) {
            System.out.println("Sorry syscall ["+params.get(0).getInt()+"] is not currently supported in simulate mode :'(");
            return Token.new_NULLToken();
        };
    };


    private static BuiltInFunction read = new BuiltInFunction() {
        @Override
        public Token run(List<Token> params) {
            //TODO: implement read
            return syscallNotSimulatedYet.run(params);
        }
    };

    private static BuiltInFunction write = new BuiltInFunction() {
        @Override
        public Token run(List<Token> params) {
            int fd = params.get(1).getInt();
            String text = Function.stringPointers.get(params.get(0).getInt());
            int count = params.get(3).getInt();
            FileManagement.writeToFile(text.substring(0, count-1), fd);
            return Token.new_NULLToken();
        }
    };

    private static BuiltInFunction open = new BuiltInFunction() {
        @Override
        public Token run(List<Token> params) {
            String path = Function.stringPointers.get(params.get(1).getInt());
            int fd = FileManagement.OpenFile(path, false);
            return Token.new_LiteralNum(-1,fd);
        }
    };

    private static BuiltInFunction close = new BuiltInFunction() {
        @Override
        public Token run(List<Token> params) {
            //TODO: implement close
            return syscallNotSimulatedYet.run(params);
        }
    };

}
