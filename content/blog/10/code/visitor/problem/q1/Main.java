import java.util.Iterator;

public class Main {
    public static void main(String[] args) {
        System.out.println("Making root entries...");
        Directory rootdir = new Directory("root");
        Directory usrdir = new Directory("usr");
        rootdir.add(usrdir);

        Directory Kim = new Directory("Kim");
        Directory Lee = new Directory("Lee");
        Directory Park = new Directory("Park");
        usrdir.add(Kim);
        usrdir.add(Lee);
        usrdir.add(Park);

        Kim.add(new File("diary.html", 100));
        Kim.add(new File("Visitor.java", 150));
        Lee.add(new File("memo.txt", 300));
        Lee.add(new File("game.doc", 200));
        Park.add(new File("index.html", 100));

        FileFindVisitor ffv = new FileFindVisitor(".html");
        rootdir.accept(ffv);

        Iterator it = ffv.iterator();
        while(it.hasNext()) {
            File file = (File) it.next();
            System.out.println(file);
        }
    }
}