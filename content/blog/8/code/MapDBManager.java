import java.util.stream.Stream;

public interface MapDBManager {
    public LolMap parseMap(Object iter);
    public Stream DBReader();
}