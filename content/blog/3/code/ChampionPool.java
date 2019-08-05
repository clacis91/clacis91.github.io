import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChampionPool {
    private List<Champion> champions;

    public ChampionPool() {
        champions = new ArrayList<>();
    }

    public void registerChampions(Champion... newChampions) {
        for(Champion champion : newChampions)
            champions.add(champion);
    }

    public Champion getChampion(int index) {
        return champions.get(index);
    }

    public List<Champion> getChampions() {
        return champions;
    }
    
    public void showChampions() {
        StringBuilder sb = new StringBuilder();

        for(Champion champion : champions) {
            sb.append( String.format("%-10s", champion.getName()) );
        }

        System.out.println(sb.toString());
    }

    public Champion randomSelect() {
        return champions.get( (int) (Math.random() * champions.size()) );
    }
}