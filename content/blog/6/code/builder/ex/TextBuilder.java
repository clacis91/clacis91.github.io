public class TextBuilder implements Builder {
    private StringBuffer buf = new StringBuffer();

    @Override
    public void makeTitle(String title) {
        buf.append("=================\n");
        buf.append("[" + title + "]\n\n");
    }

    @Override
    public void makeString(String str) {
        buf.append("* " + str + "\n\n");
    }

    @Override
    public void makeItem(String[] items) {
        for(String item : items) {
            buf.append("  - " + item + "\n");
        }
        buf.append("\n");
    }

    @Override
    public void close() {
        buf.append("=================\n");
    }

    public String getResult() {
        return buf.toString();
    }
}