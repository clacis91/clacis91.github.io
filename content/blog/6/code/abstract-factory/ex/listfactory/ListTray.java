package listfactory;

import java.util.Iterator;
import factory.*;

public class ListTray extends Tray {
    public ListTray(String caption) {
        super(caption);
    }

    public String makeHTML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<li>\n");
        buf.append(caption + "\n");
        buf.append("<ul>\n");
        Iterator it = tray.iterator();
        while(it.hasNext()) {
            Item item = (Item) it.next();
            buf.append(item.makeHTML());
        }
        buf.append("</ul>\n");
        buf.append("</li>\n");
        return buf.toString();
    }
}