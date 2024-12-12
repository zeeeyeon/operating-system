package synchronizationTools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

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

    public static void main(String[] args) throws InterruptedException {
        final SemaphoreExample semaphoreExample = new SemaphoreExample();
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            executor.submit(() -> semaphoreExample.accessResource(threadId));
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(20, TimeUnit.SECONDS);
        assert finished;
    }
}

/*
    n개의 프로세스가 존재하는 경우 : semaphore (신호기, 방향과 신호를 여러가지로 알려줄 수 있음)
    S : 정수(Integer variable) , 초기화를 어떻게 해주는가에 따라서 wait(P)와 signal(V) 두 개의 atomic 연산으로 제어
    S를 중가시키고 감소시키며 실행 (S: 열쇠, 모두가 볼 수 있게 열쇠꾸러미를 만들어서 열쇠를 모아놓는 느낌..?)

    wait : 자원 사용 시 S값 감소
    signal : 자원 반환 시 S값 증가
    S == 0 :  더 이상 사용 가능한 자원이 없음을 의미

    * busy waiting 문제가 생김 (while 문으로 반복 체크하며 CPU 낭비)
    => 해결하는 방법
    스케줄러를 사용하여 semaphore 가 not positive(== wait) 해야 한다 자기 자신을 웨이팅 큐에 가서 대기 하게 만듬
    다른 프로세스가 시그널을 보낸다면 그때 웨이팅 큐에서 대기하고 있던 애들을 레다큐에 넣어서 실행시킴 (커널 레벨에서 실행)

    acquire 할 때, wait() 걸기 (p - operation)
    release 할 때, signal 주기 (v - operation)
*/
