package parser;

public class TokenLiteralString extends Token{

    private String value;

    public TokenLiteralString(String value, Integer lineNo, Integer charNo) {
        this.value = value;
        this.name = value;
        this.lineNo = lineNo;
        this.charNo = charNo;
        this.type = TokenType.LITERAL_STRING;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Value: " + this.value);
    }
}
