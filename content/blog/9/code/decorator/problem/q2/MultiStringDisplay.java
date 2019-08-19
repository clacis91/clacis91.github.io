import java.util.ArrayList;

public class MultiStringDisplay extends Display {
    private ArrayList<String> strLine;

    public MultiStringDisplay() {
        strLine = new ArrayList<String>();
    }

    public void add(String str) {
        strLine.add(str);
    }

    public int getColumns() {
        int longest = 0;
        for(String line : strLine) {
            longest = Math.max(longest, line.getBytes().length);
        }
        return longest;
    }

    public int getRows() {
        return strLine.size();
    }

    public String getRowText(int row) {
        if(row < strLine.size()) {
            return strLine.get(row);
        }
        else {
            return null;
        }
    }
}