package synchronizationTools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

    public static void main(String[] args) throws InterruptedException {
        final MutexExample mutexExample = new MutexExample();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            executor.submit(() -> mutexExample.accessResource(threadId));
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
        assert finished;
    }
}

/*
    하드웨어 인스트럭트에서 해결하기보다 조금 더 하이어 한 레벨애서 소프트웨어로 critical section problem 을 해결 할 수 있는 방법
    => mutual exclusive 만 제대로 해결 해 보자

    1. mutex : 크리티컬 섹션을 보호하고 임계 영역을 들어 가기 위해 열쇠를 주고 들어가고 나올때 반납

    available 이라는 boolean variable 사용 (얘도 물론 atomic 하게 실행되어야 함)
        - acquire : 열쇠를 획득 (entry section)
        - releases: 열쇠 반납 (exit section)


    => busy waiting 문제가 생김
    어떤 프로세스가 크리티컬 섹션에 들어가기 위해서 무한 루프를 돌게 됨 (acquire 하기 위해서)
    다른 프로세스가 사용할 수 있는 시간을 무한 루프를 돈다고 사용하지 못하게

    => busy waiting을 하는 mutex lock을 spin lock 이라고 부름

    => 프로세스가 쓸데 없이 락을 대기하며 공회전 함 (이게 상당히 유용할 수 있음)
    => cpu 가 여러 개 일때(멀티코어 시스템에서는) spinlock 이 컨텍스트 스위치가 안일어나기 때문에 이러한 시간을 아낄 수 있음
    => spinlock 을 안하기 위해서는 웨이팅 큐에서 기다려야 함.
    => 바로 cpu 를 획득하는 것이 아니라 레디 큐를 거치고 컨텍스트 스위칭을 하는 것

    => 데드락, 기아상태는 해결할 수 없음
*/
