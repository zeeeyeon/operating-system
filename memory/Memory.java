package memory;

public class Memory {
}

/*
    memory
    - cpu 는 메모리에서 인스트럭트를 fetch 프로그램 카운트가 지정하는 주소에 있는 인스트럭트를 가져와서 실행하는데
    이 인스트럭트도 load, store 존재 (메모리에 엑세스 함)
    - 메모리의 주소 공간
    base register, limit register 를 지정하여 접근하려하는 메모리 공간을 보호함
    cpu 하드웨어에서 모든 주소 접근을 유저 모드에서 생산하면 베이스, 리밋을 체크해야함 (하드웨어에서 설정)


    address binding
    소스코드에서 a 라고 선언 한 값(메모리 번지)은 [심볼릭 한 값]
    실제로 컴파일러는 컴파일 시점에 다시 주소를 재위치시킴
    링크가 실제로 주소를 만들어내고 번지를 지정해줌
    source program -> compiler -> object file -> linker -> executable file -> loader -> program in memory


    user process 가 사용하고 있는 logical address -> physical address mapping

    MMU (하드웨어 디바이스, 논리적인 주소를 물리적인 주소로 변환)
    dynamic loading : 메모리 주소 공간을 한꺼번에 로드할 필요없고 , 필요할 때만 로딩하도록 함
    dynamic linking and shared libraries
    DLLs: Dynamically Linked Libraries
    기본으로 사용하는 라이브러리만 따로 로드되어 있는 것, 링킹해서 사용함
    실행하는 중에 DLL를 로드해서 사용함

    메모리를 할당해줘야 함 (메인 메모리를 통째로 로드시키는 것[contiguous, single process])

    free hole 에 대한 프로세스 할당 방법
    1. first-fit : linked list 를 따라가다가 들어갈 수 있는 공간이 있으면 차지하는 것
    2. best-fit : priority queue 충분히 들어갈 수 있는 가장 작은 곳에 집어 넣기
    3. worst-fit : 가장 큰 곳부터 집어 넣기


    fragmentation : 단편화 문제 (남아 있는 공간이 있어도 할당해 줄 수가 없음.)
    통째로 옮기자
    segmentation : 종류(segment)별로 메모리를 옮기자 (라이브러리, 스택, 메인함수 등)
    paging : 쪼개서 옮기자

    memory allocation
    1. 통째(contiguous)로 올리면 연속적이고 관리하기 쉬움, 하지만 크기가 커지면 dynamic allocation -> external fragmentation 문제가 발생함
    2. paging : physical memory 에 잘게 쪼개서 페이지로 만든 후, 그 페이지의 offset 으로 찾기
    page number(p), page offset (d) : logical address 를 결정하는 방법 -> physical address 로 변환

    -> page table 에서 관리함

    page number(p) 를 가지고 페이지 프레임을 찾고 page offset 으로 메모리 엑세스를 할 수 있음 (logical, physical address 로 그대로 변환할 수 있음)

    page size(== frame size) 을 정하는 기준
    - 하드웨어에 위임
    - 1 ~ 4kb 사이의 2의 배수로 지정
    페이지 사이즈 계산법 알아놓기

    페이지 테이블 관리하는 것도 쉽지않음(워낙 크기 때문에)
    page-table base register 의 주소값만 저장(두 번의 메모리 참조가 일어남)
    - 페이지 테이블에 가서 테이블 번호 가져오기
    - 실제 메모리에 엑세스

    TLB (캐시 메모리를 사용)
    - TLB hit : 찾고자 하는 페이지가 TLB 에 존재하는 것
    - TLB miss : 찾고자 하는 페이지가 TLB(캐시 메모리)에 없는 것
    - hit ratio
        - 80 % hit ratio : EAT = 0.80 x 10 + 0.20 x 20 = 12 ns.
        - 99 % hit ratio : EAT = 0.99 x 10 + 0.01 x 20 = 10.1 ns.


    페이징이 도입되면 각 프레임 별로 valid 한지 체크 (memory protection)
    - page table 의 frame number 옆에 valid - invalid bit 를 두고 legal 한지 illegal 한지 체크

    shared page : 공통의 코드를 공유할 수 있음
    - print 와 같은 공통적 코드(reentrant code, 실행중에 바뀔 수 없는 코드)의 라이브러리를 쉐어링 하는 방법

    페이징도 너무 많아질 경우 나눌 수 있는 방법
    게층적으로 페이징하기 : 페이지 테이블을 또 다른 페이지로 나누기 (two-level page)
    해시로 페이징하기 : (logical page) hashing 값으로 address 관리
    inverted page table : pid 를 사용


    swapping (전체가 로딩되는게 아니라서 메모리 크기를 초과해도 가능, 지금 당장 필요한 페이지만 올림)
    - 동시에 돌릴 수 있는 양이 많아짐
    - access 할 때만 들어오게 만듬 (swapping)

    swapping with paging(virtual memory) :
    page out, page in

*/

