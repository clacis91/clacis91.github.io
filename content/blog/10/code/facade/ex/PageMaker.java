import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class PageMaker {
    private PageMaker() {}

    public static void makeWelcomePage(String mailaddr, String filename) {
        try {
            Properties mailprop = Database.getPropertiess("maildata");
            String username = mailprop.getProperty(mailaddr);
            Htmlwriter writer = new Htmlwriter(new FileWriter(filename));
            writer.title("Welcome to " + username + "'s page!");
            writer.paragraph("Please mail to me");
            writer.mailto(mailaddr, username);
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeLinkPage(String filename) {
        try {
            Properties mailprop = Database.getPropertiess("maildata");
            Htmlwriter writer = new Htmlwriter(new FileWriter(filename));

            writer.title("Link page");

            Iterator it = mailprop.keys().asIterator();

            while(it.hasNext()) {
                String mailaddr = (String) it.next();
                writer.mailto(mailaddr, mailprop.get(mailaddr).toString());                
            }
            
            writer.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}