package listfactory;

import java.util.Iterator;

import factory.*;

public class ListPage extends Page {
    public ListPage(String title, String author) {
        super(title, author);
    }

    public String makeHTML() {
        StringBuffer buf = new StringBuffer();
        buf.append("<html><head><title>" + title + "</title></head>\n");
        buf.append("<body>\n");
        buf.append("<h1>" + title + "</h1>\n");
        buf.append("<ul>\n");
        Iterator it = content.iterator();
        while(it.hasNext()) {
            Item item = (Item) it.next();
            buf.append(item.makeHTML());
        }
        buf.append("</ul>\n");
        buf.append("<hr><address>" + author + "</address>");
        buf.append("</body></html>\n");
        return buf.toString();
    }
}