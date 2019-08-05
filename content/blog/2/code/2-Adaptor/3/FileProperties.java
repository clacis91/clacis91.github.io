import java.util.Properties;
import java.io.*;

public class FileProperties implements FileIO {
    Properties properties;

    public FileProperties() {
        properties = new Properties();
    }

    public void readFromFile(String filename) throws IOException {
        File file = new File(filename);
        FileReader filereader = new FileReader(file);

        properties.load(filereader);
    }

    public void writeToFile(String filename) throws IOException {
        File file = new File(filename);
        FileWriter filewriter = new FileWriter(file);

        properties.store(filewriter, "Written by FileProperties");
    }

    public void setValue(String key, String value) {
        properties.setProperty(key, value);
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }
}