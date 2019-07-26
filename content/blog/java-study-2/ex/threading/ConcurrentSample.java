public class ConcurrentSample implements Runnable {
    private int res = 0;

    public static void main(String[] args) {
        ConcurrentSample concurrentSample = new ConcurrentSample();
        Thread th1 = new Thread(concurrentSample);
        Thread th2 = new Thread(concurrentSample);

        th1.start();
        th2.start();

        try {
            th1.join();
            th2.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(concurrentSample.res);
    }

    @Override
    public void run() {
        sum();
    }

    private synchronized void sum() {
        for(int i = 0; i < 10000; i++) 
            res++;
    }
}