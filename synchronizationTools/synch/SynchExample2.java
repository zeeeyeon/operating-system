package synchronizationTools.synch;

public class SynchExample2 {

    static class Count{
        public static int count = 0;
        // 공유하는 변수에 synchronized 넣어주기 (여기가 임계 영역)
        // 모니터 락을 acquire
        // method 를 전체적으로 묶는 것 보다 직접 접근하는 임계 영역을 줄여보자
        synchronized static void increment() {
            count++;
        }
        // 끝나면서 release
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                Count.increment();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new MyRunnable());
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        // result => Thread count: 50000
        System.out.println("Thread count: " + Count.count);
    }
}