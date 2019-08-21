public class IncrementalNumberGenerator extends NumberGenerator {
    private int number;
    private int startNum;
    private int endNum;
    private int increment;

    public IncrementalNumberGenerator(int startNum, int endNum, int increment) {
        this.startNum = startNum;
        this.endNum = endNum;
        this.increment = increment;
    }

    public int getNumber() {
        return number;
    }
    public void execute() {
        for(int i = startNum; i < endNum; i+= increment) {
            number = i;
            notifyObservers();
        }
    }
}