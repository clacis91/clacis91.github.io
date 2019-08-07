import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Summoner u1 = new User("에블바디언더스텐");
        Summoner u2 = new Ai("ManySolutions");
        Summoner u3 = new User("바데야");
        List<Summoner> entry = new ArrayList<>();
        entry.add(u1);
        entry.add(u2);
        entry.add(u3);

        Room room = new Room(null, entry);
        room.progressPick();
    }
}