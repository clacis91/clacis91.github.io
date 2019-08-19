public class StringDisplay extends Display {
    private String str;
    public StringDisplay(String str) {
        this.str = str;
    }
    public int getColumns() {
        return str.getBytes().length;
    }
    public int getRows() {
        return 1;
    }
    public String getRowText(int row) {
        if(row == 0) {
            return str;
        }
        else {
            return null;
        }
    }
}