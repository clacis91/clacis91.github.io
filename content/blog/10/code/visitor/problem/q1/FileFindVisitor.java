import java.util.ArrayList;
import java.util.Iterator;

public class FileFindVisitor extends Visitor {
    private ArrayList<File> targetFiles = new ArrayList<>();
    private String targetExtension;

    public FileFindVisitor(String targetExtension) {
        this.targetExtension = targetExtension;
    }
    public Iterator iterator() {
        return targetFiles.iterator();
    }

    public void visit(File file) {
        if(file.getName().endsWith(targetExtension) ) {
            targetFiles.add(file);
        }
    }
    public void visit(Directory directory) {
        Iterator it = directory.iterator();
        while(it.hasNext()) {
            Entry entry = (Entry) it.next();
            entry.accept(this);
        }
    }
}