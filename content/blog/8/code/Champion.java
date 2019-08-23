public class Champion {
    private int index;
    private String name;
    private boolean available;

    public Champion(String name, int index) {
        this.index = index;
        this.name = name;
        this.available = true;
    }

    public String getName() {
        return name;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setDisable() {
        available = false;
    }

    public String toString() {
        return String.valueOf(index) + ". " + name;
    }
}