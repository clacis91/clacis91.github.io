public interface Summoner { 
    public Champion selectChampion(ChampionPool champions); 
    public void receiveTurn();

    public Champion getPick();
    public Champion getBan();
    public void setPick(Champion champion);
    public void setBan(Champion champion);
}