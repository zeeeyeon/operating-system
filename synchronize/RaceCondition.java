package synchronize;

class RunnableExample implements Runnable {
    /*
        count 선언 시 공유할 수 있는 인스턴스가 아니기 때문에
        race condition 상황을 만들기 위해 static 으로 만들어서 사용
     */
    static int count = 0;

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++) {
            count ++;
        }
    }
}

public class RaceCondition {
    public static void main(String[] args) throws InterruptedException {
        RunnableExample runnableExample1 = new RunnableExample();
        RunnableExample runnableExample2 = new RunnableExample();

        Thread t1 = new Thread(runnableExample1);
        Thread t2 = new Thread(runnableExample2);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // Result: 10437
        System.out.println("Result: " + RunnableExample.count);
    }
}


/*
    임계 영역(critical section)에서 concurrent, parallel 하게 실행되었을 때 문제를 일으키를 영역 : 데이터, 파일을 공유하는 공유 자원에 동시에 엑세스 하는 상황 (race condition, 경쟁 상황)
    이 코드 영역을 critical section 이라 했을 때, 여러 개의 프로세스가 사용하더라도 데이터의 안정성(consistency)을 보장 해 주는 방법이 동기화(synchronization)


 */
