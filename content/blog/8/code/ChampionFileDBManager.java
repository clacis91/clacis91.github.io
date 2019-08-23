import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Stream;

public class ChampionFileDBManager implements ChampionDBManager {
    private static final String CHAMPION_DB_FILE = "champion.db";

    @Override
    public Champion parseChampion(Object iter) {
        String line = String.valueOf(iter);
        String[] championData = line.split(",");

        int index = Integer.parseInt(championData[0]);
        String name = championData[1];

        return new Champion(name, index);
    }

    @Override
    public Stream DBReader() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(CHAMPION_DB_FILE)));
            return br.lines();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } 
        return null;
    }
}