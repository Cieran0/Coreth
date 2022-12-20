package parser;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import parser.Token.TokenType;

public abstract class BuiltInFunctions {

    public static void setUpMap() {
        new Function("write",
            (params) -> { write(params); },
            List.of(TokenType.LITERAL_NUM, TokenType.LITERAL_STRING)
        );
    }

    
    public static void write(List<Token> params) {
        if(params.get(0).toNumber() != 0) {
            Parser.exitWithError("Write's first paramater must be 0, was given " + params.get(0).toNumber(),-2);
        }
        System.out.println(params.get(1).toStringLit());
    }

}
