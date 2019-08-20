public class Trouble {
    private int num;
    public Trouble(int num) {
        this.num = num;
    }
    public int getNum() {
        return num;
    }
    public String toString() {
        return "[Trouble " + String.valueOf(num) + "]";
    }
}