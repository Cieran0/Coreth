package parser;

public class TokenLiteralNum extends Token{

    private int value;

    public TokenLiteralNum(int value, Integer lineNo, Integer charNo) {
        this.value = value;
        this.name = Integer.toString(value);
        this.lineNo = lineNo;
        this.charNo = charNo;
        this.type = TokenType.LITERAL_NUM;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("Value: " + this.value);
    }
}
