import java.util.LinkedList;

public class ThreadManager {
    public static void main(String[] args) {
        ResourceQueue<Integer> q = new ResourceQueue<Integer>();
        Thread producer = new Thread(new Producer(q), "producer");
        Thread consumer1 = new Thread(new Consumer(q), "consumer1");
        Thread consumer2 = new Thread(new Consumer(q), "consumer2");

        producer.start();
        consumer1.start();
        consumer2.start();
    }
}

class ResourceQueue<T> {
    private LinkedList<T> jobs = new LinkedList<T>();

    public synchronized void clear() {
        jobs.clear();
    }

    public synchronized T pop() throws InterruptedException {
        T t = null;
        if(jobs.isEmpty())
            this.wait();    
        
        t = jobs.removeFirst();
        return t;
    }

    public synchronized void put(T t) {
        jobs.addLast(t);
        this.notify();
    }
}

class Producer implements Runnable {
    private ResourceQueue<Integer> q;

    public Producer(ResourceQueue<Integer> q) {
        this.q = q;
    }

    @Override
    public void run() {
        for(int i = 0; i < 10; i++) {
            q.put(i);
            try {
                Thread.sleep(1000);
            } catch(InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

class Consumer implements Runnable {
    private ResourceQueue<Integer> q;

    public Consumer(ResourceQueue<Integer> q) {
        this.q = q;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            try {
                Integer i = q.pop();
                if(i != null) {
                    System.out.println(Thread.currentThread().getName() + " pop : " + String.valueOf(i));
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}