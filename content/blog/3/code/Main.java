public class Main {
    public static void main(String[] args) {
        Champion garen = new Champion("가렌");
        Champion ashe = new Champion("애쉬");
        Champion ahri = new Champion("아리");

        ChampionPool championPool = new ChampionPool();
        championPool.registerChampions(garen, ashe, ahri);

        Summoner u1 = new User("에블바디언더스텐");
        u1.selectChampion(championPool);
        
        //System.out.println("- 선택 가능한 챔피언 -");
        //championPool.showChampions();
        //System.out.println("챔피언 랜덤 선택");
        //Champion userSelect = championPool.randomSelect();
        String userSelect = u1.getPick();
        System.out.println(userSelect);
    }
}