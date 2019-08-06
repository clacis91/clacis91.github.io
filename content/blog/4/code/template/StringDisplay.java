public class StringDisplay extends AbstractDisplay {
    private String str;
    private int strLen;

    public StringDisplay(String str) {
        this.str = str;
        this.strLen = str.length();
    }
    
    public void open() {
        printLine();
    }
    public void close() {
        printLine();
    }
    public void print() {
        System.out.println("|" + str + "|");
    }
    private void printLine() {
        StringBuilder sb = new StringBuilder();
        sb.append("+");
        for(int i = 0; i < strLen; i++) 
            sb.append("-");
        sb.append("+");
        System.out.println(sb.toString());
    }
}