import java.util.Iterator;

public class ListVisitor extends Visitor {
    private String curDir = "";
    public void visit(File file) {
        System.out.println(curDir + "/" + file);
    }
    public void visit(Directory directory) {
        System.out.println(curDir + "/" + directory);
        String tmpDir = curDir;
        curDir = curDir + "/" + directory.getName();
        Iterator it = directory.iterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            entry.accept(this);
        }
        curDir = tmpDir;
    }
}