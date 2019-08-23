public class Ai implements Summoner {
    private String summonerId;
    private Champion pick;
    private boolean turn;

    public Ai(String summonerId) {
        this.summonerId = summonerId;
    }

    @Override
    public Champion selectChampion(ChampionPool champions) {
        Champion selected = champions.randomSelect();
        selected.setDisable();
        return selected;
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
}