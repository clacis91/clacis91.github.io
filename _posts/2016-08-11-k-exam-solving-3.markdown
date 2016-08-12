---
layout: post
title:  "K-3"
categories: [exam]
---

 1. ***캐쉬 메모리와 메인 메모리의 주소 지정 방식의 차이점이 무엇인가?***
 
	주소 지정 방식? Addressing mode? Cache는 Mapping이라는게 다른가...?
	
	대신 읽어보자 [http://micol.tistory.com/173](http://micol.tistory.com/173)
 
 2. ***캐쉬를 구성하는 컴포넌트에 무엇이 있는가? 각 컴포넌트는 어떤 역할을 하는가?***
 
 3. ***캐쉬에서 태그 매칭이 무엇이고 왜 필요한가?***
 
	CPU가 원하는 데이터가 Cache에 저장돼있는지 확인할때 Tag를 확인한다. Cache는 Memory에 비해 용량이 작기 때문에 Tag를 통해 메모리 주소를 Mapping하도록 돼있다. Mapping된 정보만 보고 Memory를 거치지 않고도 원하는 데이터가 Cache에 올라와 있는지 확인하는 것이다.
	
 4. ***캐쉬에서 블록(라인) 크기를 크게 했을 때와 작게 했을 때 어떤 장단점이 있을까?***
 
	Temporal locality(최근 사용된 데이터는 다시 사용될 가능성이 크다)
	
	Spatial locality(최근 사용된 곳의 근처의 데이터는 사용될 가능성이 크다)
	
	블록의 크기가 작아짐 -> Temporal locality 증가 - 블록의 크기가 작기 때문에 로드 시간 감소? / Spatial locality 감소 - 인접한 데이터가 한 블록에 존재하지 않을 확률이 커진다 
	
	블록의 크기가 커짐 -> Temporal locality 감소 - 블록의 크기가 크기 때문에 로드 시간 증가? / Spatial locality 증가 - 인접한 데이터가 한 블록에 존재할 확률이 커짐
	
 5. ***Direct-mapped cache와 set-associative cache의 장단점은 무엇인가?***
	
	([http://egloos.zum.com/rantis7/v/2879608](http://egloos.zum.com/rantis7/v/2879608) 참조)
	
	![](/assets/K-3-image/1.jpg)
	
	Memory는 Cache page와 Cache line이라는 단위로 나뉘어진다. Cache line이 모여 Cache page가 되고 page와 line의 크기는 환경에 따라 다르다.
	
	* Fully-associative

		![](/assets/K-3-image/2.jpg)

		Cache page를 사용하지 않는다. 따라서 Cache line은 memory의 어느 구역이든지 저장될 수 있다. 간단한 구조에서는 최고의 성능을 보이지만 Cache가 커질수록 비교해야할 line이 늘어나기에 복잡도가 증가한다.

	* Direct Mapping

		![](/assets/K-3-image/3.jpg)

		1-Way associative cache 방식이라고도 한다. Memory의 어느 Cache Page의 n line은 실제 Cache의 n line에 저장된다. 같은 page를 돌면서 특정 line만 확인하면 되기에 복잡성은 제일 낮지만, 공간 효율이 떨어진다. 

	* Set-associative

		![](/assets/K-3-image/4.jpg)

		n-Way associative cache는 Fully associative와 Direct mapping 방식이 혼합된 개념이다. Cache를 n-way의 구역으로 나눈 후 (associative) 그 안에서 line별로 mapping한다. 중간의 복잡도와 중간의 공간 효율을 가진다. 

 6. ***Write-through cache와 write-back cache에 대해서 설명해 보시오.***

	Write-through : Cache와 Memory를 같이 update함. Memory Access가 있기 때문에 상대적으로 속도가 느림
	
	Write-back : Cache만 update하고 추후 Cache가 교체될때 memory를 update함. 

	Inconsistency의 문제 : Memory와 Cache에 저장된 데이터의 일관성이 중요. Write-back은 inconsistency가 발생할 가능성이 있다(dirty bit에 표시된다).
	
 7. ***Write-through cache는 write buffer를 보통 사용하는데 이의 역할은 무엇인가?***
 
	Cache와 Memory를 동시에 update 하는 것은 개념상으로 하는 얘기지 물리적으로 속도 차이가 나기 때문에 buffer를 두는 것.

 8. ***코어가 10개 있는 cpu에서 프로세스 하나를 균등하게 처리하면 이상적인 경우, 시간이 얼마나 줄어드는가? 그런데 프로세스에서 분할되지 않는 20%의 작업이 있다고 하면, 시간이 얼마나 줄어드는가? 이제 코어가 수백개, 혹은 무한개 있다고 하면 얼마나 줄어드는가?***
 
 9. ***Cache에서 사용하는 replacement policy에는 어떤 것이 있는가?***
 
	* FIFO : 말그대로
 
	* LRU (Least Recently Used) : 오랫동안 사용되지 않은 블록을 교체
	* Optimal : 가장 오랫동안 사용되지 않을 블록을 예측하여 교체. 실제로는 불가능한 policy라고 보면 됨.

	* Random : 말그대로