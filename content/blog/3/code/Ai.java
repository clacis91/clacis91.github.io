public class Ai implements Summoner {
    private String summonerId;
    //private ? ban;
    private String pick;
    private boolean turn;

    @Override
    public void selectChampion(ChampionPool championPool) {
    }

    @Override
    public String getPick() {
        return "";
    }

    public void receiveTurn() {
    }
}