import java.util.Scanner;

public class User implements Summoner {
    private String summonerId;
    private Champion pick;
    private boolean turn;

    public User(String summonerId) {
        this.summonerId = summonerId;
    }

    @Override
    public Champion selectChampion(ChampionPool champions) {
        System.out.println("선택 가능 챔피언 목록");
        champions.showChampions();
        System.out.println("랜덤 선택 : -1");
        Scanner sc = new Scanner(System.in);

        Champion selectedChampion = getSelectChampion(champions, sc.nextLine());
        selectedChampion.setDisable();
        //pick = selectedChampion.getName();
        return selectedChampion;
    }

    @Override
    public void setPick(Champion champion) {
        this.pick = champion;
    }

    @Override
    public Champion getPick() {
        return pick;
    }

    @Override
    public void receiveTurn() {
    }

    @Override
    public String getSummonerId() {
        return summonerId;
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