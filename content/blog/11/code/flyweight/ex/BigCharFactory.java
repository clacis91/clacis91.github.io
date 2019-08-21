import java.util.HashMap;

public class BigCharFactory {
    private HashMap pool = new HashMap();
    private static BigCharFactory sigleton = new BigCharFactory();
    private BigCharFactory() {}
    public static BigCharFactory getInstance() {
        return sigleton;
    }
    public synchronized BigChar getBigChar(char charname) {
        BigChar bigChar = (BigChar) pool.get(charname);
        if(bigChar == null) {
            bigChar = new BigChar(charname);
            pool.put(charname, bigChar);
        }
        return bigChar;
    }
}