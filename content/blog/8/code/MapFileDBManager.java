import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Stream;

public class MapFileDBManager implements MapDBManager {
    private static final String MAP_DB_FILE = "map.db";

    @Override
    public LolMap parseMap(Object iter) {
        String line = String.valueOf(iter);
        String[] mapData = line.split(",");

        int index = Integer.parseInt(mapData[0]);
        String name = mapData[1];
        int summonerCnt = Integer.parseInt(mapData[2]);

        return new LolMap(name, summonerCnt);
    }

    @Override
    public Stream DBReader() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(MAP_DB_FILE)));
            return br.lines();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        return null;
    }
}