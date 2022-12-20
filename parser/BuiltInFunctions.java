package parser;

import java.util.List;

public abstract class BuiltInFunctions {

    public static void setUpMap() {
        new Function("write",
            (params) -> { write(params); },
            List.of(VariableType.INT, VariableType.STRING)
        );
    }

    
    public static void write(List<Token> params) {
        if(params.get(0).getInt() != 0) {
            Parser.exitWithError("Write's first paramater must be 0, was given " + params.get(0).getInt(),-2);
        }
        System.out.println(params.get(1).getString());
    }

}
