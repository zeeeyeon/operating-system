package synchronize;

public class PetersonAlgorithm_1 {
    static int count = 0;

    static int turn = 0;
    static boolean[] flag = new boolean[2];

    static class Producer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                // entry section
                // 그래서 atomic variable 사용 해 보자
                flag[0] = true;
                turn = 1;

                while (flag[1] && turn == 1) ;

                // critical section
                count ++;

                // exit section
                flag[0] = false;

                // remainder section
            }
        }
    }

    static class Consumer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                // entry section
                flag[1] = true;
                turn = 0;

                while (flag[0] && turn == 0) ;

                // critical section
                count --;

                // exit section
                flag[1] = false;

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

        // Result : Peterson Algorithm count: -2
        // entry section 부분에서 context switching 이 발생하여 critical section problem, race condition 발생
        System.out.println("Peterson Algorithm count: " + PetersonAlgorithm_1.count);
    }
}