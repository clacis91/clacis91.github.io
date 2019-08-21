public class BigString {
    private BigChar[] bigString;
    public BigString(String string) {
        bigString = new BigChar[string.length()];
        BigCharFactory factory = BigCharFactory.getInstance();
        for(int i = 0; i < bigString.length; i++) {
            bigString[i] = factory.getBigChar(string.charAt(i));
        }
    }
    public void print() {
        for(int i = 0; i < bigString.length; i++) {
            bigString[i].print();
        }
    }
}