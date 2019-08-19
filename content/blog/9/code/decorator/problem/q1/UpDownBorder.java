public class UpDownBorder extends Border {
    public UpDownBorder(Display display) {
        super(display);
    }

    public int getColumns() {
        return display.getColumns();
    }

    public int getRows() {
        return display.getRows() + 2;
    }

    public String getRowText(int row) {
        if(row == 0 || row == (display.getRows() + 1)) {
            return makeLine('=', display.getColumns());
        }
        else {
            return display.getRowText(row - 1);
        }
    }

    private String makeLine(char ch, int cnt) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < cnt; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }
}