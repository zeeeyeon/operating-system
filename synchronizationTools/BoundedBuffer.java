package synchronizationTools;

public class BoundedBuffer {

    class ProdRunner implements Runnable {
        CashBox cashBox;
        public ProdRunner(CashBox cashBox) {
            this.cashBox = cashBox;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep((long) (Math.random() * 500));
                    int money = ((int) (1 + Math.random() * 9)) * 10000;
                    cashBox.give(money);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    class ConsumerRunner implements Runnable {
        CashBox cashBox;
        public ConsumerRunner(CashBox cashBox) {
            this.cashBox = cashBox;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Thread.sleep((long) (Math.random() * 500));
                    int money = cashBox.take();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // cashbox 의 인스턴스에서 모니터락을 획득
    class CashBox {
        private int[] buffer;
        private int count;
        private int in;
        private int out;

        public CashBox(int bufferSize) {
            buffer = new int[bufferSize];
            count = in = out = 0;
        }

        // 동기화 문제 해결
        synchronized public void give(int money) throws InterruptedException {
            // critical section

            // 버퍼가 빌 때까지 기다려야 함
            while (count == buffer.length) {
                try {
                    // wait 를 호출하면 스레드가 모니터 락을 획득할 때까지 entry queue 에 대기(wait)하고 있음.
                    // notify 로 깨우면 InterruptedException 발 => busy waiting 발생시키지 않음
                    wait();
                } catch (InterruptedException e) {}
            }

            buffer[in] = money;
            in = (in + 1) % buffer.length;
            count++;
            System.out.printf("용돈 주기 ~ %d원\n", money);

            // 모니터락을 기다리는 애들에게 signal 을 보냄
            notify();
        }

        synchronized public int take() throws InterruptedException {
            // critical section

            // empty 상태가 되면
            while (count == 0) {
                // 가져갈 자원이 없어서 대기
                wait();
            }

            int money = buffer[out];
            out = (out + 1) % buffer.length;
            count--;
            System.out.printf("용돈 받기 ~ %d원\n", money);

            notify();
            return money;
        }
    }

    public void start() {
        CashBox cashBox = new CashBox(1);
        Thread[] producers = new Thread[1];
        Thread[] consumers = new Thread[1];

        for (int i = 0; i < producers.length; i++) {
            producers[i] = new Thread(new ProdRunner(cashBox));
            producers[i].start();
        }

        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Thread(new ConsumerRunner(cashBox));
            consumers[i].start();
        }
    }

    public static void main(String[] args) {
        BoundedBuffer buffer = new BoundedBuffer();
        buffer.start();
    }
}
