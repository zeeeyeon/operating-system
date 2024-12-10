package synchronizationTools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SemaphoreExampleTest {
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
