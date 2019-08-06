import java.util.ArrayList;
import java.util.List;

public class Room {
    private LolMap map;
    private List<Summoner> summoners;
    private ChampionPool championPool;
    
    private List<Champion> banList;
    private List<Champion> pickList;
    //private Set<Champion> pickSet;

    public Room(LolMap map, List<Summoner> summoners) {
        this.map = map;
        this.summoners = summoners;
        initChampionPool();

        banList = new ArrayList<>();
        pickList = new ArrayList<>();
    }

    public void progressBan() {
        for(Summoner summoner : summoners) {
            Champion selected = summoner.selectChampion(championPool);
            summoner.setBan(selected);
            //System.out.println(summoner.getPick());
        }
    }

    public void progressPick() {
        for(Summoner summoner : summoners) {
            Champion selected = summoner.selectChampion(championPool);
            summoner.setPick(selected);
            System.out.println(summoner.getPick().getName());
        }
    }

    private void initChampionPool() {
        championPool = new ChampionPool();
        // TODO : Champion의 필드가 변경된다면 어떻게 대응?? -> Champion builder 생성
        Champion garen = new Champion("가렌"); 
        Champion ashe = new Champion("애쉬");
        Champion ahri = new Champion("아리");

        championPool.registerChampions(garen, ashe, ahri);
    }
}