public class StringDisplayImpl extends DisplayImpl {
    private String str;
    private int width;

    public StringDisplayImpl(String str) {
        this.str = str;
        this.width = str.getBytes().length;
    }

    public void rawOpen() {
        printLine();
    }

    public void rawClose() {
        printLine();
    }

    public void rawPrint() {
        System.out.println("|" + str + "|");
    }

    private void printLine() {
        System.out.print("+");
        for(int i = 0; i < width; i++) 
            System.out.print("-");
        System.out.println("+");
    }
}