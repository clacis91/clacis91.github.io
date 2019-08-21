public class GraphObserver implements Observer {
    public void update(NumberGenerator generator) {
        int count = generator.getNumber();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < count; i++) {
            sb.append("*"); 
        }
        System.out.println("GraphObserver : " + sb.toString());
        try {
            Thread.sleep(100);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}