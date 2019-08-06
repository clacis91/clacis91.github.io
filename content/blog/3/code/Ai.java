public class Ai implements Summoner {
    private String summonerId;
    private Champion pick;
    private Champion ban;
    private boolean turn;

    @Override
    public Champion selectChampion(ChampionPool champions) {
        return null;
    }

    @Override
    public void setPick(Champion champion) {
        this.pick = champion;
    }

    @Override
    public void setBan(Champion champion) {
        this.ban = champion;
    }

    @Override
    public Champion getPick() {
        return pick;
    }

    @Override
    public Champion getBan() {
        return ban;
    }

    @Override
    public void receiveTurn() {
    }
}