public class Director {
    private Builder builder;
    public Director(Builder builder) {
        this.builder = builder;
    }

    public void construct() {
        builder.makeTitle("Greeting");
        builder.makeString("아침/낮에");
        builder.makeItem(new String[] {
            "안녕하세요",
            "굿모닝"
        });
        builder.makeString("밤에");
        builder.makeItem(new String[]{
            "안녕하세요",
            "굿나잇"
        });
        builder.close();
    }
}