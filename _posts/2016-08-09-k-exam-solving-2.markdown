---
layout: post
title:  "K-2"
categories: [exam]
---

 1. ***캐시가 필요한 이유는? Cache hit ratio 에 대해 설명하시오.***
	
	운영체제의 Cache를 설명하자면 CPU와 memory 간에 속도가 훨씬 빠른 Cache를 둠으로 두 장치 사이의 속도차를 해소하기 위함이다. (어디까지나 속도를 높여주기 위한 요소이고 필수적인 것은 아니라고 생각됨) Cache는 가격이 높기 때문에 용량이 크지는 않다. 
	
	Cache hit ratio는 CPU가 원하는 데이터(아마 page 단위)가 Cache에 존재하고 있을 확률을 뜻한다. Cache에는 이전에 사용한 데이터가 남아있거나 앞으로 사용될거라 예측되는 데이터가 올라가 있을 수 있다. 어떤 데이터를 남길지, 혹은 어떤 데이터를 미리 불러올지를 예측하는 알고리즘에 따라 Cache hit ratio에 영향을 주게 된다.
	
 2. ***메모리 접근하는데 x 사이클이 걸리고 캐시에 접근하는데 y 사이클이 걸리며 캐시 hit rate 가 h %일 때 effective access time은?***
 
    hit rate = h %
    
    memory access time = x cycle
    
    cache access time = y cycle

		 effective-access-time = hit-rate * cache-access-time + miss-rate * lower-level-access-time

		= h * y + (1 - h) * x
 
 3. ***페이지 폴트는 언제 발생하는가? 페이지 폴트 비율과 cache miss 비율 중 큰 것은? 그 이유는?***

    Page fault 는 virtual memory에는 mapping 돼있는 데이터(page)가 필요할 때 실제 memory에는 없는 경우 발생한다. Page fault가 발생하면 paging을 통해 필요한 page를 실제 memory로 로드한다. 
    
    Memory에 비해 Cache의 용량이 훨씩 작기 때문에 Page fault에 비해서 Cache miss가 더 자주 일어날 것이라 생각된다.   