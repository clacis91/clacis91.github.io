import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        /*
        Champion garen = new Champion("가렌");
        Champion ashe = new Champion("애쉬");
        Champion ahri = new Champion("아리");

        ChampionPool championPool = new ChampionPool();
        championPool.registerChampions(garen, ashe, ahri);
        */

        Summoner u1 = new User("에블바디언더스텐");
        Summoner u2 = new User("ManySolutions");
        List<Summoner> entry = new ArrayList<>();
        entry.add(u1);
        entry.add(u2);

        Room room = new Room(null, entry);
        room.progressPick();

        //u1.selectChampion(championPool);
        //u2.selectChampion(championPool);
        //System.out.println(u1.getPick());
        //System.out.println(u2.getPick());
    }
}