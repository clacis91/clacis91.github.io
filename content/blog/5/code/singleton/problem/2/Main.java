public class Main {
    public static void main(String[] args) {
        Triple obj1 = Triple.getInstance(0);
        Triple obj2 = Triple.getInstance(1);
        Triple obj3 = Triple.getInstance(2);
        Triple obj4 = Triple.getInstance(1);

        System.out.println(obj1.toString());
        System.out.println(obj2.toString());
        System.out.println(obj3.toString());
        System.out.println(obj4.toString());
    }
}