/*
    virtual memory
    - 프로세스의 실행을 완전히 다 올리지 않아도, 크기가 더 커도 실행할 수 있도록 할 수 있음
    - 파일 - 메모리 공유가 쉬워짐 (shared pages, 페이지를 공유함으로서 쉬워짐)

    코드가 적힌 파일이 어떻게 실행이 될까?
    - HDD, SDD 같은 secondary storage -> 메인 메모리 공간으로 옮기는 작업을 함
    - physical memory 를 전체 다 올리는 것이 아니라 페이지 단위로 짤라서 올림
    - 올리는 시기, 요청이 들어오면 올림 (많은 것을 고려해야함)
        - 실행 중에 요청이 들어오면 어떻게 해결할건데?
        - 페이지가 메모리, secondary memory 에 있다고 가정 (valid 한 후 요청에 응답함)
        - invalid bit 일때, memory store 에서 가져오지 않았다는 것

        - 1. valid, page in
        - 2. invalid , 메모리에서 free frame 을 찾은 후 할당
        - 3. page table 의 bit 를 valid -> invalid 로 바꿔준 후, 다시 instruction 실행

    pure demand paging
    - 요청이 오지 않으면 페이지를 절대 가져오지 않음.
    - 하나의 요청에 하나의 페이지를 가져옴

    locality of reference
    - 페이지 사이즈 128, int[128][128] data
    - 함수 호출을 자주하면 한 번 로딩시 저장됨, 특정 영역만 반복해서 실행되기 때문에 (데이터의 국부성) page-fault(현재 메모리 없는 페이지를 접근 할 때 생김) 는 낮아짐, 퍼포먼스가 올라감

    demand paging 은 하드웨어를 꼭 필요로 함 (필요할 때만 페이지 불러오기)
    - page table (virtual memory) : 페이지가 실제 하드웨어에 몇번 째에 저장되어 있는지 이걸 어떻게 관리할 것이냐 (valid, invalid 체크, page in 가능한지 체크)
    - secondary memory (= swap space)

    instruction restart
    - page fault 가 일어나면 o/s에 trap 을 걸어버림 (점유중인 프로세스를 웨이팅 큐에 보내버림)
    - context switch 하고 난 후 원래의 페이징 상태로 돌릴 수 있는가
    - process 별로 page table 을 잘 관리해야 함
    - free frame list 관리, stack, heap에서 꼭 관리를 해줘야함 (==frame pool)

    page fault 가 발생하였을 때 걸리는 시간
    - 1. page interrupt 걸어주는 시간
    - 2. page 읽어오는 시간 (대부분 차지하는 시간)
    - 3. 프로세스 재시작 하는 시간


    copy on write
    - write 할 때 복사해오기
    - fork() , exec()

*/

/*
    page replacement
    - 사용하지 않는 공간이 없을 경우(free frame), 비워줘야할 것을 찾는 것 (victim)
    - victim 을 페이지 아웃 시키고 invalid 로 바꿔줌

    그러면 victim 을 어떤 방식으로 정할 수 있을까?
    - 가장 효율적으로 victim 을 선정 할 수 있는 방법 3가지

    FIFO page replacement
    Belady's anomaly : 프레임을 많이 할당하면 (페이지 폴트가 그만큼 덜 일어나서 부하가 줄어야하는데, 페이지 프레임을 늘렸는데 부하가 커지는 현상이 나타남)

    그러면 이러한 현상이 나타나지 않는 최적의 알고리즘이 뭘까 (Optimal 이 뭘까?)

    Optimal
    - 앞으로 사용하지 않을 것 같은 페이지를 버리자 (low page fault rate 를 달성하기 위해)
    - 미래에 어떤 페이지가 와야하는지를 알아야 사용할 수 있음 (그러면 미래를 알려면 과거를 보고 측정을 해보자)

    LRU Page Replacement
    - 어떤 페이지를 바꿀 것이냐 (제일 오랫동안 사용되지 않은 것을 victim 으로 선정하자)
    - counter implementation (페이지를 참조할 때마다 counter 참조), victim 선정시, 값이 제일 큰 것(오래된 것)으로 선택
    - stack implementation (stack 에 쌓아놓기)
    => 실제로 하드웨어의 서포트를 받아야지 구현가능 (reference bit, FIFO + second-chance)


    frame allocation (n 개의 프로세스가 존재하고, m개의 프레임이 존재할 때 프로세스 단위로 프레임을 몇 개를 배정할까?)
    - equal allocation, 동등하게 줄 것인가
    - proportional allocation, 프로세스 사이즈가 큰 애한테 많이 줄 것인가

    - local replacement, 내 것을 가져와서 victim 선정 할 것인가
    - global replacement, 다른 것을 가져와서 victim 선정 할 것인가

    demand paging -> virtual memory system -> 하드웨어에 무관하게 논리적으로 큰 프로그램을 만들 수 있는데
    - page in - page out 하는게 너무 바빠서 다른 일을 못함
    - the degree of multiprogramming : 멀티 프로그래밍의 정도가 높아짐(concurrent 하게 도는 프로그램이 많아질 수록, cpu 사용량이 높아짐 그러다가 갑자기 떨어짐(== thrashing))

    Thrashing
    - working - set model : 메모리 참조에 locality 가 존재함
*/


