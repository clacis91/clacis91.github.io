public class Main {
    public static void main(String[] args) {
        Display d1 = new Display(new StringDisplayImpl("Hello, World!"));
        Display d2 = new CountDisplay(new StringDisplayImpl("World, Hello!"));

        CountDisplay d3 = new CountDisplay(new StringDisplayImpl("World, Bye!"));

        d1.display();
        d2.display();
        d3.display();
        d3.multiDisplay(3);

        RandomCountDisplay d4 = new RandomCountDisplay(new StringDisplayImpl("World, Bye!"));
        d4.randomDisplay(5);

        Display d5 = new Display(new FileDisplayImpl("q2.txt"));
        d5.display();
        CountDisplay d6 = new CountDisplay(new FileDisplayImpl("q2.txt"));
        d6.multiDisplay(3);
    }
}