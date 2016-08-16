---
layout: post
title:  "K-4"
categories: [exam]
---

 1. ***Kernel을 필요에 의해 어느정도 수정했다고 하자. 이 kernel이 제대로 작동하는지를 알기 위해서 어떤 test를 해야겠나?***

	커널패치를 무턱대고 적용해버리면 에러가 발생할 경우 OS가 맛이 가게 될것이다. 외부 저장장치를 이용해 부팅해서 테스트하거나, 멀티부트 모듈(ex. lilo) 을 이용하는 방법이 있다.

 2. ***Sequential program과 multithread program에서 error detection에 대한 차이점에는 어떤 것이 있나?***

	Multithread program은 에러 발생시 다른 thread의 상황을 고려하여 에러 처리를 해야한다.
	
 3. ***어떤 C program으로 작성되어 수행중인 process가 있다고 가정하자. C언어에서는 직접적으로 주소를 변수에게 지정해 줄 수 있다. 만약 주소 1, 2, 3, 4에 변수를 잡아서 어떤 일을 수행하는 process라고 하자. 이 process를 한 시스템에 동시에 두번 수행시켰다. 그랬을 때 한 프로세스가 1, 2, 3, 4 주소에 있는 변수를 바꿨을 때 다른 프로세스의 변수들에도 영향을 끼치는가?***

	아닐거라 생각됨. Process마다 일정 부분을 할당받고 그 안에서 relative하게 동작할거라고 생각된다. 만약 이 생각이 틀렸다면 영향을 줄 수 있다고 생각됨 
	
 4. ***Thread와 process의 차이는 무엇인가?***
 
	Process보다도 작은 작업 단위가 Thread다. 한 Process를 여러 Thread가 같이 처리하는 것이다. 각각의 Process는 서로 다른 자원이 할당되어 독립적으로 처리되지만, 한 Process내의 Thread들은 자원을 공유한다. 이는 context switch가 빨라진다는 이점이 있지만, 자원간의 sync 문제를 신경써야 한다.
	
 5. ***IPC란?***
 
	Inter Process Communication, Process간에 데이터를 주고받는 행위를 말한다
 
 6. ***Logical address와 physical address의 차이는 무엇인가?***
 
	프로그램이 Memory에 올라갈때 메모리를 일정 크기로 나눠서 사용하게 된다. 이때 사용되는 위치는 매번 다르기 때문에 주소를 특정할 수 없기 때문에 주소를 relative하게 고려해서 프로그래밍 해야하는데 이러한 개념의 address를 Logical address라 한다. Physical address는 실제로 하드웨어에 기록된 주소를 뜻한다.    
	
 7. ***Logical address를 physical address로 바꾸어주는 hw가 무엇인가?***
 
	MMU(Memory Management Unit)
	
 8. ***Interrupt란 무엇인가? interrupt를 두 종류로 나눈다면 어떻게 되는가? (software interrupt/hardware interrupt) 두 interrupt의 차이는 무엇인가?-두 interrupt의 handler를 서로 구분해서 구현해야 하는 것이 좋은가, 아니어도 상관 없는가?***
 
	Hardware interrupt는 예를들어 키보드의 입력이나 네트워크 신호 등 I/O 적인 요청이 있을 때 발생하는 interrupt고 Software interrupt는 에러 handling(0으로 나눈다던지)을 위한 interrupt다. 두 handler는 구분되어야 하는데 hardware interrupt 발생 시 현재 수행중인 instruction을 마친 후 처리해도 괜찮지만 software interrupt는 발생 즉시 수행중인 동작을 멈춰야하는 차이가 있다.
	
 9. ***C로 숫자로 직접 입력된 주소를 참조하는 프로그램을 짜서 컴파일한 후, 두 개를 실행시켰다고 하자. 그러면 이 두 프로세스는 물리적으로 같은 곳을 참조하나? 만약 아니라면, 어떻게 서로 다른 물리적 공간을 참조할 수 있나?***
 
	(3번하고 비슷한 문제?)물리적으로 같은 공간을 참조하지는 않는다. Process가 올라갈때 Memory는 Page(Frame)로 나뉘어서 공간을 할당해주는데 이때 직접 입력된 주소도 base address에 relative한 offset개념으로 지정된다. 
	
	
 1. ***Paging이 무엇인가? paging을 할 때 어떻게 실제 메모리 주소에 데이터를 전송하는가? page table에는 어떤 항목이 저장되는가? page table은 어디에 저장되는가? page table이 메모리에 저장되면, paging을 할 떄 메모리를 두 번 참조해야 되는데, 좀 더 빠르게 하는 방법은 없는가? TLB에는 어떤 항목이 저장되는가?***
 
	Memory에 Process가 할당할때 그냥 덩어리째 할당하다보면 process 할당 공간간에 빈 공간이 생기게 된다. 이를 fragmentation이라 부른다. Fragmentation을 피하고자 Memory와 Process를 일정한 크기 단위(Memory에선 Frame, Process는 Page라고 함)로 나누어서 Memory에 올린다. (다만 이 경우 internal fragmentation, Page크기보다 process가 작아 남는 공간이 생길 수 있다.) 이 동작을 paging이라 부른다.
	
	Paging시 어느 page가 어느 frame에 할당됐는지 찾아가기 위해 page table을 생성해서 memory에 저장해둔다. page table에는 process id, page number, page offset이 저장되어 이를 통해 실제 주소를 계산하여 찾아간다. 
	
	Page table 또한 memory에 저장돼있기 때문에 (1) 주소계산 (2) 그 주소로 가서 Process 수행을 위해 memory에 두번 접근할 수 밖에 없는데 이를 개선하기 위해 TLB(Translation Lookaside Buffer)라는 Cache를 둔다. 자주 사용되는 page는 TLB에 저장되고, 사용하려는 주소가 TLB에서 발견되면 (1)단계를 건너뛰고 바로 (2) 단계로 갈 수 있는 것이다. TLB table에는 page table과 비슷하게 page number, page offset이 저장된다.


 1. ***Thread와 Process의 차이는? Thread끼리 context switching 하는 과정에 대해 설명하시오. 같은 프로세스 내부의 thread들끼리 전환되는 것과 다른 프로세스간의 thread까리 전환되는 것이 어떻게 다른가?***

 
	![](/assets/K-4-image/process-vs-thread.png)
 
	Thread간에 대부분의 데이터는 공유되고 일부만(Stack, Registers) 독립적으로 가지기 때문에 context-switching 과정이 훨씬 빠르게 이루어질 수 있다.  
	
 1. ***Virtual Address와 Virtual memory에 대해 설명하시오.***
 
	가용되는 Process의 크기가 Physical memory보다 커지는 경우가 생길 수 있다. 이런 경우 memory가 아닌 하드디스크와 같은 저장장치를 memory처럼 활용하게 되는데 이를 virtual memory라고 한다.
	
	 Physical memory에 원하는 page가 존재하지 않는 경우를 page fault가 발생했다고 한다. Page fault가 발생할 경우 page replacement 알고리즘에 의해 physical memory에 있는 page와 virtual memory에 있는 page를 교체해야 한다. 알고리즘에는 FIFO, LRU, 2nd chance 등을 사용한다.
	 
	  Virtual Address는 앞서 나온 Logical address와 거의 동의어라고 생각하면 된다. 정확히 말하면 Virtual address를 구성할때 Logical address 개념을 사용한다고 보면 될듯.
	  
	  
 1. ***시스템 콜과 인터럽트의 차이는? 인터럽트가 걸리면 어떤 일이 일어나고, 인터럽트
