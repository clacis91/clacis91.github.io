import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.StringBuffer;

public class BigChar {
    private char charname;
    private String fontdata;
    public BigChar(char charname) {
        this.charname = charname;
        try {
            BufferedReader reader = new BufferedReader(
                new FileReader("big" + charname + ".txt")
            );
            String line;
            StringBuffer sb = new StringBuffer();
            while((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();
            this.fontdata = sb.toString();
        } catch(IOException e) {
            e.printStackTrace();
            this.fontdata = charname + "?";
        }
    }

    public void print() {
        System.out.println(fontdata);
    }
}