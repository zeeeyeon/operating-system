package synchronizationTools.synch;

public class SynchExample3 {

    static class Count{
        private static Object object = new Object();
        public static int count = 0;
        // method 를 전체적으로 묶는 것 보다 직접 접근하는 임계 영역을 줄여보자
        public static void increment() {
            // 어떤 오브젝트의 인스턴스를 가지고 모니터 락을 획득하겠다는 것을 지정
            synchronized (object) {
                count++;
            }
        }
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