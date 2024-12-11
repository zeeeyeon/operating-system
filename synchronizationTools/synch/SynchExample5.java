package synchronizationTools.synch;

public class SynchExample5 {

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

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[5];

        // 같은 count 를 엑세스 할 수 있도록 하기
        Count count = new Count();

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new MyRunnable(count));
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
        // result => Thread count: 50000
        System.out.println("Thread count: " + Count.count);
    }
}

/*
    liveness: deadlock, starvation 을 해결하는 방법

    priority inversion: 높은 우선순위를 가진 프로세스가 낮은 우선순위를 가진 프로세스에게 밀리는 현상 (쫓아낼 수 없음)
    priority inheritance : 낮은 우선순위에게 높은 우선순위의 자원을 복사 해주는 것 (빨리 자원을 반납하게 하기 위해서 상속해줌)

*/