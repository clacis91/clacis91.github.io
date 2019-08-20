import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Database {
    private Database(){} // new 로 인스턴스 생성하는 것을 방지

    public static Properties getPropertiess(String dbName) {
        String filename = dbName + ".txt";
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream(filename));
        } catch(IOException e) {
            e.printStackTrace();
        }
        return prop;
    }
}