public class Champion {
    private String name;
    private boolean available;

    public Champion(String name) {
        this.name = name;
        this.available = true;
    }

    public String getName() {
        return name;
    }

    public boolean getAvailable() {
        return available;
    }
}