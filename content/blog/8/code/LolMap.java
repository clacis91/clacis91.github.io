public class LolMap {
    private String name;
    private int summonerCnt; // N vs N

    public LolMap(String name, int summonerCnt) {
        this.name = name;
        this.summonerCnt = summonerCnt;
    }

    public void pickRule() {
        
    }

    public String getName() {
        return name;
    }

    public int getSummonerCnt() {
        return summonerCnt;
    }
}