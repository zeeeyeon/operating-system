package synchronizationTools.synch;

public class SynchExample1 {

    static class Count{
        public static int count = 0;
        public static void increment() {
            count++;
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
        // result => Thread count: 17355
        System.out.println("Thread count: " + Count.count);
    }
}


/*
    mutex, semaphore 는 타이밍(프로그래밍) 에러가 자주 일어남
    wait, signal 의 순서를 지키지 않으면 critical section 에 동시에 들어가는 일이 생김

    ex) wait -> wait, signal -> wait
    * 두 개를 잘못 사용하여 일어나는 일을 방지하기 위하기 이렇게 쓸 가능성을 낮춰주는 것 (synchronization Tool 을 사용하자)

    monitor type
    - 자바의 클래스 처럼 어떤 변수를 선언하고 정의된 인스턴스를 호출하도록 해주는 것
    conditional variables
    - 동기화 문제를 풀기 위 synchronization 메커니즘을 제공 해 주는 변수를 사용하는 것
    - conditional type


    java 에서는 monitor-lock 을 제공 (thread synchronization)
    - synchronized (임계 영역을 선언)
        - 임계 영역에 해당하는 코드 블럭을 synchronized 로 묶어버리자 (해당 코드 영역에는 monitor-lock 을 획득해야 진입 가능)
        - 모니터락을 지정 해 줄 수 있음 (메서드에 지정하면 메서드 코드 블럭 전체에 걸림, this 객체 인스턴스)
        - entry section, exit section 은 자바가 알아서 해주겠다.

    - wait(), notify()
        - 순서를 배정해주려면 필요함
        - thread 가 wait() 호출 시, 해당 객체의 모니터락을 획득하기 위해 대기 상태로 진입
        - notify() 호출 시, 해당 겍체 모니터에 대기 중인 쓰레드를 하나 깨움
        - notifyAll(), 모든 쓰레드를 다 깨움
*/