import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileDisplayImpl extends DisplayImpl {
    private String filename;
    private BufferedReader reader;

    public FileDisplayImpl(String filename) {
        this.filename = filename;
    }

    public void rawOpen() {
        try {
            reader = new BufferedReader(new FileReader(filename));
            reader.mark(4096);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void rawClose() {
        try {
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void rawPrint() {
        try {
            System.out.println("==========FILE READER========");
            String fileLine;
            reader.reset();
            while( (fileLine = reader.readLine()) != null ) {
                System.out.println("|" + fileLine + "|");
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}