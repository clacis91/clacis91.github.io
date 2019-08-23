import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Room {
    private LolMap map;
    private List<Summoner> summoners;
    private List<Summoner> blueTeam;
    private List<Summoner> purpleTeam;
    private ChampionPool championPool;
    
    private List<Champion> banList;
    private List<Champion> pickList;

    public Room(LolMap map, List<Summoner> summoners) {
        ChampionPoolGenerator championPoolGenerator = new ChampionPoolGenerator();
        championPool = championPoolGenerator.generate();

        this.map = map;
        this.summoners = summoners;

        divideTeam();

        banList = new ArrayList<>();
        pickList = new ArrayList<>();
    }

    private void divideTeam() {
        int summonerCnt = map.getSummonerCnt();
        Collections.shuffle(this.summoners);

        blueTeam = new ArrayList<>();
        purpleTeam = new ArrayList<>();

        blueTeam.addAll(this.summoners.subList(0, summonerCnt - 1));
        purpleTeam.addAll(this.summoners.subList(summonerCnt, this.summoners.size() - 1));
    }

    public void progressBan() {
        System.out.println("=== 챔피언 금지 ===");
        for(Summoner summoner : summoners) {
            System.out.println("- " + summoner.getSummonerId() + " 소환사님 금지 차례 -");
            Champion selected = summoner.selectChampion(championPool);
            banList.add(selected);
            System.out.println(summoner.getSummonerId() + "님의 밴 : " + selected.getName());
        }
        System.out.println("금지된 챔피언 목록" + banList);
    }

    public void progressPick() {
        // TODO : LolMap 객체의 rule 메소드로 다음 선택할 소환사를 결정하는 로직 구현
        System.out.println("=== 챔피언 선택 ===");
        for(Summoner summoner : summoners) {
            System.out.println("- " + summoner.getSummonerId() + " 소환사님 선택 차례 -");
            Champion selected = summoner.selectChampion(championPool);
            summoner.setPick(selected);
            pickList.add(selected);
            System.out.println(summoner.getSummonerId() + "님의 픽 : " + selected.getName());
        }
    }

    public void printBanPickResult() {
        System.out.println("=== 밴픽 결과 ===");
        System.out.println("금지된 챔피언 목록" + banList);
        System.out.println("챔피언 선택 결과");
        for(Summoner summoner : summoners) {
            System.out.println(summoner.getSummonerId() + " 님 : " + summoner.getPick().getName());
        }
    }

}