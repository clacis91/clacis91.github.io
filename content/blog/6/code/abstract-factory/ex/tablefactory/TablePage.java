package tablefactory;

import java.util.Iterator;

import factory.*;

public class TablePage extends Page {
    public TablePage(String title, String author) {
        super(title, author);
    }

    public String makeHTML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<html><head><title>" + title + "</title></head>\n");
        buf.append("<body>\n");
        buf.append("<h1>" + title + "</h1>\n");
        buf.append("<table>\n");
        Iterator it = content.iterator();
        while(it.hasNext()) {
            Item item = (Item) it.next();
            buf.append("<tr>" + item.makeHTML() + "</tr>");
        }
        buf.append("</table>\n");
        buf.append("<hr><address>" + author + "</address>");
        buf.append("</body></html>\n");
        return buf.toString();
    }
}