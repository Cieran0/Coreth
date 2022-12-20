package parser;

public abstract class Token {
    public static enum TokenType {
        FUNCTION_CALL,
        LITERAL_NUM, 
        LITERAL_STRING,
        VARIABLE_DECLARATION,
        VARIABLE_REFRENCE
    }

    protected String name;
    protected TokenType type;
    protected Integer lineNo;
    protected Integer charNo;

    public String getName() {
        return this.name;
    }

    public TokenType getType() {
        return this.type;
    }

    public void printInfo() {
        System.out.println("Name: "  +this.name);
        System.out.println("Type: "  +this.type);
        System.out.println("Line: "  +this.lineNo);
        System.out.println("Char: "  +this.charNo);
    }

    public int toNumber() {
        if(this.type != TokenType.LITERAL_NUM && this.type != TokenType.VARIABLE_REFRENCE) {
            Parser.exitWithError("Expected type " + TokenType.LITERAL_NUM + " got " + this.type + "!",-3);
        }
        if(this.type == TokenType.LITERAL_NUM)
            return ((TokenLiteralNum)this).getValue();
        return ((TokenVariableRefrence<Integer>)this).getValue();
    }

    public String toStringLit() {
        if(this.type != TokenType.LITERAL_STRING && this.type != TokenType.VARIABLE_REFRENCE) {
            Parser.exitWithError("Expected type " + TokenType.LITERAL_STRING + " got " + this.type + "!",-4);
        }
        if(this.type == TokenType.LITERAL_STRING)
            return ((TokenLiteralString)this).getValue();
        return ((TokenVariableRefrence<String>)this).getValue();
    }
}
