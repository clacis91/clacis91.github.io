import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

// TODO : Singleton으로 변경?
public class LolMapGenerator {
    private MapDBManager mapDBManager;
    private Stream dbStream;

    public LolMapGenerator() {
        mapDBManager = new MapFileDBManager();
    }

    public LolMap generate(int mapIdx) {
        List<LolMap> mapList = getMapList();
        return mapList.get(mapIdx);
    }

    private List<LolMap> getMapList() {
        dbStream = mapDBManager.DBReader();
        if(dbStream == null) {
            System.out.println("Map DB error!");
            return null;
        }

        List<LolMap> mapList = new ArrayList<>();

        Iterator iter = dbStream.iterator();
        while(iter.hasNext()) {
            LolMap lolmap = mapDBManager.parseMap(iter.next());
            mapList.add(lolmap);
        }
        return mapList;
    }
}