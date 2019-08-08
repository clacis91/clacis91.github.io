import factory.*;

public class Main {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage java Main class.name.of.ConcreteFactory");
            System.out.println("Ex1 : java Main listfactory.ListFactory");
            System.out.println("Ex2 : java Main tablefactory.TableFactory");
            System.exit(0);
        }

        Factory factory = Factory.getFactory(args[0]);

        Link naver = factory.createLink("네이버", "naver.com");
        Link google = factory.createLink("구글", "google.com");

        Link facebook = factory.createLink("페이스북", "facebook.com");
        Link instagram = factory.createLink("인스타그램", "instagram.com");

        Tray traySearch = factory.createTray("검색");
        traySearch.add(naver);
        traySearch.add(google);

        Tray traySns = factory.createTray("SNS");
        traySns.add(facebook);
        traySns.add(instagram);

        Page page = factory.createPage("LinkPage", "하이");
        page.add(traySearch);
        page.add(traySns);
        page.output();

    }
}