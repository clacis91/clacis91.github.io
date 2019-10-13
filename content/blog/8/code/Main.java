import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Summoner u1 = new User("에블바디언더스텐");
        Summoner u2 = new User("ManySolutions");
        Summoner u3 = new User("바데야");
        List<Summoner> entry = new ArrayList<>();
        entry.add(u1);
        entry.add(u2);
        entry.add(u3);

        Summoner ai1 = new Ai("봇1");
        Summoner ai2 = new Ai("봇2");
        Summoner ai3 = new Ai("봇3");
        entry.add(ai1);
        entry.add(ai2);
        entry.add(ai3);

        LolMap lolmap = new LolMapGenerator().generate(2);
        Room room = new Room(lolmap, entry);
        room.progressBan();
        room.progressPick();
        room.printBanPickResult();
    }
}