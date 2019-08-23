public interface Summoner { 
    public Champion selectChampion(ChampionPool champions); 
    public void receiveTurn();

    public String getSummonerId();
    public Champion getPick();
    public void setPick(Champion champion);
}