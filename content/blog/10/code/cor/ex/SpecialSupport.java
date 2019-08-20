public class SpecialSupport extends Support {
    private int num;
    public SpecialSupport(String name, int num) {
        super(name);
        this.num = num;
    }

    protected boolean resolve(Trouble trouble) {
        return (trouble.getNum() == num) ? true : false;
    }
}