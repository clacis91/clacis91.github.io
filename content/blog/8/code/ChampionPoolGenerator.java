import java.util.Iterator;
import java.util.stream.Stream;

// TODO : Singleton으로 변경?
public class ChampionPoolGenerator {
    private ChampionDBManager championDBManager;
    private Stream dbStream;

    private ChampionPool championPool;

    public ChampionPoolGenerator() {
        championPool = new ChampionPool();
        championDBManager = new ChampionFileDBManager();
    }

    public ChampionPool generate() {
        dbStream = championDBManager.DBReader();
        if(dbStream == null) {
            System.out.println("Champion DB error!");
            return null;
        }

        Iterator iter = dbStream.iterator();
        while(iter.hasNext()) {
            Champion champion = championDBManager.parseChampion(iter.next());
            championPool.registerChampions(champion);
        }
        return championPool;
    }
}