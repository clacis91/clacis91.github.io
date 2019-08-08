package tablefactory;

import java.util.Iterator;
import factory.*;

public class TableTray extends Tray {
    public TableTray(String caption) {
        super(caption);
    }

    public String makeHTML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<td>\n");
        buf.append("<table width=\"100%\" border=\"1\">\n");
        buf.append("<tr>\n");
        buf.append("<td colspan=\"" + tray.size() + "\"><b>" + caption + "</b></td>\n");
        buf.append("</tr>\n");
        buf.append("<tr>\n");
        Iterator it = tray.iterator();
        while(it.hasNext()) {
            Item item = (Item) it.next();
            buf.append(item.makeHTML());
        }
        buf.append("</tr></table>\n");
        buf.append("</td>\n");
        return buf.toString();
    }
}