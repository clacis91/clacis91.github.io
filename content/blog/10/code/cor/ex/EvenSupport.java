public class EvenSupport extends Support {
    public EvenSupport(String name) {
        super(name);
    }

    protected boolean resolve(Trouble trouble) {
        return (trouble.getNum() % 2 == 0) ? true : false;
    }
}