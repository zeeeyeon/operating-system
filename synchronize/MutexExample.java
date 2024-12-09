package synchronize;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*
    mutex : 한 번에 하나의 스레만이 특정 리소나 코드 섹션에 접근할 수 있도록 함, boolean 타입의 lock 사용
    하나의 스레드가 끝나야 다른 스레드가 진입할 수 있음.
 */
public class MutexExample {
    // lock 인터페이스 구현 클래스 : 락 획득, 해제 수기로 제어 가능
    private final Lock lock = new ReentrantLock();

    public void accessResource(int threadId) {

        // 자원 진입 시도
        System.out.println("Thread: "  +  threadId + " try to access resource");
        lock.lock();

        // 자원 진입
        try {
            System.out.println("Thread: "  +  threadId + " is accessing resource");
            // 자원에 대한 작업을 수행하는 동안 sleep 으로 로그 보기
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // 자원 사용 완료
            System.out.println("Thread: "  +  threadId + " released resource");
            lock.unlock();
        }
    }
}
