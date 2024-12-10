package synchronizationTools;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MutexExampleTest {
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
