import java.util.ArrayList;
import java.util.List;

public class Room {
    private LolMap map;
    private List<User> users;
    private ChampionPool championPool;
    
    private List<Champion> banList;
    private List<Champion> pickList;
    //private Set<Champion> pickSet;

    public Room(LolMap map, List<User> users) {
        this.map = map;
        this.users = users;
        championPool = new ChampionPool();

        banList = new ArrayList<>();
        pickList = new ArrayList<>();
    }
}