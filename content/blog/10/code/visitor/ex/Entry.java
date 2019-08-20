import java.util.Iterator;

public abstract class Entry implements Element {
    public abstract String getName();
    public abstract int getSize();
    public String toString() {
        return getName() + " (" + getSize() + ")";
    }
}