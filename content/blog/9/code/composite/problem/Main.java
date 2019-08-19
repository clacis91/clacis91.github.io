public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Making root entries...");
            Directory rootdir = new Directory("root");
            Directory bindir = new Directory("bin");
            Directory tmpdir = new Directory("tmp");
            Directory usrdir = new Directory("usr");

            rootdir.add(bindir);
            rootdir.add(tmpdir);
            rootdir.add(usrdir);

            File vi = new File("vi", 10000);
            File ex = new File("example", 20000);

            bindir.add(vi);
            usrdir.add(ex);

            System.out.println(vi.getFullpath());
            System.out.println(ex.getFullpath());
        }   catch(FileTreatmentException e) {
            e.printStackTrace();
        }
    }
}