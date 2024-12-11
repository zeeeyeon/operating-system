package synchronizationTools.synch;

public class SynchExample4 {

    static class Count{
        public static int count = 0;
        public void increment() {
            // 자기 객체 인스턴스 모니터락을 획득해서 증가 시켜라
            // 근데 count 라는 static 변수를 공유하기 때문에 동기화 문제가 생기는데, static 이 아니라면 동기화 문제가 생기지 않음.
            synchronized (this) {
                count++;
            }
        }
    }

    static class MyRunnable implements Runnable {
        Count count;

        public MyRunnable(Count count) {
            this.count = count;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                count.increment();
            }
        }
    }


    // count 인스턴스가 5개가 생긴 것
    // 즉 모니터가 5개가 생겨서 그 안의 변수들끼리만 동기화가 가능한 것, 각각의 모니터끼리는(스레드끼리는) 동기화가 되지 않음.
    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new MyRunnable(new Count()));
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        // result => Thread count: 42747
        System.out.println("Thread count: " + Count.count);
    }
}