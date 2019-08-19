public class Main {
    public static void main(String[] args) {
        MultiStringDisplay d1 = new MultiStringDisplay();
        d1.add("Hello world!");
        d1.add("How are you?");
        d1.add("Goodbye world~");
        Display d2 = new SideBorder(d1, '#');
        Display d3 = new FullBorder(d2);

        d1.show();
        d2.show();
        d3.show();
    }
}