public class Triple {
    private int id;
    private static Triple[] triples = {
        new Triple(0), new Triple(1), new Triple(2)
    };
    private Triple(int id) {
        this.id = id;
    }

    public static Triple getInstance(int id) {
        if(id < triples.length)
            return triples[id];
        else 
            return null;
    }
    public String toString() {
        return "This is instance " + String.valueOf(id);
    }
}