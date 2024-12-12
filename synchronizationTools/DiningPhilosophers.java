package synchronizationTools;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

enum State {
    THINKING, HUNGRY, EATING
}

public class DiningPhilosophers {

    public static void main(String[] args) {
        int numOfPhilosophers = 5;
        Philosopher[] philosophers = new Philosopher[numOfPhilosophers];
        DiningPhilosophersMonitor monitor = new DiningPhilosophersMonitor(numOfPhilosophers);

        for (int i = 0; i < philosophers.length; i++) {
            new Thread(new Philosopher(i, monitor)).start();
        }
    }

    static class Philosopher implements Runnable {

        private int id;
        private DiningPhilosophersMonitor monitor;

        public Philosopher(int id, DiningPhilosophersMonitor monitor) {
            this.id = id;
            this.monitor = monitor;
        }

        @Override
        public void run() {
            while (true) {
                think();
                monitor.pickup(id);
                eat();
                monitor.putdown(id);
            }
        }

        private void think() {
            try {
                System.out.println(id + " thinking");
                Thread.sleep((long)(Math.random() * 500));
            } catch (InterruptedException e) {
            }
        }

        private void eat() {
            try {
                System.out.println(id + " eating");
                Thread.sleep((long)(Math.random() * 50));
            } catch (InterruptedException e) {
            }
        }
    }

    static class DiningPhilosophersMonitor {
        private int numOfPhilosophers;
        private State[] state;
        private Condition[] self;
        private Lock lock;

        public DiningPhilosophersMonitor(int numOfPhilosophers) {
            this.numOfPhilosophers = numOfPhilosophers;
            this.state = new State[numOfPhilosophers];
            this.self = new Condition[numOfPhilosophers];
            // 재진입 가능 (cpu 에서 context switching 발생, waiting queue -> ready queue -> cpu 다시 재진입하는 것)
            this.lock = new ReentrantLock();
            for (int i = 0; i < numOfPhilosophers; i++) {
                state[i] = State.THINKING;
                self[i] = lock.newCondition();
            }
        }

        private int leftOf(int i) {
            return (i + numOfPhilosophers - 1) % numOfPhilosophers;
        }

        private int rightOf(int i) {
            return (i + 1) % numOfPhilosophers;
        }

        private void test(int i) {
            // 왼쪽과 오른쪽이 모두 먹고 있지 않을 때, 그리고 내가 hungry 상태이면 eating 하겠다.라고 시그널을 줌
            if (state[i] == State.HUNGRY &&
                    state[leftOf(i)] != State.EATING &&
                    state[rightOf(i)] != State.EATING) {
                state[i] = State.EATING;
                self[i].signal();
            }
        }


        // 젓가락을 획득 할 수 있는 조건, 양쪽에서 밥을 먹지 않을 때만 쥘 수 있음.
        // 젓가락을 잡는 행위 == critical section
        public void pickup(int id) {
            // 락 획득
            lock.lock();

            try {
                // 배가 고프니까 젓가락을 pickup 하려고 하는 것 -> 상태를 hungry 로 변경
                state[id] = State.HUNGRY;
                test(id);
                if (state[id] != State.EATING) {
                    // synchronized 된 상태에서 condition variable 는 wait() , notify() 사용 , thread monitor 락은 await(), signal() 사용
                    self[id].await();
                }
            } catch (InterruptedException e) {
            } finally {
                lock.unlock();
            }
        }

        public void putdown(int id) {
            lock.lock();

            try {
                state[id] = State.THINKING;
                test(leftOf(id));
                test(rightOf(id));
            } finally {
                lock.unlock();
            }
        }
    }
}

/*

    * Thread-safe : concurrent 하게 돌려도 문제가 발생하지 않음 (mutex, semaphore, monitor 를 사용하여 thread-safe 만들어도 괜찮지민 대안이 존재함)
    1. transactional memory : atomic operation,  입금과 출금은 별도의 트랜젝션이지만 얘를 별도로 일어난 것은 아예 일어나지 않은 것으로 하겠다 (== rollback)
        - 실행 단위의 메모리 자체를 트랜젝션하게 만들겠다.

    2. openMP : 해당 부분을 크리티컬 섹션으로 만들어줌

    3. Functional Programming Language : 명령형 프로그래밍을 가정하고 있기 때문에 (명령어에 의존)
        - 모든 것을 함수형으로 만들기

*/
