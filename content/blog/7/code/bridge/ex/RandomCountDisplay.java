public class RandomCountDisplay extends CountDisplay {
    public RandomCountDisplay(DisplayImpl impl) {
        super(impl);
    }

    public void randomDisplay(int times) {
        int randomCnt = (int) (Math.random() * (times-1) + 1);
        multiDisplay(randomCnt);
    }
}