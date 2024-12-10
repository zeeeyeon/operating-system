package synchronizationTools;

import java.util.concurrent.Semaphore;

public class SemaphoreExample {
    // 동시에 3개의 스레드에 접근 가능
    private final Semaphore semaphore = new Semaphore(3);

    public void accessResource(int threadId) {
        try {
            // 자원 진입 시도
            System.out.println("Thread: "  +  threadId + " try to access resource");

            // 허가 획득 시도
            semaphore.acquire();

            // 자원 진입
            System.out.println("Thread: "  +  threadId + " is accessing resource");

            // 자원에 대한 작업을 수행하는 동안 sleep 으로 로그 보기
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("Thread: "  +  threadId + " released resource");
            semaphore.release();
        }

    }
}
