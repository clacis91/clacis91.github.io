import java.util.stream.Stream;

public interface ChampionDBManager {
    public Champion parseChampion(Object iter);
    public Stream DBReader();
}