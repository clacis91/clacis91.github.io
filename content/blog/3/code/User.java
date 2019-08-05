import java.util.Scanner;

public class User implements Summoner {
    private String summonerId;
    //private ? ban;
    private String pick;
    private boolean turn;

    public User(String summonerId) {
        this.summonerId = summonerId;
    }

    @Override
    public void selectChampion(ChampionPool championPool) {
        System.out.println("- " + summonerId + " 소환사님 선택 차례 -");
        System.out.println("선택 가능 챔피언 목록");
        championPool.showChampions();
        System.out.println("랜덤 선택 : -1");
        
        Scanner sc = new Scanner(System.in);
        Champion selectedChampion = getSelectChampion(championPool, sc.nextLine());
        pick = selectedChampion.getName();
    }

    @Override
    public String getPick() {
        return pick;
    }

    public void receiveTurn() {
    }

    private Champion getSelectChampion(ChampionPool champions, String in) {
        int sel = Integer.parseInt(in);

        switch(sel) {
            case -1 :
                return champions.randomSelect();
            default :
                return champions.getChampion(sel);
        }
    }
}