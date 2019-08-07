import framework.*;

public class Main {
    public static void main(String[] args) {
        Manager manager = new Manager();
        Underliner underlinerProto = new Underliner('-');
        Boxer boxer1Proto = new Boxer('*');
        Boxer boxer2Proto = new Boxer('#');
        manager.register("underliner", underlinerProto);
        manager.register("boxer1", boxer1Proto);
        manager.register("boxer2", boxer2Proto);

        Product p1 = manager.create("underliner");
        Product p2 = manager.create("boxer1");
        Product p3 = manager.create("boxer2");

        p1.use("Hello world!");
        p2.use("Hello world!");
        p3.use("Hello world!");
    }
}