package parser;

import java.util.List;

public interface BuiltInFunction {
    
    public Token run(List<Token> params);

}