처리 후에 어떻게 이전 상태로 돌아가나?***

	System call은 process가 system 자원을 사용하기 위해 kernel에 요청하는 행위를 뜻하고, interrupt는 당장 수행이 필요한 process의 요청으로 현재 진행중인 것을 잠시 멈추고 다른 process를 수행하는 행위를 뜻한다.
	
	 Interrupt 발생 시 현재의 context를 저장해두고 동작이 종료되면 저장돼있는 context로 다시 돌아가서 하던 동작을 마저 수행한다.
	 
 1. ***Non-preemptive scheduling이란? Critical section이란?***
 
	Preemptive란 '선점'이란 뜻인데 scheduling을 하는데에 있어 우선권을 갖는 것을 허용하는 방식이다. 우선권을 가진 process는 다른 process가 자원을 사용하고 있어도 뺏어서 선점하는게 가능하다. 
 
	반면 Non-preemptive는 선점이 불가능하고 모든 process가 규칙을 지키며 자원을 사용하게 되는 방식이다. 단 이 방식에서는 각각의 process(혹은 thread)가 자원을 조금씩 소유한 상태에서 서로의 자원을 필요로 하게돼서 하염없이 대기만 하는 경우가 생길 수 있는데 이를 Deadlock이 발생했다고 한다. 
 
	이 Deadlock을 방지하기 위해 코드 단에 critical section 이란 개념이 도입된다. 단순하게 말해서 '이 section에서는 공유 자원이 사용되니까 다른 thread가 사용중인지 확인하는 절차를 밟고 들어가라' 라는 개념이다. 자원 사용이 가능해서 사용했다면 일종의 열쇠를 획득하고, 사용후에는 반납하는 절차를 수행하게 된다. 
 
	비슷한 개념으로 mutex, semaphore라는 것들이 있다. Critical section이 user level에서 구현되는 기능이고, mutex는 kernel level에서 구현된다. Semaphore는 mutex와 비슷한데 (정확히는 mutex가 semaphore 의 일종이라고 한다) 한번에 한개의 thread만 동작하는 것이 아니라, 한번에 동작할 수 있는 최대 thread를 정해둘 수 있게 한 것이다.
	
 1. ***세마포어의 개념은? 주요 연산 2가지는? 어떻게 구현해야 하는가?***
 
	wait(S) / signal(S)
  
		function wait(S){
			while(S<=0){} //세마포어가 0이하일 경우 계속 루프안에 갇혀있게 됨.
			S--;
		}

		function signal(S){
			S++;
		}
		
 1. ***운영 체제에서 memory management 기법중 하나로 paging이 많이 사용되고 있습니다. Paging 기법의 장단점들로는 무엇이 있으며, hierarchical paging 혹은 inverted page table은 어떤 환경에서 유리할까요?***
 
	Memory가 커질수록 Page table의 크기가 덩달아 커지게 된다. 이로인해 Page table의 크기를 관리해야하는 필요성이 생겼는데, 기법들은 다음과 같다. 
 
	* hierarchical paging
 
	![](/assets/K-4-image/hierarchical-paging.jpg)
	
	Page table을 여러개의 table로 쪼개서 관리하는 기법이다. 전체가 아닌 필요한 Page table만 로드하면 되기 때문에 memory 효율이 좋아진다. 하지만 여러단계를 거쳐서 실제 주소에 접근하기 때문에 memory access 횟수가 많아지는 단점이 있다.
	
	* Hashed paging
 
	![](/assets/K-4-image/hashed-paging.jpg)
	
	Hash function을 이용해 page table을 구성하는 방법이다. Hash값이 중복되는 경우에는 linked list로 저장
	
	* Inverted paging
 
	![](/assets/K-4-image/inverted-paging.jpg)
	
	다른 page table은 Virtual memory의 주소값과 page number, offset을 mapping하기 때문에 그에 비례하여 page table이 늘어나게 된다. 또한 process마다 page table이 생성된다. Inverted paging 방식에서는 역으로 Physical memory의 개수만큼만 page table이 생성된다. 이는 pid와 page number를 mapping 해서 구현된다. pid로 관리되기 때문에 process마다 table이 생길 필요도 없다. Page table의 크기는 비약적으로 줄어들지만 대신 frame마다 찾는 page인지 확인하기 위해 search time이 길어지는 단점이 있다.
	
 1. ***요즘 많은 사람들이 각각 자신만의 스마트폰을 사용하고 있습니다. 이처럼 각 개인 스마트폰을 위하여 process system을 design하려고 합니다. 스마트폰에서 각 application이 하나의 process로 독립해서 실행하는 것이 나을까요? 아니면, 하나의 thread로 만들어져서 실행하는 것이 나을까요? 어느 쪽이 나을지 결정하고, 그 이유를 설명하세요.***
 
	thread 아닐까. 어차피 코딱지만한 화면이라 멀티태스킹 지원이 힘든데 쓸데없이 자원 할당하기보다 context switch속도나 버는게 낫지 않을까요