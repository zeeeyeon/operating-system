package synchronizationTools;

public class SharedDB {

    // 현재 읽고 있는 프로세스가 몇개인지 체크
    private int readerCount = 0;
    // 현재 내가 쓰고 있는 상태인지
    private boolean isWriting = false;

    public void read() {
        // read from database
    }

    public void write() {
        // write into database
    }

    synchronized public void acquireReadLock() {
        // 만약 쓰는 중이면
        while (isWriting) {
            try {
                // 기다렸다가
                wait();
                // interrupted 걸려서 다시 resume 되면
            } catch (InterruptedException e) {}
        }
        // 내가 진입을 한 것이니까 count 하나 증가 시키기.
        readerCount++;
    }

    synchronized public void releaseLock() {
        readerCount--;
        if (readerCount == 0) {
            notify();
        }
    }


    synchronized public void acquireWriteLock() {
        // 읽는 사람이 없을 때 진입해야함
        while (readerCount > 0 || isWriting) {
            try {
                // 기다렸다가
                wait();
                // interrupted 걸려서 notify 받을 때까지 기다리기
            } catch (InterruptedException e) {}
        }
        isWriting = true;
    }

    synchronized public void releaseWriteLock() {
        isWriting = false;
        // 하나만 레디큐에 넣지말고 대기하는 애들을 다 넣어서 레디큐에서 다시 경쟁하게 만 => 공평한 기회를 제공
        notifyAll();
    }

    // 따로 만들어 놓으면 문제 없음.

    // read 하는 상황이 생기면 acquiredReadLock -> read -> release
//    sharedDB.acquiredReadLock();
//    sharedDB.read();
//    sharedDB.releaseReadLock();

    // write 하는 상황이 생기면 acquiredWriteLock -> write -> release
//    sharedDB.acquiredWriteLock();
//    sharedDB.write();
//    sharedDB.releaseWriteLock();
}
