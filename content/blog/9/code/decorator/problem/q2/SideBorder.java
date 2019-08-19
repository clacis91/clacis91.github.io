public class SideBorder extends Border {
    private char borderChar;
    public SideBorder(Display display, char ch) {
        super(display);
        this.borderChar = ch;
    }

    public int getColumns() {
        return display.getColumns() + 2;
    }

    public int getRows() {
        return display.getRows();
    }

    public String getRowText(int row) {
        return borderChar 
            + String.format("%-" + display.getColumns() + "s", display.getRowText(row)) 
            + borderChar;
    }
}