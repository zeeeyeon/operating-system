package synchronizationTools;

import java.util.concurrent.atomic.AtomicBoolean;

public class PetersonAlgorithm_2 {
    static int count = 0;

    static int turn = 0;
    static AtomicBoolean[] flag;

    static {
        flag = new AtomicBoolean[2];
        for (int i = 0; i < flag.length; i++) {
            flag[i] = new AtomicBoolean();
        }
    }

    static class Producer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                // entry section
                flag[0].set(true);
                turn = 1;

                // flag[0] - atomic variable
                // 얘를 read, write 할 때 절대로 context switching 일어나지 않음, interrupt 가 걸리지 않는다.
                while (flag[1].get() && turn == 1) ;

                // critical section
                count ++;

                // exit section
                flag[0].set(false);

                // remainder section
            }
        }
    }

    static class Consumer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                // entry section
                flag[1].set(true);
                turn = 0;

                while (flag[0].get() && turn == 0) ;

                // critical section
                count --;

                // exit section
                flag[1].set(false);

                // remainder section
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Producer());
        Thread t2 = new Thread(new Consumer());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // Result : Peterson Algorithm (atomic) count: 0
        System.out.println("Peterson Algorithm (atomic) count: " + PetersonAlgorithm_2.count);
    }
}

/*
    race condition : 공유된 자원에 여러개의 프로세스가 동시에 엑세스 하는 상황(경쟁 상황)
    critical section : 공유된 자원에 엑세스하는 코드 영역
    synchronization problem : critical section 보호하기 위해 동기화하는 것

    동기화를 해결하는 가장 기본적인 방법
    1. (mutual exclusive) 상호배제
    2. (progress) 진행,  데드락을 발생시키지 않는 것
    3. (bounded waiting) 한정 대기,  기아가 발생되지 않게 하는 것

    이러한 솔루션을 다 만족하는 것 => peterson's algorithm

    peterson's algorithm
    Pj, Pi 두 개를 동기화하는 방법, flag 사용하여 턴을 주며 주고 받음

    ex)
    1. 두 가구가 동시에 산책 할 수 없음
    2. 산책을 하기 위해서 flag 를 true 로 바꿈(진입 의사 표현)
    3. 턴을 미리 다음 사람으로 넘김(턴을 미리 양보)
    4. 산책 돌아와서(상대방 진입 여부 확인 후 진입) flag 를 false 로 변경 (산책이 끝났다는 의사를 표현)

    instruct level 로 해결하려고 하니 여기서 while 문 뒤에 바로 컨텍스트 스위칭이 일어나 버리는 상황이 생김 !
    => 임계 구역의 문제를 하드웨어의 instruct 해결할 수 있게 지원 해주자. (하드웨어의 instruct atomic 하게 사용할 수 있게 하자)
    => 어떠한 instruct 특수 하드웨어 instruct 로 만들어 atomic 한 것으로 만들어주자.
    => atomicBoolean 으로 원자성을 보장함

*/