/*
    second storage

    HDD Scheduling
    - 접근 시간 줄이기 (= seek time), 읽고자 하는 섹터에 가는 시간
    - disk bandwidth 크게, 한 번에 저장할 수 있는 용량

    - FIFO, 양방향으로 먼저 들어온 것부터 읽기
    - SCAN
    - C - SCAN, 한방향으로 읽기

    BOOT BLOCK
    - 컴퓨터를 구동시키 위한 init 시스템, bootstrap(운영 체제 커널)
    - ROM 에 저장

    RAID (Redundant Arrays of Independent Disks)
    - 데이터의 읽기 쓰기를 병렬
    - 올바른 정보가 가져와져있는지
    - redundant 한 정보를 저장해 놓으면 데이터가 깨지는 것을 어떻게 막을 수 있을까?

     redundancy, 중요한 데이터라면 그냥 저장 할 수 없음 (reliability 보장)
     - 모든 디스크 내용을 미러링 (데이터를 카피하여 보관)
     - high reliable (비쌈)
     parallelism, 퍼포먼스를 올리기
     - striping 을 통해서 병렬적으로 실행
     - 효율적, reliability 와는 관계 없음


     memory - mapped I/O
     - cpu I/O address 에 어떤 인스트럭트가 달려있는지 메모리에 매핑 시켜줌
     - 메모리에 명령을 주면 컨트롤 레지스터의 역할을 하는 것

     three type of I/O
     - polling (= busy waiting): 데이터가 올때까지 계속해서 체크(상태 레지스터가) 하는 것
     - interrupt: ISR (interrupt vector table에서 처리하도록 함)
     - DMA(Direct Memory Access): 대용량 데이터일 경우


    blocking I/O vs non-blocking I/O
    - blocking : 쓰레드 중단 후, running queue -> waiting queue
    - non-blocking : 쓰레드 실행을 정지시키지 않고, 바로 리턴해버림, waiting queue 에서 기다리지 않음
    - asynchronous system call : 실행을 계속 해나감

    non-blocking 의 read() vs asynchronous 의 read()
    - non-blocking: 즉시 리턴을 하고, 데이터가 사용하능한지 확인
    - asynchronous: 요청만 하고 자기 할 일을 해버림


    file system
    - apps 의 프로그램을 작성할 때, logical 하게 스토리지에 데이터를 쓰는 방법
    - file, directory

    접근 방법
    1. sequential
    2. random

    file system level
    - application programs -> logical file system(라이브러리 형태로 제공하며 우리가 사용할 수 있음) -> file - organization module -> basic file system(o/s 레벨에서 관리) -> I/O control(sector 단위로 쓰고 읽게 해줄 수 있는 것) -> devices(hdd 에 쓰고 읽기 sector 단위로)

    allocation method
    - 하드디스크의 섹터에 어떻게 올릴 수 있을까?

    - contiguous, 파일을 연속적으로 통으로 할당해주기 (FIFO, SCAN 뭘써도 효율적)
        - 파일 읽고 지우는 상황에서 external fragmentation 발생
    - linked allocation, linked list 형태로 할당하기 (start, end 값만 저장)
        - 순서대로 엑세스 하는 경우에는 괜찮음
        - FAT, 어떤 볼륨에 링크드 리스트의 인덱스를 넣어보기(모든 포인터들을 인덱스 블럭에 담아놓기)

    - free-space management 관리

*/

