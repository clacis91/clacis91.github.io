public class Main {
    public static void main(String[] args) {
        Support s1 = new NoSupport("S1");
        Support s2 = new LimitSupport("LIMIT", 100);
        Support s3 = new SpecialSupport("SPECIAL", 429);
        Support s4 = new OddSupport("ODD");
        Support s5 = new LimitSupport("LIMIT", 300);

        s1.setNext(s2).setNext(s3).setNext(s4).setNext(s5);
        
        for(int i = 0; i < 500; i+=33) {
            s1.support(new Trouble(i));
        }
    }
}